package aggregation.system

import java.nio.file.Path
import scala.io.Source
import scala.util.{ Failure, Success, Try }

trait DataSource[A] {
  def streamAllData: Try[Stream[A]]
}

object DataSource {

  def fileDataSource[A: Parser](files: List[Path]): DataSource[A] = new DataSource[A] {

    private def sequence[T](l: List[Option[T]]): Try[List[T]] =
      if (l.exists(_.isEmpty)) Failure(new Exception("error loading file(s)")) else Success(l.flatten)

    private def streamFiles(files: List[Path]): Try[Stream[String]] =
      for {
        fs    <- sequence(files.map(f => Try(Source.fromFile(f.toUri)).toOption))
        lines <- Success(fs.flatMap(_.getLines))
      } yield lines.toStream

    private def isValidLine: String => Boolean = s => !s.startsWith("#")

    override def streamAllData: Try[Stream[A]] =
      streamFiles(files)
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
