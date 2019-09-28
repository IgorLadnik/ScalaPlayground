package Main

import Extension.Helpers._
import Placement._
import Q.Qsample
import IdioticSocketsWithThreadPool.{NetworkClient, NetworkServer}
import ReadWriteFile.FileHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

import DV.UrlParser.{StrExt, UrlInfoHolder}

object Main {
  def main(args: Array[String]) {

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

    // Function ==================================================================
    val fn = (n: Int) => n * 10
    val va = fn(3)

    // apply / unapply ===========================================================
    val qsample = Qsample("aa", "bb")
    val Qsample(name) = "pp@qq"

    println("qsample: " + qsample)
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

    // Idiotic Sockets with Thread Pool ===========================================
    val port = 11511
    val numOfThreadInPool = 3
    new NetworkServer(port, numOfThreadInPool).run
    new NetworkClient("localhost", port, numOfThreadInPool).run

    // Parse Nested URL - Use Precompiled JAR ====================================
    parseUrl("url_input.txt")
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
}

