package Extension

trait Ordering[T] {
  def compare(x: T, y: T): Boolean
}

object MathInt {
  def min[Int](a: Int, b: Int)(implicit o: Ordering[Int]): Int = {
    if (o.compare(a, b)) a else b
  }
}
