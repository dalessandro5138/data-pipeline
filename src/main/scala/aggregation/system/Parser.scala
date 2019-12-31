package aggregation.system

trait Parser[A] {
  def parseRow(s: String): Option[A]
  def parseFromTable[T](headers: Seq[String])(toA: Map[String, String] => Option[T])(s: String): Option[T] = {
    val table = (headers zip s.split("\t")).toMap
    toA(table)
  }
}
