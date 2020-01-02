package aggregation.adviews

import java.io.{ BufferedWriter, File, FileWriter }
import java.nio.file.{ Path, Paths }
import aggregation.system.StringFormatter.Delimiter.Fixed
import aggregation.system.{ DataSource, ManagedResource, Pipeline, Report, Tabulations }
import scala.util.Try

object Main extends App {

  lazy val root: Path           = Paths.get(System.getProperty("user.dir") + "/")
  lazy val data: Path           = root.resolve(Paths.get("data/"))
  lazy val logFiles: List[Path] = ("ad_data.1.log" :: "ad_data.2.log" :: Nil).map(data.resolve)
  lazy val out: File            = root.resolve("output.report").toFile

  def managedBufWriter(outFile: File): ManagedResource[BufferedWriter, Int] =
    ManagedResource[BufferedWriter, Int](Try {
      if (outFile.exists) outFile.delete();
      new BufferedWriter(new FileWriter(outFile))
    }, bw => Try { bw.flush(); bw.close() })

  def tabByUserThenFrequency = {

    def tabUserAdView =
      Tabulations.mapToIndexThenCountEach[UserAdView, UserAdView](identity)(Some(5))(_)

    def tabAdViewFrequency =
      Tabulations.mapToIndexThenCountEach[(UserAdView, BigInt), AdViewFrequency] {
        case (uav, i) => AdViewFrequency(uav.adId, uav.siteId, i)
      }(None)(_)

    tabUserAdView andThen tabAdViewFrequency
  }

  def run = managedBufWriter(out).use { bw =>
    val ds       = DataSource.fileDataSource(logFiles)(userAdViewParser)
    val reporter = Report.makeTableReport[(AdViewFrequency, BigInt)](bw)(REPORT_HEADERS)(Fixed(40))
    Pipeline.build(ds, tabByUserThenFrequency, reporter)
  }

  run.fold(e => throw e, n => println(s"Wrote $n rows to file: $out"))
}
