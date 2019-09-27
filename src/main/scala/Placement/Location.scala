package Placement

case class Location(override val xc: Int, override val yc: Int, val zc :Int)
  extends Point(xc, yc){
  var z: Int = zc

  def move(dx: Int, dy: Int, dz: Int) {
    x = x + dx
    y = y + dy
    z = z + dz
    println("Point x location : " + x)
    println("Point y location : " + y)
    println("Point z location : " + z)
  }
}
