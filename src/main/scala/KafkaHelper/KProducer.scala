package KafkaHelper

import java.util.Properties

import io.confluent.kafka.serializers.{KafkaAvroDeserializer, KafkaAvroSerializer}
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class KProducer(val config: Properties,
                val errHandler: (String) => Unit) {

  import scala.collection.mutable.Queue

  def !(key: String,  genericRecord: GenericRecord): Unit = send(key, genericRecord)

  def send(key: String, genericRecord: GenericRecord): Unit = {
    enqueueGenericRecord(new Tuple2(key, genericRecord))

    if (isSending)
      return

    sendInner.onComplete {
      case Success(u: Unit) => {}
      case Failure(e: Exception) => errHandler(e.getMessage)
    }
  }

  private[KProducer] def sendInner: Future[Unit] = Future {

    isSending  = true

    while(isNonEmptyQuePair) {

      try {
        val tuple = dequeueGenericRecord
        producer.send(new ProducerRecord[String, Array[Byte]](topic,
                              config.get(KafkaPropNames.Partition).asInstanceOf[Int],
                              tuple._1, serialize(tuple._2, topic)))
      }
      catch {
        case e: Exception => errHandler(e.getMessage)
        close
      }

      isSending = false
    }
  }

  def serialize(genericRecord: GenericRecord, topic: String): Array[Byte] =
    kafkaAvroSerializer.serialize(topic, genericRecord)

  def close = {
    producer.close
    println("Kafka Producer closed")
  }

  private[KProducer] def enqueueGenericRecord(t: (String, GenericRecord)) = {
    this.synchronized {
      quePair.enqueue(t)
    }
  }

  private[KProducer] def dequeueGenericRecord: Tuple2[String, GenericRecord] = {
    this.synchronized {
      quePair.dequeue
    }
  }

  private[KProducer] def isNonEmptyQuePair: Boolean = {
    this.synchronized {
      quePair.nonEmpty
    }
  }

  val topic = config.get(KafkaPropNames.Topic).asInstanceOf[String];
  val quePair = new Queue[Tuple2[String, GenericRecord]]

  //Read avro schema file
  //val schema: Schema = new Parser().parse(Source.fromURL(getClass.getResource("/schema.avsc")).mkString)

  @volatile var isSending = false

  val recordConfig = new RecordConfig(config.get(KafkaPropNames.SchemaRegistryUrl).asInstanceOf[String])
  val schemaRegistryClient = new SchemaRegistryClientEx(recordConfig.schema, recordConfig.id, recordConfig.version)
  val kafkaAvroSerializer = new KafkaAvroSerializer(schemaRegistryClient)

  config.put(KafkaPropNames.KeySerializer, classOf[StringSerializer].getCanonicalName)
  config.put(KafkaPropNames.ValueSerializer, classOf[ByteArraySerializer].getCanonicalName)

  private[KProducer] val producer = new KafkaProducer[String, Array[Byte]](config)
}



