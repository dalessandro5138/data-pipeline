package pipeline.system

import java.nio.file.Path
import scala.util.{ Success, Try }

trait DataSource[A] {
  def streamAllData: Try[Stream[A]]
}

object DataSource {

  def fileDataSource[A: Parser](files: List[Path])(source: List[Path] => Try[Stream[String]]): DataSource[A] =
    new DataSource[A] {

      private def isValidLine: String => Boolean = s => !s.startsWith("#")

      override def streamAllData: Try[Stream[A]] =
        source(files)
          .flatMap(
            s =>
              Success(
                s.filter(isValidLine)
                  .map(implicitly[Parser[A]].parseRow)
                  .collect { case Some(a) => a }
              )
          )
    }
}
