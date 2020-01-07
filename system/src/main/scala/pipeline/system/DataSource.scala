package pipeline.system

import java.nio.file.Path
import cats.Monad
import cats.implicits._

trait DataSource[F[_], A] {
  def streamAllData: F[Stream[A]]
}

object DataSource {

  def fileDataSource[F[_], A](
    files: List[Path]
  )(source: List[Path] => F[Stream[String]])(implicit P: Parser[A], M: Monad[F]): DataSource[F, A] =
    new DataSource[F, A] {

      private def isValidLine: String => Boolean = s => !s.startsWith("#")

      override def streamAllData: F[Stream[A]] =
        source(files)
          .flatMap(
            s =>
              M.point(
                s.filter(isValidLine)
                  .map(P.parseRow)
                  .collect { case Some(a) => a }
              )
          )
    }
}
