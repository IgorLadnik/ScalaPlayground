package Messaging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Message[A](val content: A) {
  override def toString(): String = {
    s"This is message content: ${content}"
  }
}

class MessageBox() {
  def send[A](m: Message[A]) = {
    println(s"${m}")
  }

  def sendWithResponse[A](m: Message[A]): Future[Message[A]] = Future {
    println(s"${m}")
    println(m.content.getClass().toString())
    //....
    Thread.sleep(3000)
    //....
    new Message[A](m.content)
  }
}