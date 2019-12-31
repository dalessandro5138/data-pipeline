object Tabulations {

  def mapToIndexThenCountEach[A, B](toIndex: A => B)(filterNum: Option[Int])(s: Stream[A]) = {

    val f = s
      .map(toIndex)
      .foldLeft(Map.empty[B, BigInt])((s, av) => s + (av -> (s.getOrElse(av, BigInt(0)) + BigInt(1))))
      .toStream

    filterNum.fold(f)(
      n =>
        f.filter {
          case (_, v) => v > n
        }
    )
  }

}
