package pipeline.adviews

import java.util.UUID
import org.specs2.matcher.Matchers
import org.specs2.mutable.Specification
import org.specs2.specification.core.SpecStructure

class AdViewsSpec extends Specification with Matchers {
  override def is: SpecStructure =
    s2"""  
      Tabulate by user then frequency $testTabulations
      """

  private val data: Stream[UserAdView] = {
    val id1 = UUID.randomUUID
    val id2 = UUID.randomUUID

    (UserAdView(id1, "ad1", "site1") ::
      UserAdView(id1, "ad1", "site1") ::
      UserAdView(id1, "ad1", "site1") ::
      UserAdView(id1, "ad1", "site1") ::
      UserAdView(id1, "ad1", "site1") ::
      UserAdView(id1, "ad1", "site1") ::
      UserAdView(id2, "ad1", "site1") ::
      UserAdView(id2, "ad1", "site1") ::
      UserAdView(id2, "ad1", "site1") ::
      UserAdView(id2, "ad1", "site1") ::
      UserAdView(id2, "ad1", "site1") ::
      UserAdView(id2, "ad1", "site1") ::
      UserAdView(id2, "ad1", "site2") ::
      UserAdView(id2, "ad1", "site2") ::
      UserAdView(id2, "ad1", "site2") :: Nil).toStream
  }

  private def testTabulations =
    Main.tabByUserThenFrequency(data) should contain((AdViewFrequency("ad1", "site1", 6), BigInt(2)))

}
