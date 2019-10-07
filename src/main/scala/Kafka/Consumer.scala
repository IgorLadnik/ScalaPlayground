package Kafka

import java.util._

import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import org.apache.kafka.common.serialization.ByteArrayDeserializer

class Consumer(val schema: Schema,
               val id: Int,
               val version: Int,
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
          p(data.key, serDeHelper.deserialize(data.value, topic))
          //p(data.key, data.value)
        } catch {
          case e: Exception => e.printStackTrace
        }
      }
    }
  }

//  private[Consumer] def deserialize(bts: Array[Byte]): GenericRecord =
//    reader.read(null, DecoderFactory.get().binaryDecoder(bts, null))

  val serDeHelper = new SerDeHelper(schema, id, version)
  //var schemaRegistryClient: SchemaRegistryClient = new SchemaRegistryClientEx(schema, id, version)
  //val kafkaAvroDeserializer = new KafkaAvroDeserializer(schemaRegistryClient)

  private[Consumer] val props = new Properties()
  props.put("bootstrap.servers", bootstrapServers)
  //props.put("auto.offset.reset", "latest")
  props.put("key.deserializer", classOf[StringDeserializer].getCanonicalName)
  props.put("value.deserializer", classOf[ByteArrayDeserializer].getCanonicalName)
  //props.put("value.deserializer", kafkaAvroDeserializer)
  props.put("group.id", groupId)

  //Use Specific Record or else you get Avro GenericRecord.
  //props.put("specific.avro.reader", "true")
  //props.put("value.deserializer", classOf[KafkaAvroDeserializer].getCanonicalName) //new KafkaAvroDeserializer(new SchemaRegistryClientEx(User.SCHEMA$)))
  //props.put("schema.registry.url", "ga")

  //val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
  //val valueDeserializer: Deserializer[User] = new KafkaAvroDeserializerEx[User](new SchemaRegistryClientEx(User.SCHEMA$))
  //val consumer = new KafkaConsumer[String, User](props/*, new StringDeserializer, valueDeserializer*/)

  //private[Consumer] val reader = new GenericDatumReader[GenericRecord](schema)
  private[Consumer] val consumer = new KafkaConsumer[String, Array[Byte]](props)
  //private[Consumer] val consumer = new KafkaConsumer[String, GenericRecord](props)
}

//private def initSchemaRegistryClient(settings: SchemaRegistryClientSettings): SchemaRegistryClient = {
//  val config = settings.authentication match {
//  case Authentication.Basic(username, password) =>
//  Map(
//  "basic.auth.credentials.source" -> "USER_INFO",
//  "schema.registry.basic.auth.user.info" -> s"$username:$password"
//  )
//  case Authentication.None =>
//  Map.empty[String, String]
//}
//
//  new CachedSchemaRegistryClient(settings.endpoint, settings.maxCacheSize, config.asJava)
//}