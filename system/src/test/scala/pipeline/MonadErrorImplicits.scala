package pipeline

import cats.{ Id, MonadError }

object MonadErrorImplicits {
  implicit val idMonadError: MonadError[Id, Throwable] = new MonadError[Id, Throwable] {
    override def raiseError[A](e: Throwable): Id[A]                          = throw e
    override def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] = fa
    override def pure[A](x: A): Id[A]                                        = x
    override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B]              = fa match { case a => f(a) }
    override def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] = f(a) match {
      case Left(a)  => tailRecM(a)(f)
      case Right(b) => b
    }
  }
}
