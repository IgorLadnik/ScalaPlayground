package Extension

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration

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

  implicit class SendMessage(b: SendBox) {
    def !(m: MessageToSendBox) = b.send(m)
  }

  implicit def convertString2MessageToSendBox[A](a: A): MessageToSendBox = new MessageToSendBox(s"${a}")

}

class MessageToSendBox(content: String) {
  override def toString(): String = {
    s"This is message content: ${content}"
  }
}

class SendBox() {
  def send(m: MessageToSendBox) = {
    println(s"${m}")
  }
}