package pipeline.system

import org.specs2.Specification
import org.specs2.matcher.Matchers
import org.specs2.specification.core.SpecStructure
import scala.util.{ Success, Try }

class PipelineSpec extends Specification with Matchers {
  override def is: SpecStructure =
    s2"""
      Test pipeline $testPipeline
      """

  private def testPipeline = {
    val ds = new DataSource[String] {
      override def streamAllData: Try[Stream[String]] =
        Success(Stream("1", "2", "3"))
    }

    val result =
      Pipeline.build[String, Int](ds, _.map(Integer.parseInt), rows => Success(rows.size))

    result should beSuccessfulTry.withValue(3)
  }
}
