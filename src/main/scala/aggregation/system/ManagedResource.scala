package aggregation.system

import java.io.{ BufferedWriter, File, FileWriter }
import scala.util.{ Failure, Try }

case class ManagedResource[A, B](acquire: Try[A], release: A => Try[Unit]) {
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
  def bufferedWriter(outFile: File): ManagedResource[BufferedWriter, Int] =
    ManagedResource[BufferedWriter, Int](Try {
      if (outFile.exists) outFile.delete();
      new BufferedWriter(new FileWriter(outFile))
    }, bw => Try { bw.flush(); bw.close() })
}
