package pipeline.system

import java.nio.file.{ Path, Paths }

import org.specs2.Specification
import org.specs2.matcher.Matchers
import org.specs2.specification.core.SpecStructure
import cats.Id
import pipeline.MonadErrorImplicits._

class FileDataSourceSpec extends Specification with Matchers {
  override def is: SpecStructure =
    s2"""
      A File DataSource should load from a file into some Model A $testSize
      """

  case class TestData(id: String, fieldA: String, fieldB: String, fieldC: String, fieldD: String)

  val HEADERS = Seq("Id", "FieldA", "FieldB", "FieldC", "FieldD")

  implicit val testParser = new Parser[TestData] {
    override def parseRow(s: String): Option[TestData] =
      parseFromTable[TestData](HEADERS) { table =>
        for {
          id <- table.get("Id")
          a  <- table.get("FieldA")
          b  <- table.get("FieldB")
          c  <- table.get("FieldC")
          d  <- table.get("FieldD")
        } yield TestData(id, a, b, c, d)
      }(s)
  }

  val testData                                     = Stream.fill(25)("id1\tA1\tB1\tC1\tD1\n")
  val testSource: List[Path] => Id[Stream[String]] = _ => testData
  val testFile: Path                               = Paths.get("test.file")
  val fileDataSource: DataSource[Id, TestData]     = DataSource.fileDataSource[Id, TestData](testFile :: Nil)(testSource)

  val maybeData = fileDataSource.streamAllData.size

  val testSize = maybeData shouldEqual 25
}
