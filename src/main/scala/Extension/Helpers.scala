package Extension

import java.util.concurrent.TimeUnit

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import Messaging.MessageBox

object Helpers {

  implicit class DurationInt(private val n: Int) {
    def seconds = {
      Duration(n.toLong, TimeUnit.SECONDS)
    }
  }

  implicit class IntWithTimes(x: Int) {
    def times[A](f: => A) = {
      def loop(current: Int): Unit = {
        if(current > 0) {
          f
          loop(current - 1)
        }
      }

      loop(x)
    }
  }

  implicit class SendMessage(b: MessageBox) {
    import Messaging.Message

    def ![A](m: Message[A]) = b.send(m)
    def ?[A](m: Message[A]): Future[Message[A]] = b.sendWithResponse(m)
  }

  implicit def convertA2Message[A](a: A): Messaging.Message[A] = new Messaging.Message[A](a)
  implicit def convertMessage2A[A](m: Messaging.Message[A]): A = m.content

  implicit object IntOrdering extends Ordering[Int] {
    def compare(x: Int, y: Int): Boolean = x - y <= 0
  }
}
