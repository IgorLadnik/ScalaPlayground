package KafkaHelper

import org.apache.avro.Schema
import scala.io.Source
import scala.util.parsing.json.JSON

class RecordConfig(val schemaRegistryUrl: String) {

  def getSchemaString(strOrg: String): String = {
    val startIndex = strOrg.indexOf("{\"type\":")
    val endIndex = strOrg.lastIndexOf("}")
    strOrg.substring(startIndex, endIndex)
  }

  val strConfig = Source.fromFile(schemaRegistryUrl).mkString.replace("\r", "").replace("\n", "").replace("\t", "").replace(" ", "")
  val strSchema = getSchemaString(strConfig)
  val schema = new Schema.Parser().parse(strSchema)

  val jsonValue = JSON.parseFull(strConfig)

  // See what we found.
  val jsonObj = jsonValue match {
    case Some(map: Map[String, String]) => map.asInstanceOf[Map[String, String]]
    case _  => println("JSON invalid")
  }

  val mapConfig = jsonObj.asInstanceOf[Map[String, String]]
  val subject = mapConfig.getOrElse("subject", None).toString
  val version  = mapConfig.getOrElse("version", -1).asInstanceOf[Double].toInt
  val id = mapConfig.getOrElse("id", -1).asInstanceOf[Double].toInt
}
