package ai.dragonfly.color.experiments

import ai.dragonfly.color.{Color, ColorPalette, LAB}

import scala.collection.immutable

object TestColors extends App {

  var hist: immutable.Map[Color, Int] = new immutable.HashMap[Color, Int]

  for (i <- 0 until 50) hist = hist + ((Color.random(), (Math.random() * 1000).toInt))

  val cp = ColorPalette(hist)

  println(cp)

  for (i <- 0 until 10) {
    val lab: LAB = Color.random()
    val m: LAB = cp.nearestMatch[LAB](lab).color
    println(lab + " -> " + m + " " + lab.distanceTo(m))
  }

}
