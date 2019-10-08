package KafkaHelper

object KafkaPropNames {
  val BootstrapServers = "bootstrap.servers"
  val SchemaRegistryUrl = "schema.registry.url"
  val GroupId = "group.id"
  val KeySerializer = "key.serializer"
  val ValueSerializer = "value.serializer"
  val KeyDeserializer = "key.deserializer"
  val ValueDeserializer = "value.deserializer"

  val Topic = "topic"
  val Partition = "partition"
  val Offset = "offset"
}
