case class ReportRow(adId: String, siteId: String, frequency: BigInt, totalUsers: BigInt)

object ReportRow {
  val HEADERS = Seq("Ad Id", "Site Id", "Frequency", "Total users that viewed ad this frequently")
}
