package ai.dragonfly.color

import scala.collection.mutable.HashMap
import ai.dragonfly.color.Color

/**
 * Created by clifton on 1/6/17.
 */

object LabSampleSpace {
  lazy val sampleSpace = {
    val distinctColors = new HashMap[Int, Int]()
    for ( r <- 0 to 255; g <- 0 to 255; b <- 0 to 255 ) {

      val lab0: LAB = RGBA(r, g, b)
      val lab1: LAB = lab0.discretize()

      val argb = lab1.argb
      distinctColors.get(argb) match {
        case Some(c: Int) => distinctColors.put(argb, c + 1)
        case _ => distinctColors.put(argb, 1)
      }
    }

    val labSampleSpace = new Array[Int](distinctColors.size)
    var i:Int = 0
    for ( (k, v) <- distinctColors ) {
      labSampleSpace(i) = k
      i = i + 1
    }
    labSampleSpace
  }
  def randomArgb(): Int = sampleSpace((Math.random() * sampleSpace.length).toInt)

  def randomLab(): LAB = Color.toLab(sampleSpace((Math.random() * sampleSpace.length).toInt))
}