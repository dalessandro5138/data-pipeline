object Main extends App {

  val logFiles = "/ad_data.1.log" :: "/ad_data.2.log" :: Nil
  val out      = System.getProperty("user.dir") + "/ouput.report"
  val writer   = ReportWriter.writeReport(out)(_)

  val result = for {
    rowsIn  <- LogReader.streamDataFromFiles(logFiles)
    rowsOut = Tabulations.tablulateByUserThenFrequency(rowsIn)
    nRows   <- writer(rowsOut)
  } yield nRows

  result.fold(e => throw e, n => println(s"Wrote $n rows to file: $out"))
}
