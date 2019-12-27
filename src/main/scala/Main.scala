import scala.util.Try

object Main extends App {

  val root: String                       = System.getProperty("user.dir") + "/"
  val data: String                       = root + "data/"
  val logFiles: List[String]             = ("ad_data.1.log" :: "ad_data.2.log" :: Nil).map(data + _)
  val out: String                        = root + "output.report"
  val writer: Seq[ReportRow] => Try[Int] = ReportWriter.writeReport(out)(_)

  val result = for {
    rowsIn  <- LogReader.streamDataFromFiles(logFiles)
    rowsOut = Tabulations.tablulateByUserThenFrequency(rowsIn)
    nRows   <- writer(rowsOut)
  } yield nRows

  result.fold(e => throw e, n => println(s"Wrote $n rows to file: $out"))
}
