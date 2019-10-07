package Kafka

import java.util.Properties

import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.StringSerializer
//import org.apache.avro.specific.SpecificDatumWriter
//import io.confluent.kafka.schemaregistry.client;
import org.apache.kafka.common.serialization.ByteArraySerializer

class Producer(val schema: Schema,
               val id: Int,
               val version: Int,
               val bootstrapServers: String,
               val topic: String,
               val groupId: String,
               val partition: Int,
               val offset: Int) {

  def send(key: String, genericRecord: GenericRecord) = {
    try {
      //import kafka.producer.KeyedMessage
      //new KeyedMessage[String, Array[Byte]]
      producer.send(new ProducerRecord[String, Array[Byte]](topic, partition, key, serDeHelper.serialize(genericRecord, topic)))
      //producer.send(new ProducerRecord[String, GenericRecord](topic, partition, key, genericRecord))
    } catch {
      case e: Exception => e.printStackTrace
      close
    }
  }

  def close = producer.close

//  private[Producer] def serialize(genericRecord: GenericRecord): Array[Byte] = {
//
//    val schema = genericRecord.getSchema()
//    val namespace = schema.getNamespace()
//    val subject = schema.getProp("Subject")
//    // Serialize generic record into byte array
//    //val writer = new SpecificDatumWriter[GenericRecord](genericRecord.getSchema)
//
//    val writer = new GenericDatumWriter[GenericRecord](genericRecord.getSchema/*, gd*/)
//    val out = new ByteArrayOutputStream
//    val encoder: BinaryEncoder = EncoderFactory.get.binaryEncoder(out, null)
//    writer.write(genericRecord, encoder)
//    encoder.flush
//    out.close
//    out.toByteArray
//  }

  //Read avro schema file
  //val schema: Schema = new Parser().parse(Source.fromURL(getClass.getResource("/schema.avsc")).mkString) //1

  val serDeHelper = new SerDeHelper(schema, id, version)
  //val schemaRegistryClient: SchemaRegistryClient = new SchemaRegistryClientEx(schema, id, version)
  //val kafkaAvroSerializer: KafkaAvroSerializer = new KafkaAvroSerializer(schemaRegistryClient)

  private[Producer] val props = new Properties()
  props.put("bootstrap.servers", bootstrapServers)
  props.put("key.serializer", classOf[StringSerializer].getCanonicalName)
  props.put("value.serializer", classOf[ByteArraySerializer].getCanonicalName)
  //props.put("value.serializer", kafkaAvroSerializer)

  private[Producer] val producer = new KafkaProducer[String, Array[Byte]](props)
  //private[Producer] val producer = new KafkaProducer[String, GenericRecord](props)
}

