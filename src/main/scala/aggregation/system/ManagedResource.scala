package aggregation.system

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
