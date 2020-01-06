package pipeline.system

import java.io.Writer
import pipeline.MonadErrorImplicits._
import cats.{ Id, MonadError }
import pipeline.system.StringFormatter.Delimiter
import org.specs2.Specification
import org.specs2.specification.core.SpecStructure
import cats.implicits._

class ReportSpec extends Specification {
  override def is: SpecStructure =
    s2"""
      test $test
      """

  implicit val idMonadError: MonadError[Id, Throwable] = new MonadError[Id, Throwable] {
    override def raiseError[A](e: Throwable): Id[A]                          = throw e
    override def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] = fa
    override def pure[A](x: A): Id[A]                                        = x
    override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B]              = fa match { case a => f(a) }
    override def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B]       = f(a).right.get
  }

  object TestWriter extends Writer {
    override def write(cbuf: Array[Char], off: Int, len: Int): Unit = ()
    override def flush(): Unit                                      = ()
    override def close(): Unit                                      = ()
  }

  implicit val ord = Ordering.by[(String, String), String] { case (a, _) => a }

  implicit val fmt = new StringFormatter[(String, String)] {
    override def tokenize(a: (String, String)): Seq[String] = Seq(a._1, a._2)
  }
  private def test = {

    val report =
      Report.makeTableReport[Id, (String, String)](TestWriter)(Seq("Field1", "Field2"))(Delimiter.Comma)

    report.generateReport(Stream(("aloha", "honua"), ("hello", "world"))) shouldEqual 2
  }

}
