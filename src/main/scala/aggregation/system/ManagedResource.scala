package aggregation.system

import java.io.{ BufferedWriter, File, FileWriter }
import scala.io.{ BufferedSource, Codec, Source }
import scala.util.{ Failure, Success, Try }

case class ManagedResource[A, B] private (private val acquire: Try[A], private val release: A => Try[Unit]) {
  def use(f: A => Try[B]): Try[B] =
    for {
      a <- acquire
      b <- f(a).recoverWith {
            case e =>
              release(a) flatMap { _ =>
                Failure(e)
              }
          }
      _ <- release(a)
    } yield b
}

object ManagedResource {
  def bufferedWriter[A](outFile: File): ManagedResource[BufferedWriter, A] =
    ManagedResource[BufferedWriter, A](Try {
      if (outFile.exists) outFile.delete();
      new BufferedWriter(new FileWriter(outFile))
    }, bw => Try { bw.flush(); bw.close() })

  def bufferedSource[A](sourceFiles: List[File]): ManagedResource[List[BufferedSource], A] = {

    def sequence[T](l: List[Option[T]]): Try[List[T]] =
      if (l.exists(_.isEmpty)) Failure(new Exception("error loading file(s)")) else Success(l.flatten)

    val acquire = sequence(sourceFiles.map(f => Try(Source.fromFile(f))).map(_.toOption))

    ManagedResource[List[BufferedSource], A](acquire, bs => Try(bs.foreach(_.close())))
  }
}
