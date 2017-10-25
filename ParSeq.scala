package ionsap.util

import common._

object ParSeq {
  val threshold = 8

  private def seqMap[A, R: Manifest](in: Array[A], from: Int, until: Int, transformFunc: A => R): Array[R] = {
    if ((until - from) > 0)
      transformFunc(in(from)) +: seqMap[A, R](in, from + 1, until, transformFunc)
    else
      Array()
  }

  def parMap[A, R: Manifest](in: Array[A], from: Int, until: Int, transformFunc: A => R): Array[R] = {
    if ((until - from) < threshold) {
      seqMap(in, from, until, transformFunc)
    } else {
      val mid = (from + until) / 2
      val (left, right) = parallel(parMap(in, from, mid, transformFunc), parMap(in, mid, until, transformFunc))
      left ++ right
    }
  }

  private def seqProcessTransformFunc[A](in: Array[A], from: Int, until: Int)(transformFunc: (A, A) => A): A = {
    if ((until - from) == 1) in(from)
    else transformFunc(in(from), seqProcessTransformFunc(in, from + 1, until)(transformFunc))
  }

  private def fold[A](in: Array[A], from: Int, until: Int)(transformFunc: (A, A) => A): A = {
    if ((until - from) < threshold) {
      seqProcessTransformFunc(in, from, until)(transformFunc)
    } else {
      var mid = (from + until) / 2
      val (left, right) = parallel(fold(in, from, mid)(transformFunc), fold(in, mid, until)(transformFunc))
      transformFunc(left, right)
    }
  }

  def parFold[A](in: Array[A], initVal: A)(transformFunc: (A, A) => A): A = {
    if (in.length == 0) initVal
    else transformFunc(initVal, fold(in, 0, in.length)(transformFunc))
  }

  def parReduce[A](in: Array[A])(transformFunc: (A, A) => A): A = {
    require(in.length > 1, "Required Array conntains more than 1 element")
    fold(in, 0, in.length)(transformFunc)
  }

  def parScan[A](input: Array[A], a0: A, func: (A, A) => A, output: Array[A]): Unit = ???


}
