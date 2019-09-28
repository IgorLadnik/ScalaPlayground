package ReadWriteFile

import java.io.{BufferedWriter, File, FileWriter}
import scala.io.Source
import DV.UrlParser.UrlInfoHolder

object FileHelper {
  def readUrlFromFile(inputFilePath: String): String = {
    for (line <- Source.fromFile(inputFilePath).getLines)
      return line

    return null;
  }

  def outputStringToFile(s: String = "", bw: BufferedWriter = null) {
    if (bw != null)
      bw.write(s"${s}\n")
    else
      println(s)
  }

  def outputToFile(urlInfoHolder: UrlInfoHolder) {
    val outputFileRath = "NestedUrlParser_Scala_output.txt"

    val file = new File(outputFileRath)
    val bw = new BufferedWriter(new FileWriter(file))

    outputStringToFile(s"URL = ${urlInfoHolder.Url}\n", bw)

    var i = 0
    for (urlInfo <- urlInfoHolder.LstUrlInfo) {
      outputStringToFile(s"*** ${i}\nUrl: ${urlInfo.Url}\nHost: ${urlInfo.Host}\nDepth = ${urlInfo.Depth}\nParameters:", bw)

      for (param <- urlInfo.Params)
        outputStringToFile(s"  ${param._1} = ${param._2}", bw)

      outputStringToFile("", bw)
      i += 1
    }

    bw.close()
  }
}
