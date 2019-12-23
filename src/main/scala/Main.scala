import scala.util.Success

object Main extends App {

  val logFiles = "/ad_data.1.log" :: "/ad_data.2.log" :: Nil
  val out      = System.getProperty("user.dir") + "/ouput"
  val writer   = ReportWriter.writeReport(out)(_)

  val result = for {
    x <- LogReader.streamDataFromFiles(logFiles)
    y <- Success(Tabulations.tabulateByUser(x))
    c = Tabulations.tabulateByFrequency(y)
    _ <- writer(c)
  } yield ()

}
