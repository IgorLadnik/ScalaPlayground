package IdioticSocketsWithThreadPool

import java.net.{ServerSocket, Socket}
import java.util.concurrent.{ExecutorService, Executors}
import java.net.InetSocketAddress

class NetworkServer(port: Int, poolSize: Int) {
  val serverSocket = new ServerSocket(port)
  val pool: ExecutorService = Executors.newFixedThreadPool(poolSize)

  def run() {
    try {
      // This will block until a connection comes in.
      pool.execute(new HandlerServer(serverSocket))
    } finally {
      pool.shutdown()
    }
  }
}

class NetworkClient(hostName: String, port: Int, poolSize: Int) extends Runnable {
  val clientSocket = new Socket()
  val toAddr = new InetSocketAddress(hostName, port)
  val pool: ExecutorService = Executors.newFixedThreadPool(poolSize)

  def run() {
    try {
      clientSocket.connect(toAddr)
      // This will block until a message comes in.
      pool.execute(new HandlerClient(clientSocket))
    } finally {
      pool.shutdown()
    }
  }
}

class HandlerServer(serverSocket: ServerSocket) extends Runnable {
  def run() {
    val strToBeSent = Thread.currentThread.getName
    def bts = strToBeSent.getBytes

    val socket = serverSocket.accept()
    println(s"To be send by ServivceSocket: $strToBeSent")
    socket.getOutputStream.write(bts)
    socket.getOutputStream.close()
  }
}

class HandlerClient(socket: Socket) extends Runnable {
  def run() {
    val bts = socket.getInputStream.readAllBytes
    val message = bts.map(_.toChar).mkString
    socket.getInputStream.close
    println(s"Received by ClientSocket: $message")
  }
}
