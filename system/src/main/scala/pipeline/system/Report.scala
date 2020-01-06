package pipeline.system

import java.io.Writer

import cats.MonadError
import pipeline.system.StringFormatter.Delimiter

import cats.implicits._

trait Report[F[_], A] {
  def generateReport(as: Iterable[A]): F[Int]
}

object Report {

  def makeTableReport[F[_], A: Ordering: StringFormatter](
    W: Writer
  )(headers: Seq[String])(delimiter: Delimiter)(implicit ME: MonadError[F, Throwable]): Report[F, A] =
    new Report[F, A] {

      private def makeDelimitedLine: A => String        = implicitly[StringFormatter[A]].format(_)(delimiter)
      private def makeHeaderLine: Seq[String] => String = implicitly[StringFormatter[A]].format(_)(delimiter)

      override def generateReport(as: Iterable[A]): F[Int] =
        for {
          n <- ME.catchNonFatal {
                W.write(makeHeaderLine(headers))
                as.foldLeft(0) { (s, l) =>
                  { W.write(makeDelimitedLine(l)); s + 1 }
                }
              }
        } yield n
    }

}
