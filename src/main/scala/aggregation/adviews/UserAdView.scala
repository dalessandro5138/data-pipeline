package aggregation.adviews

import java.util.UUID

case class UserAdView(guid: UUID, adId: String, siteId: String)

object UserAdView {
  val GUID    = "GUID"
  val AD_ID   = "Ad_ID"
  val SITE_ID = "Site_ID"
}
