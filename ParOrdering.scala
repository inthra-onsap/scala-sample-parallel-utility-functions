package ionsap.util

import common._
import scala.util._
import scala.math._

object ParOrdering {
  val maxDepth: Int = 5
  val rand = new Random

  private def swap[T](in: Array[T], i: Int, j: Int): Unit = {
    val tmp = in(i)
    in(i) = in(j)
    in(j) = tmp
  }

  private def quickSort[T](in: Array[T], from: Int, until: Int, ord: => Ordering[T]): Unit = {
    if ((until - from) > 1) {
      var pivot = from + (rand.nextInt(Integer.MAX_VALUE) % (until - from))
      var i = from
      var j = until - 1

      swap(in, i, pivot)
      pivot = from

      while (i <= j) {
        if (ord.lt(in(i), in(pivot))) {
          swap(in, pivot, i)
          i += 1
          pivot += 1
        } else if (ord.gt(in(i), in(pivot))) {
          swap(in, i, j)
          j -= 1
        } else {
          i += 1
        }
      }
      quickSort(in, from, pivot, ord)
      quickSort(in, i, until, ord)
    }
  }

  private def parCopy[T](in: Array[T], out: Array[T], from: Int, until: Int, depth: Int): Unit = {
    if ((until - from) <= 1 || depth == maxDepth) {
      Array.copy(in, from, out, from, until - from)
    } else {
      val mid = (until + from) / 2
      parallel(
        parCopy(in, out, from, mid, depth + 1),
        parCopy(in, out, mid, until, depth + 1)
      )
    }
  }

  private def merge[T](in: Array[T], out: Array[T], from: Int, mid: Int, until: Int, ord: => Ordering[T]): Unit = {
    var left = from
    val leftBound = mid
    var right = mid
    val rightBound = until
    var runner = from

    while (left < leftBound && right < rightBound) {
      if (ord.lteq(in(left), in(right))) {
        out(runner) = in(left)
        left += 1
      } else {
        out(runner) = in(right)
        right += 1
      }
      runner += 1
    }

    while (left < leftBound) {
      out(runner) = in(left)
      left += 1
      runner += 1
    }

    while (right < rightBound) {
      out(runner) = in(right)
      right += 1
      runner += 1
    }
  }

  private def mergeSort[T](in: Array[T], out: Array[T], from: Int, until: Int, depth: Int, ord: => Ordering[T]): Unit = {
    if (depth == maxDepth) {
      quickSort(in, from, until, ord)
    } else {
      val mid = (until + from) / 2
      parallel(mergeSort(in, out, from, mid, depth + 1, ord), mergeSort(in, out, mid, until, depth + 1, ord))

      val flip = (maxDepth - depth) % 2 == 0
      merge(if (flip) out else in, if (flip) in else out, from, mid, until, ord)
    }
  }

  def parMergeSort[T: Ordering](in: Array[T]): Unit = {
    val out = in.clone()
    mergeSort(in, out, 0, in.length, 0, implicitly[Ordering[T]])
    if (maxDepth % 2 != 0) {
      parCopy(out, in, 0, in.length, 0)
    }
  }
}
