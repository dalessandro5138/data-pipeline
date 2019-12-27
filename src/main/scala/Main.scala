import scala.util.Try

object Main extends App {

  lazy val root: String                          = System.getProperty("user.dir") + "/"
  lazy val data: String                          = root + "data/"
  lazy val logFiles: List[String]                = ("ad_data.1.log" :: "ad_data.2.log" :: Nil).map(data + _)
  lazy val out: String                           = root + "output.report"
  lazy val writer: Stream[ReportRow] => Try[Int] = ReportWriter.genReportM(out)(_)

  def result =
    for {
      rowsIn  <- LogReader.streamDataFromFiles(logFiles)
      rowsOut = Tabulations.tablulateByUserThenFrequency(rowsIn).toStream
      nRows   <- writer(rowsOut)
    } yield nRows

  result.fold(e => throw e, n => println(s"Wrote $n rows to file: $out"))
}
