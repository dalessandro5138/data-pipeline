package aggregation.system

import java.io.Writer
import aggregation.system.StringFormatter.Delimiter
import org.specs2.Specification
import org.specs2.specification.core.SpecStructure

class ReportSpec extends Specification {
  override def is: SpecStructure =
    s2"""
      test $test
      """

  object TestWriter extends Writer {
    override def write(cbuf: Array[Char], off: Int, len: Int): Unit = ()
    override def flush(): Unit                                      = ()
    override def close(): Unit                                      = ()
  }

  val fmt = new StringFormatter[(String, String)] {
    override def tokenize(a: (String, String)): Seq[String] = Seq(a._1, a._2)
  }
  private def test = {

    val report =
      Report.makeTableReport[(String, String)](TestWriter)(Seq("Field1", "Field2"))(Delimiter.Comma)(
        Ordering.by[(String, String), String] { case (a, _) => a },
        fmt
      )

    report.generateReport(Stream(("aloha", "honua"), ("hello", "world"))) should beASuccessfulTry(2)
  }

}
