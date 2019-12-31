package aggregation.adviews

import java.util.UUID
import aggregation.system.DataSource
import org.specs2.matcher.Matchers
import org.specs2.mutable.Specification
import org.specs2.specification.core.SpecStructure
import scala.util.{ Success, Try }

class AdViewsSpec extends Specification with Matchers {
  override def is: SpecStructure =
    s2"""  
      Tabulate by user then frequency and sort     $testTabulations
      Test program with mock datasource and writer $testProgram
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
    Main.tabulateAndReport(data) should contain(ReportRow("ad1", "site1", 6, 2))

  private def testProgram = {
    val ds = new DataSource[UserAdView] {
      override def streamAllData: Try[Stream[UserAdView]] =
        Success(data)
    }

    Main.program(ds, rows => Success(rows.size)) should beSuccessfulTry.withValue(1)
  }
}
