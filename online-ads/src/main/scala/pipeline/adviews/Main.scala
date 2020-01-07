package pipeline.adviews

import java.nio.file.Path
import cats.effect.Bracket
import cats.implicits._
import pipeline.system.StringFormatter.Delimiter.Fixed
import pipeline.system.{ DataSource, ManagedResource, Pipeline, Report, Tabulations }
import pipeline.system
import zio.{ DefaultRuntime, Task }

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

  def source[F[_]](
    paths: List[Path]
  )(implicit B: Bracket[F, Throwable]): F[Stream[String]] =
    ManagedResource.bufferedSource[F](paths).use(s => B.point(s.toStream.flatMap(_.getLines().toStream)))

  def run[F[_]](implicit B: Bracket[F, Throwable]) =
    for {
      c  <- system.Config.loadConfig[F, AppConfig]("config")(AppConfig.fromProps[F])
      ds = DataSource.fileDataSource[F, UserAdView](c.sourceFiles)(source[F])
      n <- ManagedResource.bufferedWriter(c.reportDestination)(B).use { bw =>
            val reporter = Report.makeTableReport[F, (AdViewFrequency, BigInt)](bw)(REPORT_HEADERS)(Fixed(40))
            Pipeline.build[F, UserAdView, (AdViewFrequency, BigInt)](ds, tabByUserThenFrequency, reporter)
          }
    } yield n

  new DefaultRuntime {}.unsafeRun(run[Task].fold(e => throw e, n => println(s"Wrote $n rows to destination file")))
}
