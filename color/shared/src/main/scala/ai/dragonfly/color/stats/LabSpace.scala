package ai.dragonfly.color.stats

import ai.dragonfly.color.{LAB, RGBA, SlowSlimLab}
import ai.dragonfly.math.stats.mesh.Tetrahedron
import ai.dragonfly.math.vector.Vector3

import scala.collection.mutable
import scala.scalajs.js.annotation.JSExportTopLevel

/**
 * Created by clifton on 1/6/17.
 */


@JSExportTopLevel("ai.dragonfly.color.stats.LabSpace")
object LabSpace {

  println("buildTetrahedrons")

  // Center all points about the origin.
  // Build a tetrahedron from each triangle.
  // Build a histogram out of all of the tetrahedra.

  val pointMap = new mutable.HashMap[Int, Vector3]

  def addPoint(c: LAB): Unit = {
    pointMap.put(c.argb, Vector3(c.L, c.a, c.b))
  }

  val v0 = Vector3(57.490340794327246, 6.988950335808067, 3.642550587704847)
  var size = 0
  var totalVolume = 0.0

  val cumulative = new Array[Double](768108)
  val tetrahedrons = new Array[Tetrahedron](768108)

  def addTetrahedrons(p0: Vector3, p1: Vector3, p2: Vector3, p3: Vector3): Unit = {
    var t = Tetrahedron(p0, p1, p2, v0)
    totalVolume = totalVolume + Math.abs(t.volume)
    cumulative(size) = totalVolume
    tetrahedrons(size) = t
    size = size + 1

    t = Tetrahedron(p3, p2, p1, v0)
    totalVolume = totalVolume + Math.abs(t.volume)
    cumulative(size) = totalVolume
    tetrahedrons(size) = t
    size = size + 1
  }

  // generate points
  for (c <- 1 until 255; i <- 1 until 255) {
    addPoint( RGBA(255, c,   i) )
    addPoint( RGBA(  0, c,   i) )

    addPoint( RGBA(c, 255,   i) )
    addPoint( RGBA(c,   0,   i) )

    addPoint( RGBA(c,   i, 255) )
    addPoint( RGBA(c,   i,   0) )
  }


  // connect faces
  for (c <- 1 until 255; i <- 1 until 255) {
    // make two tetrahedra for each point.
    for {
      p0 <- pointMap.get(RGBA(255, c, i))
      p1 <- pointMap.get(RGBA(255, c + 1, i))
      p2 <- pointMap.get(RGBA(255, c, i + 1))
      p3 <- pointMap.get(RGBA(255, c + 1, i + 1))
    } yield addTetrahedrons(p0, p1, p2, p3)

    for {
      p0 <- pointMap.get(RGBA(0, c, i))
      p1 <- pointMap.get(RGBA(0, c + 1, i))
      p2 <- pointMap.get(RGBA(0, c, i + 1))
      p3 <- pointMap.get(RGBA(0, c + 1, i + 1))
    } yield addTetrahedrons(p0, p1, p2, p3)

    for {
      p0 <- pointMap.get(RGBA(c, 255, i))
      p1 <- pointMap.get(RGBA(c + 1, 255, i))
      p2 <- pointMap.get(RGBA(c, 255, i + 1))
      p3 <- pointMap.get(RGBA(c + 1, 255, i + 1))
    } yield addTetrahedrons(p0, p1, p2, p3)

    for {
      p0 <- pointMap.get(RGBA(c, 0, i))
      p1 <- pointMap.get(RGBA(c + 1, 0, i))
      p2 <- pointMap.get(RGBA(c, 0, i + 1))
      p3 <- pointMap.get(RGBA(c + 1, 0, i + 1))
    } yield addTetrahedrons(p0, p1, p2, p3)

    for {
      p0 <- pointMap.get(RGBA(c, i, 255))
      p1 <- pointMap.get(RGBA(c + 1, i, 255))
      p2 <- pointMap.get(RGBA(c, i + 1, 255))
      p3 <- pointMap.get(RGBA(c + 1, i + 1, 255))
    } yield addTetrahedrons(p0, p1, p2, p3)

    for {
      p0 <- pointMap.get(RGBA(c, i, 0))
      p1 <- pointMap.get(RGBA(c + 1, i, 0))
      p2 <- pointMap.get(RGBA(c, i + 1, 0))
      p3 <- pointMap.get(RGBA(c + 1, i + 1, 0))
    } yield addTetrahedrons(p0, p1, p2, p3)
  }

  println(s"tetrahedrons complete: $size")
  println(s"total volume: $totalVolume")

  private def getNearestIndex(target: Double): Int = {
    var left = 0
    var right = cumulative.length-1
    while (left <= right) {
      val mid = (left + right)/2
      if (cumulative(mid) < target) left = mid + 1
      else if (cumulative(mid) > target) right = mid - 1
      else return mid
    }
    right
  }

  def apply(): LAB = {
    val x = Math.random() * totalVolume
    val i = getNearestIndex(x)

    val v = tetrahedrons(i).draw()
    SlowSlimLab(v.x.toFloat, v.y.toFloat, v.z.toFloat)

  }

}