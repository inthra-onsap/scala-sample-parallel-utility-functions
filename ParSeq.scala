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

  private def seqProcessCombineFunc[A](in: Array[A], from: Int, until: Int)(combineFunc: (A, A) => A): A = {
    if ((until - from) == 1) in(from)
    else combineFunc(in(from), seqProcessCombineFunc(in, from + 1, until)(combineFunc))
  }

  private def fold[A](in: Array[A], from: Int, until: Int)(combineFunc: (A, A) => A): A = {
    if ((until - from) < threshold) {
      seqProcessCombineFunc(in, from, until)(combineFunc)
    } else {
      var mid = (from + until) / 2
      val (left, right) = parallel(fold(in, from, mid)(combineFunc), fold(in, mid, until)(combineFunc))
      combineFunc(left, right)
    }
  }

  def parFold[A](in: Array[A], initVal: A)(combineFunc: (A, A) => A): A = {
    if (in.length == 0) initVal
    else combineFunc(initVal, fold(in, 0, in.length)(combineFunc))
  }

  def parReduce[A](in: Array[A])(combineFunc: (A, A) => A): A = {
    require(in.length > 1, "Required Array conntains more than 1 element")
    fold(in, 0, in.length)(combineFunc)
  }


  sealed abstract class Tree[A]

  case class Leaf[A](value: A) extends Tree[A]

  case class Node[A](left: Tree[A], right: Tree[A], value: A) extends Tree[A]

  sealed abstract class ResTree[A]

  case class ResLeaf[A](value: A) extends ResTree[A]

  case class ResNode[A](left: ResTree[A], right: ResTree[A]) extends ResTree[A]

  private def buildTreeFromArray[A](in: Array[A], from: Int, until: Int)(combineFunc: (A, A) => A): Tree[A] = {
    if ((until - from) == 1) {
      Leaf(in(from))
    } else {
      val mid = (until + from) / 2
      val (left, right) = parallel(buildTreeFromArray(in, from, mid)(combineFunc), buildTreeFromArray(in, mid, until)(combineFunc))

      (left, right) match {
        case (Leaf(v1), Leaf(v2)) => Node(left, right, combineFunc(v1, v2))
        case (Leaf(v1), Node(_, _, v2)) => Node(left, right, combineFunc(v1, v2))
        case (Node(_, _, v1), Leaf(v2)) => Node(left, right, combineFunc(v1, v2))
        case (Node(_, _, v1), Node(_, _, v2)) => Node(left, right, combineFunc(v1, v2))
      }
    }
  }

  private def buildLeftistTree[A](tree: Tree[A], a0: A)(combineFunc: (A, A) => A): ResTree[A] = tree match {
    case Leaf(v) => ResLeaf(combineFunc(a0, v))
    case Node(left, right, v) => {
      val lValue = left match {
        case Node(_, _, v) => v
        case Leaf(v) => v
      }
      val (l, r) = parallel(
        buildLeftistTree(left, a0)(combineFunc),
        buildLeftistTree(right, combineFunc(a0, lValue))(combineFunc)
      )
      ResNode(l, r)
    }
  }

  private def buildRightistTree[A](tree: Tree[A], a0: A)(combineFunc: (A, A) => A): ResTree[A] = tree match {
    case Leaf(v) => ResLeaf(combineFunc(v, a0))
    case Node(left, right, v) => {
      val rValue = right match {
        case Node(_, _, v) => v
        case Leaf(v) => v
      }
      val (l, r) = parallel(
        buildRightistTree(left, combineFunc(a0, rValue))(combineFunc),
        buildRightistTree(right, a0)(combineFunc)
      )
      ResNode(l, r)
    }
  }

  private def buildArrayFromTree[A: Manifest](tree: ResTree[A]): Array[A] = tree match {
    case ResLeaf(v) => Array(v)
    case ResNode(l, r) => {
      val (leftArr, rightArr) = parallel(buildArrayFromTree(l), buildArrayFromTree(r))
      leftArr ++ rightArr
    }
  }

  def parScanLeft[A: Manifest](in: Array[A], a0: A)(combineFunc: (A, A) => A): Array[A] = {
    if (in.length == 0)
      Array(a0)
    else
      a0 +: buildArrayFromTree(
        buildLeftistTree(
          buildTreeFromArray(in, 0, in.length)(combineFunc),
          a0
        )(combineFunc)
      )
  }

  def parScanRight[A: Manifest](in: Array[A], a0: A)(combineFunc: (A, A) => A): Array[A] = {
    if (in.length == 0)
      Array(a0)
    else
      buildArrayFromTree(
        buildRightistTree(
          buildTreeFromArray(in, 0, in.length)(combineFunc),
          a0
        )(combineFunc)
      ) :+ a0
  }
}
