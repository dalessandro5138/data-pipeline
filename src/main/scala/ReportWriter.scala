import java.io.{ BufferedWriter, File, FileWriter }

import scala.util.{ Success, Try }

object ReportWriter {

  private val HEADERS        = Seq("Ad Id", "Site Id", "Frequency", "Total users that viewed ad this frequently")
  private val PADDED_HEADERS = HEADERS.map(padRight).mkString("", "", "\n")

  private def padRight(s: String) = s.padTo(40, ' ')

  private def formatRow(row: ReportRow): String =
    padRight(row.adId) +
      padRight(row.siteId) +
      padRight(row.frequency.toString) +
      padRight(row.totalUsers.toString) + "\n"

  def writeReport(filename: String)(rows: Seq[ReportRow]): Try[Unit] =
    for {
      file <- Try(new File(filename))
      _    <- if (file.exists) Try(file.delete()) else Success(true)
      n <- Try {
            val bw = new BufferedWriter(new FileWriter(file))
            bw.write(PADDED_HEADERS)
            for (r <- rows) {
              bw.write(formatRow(r))
            }
            bw.flush()
            bw.close()
            rows.size
          }
      _ <- Success(println(s"Wrote $n rows to file: $filename"))
    } yield n

}
