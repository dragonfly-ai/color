package ai.dragonfly.color

import scala.collection.immutable
import scala.scalajs.js.annotation.{JSExport, JSExportAll}

@JSExportAll
object ColorPalette {

  /**
    * apply method to create a ColorPalette object from a color frequency histogram.
    * @param hist a map with color objects as Keys and Integer values as frequencies.
    * @return an instance of the ColorPalette class.
    * @example {{{ val cp = ColorPalette(histogram) }}}
    */
  def apply(hist: Map[Color, Int]): ColorPalette = {
    // Normalize
    val frequencyTotal: Double = totalFrequencies(hist)
    var treeSet = new immutable.TreeSet[ColorFrequency]()

    // Sort
    for ((c, f) <- hist) treeSet = treeSet + ColorFrequency(c, f / frequencyTotal)

    // reverse order
    val colorFrequencies: Array[ColorFrequency] = new Array[ColorFrequency](treeSet.size)
    var i = treeSet.size - 1
    for ( cf <- treeSet ) {
      colorFrequencies(i) = cf
      i = i - 1
    }

    new ColorPalette(colorFrequencies)
  }

  private def totalFrequencies(hist: Map[Color, Int]): Double = {
    var total = 0.0
    for (f <- hist.values) total = total + f
    total
  }

}

/**
  * ColorPalette organizes a sorted array of color frequencies, ranked from highest to lowest.
  * @param colorFrequencies an array of ColorFrequency objects.
  */

@JSExportAll
class ColorPalette(val colorFrequencies: Array[ColorFrequency]) {

  /**
    * Search the palette for the closest match to a query color.
    *
    * @tparam T encodes the color space to compute the color distance in.
    * @param color a color object to query with, e.g. L*a*b*, XYZ, or RGB.
    * @return an instance of the ColorFrequency class which is nearest match to the query color.
    */

  def nearestMatch[T <: Color](color: T): ColorFrequency = {
    var distSquared = Double.MaxValue
    var colorMatch: ColorFrequency = null
    for ( m <- colorFrequencies ) {
      val dist = color.distanceSquaredTo(m.color)
      if (distSquared > dist) {
        distSquared = dist
        colorMatch = m
      }
    }
    colorMatch
  }

  override def toString(): String = {
    val sb = new StringBuilder(colorFrequencies.length * 30)
    sb.append("ColorPalette(")
    for (cf <- colorFrequencies) {
      sb.append( cf ).append(" ")
    }
    sb.append(")")
    sb.toString()
  }
}

/**
  * ColorFrequency couples a color object to a frequency.
  *
  * @constructor Create a new RGBA object from an Int.
  * @param color a color object.
  * @param frequency a frequency normalized between 0 and 1.  This encodes the prominence of a color relative to others in a ColorPalette.
  * @return an instance of the ColorFrequency class.
  */

@JSExportAll
case class ColorFrequency(color: Color, frequency: Double) extends Ordered[ColorFrequency] {

  /**
    * Compares this color's frequency to that color's frequency.
    * @param cf a map with color objects as Keys and Integer values as frequencies.
    * @return Returns x where: x < 0 when this < that, x == 0 when this == that, x > 0 when this > that
   */
  def compare(cf: ColorFrequency) = {
    if (frequency < cf.frequency ) -1
    else if (frequency > cf.frequency) 1
    else 0
  }
}
