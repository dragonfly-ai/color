package ai.dragonfly.color.stats

import ai.dragonfly.color.{LAB, RGBA, SlowSlimLab}
import ai.dragonfly.math.stats.UnorderedSampleableObjectDistribution
import ai.dragonfly.math.stats.mesh.Tetrahedron
import ai.dragonfly.math.vector.Vector3

import scala.collection.mutable
import scala.scalajs.js.annotation.JSExportTopLevel

/**
 * Created by clifton on 1/6/17.
 */

@JSExportTopLevel("ai.dragonfly.color.stats.LabSpace")
object LabSpace {

  // Center all points about the origin.
  // Build a tetrahedron from each triangle.
  // Build a histogram out of all of the tetrahedra.

  private val d = 1

  private val pointMap = new mutable.HashMap[Int, (Int,Vector3)]
  private var index = 1

  private def addPoint(c: LAB): Unit = {

    if (pointMap.contains(c.argb)) {
      //println(s"Skipped: ${RGBA(c)}")
    } else {
      pointMap.put(c.argb, (index, Vector3(c.L, c.a, c.b)))
      index = index + 1
    }
  }

  private val v0 = Vector3(57.490340794327246, 6.988950335808067, 3.642550587704847)

  private val tetrahedrons = new UnorderedSampleableObjectDistribution[Tetrahedron]

  private def addTetrahedrons(p0: (Int, Vector3), p1: (Int, Vector3), p2: (Int, Vector3), p3: (Int, Vector3)): Unit = {

    tetrahedrons(Tetrahedron(p0._2, p1._2, p2._2, v0))
    tetrahedrons(Tetrahedron(p3._2, p2._2, p1._2, v0))

  }

  // generate points
  for (c <- 0 to 255 by d; i <- 0 to 255 by d) {
    addPoint( RGBA(255, c,   i) )
    addPoint( RGBA(  0, c,   i) )

    addPoint( RGBA(c, 255,   i) )
    addPoint( RGBA(c,   0,   i) )

    addPoint( RGBA(c,   i, 255) )
    addPoint( RGBA(c,   i,   0) )
  }

  // connect faces

  for (c <- 0 to 255 by d; i <- 0 to 255 by d) {
    // make two triangles for each point.
    for {
      p0 <- pointMap.get(RGBA(255, c, i))
      p1 <- pointMap.get(RGBA(255, c + d, i))
      p2 <- pointMap.get(RGBA(255, c, i + d))
      p3 <- pointMap.get(RGBA(255, c + d, i + d))
    } yield addTetrahedrons(p0, p1, p2, p3)

    for {
      p0 <- pointMap.get(RGBA(0, c, i))
      p1 <- pointMap.get(RGBA(0, c + d, i))
      p2 <- pointMap.get(RGBA(0, c, i + d))
      p3 <- pointMap.get(RGBA(0, c + d, i + d))
    } yield addTetrahedrons(p0, p1, p2, p3)

    for {
      p0 <- pointMap.get(RGBA(c, 255, i))
      p1 <- pointMap.get(RGBA(c + d, 255, i))
      p2 <- pointMap.get(RGBA(c, 255, i + d))
      p3 <- pointMap.get(RGBA(c + d, 255, i + d))
    } yield addTetrahedrons(p0, p1, p2, p3)

    for {
      p0 <- pointMap.get(RGBA(c, 0, i))
      p1 <- pointMap.get(RGBA(c + d, 0, i))
      p2 <- pointMap.get(RGBA(c, 0, i + d))
      p3 <- pointMap.get(RGBA(c + d, 0, i + d))
    } yield addTetrahedrons(p0, p1, p2, p3)

    for {
      p0 <- pointMap.get(RGBA(c, i, 255))
      p1 <- pointMap.get(RGBA(c + d, i, 255))
      p2 <- pointMap.get(RGBA(c, i + d, 255))
      p3 <- pointMap.get(RGBA(c + d, i + d, 255))
    } yield addTetrahedrons(p0, p1, p2, p3)

    for {
      p0 <- pointMap.get(RGBA(c, i, 0))
      p1 <- pointMap.get(RGBA(c + d, i, 0))
      p2 <- pointMap.get(RGBA(c, i + d, 0))
      p3 <- pointMap.get(RGBA(c + d, i + d, 0))
    } yield addTetrahedrons(p0, p1, p2, p3)
  }

  def apply(): LAB = {
    val v = tetrahedrons.apply()
    SlowSlimLab(v.x.toFloat, v.y.toFloat, v.z.toFloat)
  }

}
