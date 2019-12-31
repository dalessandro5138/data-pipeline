import java.io.{ BufferedWriter, File, FileWriter }
import java.nio.file.Path
import java.nio.file.Paths
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

  def tabulateByUserThenFrequency = {

    def tabUserAdView = Tabulations.mapToIndexThenCountEach[UserAdView, UserAdView](identity)(Some(5))(_)

    def tabFrequency =
      Tabulations.mapToIndexThenCountEach[(UserAdView, BigInt), AdViewFrequency] {
        case (uav, i) => AdViewFrequency(uav.adId, uav.siteId, i)
      }(None)(_)

    tabUserAdView andThen tabFrequency
  }

  implicit def reportOrdering: Ordering[ReportRow] = Ordering.by[ReportRow, BigInt](r => -r.frequency)

  def makeReport =
    Report.makeReportFrom[(AdViewFrequency, BigInt), ReportRow] {
      case (avf, i) => ReportRow(avf.adId, avf.siteId, avf.frequency, i)
    }(_)

  def program(dataSource: DataSource[UserAdView], writer: Writer, headers: Seq[String]) =
    for {
      rowsIn  <- dataSource.streamAllData
      tabs    = tabulateByUserThenFrequency(rowsIn)
      rowsOut = makeReport(tabs).toStream
      nRows <- Report.writeReport(
                writer,
                StringFormatter.seqStringFormatter,
                StringFormatter.reportRowStringFormatter
              )(headers, rowsOut)(
                )
    } yield nRows

  def run = managedBufWriter(out).use { bw =>
    val ds     = DataSource.fileDataSource(logFiles)
    val writer = Writer.fileWriter(bw)
    program(ds, writer, ReportRow.HEADERS)
  }

  run.fold(e => throw e, n => println(s"Wrote $n rows to file: $out"))
}
