package Placement

class Point(val xc: Int, val yc: Int) {
  var x: Int = xc
  var y: Int = yc

  def move(dx: Int, dy: Int) {
    x = x + dx
    y = y + dy
    println("Point x location : " + x)
    println("Point y location : " + y)
  }

  override def toString(): String = {
    s"(${x}, ${y})"
  }

  def +(pt: Point) : Point = {
    new Point(this.x + pt.x, this.y + pt.y)
  }

  val ? = (str: String) => s"$str: ${this}"

  //def unary_~ = Math.sqrt(real * real + imag * imag)

  //def unary_2 =
}




