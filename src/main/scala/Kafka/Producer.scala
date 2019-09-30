package Kafka

import java.util.Properties
import org.apache.kafka.clients.producer._

class Producer(val bootstrapServers: String,
               val topic: String,
               val groupId: String,
               val partition: Int,
               val offset: Int) {

  def send(key: String, value: String) = {
    try {
      producer.send(new ProducerRecord[String, String](topic, key, value))
    } catch {
      case e: Exception => e.printStackTrace
      close
    }
  }

  def close = producer.close

  val props = new Properties()
  props.put("bootstrap.servers", bootstrapServers)
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  val producer = new KafkaProducer[String, String](props)
}

