import scala.util.Try

object Report {

  def generateReport[A, B](writer: Writer, HF: StringFormatter[A], RF: StringFormatter[B])(headers: A, rows: Stream[B])(
    ): Try[Int] =
    for {
      n <- Try {
            writer.write(HF.format(40, headers))
            rows.foldLeft(0)((s, r) => { writer.write(RF.format(40, r)); s + 1 })
          }
    } yield n
}
