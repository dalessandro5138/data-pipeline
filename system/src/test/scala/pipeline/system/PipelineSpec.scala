package pipeline.system

import org.specs2.Specification
import org.specs2.matcher.Matchers
import org.specs2.specification.core.SpecStructure
import cats.Id
import pipeline.MonadErrorImplicits._

class PipelineSpec extends Specification with Matchers {
  override def is: SpecStructure =
    s2"""
      Test pipeline $testPipeline
      """

  private def testPipeline = {
    val ds = new DataSource[Id, String] {
      override def streamAllData: Id[Stream[String]] =
        Stream("1", "2", "3")
    }

    val result =
      Pipeline.build[Id, String, Int](ds, _.map(Integer.parseInt), rows => rows.size)

    result shouldEqual 3
  }
}
