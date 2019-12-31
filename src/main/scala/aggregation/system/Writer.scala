package aggregation.system

import java.io.BufferedWriter
import scala.util.Try

trait Writer {
  def write(a: String): Try[Unit]
}

object Writer {
  def fileWriter(bw: BufferedWriter): Writer = new Writer {
    override def write(a: String): Try[Unit] = Try {
      bw.write(a)
    }
  }
}
