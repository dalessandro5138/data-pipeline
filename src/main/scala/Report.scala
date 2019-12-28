import scala.util.Try

object Report {

  def generateReport[A](writer: Writer)(headers: Seq[String], rows: Stream[A])(
    implicit rowFormat: StringFormatter[A]
  ): Try[Int] =
    for {
      n <- Try {
            writer.write(headers)(StringFormatter.seqStringFormatter)
            rows.foldLeft(0)((s, r) => { writer.write(r); s + 1 })
          }
    } yield n
}
