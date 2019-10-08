package Main

import java.util.Properties
import ApplyUnapply._
import DV.UrlParser.{StrExt, UrlInfoHolder}
import Extension.Helpers._
import Extension.MathInt
import IdioticSocketsWithThreadPool.{NetworkClient, NetworkServer}
import KafkaHelper._
import Placement._
import ReadWriteFile.FileHelper
import Variance._
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericData.Record
import KafkaHelper.KafkaPropNames

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Main {
  def main(args: Array[String]) {

    // Kafka ===================================================================
    val config = new Properties
    config.put(KafkaPropNames.BootstrapServers, "localhost:9092")
    config.put(KafkaPropNames.SchemaRegistryUrl, "schema.json")
    config.put(KafkaPropNames.Topic, "quick-start")
    config.put(KafkaPropNames.GroupId, "consumer-group")
    config.put(KafkaPropNames.Partition, 0)
    config.put(KafkaPropNames.Offset, 0)

    new KConsumer(config, (key, value) =>
        println(s"From Kafka: ${key} -> ${value.get("ID")} ${value.get("CreationTime")}"),
        e => e.printStackTrace)
      .startConsuming

    val producer = new KProducer(config, e => e.printStackTrace)

    // Create avro generic record object
    val genericUser: Record = new GenericData.Record(producer.recordConfig.schema)

    //Put data in that generic record and send it to Kafka
    {
      val ticks: Long = 1111
      genericUser.put("SEQUENCE", 1)
      genericUser.put("ID", 1)
      genericUser.put("CategoryID", 1)
      genericUser.put("YouTubeCategoryTypeID", 1)
      genericUser.put("CreationTime", ticks) //Calendar.getInstance.getTime)

      producer ! ("key1", genericUser)
    }
    {
      val ticks: Long = 2222
      genericUser.put("SEQUENCE", 2)
      genericUser.put("ID", 2)
      genericUser.put("CategoryID", 2)
      genericUser.put("YouTubeCategoryTypeID", 2)
      genericUser.put("CreationTime", ticks)

      producer ! ("key2", genericUser)
    }

    println("Press <Enter> to continue...")
    System.in.read

    producer.close

    // Collections ==============================================================
    collectionsExamples

    // Implicit =================================================================
    5 seconds
    val t: Unit = 5 times println("HI")

    val messageBox = new Messaging.MessageBox()
    messageBox ! new Messaging.Message("MSG")
    messageBox ! "a string"
    messageBox ! 139.5

    // Also Future
    (messageBox ? 1511).onComplete {
      case Success(msg) => println(s"Response on ? : Success of Future: ${msg}")
      case Failure(e) => println(s"Failure: ${e}")
    }

    val a = 9
    val b = 7
    println(s"Minimum of ${a} and ${b}: ${MathInt.min(a, b)}")

    // Function ==================================================================
    val fn = (n: Int) => n * 10
    val va = fn(3)

    // apply / unapply ===========================================================
    val au = ApplyUnapplyObj("aa", "bb")
    val ApplyUnapplyObj(name) = "pp@qq"

    println("qsample: " + au)
    println("name: " + name)

    // Operators overloading =====================================================
    val ptSum = new Point(2, 3) + new Point(3, 7)
    println(s"ptSum = ${ptSum}")
    println(ptSum ? "prefix")

    // Option ====================================================================
    val opStr: Option[String] = None //Some("Ga!") //None
    println(s"Print the option: ${opStr.getOrElse("option placeholder")}")

    // Companion Object was created implicitly for case class Location
    val loc = Location(10, 20, 15) // without "new" since this is case class

    // Move to a new location
    loc.move(10, 10, 5)

    // Function, Companion Object ================================================
    gps(x = 1, y = 1, p = /*new*/ Location(10, 20, 15))

    val zpt = new Location(10, 2, 7)
    val pt = getPoint(1, 1, zpt)

    // Array
    val list = Array(1.9, 2.1, 3.78)
    for (x <- list)
      println(x)

    // Futures test ==============================================================
    // Future call 1
    val testFutures = new TestFutures()
    testFutures.firstLengthyFunc("Ga1?").onComplete {
      case Success(num) => println(s"Success of Future: ${num}")
      case Failure(e) => println(s"Failure: ${e}")
    }
    Thread.sleep(5000)

    // Future call 2
    for {
      num1 <- testFutures.firstLengthyFunc("Ga2?")
      num2 <- testFutures.secondLengthyFunc(num = num1)
    } yield println(s"Success of Future 2: num = ${num1}, num2 = ${num2}")

    Thread.sleep(10000)

    // Idiotic Sockets with Thread Pool ==========================================
    val port = 11511
    val numOfThreadInPool = 3
    new NetworkServer(port, numOfThreadInPool).run
    new NetworkClient("localhost", port, numOfThreadInPool).run

    // Parse Nested URL - Use Precompiled JAR ====================================
    parseUrl("url_input.txt")

    // Variance ==================================================================
    val father = new Father
    val child = new Child

    // Co
    val personWrapperCoFather: PersonWrapperCo[Father] = new PersonWrapperCo[Father](father)
    val personWrapperCoChild: PersonWrapperCo[Father] = new PersonWrapperCo[Child](child)

    val fatherContainerCo = new PersonWrapperContainerCo(personWrapperCoFather)
    val childContainerCo = new PersonWrapperContainerCo(personWrapperCoChild)

    // Contra
    val personWrapperContraFather: PersonWrapperContra[Child] = new PersonWrapperContra[Father](father)
    val personWrapperContraChild: PersonWrapperContra[Child] = new PersonWrapperContra[Child](child)
  }

  // Call function by name ========================================================
  def gps(x: Int, y: Int, p: => Point) {
    p.move(x, y)
    println("x = " + x)
    println("y = " + y)
  }

  // "val" in stead of "def" ======================================================
  val getPoint = (x: Int, y: Int, p: Point) => {
    println(s"POINT${p}")
    new Point(x, y)
  }

  def parseUrl(inputFilePath: String) {
    //val url = "http://domain.com:88/?a=123&b=https%3A%2F%2Fwww.soundingsonline.com%2Ffeatures%2Fbucking-the-tide%3Fy1%3D55%26y2%3Dhttps%253A%252F%252Fwww.bgu.ac.il%253Fp1%253Dhttp%25253A%25252F%25252Fwww.iitk.ac.in%25253Fp1%25253D1%252526p2%25253D2%2526p2%253D2121%2526p3%253Dhttps%25253A%25252F%25252Fsome.org%25253Fp1%25253D10%252526p2%25253D20%26y3%3D139&c=99&d=https%3A%2F%2Fwww.qqqq.io%3Fa%3Daaa%26b%3Dbbb"

    val url = FileHelper.readUrlFromFile("url_input.txt")
    if (StrExt.isNullOrEmpty(url))
      return

    println(s"URL = ${url}\n")

    val test = new UrlInfoHolder().parse(url)

    if (test.Ex != null) {
      println(test.Ex)
      return
    }

    FileHelper.outputToFile(test)
  }

  def collectionsExamples = {
    val arr = Array(32, 4, -3, 7, 101, 21)
    val arr0 = arr.filter(a => 10 < a && a < 100)  // Where

    class SomeClass(val id: Int, val content: String, val sn: Int)

    val arrSomeClass = Array(
      new SomeClass(32, "thirty-two", 1),
      new SomeClass(4, "four", 2),
      new SomeClass(-3, "minus-three", 3),
      new SomeClass(7, "seven", 4),
      new SomeClass(101, "one-hundred-and-one", 5),
      new SomeClass(21, "twenty-one", 6)
    )

    println("map")
    val arrContent: Seq[Int] = arrSomeClass.filter(a => 10 < a.id && a.id < 100).map(_.sn)  // Where & Select
    for (s <- arrContent)
      println(s)

    println("flatMap")
    val arrFlatMap: Seq[Char] = arrSomeClass.filter(a => 10 < a.id && a.id < 100).flatMap(_.content)  // Where & Select
    //val arrFlatMap = arrSomeClass.filter(a => 10 < a.id && a.id < 100).flatMap(_.sn.toString)  // Where & Select
    for (s <- arrFlatMap)
      println(s)

    println("End of Collections")
  }
}

