package pipeline

import java.util.UUID
import pipeline.system.{ Parser, StringFormatter }
import scala.util.Try

package object adviews {

  val REPORT_HEADERS = Seq("Ad Id", "Site Id", "Frequency", "Total users that viewed ad this frequently")

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

  implicit val adViewFreqStringFormatter: StringFormatter[(AdViewFrequency, BigInt)] =
    new StringFormatter[(AdViewFrequency, BigInt)] {
      override def tokenize(
        a: (AdViewFrequency, BigInt)
      ): Seq[String] = {
        val (avf, i) = a
        Seq(avf.adId.toString, avf.siteId.toString, avf.frequency.toString, i.toString)
      }
    }

  implicit val reportOrdering: Ordering[(AdViewFrequency, BigInt)] =
    Ordering.by[(AdViewFrequency, BigInt), BigInt] { case (freq, i) => -freq.frequency }

}
