trait StringFormatter[A] {
  def format(pad: Int, a: A): String
}

object StringFormatter {
  val reportRowStringFormatter: StringFormatter[ReportRow] = new StringFormatter[ReportRow] {
    override def format(pad: Int, a: ReportRow): String =
      seqStringFormatter.format(
        pad,
        a.adId ::
          a.siteId ::
          a.frequency.toString ::
          a.totalUsers.toString :: Nil
      )
  }

  val seqStringFormatter: StringFormatter[Seq[String]] = new StringFormatter[Seq[String]] {
    override def format(pad: Int, a: Seq[String]): String = a.map(_.padTo(pad, ' ')).mkString("", "", "\n")

  }
}
