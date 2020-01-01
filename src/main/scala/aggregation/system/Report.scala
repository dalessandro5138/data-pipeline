package aggregation.system

import java.io.BufferedWriter
import aggregation.system.StringFormatter.Delimiter
import scala.util.Try

trait Report[A] {
  def generateReport(as: Iterable[A]): Try[Int]
}

object Report {

  def makeTableReport[A: Ordering: StringFormatter](
    bw: BufferedWriter
  )(headers: Seq[String])(delimiter: Delimiter): Report[A] = new Report[A] {

    private def makeDelimitedLine: A => String        = implicitly[StringFormatter[A]].format(_)(delimiter)
    private def makeHeaderLine: Seq[String] => String = StringFormatter.seqStringFormatter.format(_)(delimiter)

    override def generateReport(as: Iterable[A]): Try[Int] =
      for {
        n <- Try {
              bw.write(makeHeaderLine(headers))
              as.foldLeft(0) { (s, l) =>
                { bw.write(makeDelimitedLine(l)); s + 1 }
              }
            }
      } yield n
  }

}
