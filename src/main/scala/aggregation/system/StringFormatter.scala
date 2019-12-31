package aggregation.system

trait StringFormatter[A] {
  def format(pad: Int, a: A): String
}
