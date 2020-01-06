package pipeline.system

import cats.implicits._
import cats.MonadError

object Pipeline {
  def build[F[_], A, B](
    source: DataSource[F, A],
    stream: Stream[A] => Stream[B],
    sink: Report[F, B]
  )(implicit ME: MonadError[F, Throwable]): F[Int] =
    for {
      in  <- source.streamAllData
      out = stream(in)
      n   <- sink.generateReport(out)
    } yield n
}
