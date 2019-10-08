package KafkaHelper

import ReadWriteFile.FileHelper
import org.apache.avro.Schema

class RecordConfig(val schemaRegistryUrl: String) {
  val strSchema = FileHelper.readUrlFromFile(schemaRegistryUrl)
  //if (!StrExt.isNullOrEmpty(strSchema)) {
    val schema = new Schema.Parser().parse(strSchema)
    val id = 322272
    val version = 5
  //}
}
