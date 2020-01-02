package aggregation.system

import aggregation.system.StringFormatter.Delimiter.{ Comma, Fixed, Tab }

trait StringFormatter[A] {
  def format(a: A)(delimiter: StringFormatter.Delimiter): String = {

    val f = (a => tokenize(a)) andThen (ts => format(ts)(delimiter))

    f(a)
  }

  def format(a: Seq[String])(delimiter: StringFormatter.Delimiter): String = delimiter match {
    case Comma    => a.mkString("", ",", "\n")
    case Tab      => a.mkString("", "\t", "\n")
    case Fixed(i) => a.map(_.padTo(i, ' ')).mkString("", "", "\n")
  }

  def tokenize(a: A): Seq[String]
}

object StringFormatter {
  sealed trait Delimiter

  object Delimiter {
    case object Comma            extends Delimiter
    case object Tab              extends Delimiter
    case class Fixed(chars: Int) extends Delimiter
    case object None             extends Delimiter
  }

//  val seqStringFormatter: StringFormatter[Seq[String]] = new StringFormatter[Seq[String]] {
//    override def format(a: Seq[String])(
//      delimiter: Delimiter
//    ): String = delimiter match {
//      case Comma    => a.mkString("", ",", "\n")
//      case Tab      => a.mkString("", "\t", "\n")
//      case Fixed(i) => a.map(_.padTo(i, ' ')).mkString("", "", "\n")
//    }
//    override def tokenize(a: Seq[String]): Seq[String] = a
//  }
}
