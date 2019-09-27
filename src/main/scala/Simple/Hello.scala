package ScalaPlayground

import Extension._
import Extension.Helpers._
import Placement._
import Q.Qsample
import Threading._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Hello {
  def main(args: Array[String]) {
    // Implicit
    5 seconds
    val t: Unit = 5 times println("HI")

    val sendBox = new SendBox()
    sendBox ! new MessageToSendBox("MSG")
    sendBox ! "a string"
    sendBox ! 139.5

    // apply / unapply ------
    val qsample = Qsample("aa", "bb")
    val Qsample(name) = "pp@qq"

    println("qsample: " + qsample)
    println("name: " + name)
    // ----------------------

    // Operators overloading
    val ptSum = new Point(2, 3) + new Point(3, 7)
    println(s"ptSum = ${ptSum}")
    println(ptSum ? "prefix")

    // Option
    val opStr: Option[String] = None //Some("Ga!") //None
    println(s"Print the option: ${opStr.getOrElse("option placeholder")}")

    // Companion Object was created implicitly for case class Location
    val loc = Location(10, 20, 15) // without "new" since this is case class

    // Move to a new location
    loc.move(10, 10, 5)

    // Function, Companion Object
    gps(x = 1, y = 1, p = /*new*/ Location(10, 20, 15))

    val zpt = new Location(10, 2, 7)
    val pt = getPoint(1, 1, zpt)

    // Array
    val list = Array(1.9, 2.1, 3.78)
    for (x <- list)
      println(x)

    // Futures test =================================================================================
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

    // Threading ===================================================================================
    val network = new NetworkService(11511, 5)
    network.run()
  }

  // Call function by name
  def gps(x: Int, y: Int, p: => Point) {
    p.move(x, y)
    println("x = " + x)
    println("y = " + y)
  }

  // "val" in stead of "def"
  val getPoint = (x: Int, y: Int, p: Point) => {
    println(s"POINT${p}")
    new Point(x, y)
  }
}
