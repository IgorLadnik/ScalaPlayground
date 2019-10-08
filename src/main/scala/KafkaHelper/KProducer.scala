package KafkaHelper

import java.util.Properties

import io.confluent.kafka.serializers.{KafkaAvroDeserializer, KafkaAvroSerializer}
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

class KProducer(val config: Properties,
                val errHandler: (Exception) => Unit) {

  def !(key: String,  genericRecord: GenericRecord) = send(key, genericRecord)

  def send(key: String, genericRecord: GenericRecord) = {
    try {
      val topic = config.get(KafkaPropNames.Topic).asInstanceOf[String]
      producer.send(new ProducerRecord[String, Array[Byte]](topic,
        config.get(KafkaPropNames.Partition).asInstanceOf[Int],
        key, serialize(genericRecord, topic)))
    } catch {
      case e: Exception => errHandler(e)
      close
    }
  }

  def serialize(genericRecord: GenericRecord, topic: String): Array[Byte] =
    kafkaAvroSerializer.serialize(topic, genericRecord)

  def close = producer.close

  //Read avro schema file
  //val schema: Schema = new Parser().parse(Source.fromURL(getClass.getResource("/schema.avsc")).mkString) //1

  val recordConfig = new RecordConfig(config.get(KafkaPropNames.SchemaRegistryUrl).asInstanceOf[String])
  val schemaRegistryClient = new SchemaRegistryClientEx(recordConfig.schema, recordConfig.id, recordConfig.version)
  val kafkaAvroSerializer = new KafkaAvroSerializer(schemaRegistryClient)

  config.put(KafkaPropNames.KeySerializer, classOf[StringSerializer].getCanonicalName)
  config.put(KafkaPropNames.ValueSerializer, classOf[ByteArraySerializer].getCanonicalName)

  private[KProducer] val producer = new KafkaProducer[String, Array[Byte]](config)
}

