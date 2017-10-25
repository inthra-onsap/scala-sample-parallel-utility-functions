# Scala sample parallel utility functions

A sample code implementation for merge sort, map, reduce, fold and scan in parallel scheme.
 

### Usage Examples


##### ParOrdering Object

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


##### ParSeq Object
```sbtshell
scala> import ionsap.util._

scala> val in = Array[Int](1, 5, 78, 9, 0, 7, 3, 1, 5, 7, 82, 9, 5)
in: Array[Int] = Array(1, 5, 78, 9, 0, 7, 3, 1)

/**
* Parallel Map utility
*/
scala> ParSeq.parMap(in, 0, in.length, (x: Int) => {x + 0.5})
res1: Array[Double] = Array(1.5, 5.5, 78.5, 9.5, 0.5, 7.5, 3.5, 1.5, 5.5, 7.5, 82.5, 9.5, 5.5)

/**
* Parallel Fold utility
*/
scala> ParSeq.parFold(in, 0)((acc, x) => acc + x)
res2:Int = 212


/**
* Parallel Reduce utility
*/
scala> ParSeq.parReduce(in, 0)((acc, x) => acc + x)
res3:Int = 212


```
