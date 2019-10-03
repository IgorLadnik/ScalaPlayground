//package KafkaObjects
//
///** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
//
//import ReadWriteFile.FileHelper
//
//import scala.annotation.switch
//
//case class User(var id: Int, var name: String) extends org.apache.avro.specific.SpecificRecordBase {
//  def this() = this(0, "")
//  def get(field$: Int): AnyRef = {
//    (field$: @switch) match {
//      case pos if pos == 0 => {
//        id
//        }.asInstanceOf[AnyRef]
//      case pos if pos == 1 => {
//        name
//        }.asInstanceOf[AnyRef]
//      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
//    }
//  }
//  def put(field$: Int, value: Any): Unit = {
//    (field$: @switch) match {
//      case pos if pos == 0 => this.id = {
//        value
//        }.asInstanceOf[Int]
//      case pos if pos == 1 => this.name = {
//        value.toString
//        }.asInstanceOf[String]
//      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
//    }
//    ()
//  }
//  def getSchema: org.apache.avro.Schema = User.SCHEMA$
//}
//
//object User {
//  //  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
//  //    Source.fromURL(getClass.getResource("/userSchema.avsc")).mkString)
//  val strSchema = FileHelper.readUrlFromFile("UserSchema.txt")
////  if (StrExt.isNullOrEmpty(url))
////    return null
//  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse(strSchema)
//}