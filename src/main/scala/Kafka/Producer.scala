package Kafka

import java.io.ByteArrayOutputStream
import java.util.Properties

import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.{BinaryEncoder, EncoderFactory}
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import org.apache.avro.specific.SpecificDatumWriter

class Producer(val bootstrapServers: String,
               val topic: String,
               val groupId: String,
               val partition: Int,
               val offset: Int) {

  def send(key: String, genericRecord: GenericRecord) = {
    try {
      producer.send(new ProducerRecord[String, Array[Byte]](topic, key, serialize(genericRecord)))
    } catch {
      case e: Exception => e.printStackTrace
      close
    }
  }

  def close = producer.close

  private[Producer] def serialize(genericRecord: GenericRecord): Array[Byte] = {

    // Serialize generic record into byte array
    val writer = new SpecificDatumWriter[GenericRecord](genericRecord.getSchema)
    //val writer = new GenericDatumWriter[GenericRecord](genericRecord.getSchema)
    val out = new ByteArrayOutputStream
    val encoder: BinaryEncoder = EncoderFactory.get.binaryEncoder(out, null)
    writer.write(genericRecord, encoder)
    encoder.flush
    out.close
    out.toByteArray
  }

  //Read avro schema file
  //val schema: Schema = new Parser().parse(Source.fromURL(getClass.getResource("/schema.avsc")).mkString) //1

  private[Producer] val props = new Properties()
  props.put("bootstrap.servers", bootstrapServers)
  props.put("key.serializer", classOf[StringSerializer].getCanonicalName)
  props.put("value.serializer", classOf[ByteArraySerializer].getCanonicalName)

  private[Producer] val producer = new KafkaProducer[String, Array[Byte]](props)
}

