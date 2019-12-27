import java.io.{ BufferedWriter, File, FileWriter }
import scala.util.{ Failure, Try }

object ReportWriter {

  private val HEADERS        = Seq("Ad Id", "Site Id", "Frequency", "Total users that viewed ad this frequently")
  private val PADDED_HEADERS = HEADERS.map(padRight).mkString("", "", "\n")

  private def makeBufferedWriter(outFile: String) =
    Try {
      val f = new File(outFile)
      if (f.exists) f.delete();
      new BufferedWriter(new FileWriter(f))
    }

  private def padRight(s: String) = s.padTo(40, ' ')

  private def formatRow(row: ReportRow): String =
    (row.adId ::
      row.siteId ::
      row.frequency.toString ::
      row.totalUsers.toString :: Nil).map(padRight).mkString("", "", "\n")

  private def writeRow(bw: BufferedWriter)(row: String): Unit = bw.write(row)

  def genReportM(filename: String)(rows: Stream[ReportRow]): Try[Int] =
    for {
      bw       <- makeBufferedWriter(filename)
      writeRoe = writeRow(bw) _
      writeFn  = writeRoe compose formatRow
      n <- Try {
            writeRoe(PADDED_HEADERS)
            rows.foldLeft(0)((s, r) => { writeFn(r); s + 1 })
          }.recoverWith {
            case e =>
              Try { bw.flush(); bw.close() } flatMap { _ =>
                Failure(e)
              }
          }
      _ <- Try { bw.flush(); bw.close() }
    } yield n

}
