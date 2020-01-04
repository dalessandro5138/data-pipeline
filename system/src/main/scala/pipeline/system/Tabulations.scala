package pipeline.system

object Tabulations {

  def mapToIndexThenCountEach[A, B](toIndex: A => B)(filterNum: Option[Int])(s: Stream[A]): Stream[(B, BigInt)] = {

    val f = s
      .map(toIndex)
      .foldLeft(Map.empty[B, BigInt])((s, av) => s + (av -> (s.getOrElse(av, BigInt(0)) + BigInt(1))))

    filterNum
      .fold(f)(
        n =>
          f.filter {
            case (_, v) => v > n
          }
      )
      .toStream
  }

}
