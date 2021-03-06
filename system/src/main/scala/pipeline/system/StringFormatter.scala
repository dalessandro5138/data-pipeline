package pipeline.system

import pipeline.system.StringFormatter.Delimiter.{ Comma, Fixed, NoDel, Tab }

trait StringFormatter[A] {
  def format(a: A)(delimiter: StringFormatter.Delimiter): String = {

    val f = (a => tokenize(a)) andThen (ts => format(ts)(delimiter))

    f(a)
  }

  def format(a: Seq[String])(delimiter: StringFormatter.Delimiter): String = delimiter match {
    case Comma    => a.mkString("", ",", "\n")
    case Tab      => a.mkString("", "\t", "\n")
    case Fixed(i) => a.map(_.padTo(i, ' ')).mkString("", "", "\n")
    case NoDel    => a.mkString("")
  }

  def tokenize(a: A): Seq[String]
}

object StringFormatter {
  sealed trait Delimiter

  object Delimiter {
    case object Comma            extends Delimiter
    case object Tab              extends Delimiter
    case class Fixed(chars: Int) extends Delimiter
    case object NoDel            extends Delimiter
  }

}
