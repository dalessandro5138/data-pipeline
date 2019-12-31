package aggregation

import java.util.UUID
import aggregation.system.{ Parser, StringFormatter }
import scala.util.Try

package object adviews {

  implicit val userAdViewParser: Parser[UserAdView] = new Parser[UserAdView] {
    import UserAdView.{ AD_ID, GUID, SITE_ID }

    override def parseRow(s: String): Option[UserAdView] =
      parseFromTable[UserAdView](DataHeaders.DATA_HEADERS) { table =>
        for {
          guid   <- table.get(GUID)
          uuid   <- Try(UUID.fromString(guid)).toOption
          adId   <- table.get(AD_ID)
          siteId <- table.get(SITE_ID)
        } yield UserAdView(uuid, adId, siteId)
      }(s)
  }

  implicit val reportRowStringFormatter: StringFormatter[ReportRow] = new StringFormatter[ReportRow] {
    override def format(pad: Int, a: ReportRow): String =
      seqStringFormatter.format(
        pad,
        a.adId ::
          a.siteId ::
          a.frequency.toString ::
          a.totalUsers.toString :: Nil
      )
  }

  implicit val seqStringFormatter: StringFormatter[Seq[String]] = new StringFormatter[Seq[String]] {
    override def format(pad: Int, a: Seq[String]): String = a.map(_.padTo(pad, ' ')).mkString("", "", "\n")

  }

  implicit val reportOrdering: Ordering[ReportRow] = Ordering.by[ReportRow, BigInt](r => -r.frequency)

}
