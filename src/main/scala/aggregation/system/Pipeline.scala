package aggregation.system

import scala.util.Try

object Pipeline {
  def build[A, B](source: DataSource[A], stream: Stream[A] => Stream[B], sink: Report[B]): Try[Int] =
    for {
      in  <- source.streamAllData
      out = stream(in)
      n   <- sink.generateReport(out)
    } yield n
}
