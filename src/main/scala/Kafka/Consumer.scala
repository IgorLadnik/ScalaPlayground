package Kafka

import java.util._
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DecoderFactory
import org.apache.avro.generic.GenericDatumReader
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

class Consumer(val schema: Schema,
               val bootstrapServers: String,
               val topic: String,
               val groupId: String,
               val partition: Int,
               val offset: Int) {

  def startConsuming(topic: String, p: (String, GenericRecord) => Unit): Future[Unit] = Future {
    consumer.subscribe(Arrays.asList(topic))
    while (true) {
      val record = consumer.poll(100).asScala
      for (data <- record.iterator) {
        try {
          p(data.key, deserialize(data.value))
        } catch {
          case e: Exception => e.printStackTrace
        }
      }
    }
  }

  private[Consumer] def deserialize(bts: Array[Byte]): GenericRecord =
    reader.read(null, DecoderFactory.get().binaryDecoder(bts, null))

  private[Consumer] val props = new Properties()
  props.put("bootstrap.servers", bootstrapServers)
  props.put("auto.offset.reset", "latest")
  props.put("key.deserializer", classOf[StringDeserializer].getCanonicalName)
  props.put("value.deserializer",classOf[ByteArrayDeserializer].getCanonicalName)
  props.put("group.id", groupId)

  //Use Specific Record or else you get Avro GenericRecord.
  //props.put("specific.avro.reader", "true")
  //props.put("value.deserializer", classOf[KafkaAvroDeserializer].getCanonicalName) //new KafkaAvroDeserializer(new SchemaRegistryClientEx(User.SCHEMA$)))
  //props.put("schema.registry.url", "ga")

  //val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
  //val valueDeserializer: Deserializer[User] = new KafkaAvroDeserializerEx[User](new SchemaRegistryClientEx(User.SCHEMA$))
  //val consumer = new KafkaConsumer[String, User](props/*, new StringDeserializer, valueDeserializer*/)

  private[Consumer] val reader = new GenericDatumReader[GenericRecord](schema)
  private[Consumer] val consumer = new KafkaConsumer[String, Array[Byte]](props)
}
