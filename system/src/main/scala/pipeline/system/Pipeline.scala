package pipeline.system

import cats.implicits._
import cats.Monad

object Pipeline {
  def build[F[_], A, B](
    source: DataSource[F, A],
    stream: Stream[A] => Stream[B],
    sink: Report[F, B]
  )(implicit M: Monad[F]): F[Int] =
    for {
      in  <- source.streamAllData
      out = stream(in)
      n   <- sink.generateReport(out)
    } yield n
}
