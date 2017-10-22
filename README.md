# Custom parallel utility functions

 

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