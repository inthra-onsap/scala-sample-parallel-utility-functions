package ionsap.util

object ParSeq {
  def parMap() = ???

  def parFoldLeft() = ???

  def parFoldRight() = ???

  def parReduceLeft() = ???

  def parReduceRight() = ???

  def parScanLeft[A](input: Array[A], a0: A, func: (A, A) => A, output: Array[A]): Unit = ???

  def parScanRight[A](input: Array[A], a0: A, func: (A, A) => A, output: Array[A]): Unit = ???
}

