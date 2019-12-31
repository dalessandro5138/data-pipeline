import java.util.UUID

case class UserAdView(guid: UUID, adId: String, siteId: String)

case class AdViewFrequency(adId: String, siteId: String, frequency: BigInt)
