object Tabulations {

  def tabulateByUser(s: Stream[UserAdView]): Map[UserAdView, BigInt] =
    s.foldLeft(Map.empty[UserAdView, BigInt])((s, av) => s + (av -> (s.getOrElse(av, BigInt(0)) + BigInt(1))))

  def tabulateByFrequency(userTabs: Map[UserAdView, BigInt]): Seq[ReportRow] =
    userTabs.filter {
      case (_, v) => v > 5
    }.groupBy {
      case (UserAdView(_, adId, siteId), views) => (adId, siteId, views)
    }.mapValues(_.size)
      .map {
        case ((aId, sId, f), u) => ReportRow(aId, sId, f, u)
      }
      .toList
      .sortBy(rr => -rr.frequency)

  val tablulateByUserThenFrequency: Stream[UserAdView] => Seq[ReportRow] = tabulateByFrequency _ compose tabulateByUser

}
