import java.io.{ BufferedWriter, File, FileWriter }
import java.nio.file.Path
import java.nio.file.Paths
import scala.util.{ Success, Try }

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

  def run(dataSource: DataSource[UserAdView], writerM: BufferedWriter => Writer) =
    for {
      _       <- Success(())
      rowsIn  <- dataSource.streamAllData
      rowsOut = Tabulations.tablulateByUserThenFrequency(rowsIn).toStream
      nRows <- managedBufWriter(out).use { bw =>
                val writer = writerM(bw)
                Report.generateReport(writer)(ReportRow.HEADERS, rowsOut)(
                  StringFormatter.reportRowStringFormatter
                )
              }
    } yield nRows

  run(DataSource.fileDataSource(logFiles), Writer.fileWriter)
    .fold(e => throw e, n => println(s"Wrote $n rows to file: $out"))
}
