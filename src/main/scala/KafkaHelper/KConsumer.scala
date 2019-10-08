package KafkaHelper

import java.util._

import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class KConsumer(val config: Properties,
                val p: (String, GenericRecord) => Unit,
                val errHandler: (Exception) => Unit) {

  def startConsuming: Future[Unit] = Future {
    consumer.subscribe(Arrays.asList(topic))
    while (true) {
      val record = consumer.poll(100).asScala
      for (data <- record.iterator) {
        try {
          p(data.key, deserialize(data.value, topic))
        } catch {
          case e: Exception => e.printStackTrace
        }
      }
    }
  }

  def deserialize(bts: Array[Byte], topic: String): GenericRecord =
    kafkaAvroDeserializer.deserialize(topic, bts, recordConfig.schema).asInstanceOf[GenericRecord]

  val topic = config.get(KafkaPropNames.Topic).asInstanceOf[String]

  val recordConfig = new RecordConfig(config.get(KafkaPropNames.SchemaRegistryUrl).asInstanceOf[String])
  val schemaRegistryClient = new SchemaRegistryClientEx(recordConfig.schema, recordConfig.id, recordConfig.version)
  val kafkaAvroDeserializer = new KafkaAvroDeserializer(schemaRegistryClient)

  config.put(KafkaPropNames.KeyDeserializer, classOf[StringDeserializer].getCanonicalName)
  config.put(KafkaPropNames.ValueDeserializer, classOf[ByteArrayDeserializer].getCanonicalName)

  //config.put("auto.offset.reset", "latest")

  // Use Specific Record or else you get Avro GenericRecord.
  config.put("specific.avro.reader", "false")

  private[KConsumer] val consumer = new KafkaConsumer[String, Array[Byte]](config)
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