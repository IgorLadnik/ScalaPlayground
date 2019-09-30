package Kafka

import java.util._

import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Consumer(val bootstrapServers: String,
               val topic: String,
               val groupId: String,
               val partition: Int,
               val offset: Int) {

  def startConsuming(topic: String, p: (String, String) => Unit): Future[Unit] = Future {
    consumer.subscribe(Arrays.asList(topic))
    while (true) {
      val record = consumer.poll(1000).asScala
      for (data <- record.iterator) {
        try {
          p(data.key, data.value)
        } catch {
          case e: Exception => e.printStackTrace
        }
      }
    }
  }

  val props = new Properties()
  props.put("bootstrap.servers", bootstrapServers)
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("auto.offset.reset", "latest")
  props.put("group.id", groupId)
  val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
}
