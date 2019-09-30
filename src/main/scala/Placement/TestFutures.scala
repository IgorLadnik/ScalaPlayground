package Placement

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class TestFutures {
  def firstLengthyFunc(question: String): Future[Int] = Future {
    // assume some long running database operation
    println("1st lengthy operation started...")
    Thread.sleep(3000)
    println("1st lengthy operation completed")
    11
  }

  def secondLengthyFunc(num: Int): Future[Int] = Future {
    // assume some long running database operation
    println("2nd lengthy operation started...")
    Thread.sleep(3000)
    println("2nd lengthy operation completed")
    num * 10
  }
}
