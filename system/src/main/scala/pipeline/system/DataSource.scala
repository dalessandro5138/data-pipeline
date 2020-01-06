package pipeline.system

import java.nio.file.Path

import cats.MonadError
import cats.implicits._

trait DataSource[F[_], A] {
  def streamAllData: F[Stream[A]]
}

object DataSource {

  def fileDataSource[F[_], A](
    files: List[Path]
  )(source: List[Path] => F[Stream[String]])(implicit P: Parser[A], ME: MonadError[F, Throwable]): DataSource[F, A] =
    new DataSource[F, A] {

      private def isValidLine: String => Boolean = s => !s.startsWith("#")

      override def streamAllData: F[Stream[A]] =
        source(files)
          .flatMap(
            s =>
              ME.point(
                s.filter(isValidLine)
                  .map(P.parseRow)
                  .collect { case Some(a) => a }
              )
          )
    }
}
