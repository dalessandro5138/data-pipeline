package aggregation.adviews

import java.io.File
import aggregation.system.StringFormatter.Delimiter.Fixed
import aggregation.system.{ DataSource, ManagedResource, Pipeline, Report, Tabulations }
import scala.util.{ Success, Try }

object Main extends App {

  lazy val logFiles: List[File] = ???
  lazy val out: File            = ???

  def tabByUserThenFrequency = {

    def tabUserAdView =
      Tabulations.mapToIndexThenCountEach[UserAdView, UserAdView](identity)(Some(5))(_)

    def tabAdViewFrequency =
      Tabulations.mapToIndexThenCountEach[(UserAdView, BigInt), AdViewFrequency] {
        case (uav, i) => AdViewFrequency(uav.adId, uav.siteId, i)
      }(None)(_)

    tabUserAdView andThen tabAdViewFrequency
  }

  val source: List[File] => Try[Stream[String]] =
    ManagedResource.bufferedSource(_).use(s => Success(s.toStream.flatMap(_.getLines().toStream)))
  val ds = DataSource.fileDataSource(logFiles)(source)(userAdViewParser)

  def run = ManagedResource.bufferedWriter(out).use { bw =>
    val reporter = Report.makeTableReport[(AdViewFrequency, BigInt)](bw)(REPORT_HEADERS)(Fixed(40))
    Pipeline.build(ds, tabByUserThenFrequency, reporter)
  }

  run.fold(e => throw e, n => println(s"Wrote $n rows to file: $out"))
}
