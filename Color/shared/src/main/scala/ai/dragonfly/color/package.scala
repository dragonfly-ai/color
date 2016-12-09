package ai.dragonfly.color

import scalajs.js

import scala.scalajs.js.annotation.{JSExport, JSExportAll}


/**
 * Created by clifton on 4/24/15.
 */

/***
 * TODO:
 * 1.  add sRGB color space
 * 2.  add alpha byte to color types that don't already have it?
 * 3.  export to javascript
 * 4.  test
 ***/

trait Color {
  @JSExport def rgba: Int
  @JSExport def red = rgba >> 16 & 0xff
  @JSExport def green = rgba >> 8 & 0xff
  @JSExport def blue = rgba & 0xff
  @JSExport def alpha = rgba >> 24 & 0xff

  override def hashCode(): Int = rgba

  override def equals(o: Any): Boolean = o match {
    case c: Color => this.rgba == c.rgba
    case _ => false
  }

  @JSExport def hex(): String = Integer.toHexString(rgba)

  @JSExport def html(): String = "#" + Integer.toHexString(rgba | 0xff000000).substring(2)

  @JSExport def svg(): String = {
    if (alpha < 255) "rgba(" + red + "," + green + "," + blue + "," + (alpha / 255f) + ")"
    else "rgb(" + red + "," + green + "," + blue + ")"
  }
}

object RGBA {
  def apply(red: Int, green: Int, blue: Int, alpha: Int = 255): RGBA = RGBA(red<<16|green<<8|blue|(alpha<<24))
}

@SerialVersionUID(1L)
case class RGBA(override val rgba: Int) extends Color {
  @JSExport def distanceTo (c: Color): Double = {
    var dR = red - c.red; dR = dR * dR
    var dG = green - c.green; dG = dG * dG
    var dB = blue - c.blue; dB = dB * dB
    Math.sqrt(dR + dG + dB)
  }
  override def toString() = "RGBA(" + red + "," + green + "," + blue + "," + alpha + ")"
}

@JSExportAll @SerialVersionUID(1L)
case class HSV(hue: Float, saturation: Float, value: Float) extends Color {
  override def rgba: Int = Color.toRgba(this)
  @JSExport override def toString() = "HSV(" + f"$hue%1.3f" + "," + f"$saturation%1.3f" + "," + f"$value%1.3f" + ")"
}

@JSExportAll @SerialVersionUID(1L)
case class HSL(hue: Float, saturation: Float, lightness: Float) extends Color {
  override def rgba: Int = Color.toRgba(this)
  override def toString() = "HSL(" + f"$hue%1.3f" + "," + f"$saturation%1.3f" + "," + f"$lightness%1.3f" + ")"
  override def svg(): String = "hsl(" +f"$hue%1.3f" + "," + f"$saturation%1.1f" + "%," + f"$lightness%1.1f" + "%)"
}

@JSExportAll @SerialVersionUID(1L)
case class CMYK(cyan: Float, magenta: Float, yellow: Float, black: Float) extends Color {
  override def rgba: Int = Color.toRgba(this)
  override def toString() = "CMYK(" + f"$cyan%1.3f" + "," + f"$magenta%1.3f" + "," + f"$yellow%1.3f" + "," + f"$black%1.3f" + ")"
}

trait XYZ extends Color {
  @JSExport def X: Float
  @JSExport def Y: Float
  @JSExport def Z: Float

  @JSExport override def toString() = "XYZ(" + f"$X%1.3f" + "," + f"$Y%1.3f" + "," + f"$Z%1.3f" + ")"
}

object XYZ {
  def apply(rgba: RGBA): XYZ = rgba
  def apply(X: Float, Y: Float, Z: Float): XYZ = SlowSmallXYZ(X, Y, Z)
}

@SerialVersionUID(1L)
case class SlowSmallXYZ(override val X: Float, override val Y: Float, override val Z: Float) extends XYZ {
  lazy val rgbA: Int = Color.toRgba(this)
  override def rgba = { rgbA }
}

@SerialVersionUID(1L)
case class FatFastXYZ(override val X: Float, override val Y: Float, override val Z: Float, override val rgba: Int) extends XYZ

trait LAB extends Color {
  @JSExport def L: Float
  @JSExport def a: Float
  @JSExport def b: Float

  @JSExport def distanceTo (that: LAB): Double = LAB.labDistance(this, that)

  @JSExport def valueDistanceTo(lab: LAB): Double = {
    val dL = L - lab.L
    Math.sqrt(dL * dL)
  }

  /*
    L*a*b* color space extrema:
      L: (-5.5999998E-8, 100.0)
      a: (-86.18464, 98.25422)
      b: (-107.86368, 94.48248)
  */
  // Found 857621 colors 16785409
  @JSExport def discretize(): LAB = SlowSmallLAB(Math.round(L), Math.round(a), Math.round(b))

  @JSExport def discretize(r: Float): LAB = {
    SlowSmallLAB(Math.round(L/r)*r, Math.round(a/r)*r, Math.round(b/r)*r)
  }

  @JSExport override def equals(o: Any): Boolean = {
    o match {
      case lab: LAB => LAB.labDistanceSquared(this, lab) < 0.01
      case _ => false
    }
  }

  @JSExport override def toString() = "LAB(" + f"$L%1.3f" + "," + f"$a%1.3f" + "," + f"$b%1.3f" + ")"
}

object LAB {
  val L_MAX = 100.0f
  val L_MIN = -5.5999998E-8f

  val a_MAX = 98.25422f
  val a_MIN = -86.18464f

  val b_MAX = 94.48248f
  val b_MIN = -107.86368f

  val EXPECTED_DISTANCE = 83.6f
  val MAX_DISTANCE = 258.6930341882054f
  val STANDARD_DEVIATION_NORMAL = 41f
  val STANDARD_DEVIATION_POISSON = 9.14f

  def labDistanceSquared (lab1: LAB, lab2: LAB): Double = {
    var dL = lab1.L - lab2.L; dL = dL * dL
    var dA = lab1.a - lab2.a; dA = dA * dA
    var dB = lab1.b - lab2.b; dB = dB * dB
    dL + dA + dB
  }

  def labDistance (lab1: LAB, lab2: LAB): Double = {
    Math.sqrt(labDistanceSquared(lab1, lab2))
  }
}

@SerialVersionUID(1L)
case class SlowSmallLAB(override val L: Float, override val a: Float, override val b: Float) extends LAB {
  lazy val rgbA: Int = Color.toRgba(this)
  override def rgba = { rgbA }
}

@SerialVersionUID(1L)
case class FatFastLAB(override val L: Float, override val a: Float, override val b: Float, override val rgba: Int) extends LAB

trait LUV extends Color {
  @JSExport def L: Float
  @JSExport def u: Float
  @JSExport def v: Float

  @JSExport def distanceTo (that: LUV): Double = LUV.luvDistance(this, that)

  @JSExport def valueDistanceTo(lab: LUV): Double = {
    val dL = L - lab.L
    Math.sqrt(dL * dL)
  }

  @JSExport override def equals(o: Any): Boolean = {
    o match {
      case luv: LUV => LUV.luvDistanceSquared(this, luv) < 0.01
      case _ => false
    }
  }

  @JSExport override def toString() = "LAB(" + f"$L%1.3f" + "," + f"$u%1.3f" + "," + f"$v%1.3f" + ")"

}

object LUV {
  def luvDistanceSquared (luv1: LUV, luv2: LUV): Double = {
    var dL = luv1.L - luv2.L; dL = dL * dL
    var dU = luv1.u - luv2.u; dU = dU * dU
    var dV = luv1.v - luv2.v; dV = dV * dV
    dL + dU + dV
  }

  def luvDistance (luv1: LUV, luv2: LUV): Double = Math.sqrt(luvDistanceSquared(luv1, luv2))
}

@SerialVersionUID(1L)
case class SlowSmallLUV(override val L: Float, override val u: Float, override val v: Float) extends LUV {
  lazy val rgbA: Int = Color.toRgba(this)
  override def rgba = { rgbA }
}

@SerialVersionUID(1L)
case class FatFastLUV(override val L: Float, override val u: Float, override val v: Float, override val rgba: Int) extends LUV


@JSExport("Color")
object Color {

  @JSExport val CLEAR = RGBA(0, 0, 0, 0)
  @JSExport val BLACK = RGBA(0, 0, 0)
  @JSExport val WHITE = RGBA(255, 255, 255)
  @JSExport val GRAY = RGBA(128, 128, 128)
  @JSExport val DARK_GRAY = gray(64)
  @JSExport val LIGHT_GRAY = gray(192)

  @JSExport def random(): RGBA = {
    RGBA(
      (Math.random() * 255).toInt,
      (Math.random() * 255).toInt,
      (Math.random() * 255).toInt
    )
  }

  @JSExport def gray(value: Int): RGBA = RGBA(value, value, value)

  implicit def fromAwtColor(awtColor: java.awt.Color): RGBA = RGBA(awtColor.getRGB)

  implicit def toAwtColor(color: Color): java.awt.Color = new java.awt.Color(color.rgba, true)

  @JSExport implicit def toRgba(rgba: Int): RGBA = RGBA(rgba)

  @JSExport implicit def toInt(c: Color): Int = c.rgba

  /*
   * For HSL, HSV, and CMYK conversion formulas:
   * http://www.rapidtables.com/convert/color/rgb-to-hsl.htm
   * http://www.rapidtables.com/convert/color/rgb-to-hsv.htm
 */

  case class HueMaxMin(hue: Float, cMax: Float, cMin: Float)

  case class HueCXM(hue: Float, c: Float, x: Float, m: Float)

  private def colorToHueMaxMin(c: Color): HueMaxMin = {
    //  1/255 = 0.00392156862745098
    val r = c.red * 0.00392156862745098f
    val g = c.green * 0.00392156862745098f
    val b = c.blue * 0.00392156862745098f

    val cMin = Math.min(r, Math.min(g, b))
    val cMax = Math.max(r, Math.max(g, b))

    val delta = cMax - cMin

    val h = if (delta == 0) 0 else ((
      if (r == cMax) ((g - b) / delta) % 6
      else if (g == cMax) (( b - r ) / delta) + 2
      else ((r - g) / delta) + 4
      ) * 60f + 360f) % 360

    HueMaxMin( h, cMax, cMin )
  }

  private def hueCxmToRgba(hcxm: HueCXM): RGBA = {
    val X = Math.round(255f * (hcxm.m + hcxm.x))
    val C = Math.round(255f * (hcxm.m + hcxm.c))
    val zero = Math.round(hcxm.m * 255f)

    // val h = if (hcxm.hue < 0) 60 - (-hcxm.hue) else hcxm.hue
    val h = hcxm.hue

    if (h >= 0 && h < 60) RGBA(C, X, zero)
    else if (h < 120) RGBA(X, C, zero)
    else if (h < 180) RGBA(zero, C, X)
    else if (h < 240) RGBA(zero, X, C)
    else if (h < 300) RGBA(X, zero, C)
    else RGBA(C, zero, X)
  }

  private def hcToX(H: Float, C: Float): Float = {
    val hh = H/60f
    C * ( 1 - Math.abs( hh % 2 - 1 ) )
  }

  @JSExport implicit def toHsv(c: Color): HSV = {
    val hmm = colorToHueMaxMin(c)
    HSV(hmm.hue, 100 * (hmm.cMax - hmm.cMin) / hmm.cMax,  100f * hmm.cMax)
  }

  @JSExport implicit def toRgba (hsv: HSV): RGBA = {
    val C = (hsv.value / 100f) * (hsv.saturation / 100f)  // X
    val X = hcToX(hsv.hue, C)
    val m = (hsv.value / 100f) - C
    hueCxmToRgba( HueCXM( hsv.hue, C, X, m ) )
  }

  @JSExport implicit def toHsl(c: Color): HSL = {
    val hmm = colorToHueMaxMin(c)
    val delta = hmm.cMax - hmm.cMin
    val L = (hmm.cMax + hmm.cMin) / 2f
    val denom = 1f - Math.abs(2*L-1)
    val S = if (denom <= 0f) 0 else delta / denom
    HSL(hmm.hue,  100f * S,  100f * L)
  }

  @JSExport implicit def toRgba (hsl: HSL): RGBA = {
    val C = (1 - Math.abs(2f*(hsl.lightness / 100f) - 1f)) * (hsl.saturation / 100f)
    val X = hcToX(hsl.hue, C)
    val m = (hsl.lightness / 100f) - (C / 2f)
    hueCxmToRgba( HueCXM( hsl.hue, C, X, m ) )
  }

  @JSExport implicit def toCmyk(c: Color): CMYK = {
    //  1/255 = 0.00392156862745098
    val r = c.red * 0.00392156862745098f
    val g = c.green * 0.00392156862745098f
    val b = c.blue * 0.00392156862745098f

    val K = 1 - Math.max(r, Math.max(g, b))
    val kInv = 1 - K
    val C = (1 - r - K) / kInv
    val M = (1 - g - K) / kInv
    val Y = (1 - b - K) / kInv

    CMYK(C, M, Y, K)
  }

  @JSExport implicit def toRgba(cmyk: CMYK): RGBA = {
    RGBA(
      Math.round(255 * (1 - cmyk.cyan) * (1 - cmyk.black)).toInt,
      Math.round(255 * (1 - cmyk.magenta) * (1 - cmyk.black)).toInt,
      Math.round(255 * (1 - cmyk.yellow) * (1 - cmyk.black)).toInt
    )
  }

  /**
   * CIE color space conversions based on pseudo code from:
   * http://www.easyrgb.com/index.php?X=MATH
   */

  @JSExport implicit def toXyz(c: Color): XYZ = {
    val R = Color.prepXyz(c.red)
    val G = Color.prepXyz(c.green)
    val B = Color.prepXyz(c.blue)

    FatFastXYZ (
      (R * 0.4124 + G * 0.3576 + B * 0.1805).toFloat,
      (R * 0.2126 + G * 0.7152 + B * 0.0722).toFloat,
      (R * 0.0193 + G * 0.1192 + B * 0.9505).toFloat,
      c.rgba
    )
  }

  @JSExport implicit def toRgba (xyz: XYZ): RGBA = {
    RGBA(
      strikeXyz(xyz.X * 3.24065 + xyz.Y * -1.5372 + xyz.Z * -0.4986).toInt,
      strikeXyz(xyz.X * -0.9689 + xyz.Y * 1.87585 + xyz.Z * 0.04155).toInt,
      strikeXyz(xyz.X * 0.05575 + xyz.Y * -0.2040 + xyz.Z * 1.0570).toInt
    )
  }

  @JSExport implicit def toXyz(lab: LAB): XYZ = {
    val labY = (lab.L + 16.0) / 116.0

    SlowSmallXYZ(
      (strikeLab(lab.a / 500.0 + labY) * 95.047).toFloat,
      (strikeLab(labY) * 100.0).toFloat,
      (strikeLab(labY - lab.b / 200.0) * 108.883).toFloat
    )
  }

  @JSExport implicit def toRgba(lab: LAB): RGBA = toXyz(lab)

  @JSExport implicit def toLab(c: Color): LAB = {
    val R = Color.prepXyz(c.red)
    val G = Color.prepXyz(c.green)
    val B = Color.prepXyz(c.blue)

    val labX = prepLab(R * 0.004338906014918935 + G * 0.0037623491535766513 + B * 0.0018990604648226666)
    val labY = prepLab(R * 0.0021260000000000003 + G * 0.0071519999999999995 + B * 0.000722)
    val labZ = prepLab(R * 0.00017725448417108274 + G * 0.0010947530835851327 + B * 0.008729553741171717)

    FatFastLAB(
      ((116.0 * labY) - 16.0).toFloat,
      (500.0 * (labX - labY)).toFloat,
      (200.0 * (labY - labZ)).toFloat,
      c.rgba
    )
  }

  // Observer = 2 degrees, Illuminant = D65
  // ref_U and ref_V are constants used to convert between LUV and XYZ.
  val ref_U: Double = ( 4.0 * 95.047 ) / ( 95.047 + ( 15 * 100.0 ) + ( 3 * 108.883 ) )
  val ref_V: Double = ( 9.0 * 100.0 ) / ( 95.047 + ( 15 * 100.0 ) + ( 3 * 108.883 ) )

  @JSExport implicit def toLuv (xyz: XYZ): LUV = {
		val U: Double = ( 4.0 * xyz.X ) / ( xyz.X + ( 15.0 * xyz.Y ) + ( 3.0 * xyz.Z ) )
    val V: Double = ( 9.0 * xyz.Y ) / ( xyz.X + ( 15.0 * xyz.Y ) + ( 3.0 * xyz.Z ) )

    var y0: Double = xyz.Y / 100
		if ( y0 > 0.008856 ) y0 = Math.pow(y0, 1.0 / 3.0).toFloat
		else y0 = ( 7.787 * y0 ) + ( 16.0 / 116.0 )

    val L: Float = (( 116.0 * y0 ) - 16.0).toFloat
    val u: Float = (13.0 * L * ( U - ref_U )).toFloat
    val v: Float = (13.0 * L * ( V - ref_V )).toFloat

		FatFastLUV(L, u, v, xyz.rgba)
	}

  @JSExport implicit def toXYZ (luv: LUV): XYZ = {

    var y0: Double = ( luv.L + 16.0 ) / 116.0
    val powY0 = Math.pow(y0, 3.0)
		if ( powY0 > 0.008856 ) y0 = powY0
		else y0 = ( y0 - 16.0 / 116.0 ) / 7.787

    val var_U: Double = luv.u / ( 13.0 * luv.L ) + ref_U
    val var_V: Double = luv.v / ( 13.0 * luv.L ) + ref_V

    val Y: Float = (y0 * 100.0).toFloat
    val X: Float = -(( 9.0 * Y * var_U ) / ( ( var_U - 4.0 ) * var_V  - var_U * var_V )).toFloat
    val Z: Float = (( 9.0 * Y - ( 15.0 * var_V * Y ) - ( var_V * X ) ) / ( 3.0 * var_V )).toFloat

    FatFastXYZ(X, Y, Z, luv.rgba)
	}

  @JSExport implicit def toLuv(c: Color): LUV = toXyz(c)

  @JSExport implicit def toRgba(luv: LUV): RGBA = toXYZ(luv)

  //@JSExport implicit def luvToInt(luv: LUV): Int = luv.rgba

  //@JSExport implicit def intToLuv(rgba: Int): Int = rgbaToLuv(rgba)

  // xyz & lab conversion convenience methods:
  private def prepXyz(u: Int): Double = {
    if (u > 10) Math.pow(u * 0.003717126661090977 + 0.05213270142180095, 2.4) * 100.0
    else u * 0.03035269835488375
  }

  private def strikeXyz(v: Double): Int = {
    val u0 = (Math.pow((v * 0.01), 0.4166666666666667) - 0.05213270142180095) * 269.025
    val u =
      if (u0 > 10.0) u0.toInt
      else (v / 0.03035269835488375).toInt
    Math.max(0, Math.min(u, 255))
  }

  private def prepLab(u: Double): Double = if (u > 0.008856) Math.pow(u, 1.0/3.0) else (7.787 * u) + 0.137931034

  private def strikeLab(v: Double): Double = {
    val u = v*v*v
    if (u > 0.008856) u else (v - 0.137931034) / 7.787
  }

  // Utility methods:

  @JSExport def alphaBlend(c1: RGBA, c2: RGBA): RGBA = {
    if (c1.alpha >= 255) c1.rgba
    else {
      val w1 = c1.alpha / 255.0
      val w2 = c2.alpha / 255.0
      RGBA(
        (w1 * c1.red + w2 * c2.red * (1 - w1)).toInt,
        (w1 * c1.green + w2 * c2.green * (1 - w1)).toInt,
        (w1 * c1.blue + w2 * c2.blue * (1 - w1)).toInt,
        Math.max(0, Math.min(255, c1.alpha + c2.alpha * (1 - w1))).toInt
      )
    }
  }

  @JSExport def weightedAverage(c1: RGBA, w1: Float, c2: RGBA, w2: Float): RGBA = {
    if (c1.alpha == 0 && c2.alpha != 0) c2
    else if (c1.alpha != 0 && c2.alpha == 0) c1
    else {
      RGBA(
        (w1 * c1.red + w2 * c2.red).toInt,
        (w1 * c1.green + w2 * c2.green).toInt,
        (w1 * c1.blue + w2 * c2.blue).toInt,
        Math.max(0, Math.min(255, (w1 * c1.alpha +  w2 * c2.alpha).toInt))
      )
    }
  }

  // rgb component validation
  private def validRgbComponent(component: Int): Boolean = component >= 0 && component < 256
  private def validPercentage(percentage: Float): Boolean = percentage >= 0f && percentage <= 100f
  private def validHue(hue: Float): Boolean = hue >= 0f && hue <= 360f
  private def validWeight(weight: Float): Boolean = weight >= 0f && weight <= 1f

  // factory methods for the javascript library:
  @JSExport("RGBA") def rgba(red: Int, green: Int, blue: Int, alpha: Int = 255): RGBA = {
    if (!validRgbComponent(red)) throw ColorComponentOutOfRangeException(f"Red $red outside range [0-255]")
    if (!validRgbComponent(green)) throw ColorComponentOutOfRangeException(f"Green $green outside range [0-255]")
    if (!validRgbComponent(blue)) throw ColorComponentOutOfRangeException(f"Blue $blue outside range [0-255]")
    if (!validRgbComponent(alpha)) throw ColorComponentOutOfRangeException(f"Alpha $alpha outside range [0-255]")

    RGBA(red<<16|green<<8|blue|(alpha<<24))
  }

  @JSExport("HSV") def hsv(hue: Float, saturation: Float, value: Float): HSV = {
    if (!validHue(hue)) throw ColorComponentOutOfRangeException(f"Hue $hue outside range [0-360]")
    if (!validPercentage(saturation)) throw ColorComponentOutOfRangeException(f"Saturation $saturation outside range [0-100]")
    if (!validPercentage(value)) throw ColorComponentOutOfRangeException(f"Value $value outside range [0-100]")

    HSV(hue, saturation, value)
  }

  @JSExport("HSL") def hsl(hue: Float, saturation: Float, lightness: Float): HSL = {
    if (!validHue(hue)) throw ColorComponentOutOfRangeException(f"Hue $hue outside range [0-360]")
    if (!validPercentage(saturation)) throw ColorComponentOutOfRangeException(f"Saturation $saturation outside range [0-100]")
    if (!validPercentage(lightness)) throw ColorComponentOutOfRangeException(f"Lightness $lightness outside range [0-100]")
    HSL(hue, saturation, lightness)
  }

  @JSExport("CMYK") def cmyk(cyan: Float, magenta: Float, yellow: Float, black: Float): CMYK = {
    if (!validWeight(cyan)) throw ColorComponentOutOfRangeException(f"Cyan $cyan outside range [0-1]")
    if (!validWeight(magenta)) throw ColorComponentOutOfRangeException(f"Magenta $magenta outside range [0-1]")
    if (!validWeight(yellow)) throw ColorComponentOutOfRangeException(f"Yellow $yellow outside range [0-1]")
    if (!validWeight(black)) throw ColorComponentOutOfRangeException(f"Black $black outside range [0-1]")
    CMYK(cyan, magenta, yellow, black)
  }

  @JSExport("XYZ") def xyz(x: Float, y: Float, z: Float): XYZ = {
    val xyz = XYZ(x, y, z)
    val c = toRgba(xyz)
    if (toXyz(c) != c) throw ColorComponentOutOfRangeException(f"Invalid component(s): XYZ($x, $y, $z)")
    else xyz
  }

  @JSExport("LAB") def lab(l: Float, a: Float, b: Float): LAB = SlowSmallLAB(l, a, b)

  @JSExport("LUV") def luv(l: Float, u: Float, v: Float): LUV = SlowSmallLUV(l, u, v)

}

case class ColorComponentOutOfRangeException(message: String) extends Exception(message)

object TestColors extends App {

  for (i <- 0 to 100) {
    val rgba: RGBA = Color.random()
    val hsl: HSL = rgba
    val hsv: HSV = rgba
    println(rgba + " " + hsl + " " + hsv)
  }

  //  L*a*b* color space extrema:
  //  L: (-5.5999998E-8, 100.0)
  //  a: (-86.18464, 98.25422)
  //  b: (-107.86368, 94.48248)

  //  var lab1: LAB = RGBA(0,255,0,255)
  //  var lab2: LAB = RGBA(0,0,255,255)
  //  println(lab1.labDistanceTo(lab2))

  if (false) {
    var cumulative = 0.0
    var counter = 0

    val histogram: Array[Int] = Array.fill[Int](260)(0)

    for (i <- 0 to 10000000) {
      val c1 = Color.toLab(Color.random()).discretize
      val c2 = Color.toLab(Color.random()).discretize
      if (c1 != c2) {
        counter = counter + 1
        val dist = c1.distanceTo(c2)
        histogram(dist.toInt) = histogram(dist.toInt) + 1
        val dM = 83.6 - dist
        cumulative = cumulative + (dM * dM)
      }
    }

    for (i <- histogram) {
      println(i)
    }

    println("Standard Deviation of distance between colors: " + Math.sqrt(cumulative / counter) + " Sample Size: " + counter)
  }
}
