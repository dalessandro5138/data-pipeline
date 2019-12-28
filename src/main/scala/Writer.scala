import java.io.BufferedWriter
import scala.util.Try

trait Writer {
  def write[A](a: A)(implicit F: StringFormatter[A]): Try[Unit]
}

object Writer {
  def fileWriter(bw: BufferedWriter): Writer = new Writer {

    override def write[A](a: A)(implicit F: StringFormatter[A]): Try[Unit] =
      Try {
        bw.write(F.format(40, a))
      }
  }
}
