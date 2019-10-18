package KafkaHelper

import java.util._
import io.confluent.kafka.schemaregistry.client.{SchemaMetadata, _}
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.avro.Schema

class SchemaRegistryClientEx(val schema: Schema, val id: Int, val version: Int) extends SchemaRegistryClient {

  val strSchema = schema.toString
  val schemaMetadata = new SchemaMetadata(id, version, schema.toString)

  override def register(var1: String, var2: Schema): Int = id
  override def getByID(var1: Int): Schema = schema
  override def getBySubjectAndID(var1: String, var2: Int): Schema = schema
  override def getLatestSchemaMetadata(var1: String): SchemaMetadata = schemaMetadata
  override def getSchemaMetadata(var1: String, var2: Int): SchemaMetadata = schemaMetadata
  override def getVersion(var1: String, var2: Schema ): Int = version
  override def testCompatibility(var1: String, var2: Schema): Boolean = true
  override def updateCompatibility(var1: String, var2: String): String = ""
  override def getCompatibility(var1: String): String = ""
  override def getAllSubjects(): Collection[String] = new ArrayList[String]
}
