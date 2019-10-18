package KafkaHelper

import java.util.Properties
import io.confluent.kafka.serializers.{KafkaAvroDeserializer, KafkaAvroSerializer}
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class KProducer(val config: Properties,
                val logger: (String) => Unit) {
  // Ctor

  val topic = config.get(KafkaPropNames.Topic).asInstanceOf[String];

  //Read avro schema file
  //val schema: Schema = new Parser().parse(Source.fromURL(getClass.getResource("/schema.avsc")).mkString)

  val recordConfig = new RecordConfig(config.get(KafkaPropNames.SchemaRegistryUrl).asInstanceOf[String])
  val schemaRegistryClient = new SchemaRegistryClientEx(recordConfig.schema, recordConfig.id, recordConfig.version)
  val kafkaAvroSerializer = new KafkaAvroSerializer(schemaRegistryClient)

  config.put(KafkaPropNames.KeySerializer, classOf[StringSerializer].getCanonicalName)
  config.put(KafkaPropNames.ValueSerializer, classOf[ByteArraySerializer].getCanonicalName)

  private[KProducer] val producer = new KafkaProducer[String, Array[Byte]](config)

  // Methods

  // Same as send()
  def !(key: String, genericRecord: GenericRecord) = send(key, genericRecord)

  def send(key: String, genericRecord: GenericRecord) = {
    Await.ready(sendInner(key, genericRecord), Duration.Inf).onComplete {
      case Success(u: Unit) => { }
      case Failure(e: Exception) => { logger(e.getMessage); close }
    }
  }

  private[KProducer] def sendInner(key: String, genericRecord: GenericRecord): Future[Unit] = Future {
      producer.send(new ProducerRecord[String, Array[Byte]](topic,
                    config.get(KafkaPropNames.Partition).asInstanceOf[Int],
                    key, serialize(genericRecord, topic)))
  }

  def serialize(genericRecord: GenericRecord, topic: String): Array[Byte] =
    kafkaAvroSerializer.serialize(topic, genericRecord)

  def close = {
    producer.close
    println("Kafka Producer closed")
  }
}



