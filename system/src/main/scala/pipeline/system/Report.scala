package pipeline.system

import java.io.Writer
import pipeline.system.StringFormatter.Delimiter
import scala.util.Try

trait Report[A] {
  def generateReport(as: Iterable[A]): Try[Int]
}

object Report {

  def makeTableReport[A: Ordering: StringFormatter](
    W: Writer
  )(headers: Seq[String])(delimiter: Delimiter): Report[A] = new Report[A] {

    private def makeDelimitedLine: A => String        = implicitly[StringFormatter[A]].format(_)(delimiter)
    private def makeHeaderLine: Seq[String] => String = implicitly[StringFormatter[A]].format(_)(delimiter)

    override def generateReport(as: Iterable[A]): Try[Int] =
      for {
        n <- Try {
              W.write(makeHeaderLine(headers))
              as.foldLeft(0) { (s, l) =>
                { W.write(makeDelimitedLine(l)); s + 1 }
              }
            }
      } yield n
  }

}
