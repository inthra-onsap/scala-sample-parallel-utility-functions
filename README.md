# Scala sample parallel utility functions

A sample code implementation for merge sort, map, reduce, fold and scan in parallel scheme.
 

## Usage Examples

- [ParOrdering Object](#parordering-object)
- [ParSeq Object](#parseq-object)


### [ParOrdering Object](#parordering-object)

```sbtshell
scala> import ionsap.util._

/**
* Ordering by Default comparator
*/
scala> val data: Array[Int] = Array[Int](5, 8, 2, 9, 0, 17, 8, 8, 9, 0, 2, 89, 9, 0, 0, -2, 900)
data: Array[Int] = Array(5, 8, 2, 9, 0, 17, 8, 8, 9, 0, 2, 89, 9, 0, 0, -2, 900)

scala> ParOrdering.parMergeSort(data)

scala> data
res1: Array[Int] = Array(-2, 0, 0, 0, 0, 2, 2, 5, 8, 8, 8, 9, 9, 9, 17, 89, 900)

/**
* Ordering by custom ordering object
*/
scala> object CustomIntOrdering extends Ordering[Int] {
     |   def compare(a: Int, b: Int): Int = (a compare b)
     | }
defined object CustomIntOrdering

scala> val data: Array[Int] = Array[Int](5, 8, 2, 9, 0, 17, 8, 8, 9, 0, 2, 89, 9, 0, 0, -2, 900)
data: Array[Int] = Array(5, 8, 2, 9, 0, 17, 8, 8, 9, 0, 2, 89, 9, 0, 0, -2, 900)

scala> ParOrdering.parMergeSort(data)(CustomIntOrdering)

scala> data
res2: Array[Int] = Array(-2, 0, 0, 0, 0, 2, 2, 5, 8, 8, 8, 9, 9, 9, 17, 89, 900)
```


### [ParSeq Object](#parseq-object)
```sbtshell
scala> import ionsap.util._

scala> val in = Array[Int](1, 5, 78, 9, 0, 7, 3, 1, 5, 7, 82, 9, 5)
in: Array[Int] = Array(1, 5, 78, 9, 0, 7, 3, 1)

/**
* Parallel parMap utility
*/
scala> ParSeq.parMap(in, 0, in.length, (x: Int) => {x + 0.5})
res1: Array[Double] = Array(1.5, 5.5, 78.5, 9.5, 0.5, 7.5, 3.5, 1.5, 5.5, 7.5, 82.5, 9.5, 5.5)

/**
* Parallel parFold utility
*/
scala> val initVal:Int = 0
initVal: Int = 0

scala> ParSeq.parFold(in, initVal)((acc, x) => acc + x)
res2: Int = 212

/**
* Parallel parReduce utility
*/
scala> ParSeq.parReduce(in)((acc, x) => acc + x)
res3: Int = 212

/**
* Parallel parScanLeft & parScanRight utility
*/
scala> val initVal:Int = 5
initVal: Int = 5

scala> ParSeq.parScanLeft(in, initVal)((acc, x) => acc + x)
res4: Array[Int] = Array(5, 6, 11, 89, 98, 98, 105, 108, 109, 114, 121, 203, 212, 217)

scala> ParSeq.parScanRight(in, initVal)((x, acc) => x + acc)
res5: Array[Int] = Array(217, 216, 211, 133, 124, 124, 117, 114, 113, 108, 101, 19, 10, 5)
```
