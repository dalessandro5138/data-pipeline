package pipeline.adviews

import java.nio.file.Path
import pipeline.system.StringFormatter.Delimiter.Fixed
import pipeline.system.{ DataSource, ManagedResource, Pipeline, Report, Tabulations }
import scala.util.{ Success, Try }
import pipeline.system
object Main extends App {

  def tabByUserThenFrequency = {

    def tabUserAdView =
      Tabulations.mapToIndexThenCountEach[UserAdView, UserAdView](identity)(Some(5))(_)

    def tabAdViewFrequency =
      Tabulations.mapToIndexThenCountEach[(UserAdView, BigInt), AdViewFrequency] {
        case (uav, i) => AdViewFrequency(uav.adId, uav.siteId, i)
      }(None)(_)

    tabUserAdView andThen tabAdViewFrequency
  }

  val source: List[Path] => Try[Stream[String]] =
    ManagedResource.bufferedSource(_).use(s => Success(s.toStream.flatMap(_.getLines().toStream)))

  def run =
    for {
      c  <- system.Config.loadConfig("config")(AppConfig.fromProps)
      ds = DataSource.fileDataSource(c.sourceFiles)(source)(userAdViewParser)
      n <- ManagedResource.bufferedWriter(c.reportDestination).use { bw =>
            val reporter = Report.makeTableReport[(AdViewFrequency, BigInt)](bw)(REPORT_HEADERS)(Fixed(40))
            Pipeline.build(ds, tabByUserThenFrequency, reporter)
          }
    } yield n

  run.fold(e => throw e, n => println(s"Wrote $n rows to destination file"))
}
