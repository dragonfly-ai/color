package ai.dragonfly.color

import scalajs.js

import js.annotation.{JSExport, JSExportAll}


/**
 * Created by clifton on 4/24/15.
 */

/**
 * Color is the base trait from which all other color types inherit.
 */

trait Color {
  /**
   * @return a 32 bit integer that represents this color in RGBA space.
   * The most significant byte encodes the alpha value, the second most significant byte encodes red,
   * the third most significant byte encodes green, and the least significant byte encodes blue.
   */
  @JSExport def argb: Int

  /**
   * @return the red component of this color in RGB space.
   */
  @JSExport def red = argb >> 16 & 0xff
  /**
   * @return the green component of this color in RGB space.
   */
  @JSExport def green = argb >> 8 & 0xff
  /**
   * @return the blue component of this color in RGB space.
   */
  @JSExport def blue = argb & 0xff
  /**
   * @return the alpha component of this color in RGBA space.
   */
  @JSExport def alpha = argb >> 24 & 0xff

  @JSExport def distanceSquaredTo (c: Color): Double

  @JSExport def distanceTo (c: Color): Double = Math.sqrt(distanceSquaredTo(c))
  /**
   * @return the hashcode.  For all color types, the hashcode function returns the same result as argb
   */
  override def hashCode(): Int = argb

  /**
   * @return true if these colors are equal in RGBA space, false otherwise
   */
  override def equals(o: Any): Boolean = o match {
    case c: Color => this.argb == c.argb
    case _ => false
  }

  /**
   * @return a hexadecimal string representing the rgba integer for this color.
   * @example {{{
   * val c = RGBA(72,105,183)
   * c.hex() // returns "ff4869b7"
   * }}}
   */
  @JSExport def hex(): String = Integer.toHexString(argb)

  /**
   * @return a string representing the color in an html friendly way.
   * @example {{{
   * val c = RGBA(72,105,183)
   * c.html() // returns "#4869b7"
   * }}}
   */
  @JSExport def html(): String = "#" + Integer.toHexString(argb | 0xff000000).substring(2)

  /**
   * @return a string representing the color in an SVG friendly way.
   * @example {{{
   * val c = RGBA(72,105,183)
   * c.svg() // returns "rgb(72,105,183)"
   * }}}
   *
   * if the color has an alpha value less than 255, in other words, if the color has any measure of translucency,
   * this method returns an rgba svg string instead of an rgb string.
   * @example {{{
   * val c = RGBA(72,105,183, 128)
   * c.svg() // returns "rgba(72,105,183,0.501960813999176)"
   * }}}
   */
  @JSExport def svg(): String = {
    if (alpha < 255) "rgba(" + red + "," + green + "," + blue + "," + (alpha / 255f) + ")"
    else "rgb(" + red + "," + green + "," + blue + ")"
  }
}

/**
 * Companion object for the RGBA case class.
 */

object RGBA {
  /**
   * apply method to create an RGBA instance from separate, specified red, green, blue, and optional alpha components.
   * parameter values are derived from the least significant byte.  Integer values that range outside of [0-255] may
   * give unexpected results.  For values taken from user input, sensors, or otherwise uncertain sources, consider using
   * the factory method in the Color companion object.
   * @see [[ai.dragonfly.color.Color.argb]] for a method of constructing RGBA objects that validates inputs.
   * @param red integer value from [0-255] representing the red component in RGB space.
   * @param green integer value from [0-255] representing the green component in RGB space.
   * @param blue integer value from [0-255] representing the blue component in RGB space.
   * @param alpha optional integer value from [0-255] representing the alpha component in RGBA space.  Defaults to 255.
   * @return an instance of the RGBA case class.
   * @example {{{ val c = RGBA(72,105,183) }}}
   */
  def apply(red: Int, green: Int, blue: Int, alpha: Int = 255): RGBA = RGBA(red<<16|green<<8|blue|(alpha<<24))
}

/**
 * RGBA is the primary case class for representing colors in RGBA space.
 *
 * @constructor Create a new RGBA object from an Int.
 *
 *  @see [[https://en.wikipedia.org/wiki/RGB_color_space]] for more information on the RGB color space.
 *
 *  @param argb a 32 bit integer that represents this color in RGBA space.
 * The most significant byte encodes the alpha value, the second most significant byte encodes red,
 * the third most significant byte encodes green, and the least significant byte encodes blue.
 * @return an instance of the RGBA case class.
 * @example {{{
 * val c = RGBA(-1)  // returns fully opaque white
 * c.toString()  // returns "RGBA(255,255,255,255)"
 * RGBA(0xFF0000FF).toString() // returns "RGBA(255,0,0,255)"
 * }}}
 */
@SerialVersionUID(1L)
case class RGBA(override val argb: Int) extends Color {
  /**
   * @return the distance between this color and the parameter in rgb space.
   * Distances exclude alpha information.
   * Distances range from [0, 441.6729559300637]
   * @example {{{
   * val c1 = RGBA(72,105,183)
   * val c2 = RGBA(0,105,255)
   * c1.distanceTo(c1) // returns 0
   * c1.distanceTo(c2) // returns 101.82337649086284
   * }}}
   */
  @JSExport def distanceSquaredTo (c: Color): Double = {
    var dR = red - c.red; dR = dR * dR
    var dG = green - c.green; dG = dG * dG
    var dB = blue - c.blue; dB = dB * dB
    dR + dG + dB
  }
  override def toString() = "RGBA(" + red + "," + green + "," + blue + "," + alpha + ")"
}

/**
 * HSV is the primary case class for representing colors in HSV space.
 *
 * @constructor Create a new HSV object from three float values.  This constructor does not validate
 * input parameters.  For values taken from user input, sensors, or otherwise uncertain sources, consider using
 * the factory method in the Color companion object.
 *
 * @see [[ai.dragonfly.color.Color.hsv]] for a method of constructing HSV objects that validates inputs.
 * @see [[https://en.wikipedia.org/wiki/HSL_and_HSV]] for more information about the HSV color space.
 * @param hue an angle ranging from [0-360] degrees.  Values outside of this range may cause errors.
 * @param saturation a percentage ranging from [0-100].  Values outside of this range may cause errors.
 * @param value a percentage ranging from [0-100].  Values outside of this range may cause errors.
 * @return an instance of the HSV case class.
 * @example {{{
 * val c = HSV(211f, 75f, 33.3333f)
 * c.toString()  // returns "HSV(211.000,75.000,33.333)"
 * }}}
 */
@JSExportAll @SerialVersionUID(1L)
case class HSV(hue: Float, saturation: Float, value: Float) extends Color {
  override def argb: Int = Color.toRgba(this)
  @JSExport override def toString() = "HSV(" + f"$hue%1.3f" + "," + f"$saturation%1.3f" + "," + f"$value%1.3f" + ")"

  @JSExport override def distanceSquaredTo(c: Color): Double = {
    val c1: HSV = c
    var dH = hue - c1.hue; dH = dH * dH
    var dS = saturation - c1.saturation; dS = dS * dS
    var dV = value - c1.value; dV = dV * dV
    dH + dS + dV
  }
}

/**
 * HSL is the primary case class for representing colors in HSL space.
 *
 * @constructor Create a new HSV object from three float values.  This constructor does not validate input parameters.
 * For values taken from user input, sensors, or otherwise uncertain sources, consider using the factory method in the Color companion object.
 *
 * @see [[ai.dragonfly.color.Color.hsl]] for a method of constructing HSL objects that validates inputs.
 * @see [[https://en.wikipedia.org/wiki/HSL_and_HSV]] for more information about the HSL color space.
 * @param hue an angle ranging from [0-360] degrees.  Values outside of this range may cause errors.
 * @param saturation a percentage ranging from [0-100].  Values outside of this range may cause errors.
 * @param lightness a percentage ranging from [0-100].  Values outside of this range may cause errors.
 * @return an instance of the HSL case class.
 * @example {{{
 * val c = HSL(211f, 75f, 33.3333f)
 * c.toString()  // returns "HSL(211.000,75.000,33.333)"
 * }}}
 */
@JSExportAll @SerialVersionUID(1L)
case class HSL(hue: Float, saturation: Float, lightness: Float) extends Color {
  override def argb: Int = Color.toRgba(this).argb

  override def distanceSquaredTo(c: Color): Double = {
    val c1: HSL = c
    var dH = hue - c1.hue; dH = dH * dH
    var dS = saturation - c1.saturation; dS = dS * dS
    var dL = lightness - c1.lightness; dL = dL * dL
    dH + dS + dL
  }

  override def toString() = "HSL(" + f"$hue%1.3f" + "," + f"$saturation%1.3f" + "," + f"$lightness%1.3f" + ")"

  /**
   * @return a string representing the color in an SVG friendly way.
   * @example {{{
   * val c = HSL(211f, 75f, 33.3333f)
   * c.svg() // returns "hsl(211.000,75.0%,33.3%)"
   * }}}
   */
  override def svg(): String = "hsl(" +f"$hue%1.3f" + "," + f"$saturation%1.1f" + "%," + f"$lightness%1.1f" + "%)"
}

/**
 * CMYK is the primary case class for representing colors in CMYK space.
 *
 * @constructor Create a new HSV object from three float values.  This constructor does not validate input parameters.
 * For values taken from user input, sensors, or otherwise uncertain sources, consider using the factory method in the Color companion object.
 *
 * @see [[ai.dragonfly.color.Color.cmyk]] for a method of constructing CMYK objects that validates inputs.
 * @see [[https://en.wikipedia.org/wiki/CMYK_color_model]] for more information about the CMYK color space.
 * @param cyan a value ranging from [0-1].  Values outside of this range may cause errors.
 * @param magenta a value ranging from [0-1].  Values outside of this range may cause errors.
 * @param yellow a value ranging from [0-1].  Values outside of this range may cause errors.
 * @param black a value ranging from [0-1].  Values outside of this range may cause errors.
 * @return an instance of the HSV case class.
 * @example {{{
 * val c = CMYK(1f, 0.25f, 0.5f, 0f)
 * c.toString()  // returns "CMYK(1.000,0.250,0.500,0.000)"
 * }}}
 */
@JSExportAll @SerialVersionUID(1L)
case class CMYK(cyan: Float, magenta: Float, yellow: Float, black: Float) extends Color {
  override def argb: Int = Color.toRgba(this).argb
  override def toString() = "CMYK(" + f"$cyan%1.3f" + "," + f"$magenta%1.3f" + "," + f"$yellow%1.3f" + "," + f"$black%1.3f" + ")"

  override def distanceSquaredTo(c: Color): Double = {
    val c1: CMYK = c
    var dC = cyan - c1.cyan; dC = dC * dC
    var dM = magenta - c1.magenta; dM = dM * dM
    var dY = yellow - c1.yellow; dY = dY * dY
    var dB = black - c1.black; dB = dB * dB
    dC + dM + dY + dB
  }
}

/**
 * XYZ is the base trait for classes that encode colors in the CIE XYZ color space.
 * LAB and LUV classes depend on XYZ for conversions to and from RGB, HSL, HSV, and CMYK
 *
 * @see [[https://en.wikipedia.org/wiki/CIE_1931_color_space]] for more information on CIE XYZ.
 */
trait XYZ extends Color {
  /** @return the X component of this color in XYZ space. */
  @JSExport def X: Float

  /** @return the Y component of this color in XYZ space. */
  @JSExport def Y: Float

  /** @return the Z component of this color in XYZ space. */
  @JSExport def Z: Float

  override def distanceSquaredTo(c: Color): Double = {
    val c1: XYZ = c
    var dX = X - c1.X; dX = dX * dX
    var dY = Y - c1.Y; dY = dY * dY
    var dZ = Z - c1.Z; dZ = dZ * dZ
    dX + dY + dZ
  }

  @JSExport override def toString() = "XYZ(" + f"$X%1.3f" + "," + f"$Y%1.3f" + "," + f"$Z%1.3f" + ")"
}

/**
 * Companion object for SlowSlimXYZ and FastFatXYZ classes.
 */
object XYZ {
  /**
   * apply method to create instances of the SlowSlimXYZ case class.  This method does not validate its input parameters.
   *
   * @param X the X component of the XYZ color.
   * @param Y the Y component of the XYZ color.
   * @param Z the Z component of the XYZ color.
   * @return an instance of the XYZ case class.
   * @example {{{ val c = XYZ(22.527,38.820,26.728) }}}
   */
  def apply(X: Float, Y: Float, Z: Float): XYZ = SlowSlimXYZ(X, Y, Z)
}

/**
 * The SlowSlimXYZ class stores only the X, Y, and Z components of the XYZ color it encodes while FastFatXYZ also stores
 * an Int representing its argb value.
 *
 * SlowSlimXYZ requires 4 bytes less memory than FastFatXYZ, however conversions from SlowSlimXYZ to non CIE color spaces:
 * RGB, HSV, HSL, CMYK, require more computational resources than FastFatXYZ.
 *
 * Use SlowSlimXYZ to save memory on XYZ colors that will rarely or never require conversion to other color spaces.
 * Use FastFatXYZ in situations where color space conversion speed matters more than memory.
 *
 * @constructor Create a new SlowSlimXYZ object from three float values.  This constructor does not validate input parameters.
 *
 * @param X the X component of the XYZ color.
 * @param Y the Y component of the XYZ color.
 * @param Z the Z component of the XYZ color.
 * @return an instance of the SlowSlimXYZ case class.
 * @example {{{ val c = SlowSlimXYZ(22.527,38.820,26.728) }}}
 */
@SerialVersionUID(1L)
case class SlowSlimXYZ(override val X: Float, override val Y: Float, override val Z: Float) extends XYZ {
  override def argb: Int = this
}

/**
 * FastFatXYZ requires 4 bytes more memory than SlowSlimXYZ, however conversions from FastFatXYZ to non CIE color spaces:
 * RGB, HSV, HSL, CMYK, compute much faster than from SlowSlimXYZ.
 *
 * Use FastFatXYZ in situations where color space conversion speed matters more than memory.
 * Use SlowSlimXYZ to save memory on XYZ colors that will rarely or never require conversion to other color spaces.
 *
 * @constructor Create a new FastFatXYZ object from three float values and an Int.  This constructor does not validate input parameters.
 * @param X the X component of the XYZ color.
 * @param Y the Y component of the XYZ color.
 * @param Z the Z component of the XYZ color.
 * @param argb a 32 bit integer that represents this color in RGBA space.
 * @return an instance of the FastFatXYZ case class.
 * @example {{{ val c = FastFatXYZ(22.527, 38.820, 26.728, 0xff00bf80) }}}
 */
@SerialVersionUID(1L)
case class FastFatXYZ(override val X: Float, override val Y: Float, override val Z: Float, override val argb: Int) extends XYZ


/**
 * LAB is the base trait for classes that encode colors in the CIE L*a*b* color space.
 *
 * @see [[https://en.wikipedia.org/wiki/Lab_color_space]] for more information on CIE L*a*b*.
 */
trait LAB extends Color {
  /** @return the L* component of this color in CIE L*a*b* color space. */
  @JSExport def L: Float

  /** @return the a* component of this color in CIE L*a*b* color space. */
  @JSExport def a: Float

  /** @return the a* component of this color in CIE L*a*b* color space. */
  @JSExport def b: Float

  /**
   * @param c a color
   * @return the euclidean distance, in CIE L*a*b* color space, between this and the color passed as an argument.
   */
  @JSExport override def distanceSquaredTo (c: Color): Double = LAB.labDistanceSquared(this, c)

  /**
   * @param lab a color
   * @return the distance, in CIE L*a*b* color space, between the brightness of this color and the brightness of the color passed as an argument.
   */
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
  /**
   * @return a version of this color with all color components rounded to the nearest integer.
   */
  @JSExport def discretize(): LAB = SlowSlimLab(Math.round(L), Math.round(a), Math.round(b))

  /**
   * @param r a color
   * @return a version of this color with all color components rounded to the nearest coordinate in a uniform, three dimensional, grid
   * with grid cell dimensions = r X r X r.
   */
  @JSExport def discretize(r: Float): LAB = {
    SlowSlimLab(Math.round(L/r)*r, Math.round(a/r)*r, Math.round(b/r)*r)
  }

  /**
   * This equals method considers two colors equal if they are imperceptibly different from each other.
   * @param o an object to compare to this color.
   * @return true if the parameter is a color and it's squared euclidean distance from this color is less than 0.01 in CIE L*a*b* color space, false otherwise.
   */
  @JSExport override def equals(o: Any): Boolean = {
    o match {
      case lab: LAB => LAB.labDistanceSquared(this, lab) < 0.01
      case _ => false
    }
  }

  @JSExport override def toString() = "LAB(" + f"$L%1.3f" + "," + f"$a%1.3f" + "," + f"$b%1.3f" + ")"
}

/**
 * Companion object for SlowSlimLAB and FastFatLAB classes.
 */
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

  /**
   * @param lab1 a color
   * @param lab2 a color
   * @return the square of the euclidean distance, in CEI L*a*b* space, between the two colors passed as arguments.
   */
  def labDistanceSquared (lab1: LAB, lab2: LAB): Double = {
    var dL = lab1.L - lab2.L; dL = dL * dL
    var dA = lab1.a - lab2.a; dA = dA * dA
    var dB = lab1.b - lab2.b; dB = dB * dB
    dL + dA + dB
  }

  /**
   * @param lab1 a color
   * @param lab2 a color
   * @return the euclidean distance, in CEI L*a*b* space, between the two colors passed as arguments.
   */
  def labDistance (lab1: LAB, lab2: LAB): Double = {
    Math.sqrt(labDistanceSquared(lab1, lab2))
  }
}

/**
 * The SlowSlimLab class stores only the L, a, and b components of the CIE L*a*b* color it encodes while FastFatLab also stores
 * an Int representing its argb value.
 *
 * SlowSlimLab requires 4 bytes less memory than FatFastLab, however conversions from SlowSlimLab to non CIE color spaces:
 * RGB, HSV, HSL, CMYK, require more computational resources than FastFatLab.
 *
 * Use SlowSlimLab to save memory on CIE L*a*b* colors that will rarely or never require conversion to other color spaces.
 * Use FastFatLab in situations where color space conversion speed matters more than memory.
 *
 * @constructor Create a new SlowSlimLab object from three float values.  This constructor does not validate input parameters.
 *
 * @param L the L* component of the CIE L*a*b* color.
 * @param a the a* component of the CIE L*a*b* color.
 * @param b the b* component of the CIE L*a*b* color.
 * @return an instance of the SlowSlimLab case class.
 * @example {{{ val c = SlowSlimLab(72.872, -0.531, 71.770) }}}
 */
@SerialVersionUID(1L)
case class SlowSlimLab(override val L: Float, override val a: Float, override val b: Float) extends LAB {
  lazy val _argb: Int = Color.toRgba(this)
  override def argb = { _argb }
}

/**
 * FastFatLab requires 4 bytes more memory than SlowSlimLab, however conversions from FastFatLab to non CIE color spaces:
 * RGB, HSV, HSL, CMYK, compute much faster than from SlowSlimLab.
 *
 * Use FastFatLab in situations where color space conversion speed matters more than memory.
 * Use SlowSlimLab to save memory on CIE L*a*b* colors that will rarely or never require conversion to other color spaces.
 *
 * @constructor Create a new FastFatLab object from three float values and an Int.  This constructor does not validate input parameters.
 * @param L the L* component of the CIE L*a*b* color.
 * @param a the a* component of the CIE L*a*b* color.
 * @param b the b* component of the CIE L*a*b* color.
 * @param argb a 32 bit integer that represents this color in RGBA space.
 * @return an instance of the FastFatLab case class.
 * @example {{{ val c = FastFatLab(70.263, -66.371, 65.333, 0xff31c61c) }}}
 */
@SerialVersionUID(1L)
case class FastFatLab(override val L: Float, override val a: Float, override val b: Float, override val argb: Int) extends LAB


/**
 * LUV is the base trait for classes that encode colors in the CIE L*u*v* color space.
 *
 * @see [[https://en.wikipedia.org/wiki/CIELUV]] for more information on CIE L*u*v*.
 */
trait LUV extends Color {
  /** @return the L* component of this color in CIE L*u*v* color space. */
  @JSExport def L: Float
  /** @return the u* component of this color in CIE L*u*v* color space. */
  @JSExport def u: Float
  /** @return the u* component of this color in CIE L*u*v* color space. */
  @JSExport def v: Float

  /**
   * @param c a color
   * @return the euclidean distance, in CIE L*u*v* color space, between this and the color passed as an argument.
   */
  @JSExport override def distanceSquaredTo (c: Color): Double = LUV.luvDistanceSquared(this, c)

  /**
   * @param luv a color
   * @return the distance, in CIE L*u*v* color space, between the brightness of this color and the brightness of the color passed as an argument.
   */
  @JSExport def valueDistanceTo(luv: LUV): Double = {
    val dL = L - luv.L
    Math.sqrt(dL * dL)
  }

  /**
    * @return a version of this color with all color components rounded to the nearest integer.
    */
  @JSExport def discretize(): LUV = SlowSlimLuv(Math.round(L), Math.round(u), Math.round(v))

  /**
   * This equals method considers two colors equal if they are imperceptibly different from each other.
   * @param o an object to compare to this color.
   * @return true if the parameter is a color and it's squared euclidean distance from this color is less than 0.01 in CIE L*u*v* color space, false otherwise.
   */
  @JSExport override def equals(o: Any): Boolean = {
    o match {
      case luv: LUV => LUV.luvDistanceSquared(this, luv) < 0.01
      case _ => false
    }
  }

  @JSExport override def toString() = "LUV(" + f"$L%1.3f" + "," + f"$u%1.3f" + "," + f"$v%1.3f" + ")"

}

/**
 * Companion object for SlowSlimLUV and FastFatLUV classes.
 */
object LUV {
  /**
   * @param luv1 a color
   * @param luv2 a color
   * @return the square of the euclidean distance, in CEI L*u*v* space, between the two colors passed as arguments.
   */
  def luvDistanceSquared (luv1: LUV, luv2: LUV): Double = {
    var dL = luv1.L - luv2.L; dL = dL * dL
    var dU = luv1.u - luv2.u; dU = dU * dU
    var dV = luv1.v - luv2.v; dV = dV * dV
    dL + dU + dV
  }

  /**
   * @param luv1 a color
   * @param luv2 a color
   * @return the euclidean distance, in CEI L*u*v* space, between the two colors passed as arguments.
   */
  def luvDistance (luv1: LUV, luv2: LUV): Double = Math.sqrt(luvDistanceSquared(luv1, luv2))
}

/**
 * The SlowSlimLuv class stores only the L, a, and b components of the CIE L*u*v* color it encodes while FastFatLuv also stores
 * an Int representing its argb value.
 *
 * SlowSlimLuv requires 4 bytes less memory than FastFatLuv, however conversions from SlowSlimLuv to non CIE color spaces:
 * RGB, HSV, HSL, CMYK, require more computational resources than FastFatLuv.
 *
 * Use SlowSlimLuv to save memory on CIE L*u*v* colors that will rarely or never require conversion to other color spaces.
 * Use FastFatLuv in situations where color space conversion speed matters more than memory.
 *
 * @constructor Create a new SlowSlimLuv object from three float values.  This constructor does not validate input parameters.
 *
 * @param L the L* component of the CIE L*u*v* color.
 * @param u the u* component of the CIE L*u*v* color.
 * @param v the v* component of the CIE L*u*v* color.
 * @return an instance of the SlowSlimLuv case class.
 * @example {{{ val c = SlowSlimLuv(14.756, -3.756, -58.528) }}}
 */
@SerialVersionUID(1L)
case class SlowSlimLuv(override val L: Float, override val u: Float, override val v: Float) extends LUV {
  lazy val _argb: Int = Color.toRgba(this).argb
  override def argb = { _argb }
}

/**
 * FastFatLuv requires 4 bytes more memory than SlowSlimLuv, however conversions from FastFatLuv to non CIE color spaces:
 * RGB, HSV, HSL, CMYK, compute much faster than from SlowSlimLuv.
 *
 * Use FastFatLuv in situations where color space conversion speed matters more than memory.
 * Use SlowSlimLuv to save memory on CIE L*u*v* colors that will rarely or never require conversion to other color spaces.
 *
 * @constructor Create a new FastFatLuv object from three float values and an Int.  This constructor does not validate input parameters.
 * @param L the L* component of the CIE L*u*v* color.
 * @param u the u* component of the CIE L*u*v* color.
 * @param v the v* component of the CIE L*u*v* color.
 * @param argb a 32 bit integer that represents this color in RGBA space.
 * @return an instance of the FastFatLuv case class.
 * @example {{{ val c = FastFatLuv(14.756, -3.756, -58.528, 0xff0a0188) }}}
 */
@SerialVersionUID(1L)
case class FastFatLuv(override val L: Float, override val u: Float, override val v: Float, override val argb: Int) extends LUV

/**
 * Color contains convenience methods, fields, and implicit conversion methods.
 */
@JSExport("Color")
object Color {

  @JSExport val CLEAR = RGBA(0, 0, 0, 0)
  @JSExport val BLACK = RGBA(0, 0, 0)
  @JSExport val WHITE = RGBA(255, 255, 255)
  @JSExport val GRAY = RGBA(128, 128, 128)
  @JSExport val DARK_GRAY = gray(64)
  @JSExport val LIGHT_GRAY = gray(192)

  /**
   * Use Color.random() to obtain a random color in the form of an RGBA instance.
   * This method executes quickly and without memory costs, but the RGB color space biases toward cool colors.
   * In contrast, the Color.randomFromLabSpace() method takes seconds to initialize and has a memory footprint of several megabytes
   * However, it samples from a perceptually uniform color space and avoids the bias toward cool colors.
   * This method samples the Red, Green, and Blue color components uniformly, but always returns 255 for the alpha component.
   * @return a randomly generated color sampled from the RGB Color Space.
   */
  @JSExport def random(): RGBA = {
    RGBA(
      (Math.random() * 255).toInt,
      (Math.random() * 255).toInt,
      (Math.random() * 255).toInt
    )
  }

  /**
   * Returns a random color in the form of an RGBA instance.
   * This method takes seconds to initialize and has a memory footprint of several megabytes.  Once initialized, though, it executes
   * as fast as Color.random()
   * Although it samples from a perceptually uniform color space that appears unbiased to the human eye, it samples from a
   * discritized version of the CEI L*a*b* color space which contains only colors that have integer values for L*, a*, and b*.
   *
   * In contrast, the Color.random() executes quickly and without memory costs, but the RGB color space biases toward cool colors.
   *
   * This method always returns 255 for the alpha component.
   * @return a randomly generated color sampled from a discritized version of the CEI L*a*b* color space.
   */
  @JSExport def randomFromLabSpace(): RGBA = LabSampleSpace.randomArgb()

  /**
   * generate an RGBA instance from a single value.  This method validates the intensity parameter.
   *
   * @param intensity the intensity of the desired gray value ranging from [0-255].
   * @return an RGBA instance encoding the desired grayscale intensity.
   * @throws ColorComponentOutOfRangeException if intensity escapes the range [0-255].
   */
  @JSExport def gray(intensity: Int): RGBA = {
    if (validRgbaIntensity(intensity)) RGBA(intensity, intensity, intensity)
    else throw ColorComponentOutOfRangeException(f"Intensity $intensity outside range [0-255]")
  }

  implicit def fromAwtColor(awtColor: java.awt.Color): RGBA = RGBA(awtColor.getRGB)

  implicit def toAwtColor(color: Color): java.awt.Color = new java.awt.Color(color.argb, true)

  @JSExport implicit def toRgba(argb: Int): RGBA = RGBA(argb)

  @JSExport implicit def toInt(c: Color): Int = c.argb

  /*
   * For HSL, HSV, and CMYK conversion formulas:
   * http://www.rapidtables.com/convert/color/rgb-to-hsl.htm
   * http://www.rapidtables.com/convert/color/rgb-to-hsv.htm
 */

  private case class HueMaxMin(hue: Float, cMax: Float, cMin: Float)

  private case class HueCXM(hue: Float, c: Float, x: Float, m: Float)

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
    val R = prepXyz(c.red)
    val G = prepXyz(c.green)
    val B = prepXyz(c.blue)

    FastFatXYZ (
      (R * 0.4124 + G * 0.3576 + B * 0.1805).toFloat,
      (R * 0.2126 + G * 0.7152 + B * 0.0722).toFloat,
      (R * 0.0193 + G * 0.1192 + B * 0.9505).toFloat,
      c.argb
    )
  }

  @JSExport implicit def toRgba (xyz: XYZ): RGBA = {
    RGBA(
      strikeXyz(xyz.X * 3.24065 + xyz.Y * -1.5372 + xyz.Z * -0.4986),
      strikeXyz(xyz.X * -0.9689 + xyz.Y * 1.87585 + xyz.Z * 0.04155),
      strikeXyz(xyz.X * 0.05575 + xyz.Y * -0.2040 + xyz.Z * 1.0570)
    )
  }

  @JSExport implicit def toXyz(lab: LAB): XYZ = {
    val labY = (lab.L + 16.0) / 116.0

    SlowSlimXYZ(
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

    FastFatLab(
      ((116.0 * labY) - 16.0).toFloat,
      (500.0 * (labX - labY)).toFloat,
      (200.0 * (labY - labZ)).toFloat,
      c.argb
    )
  }

  // Observer = 2 degrees, Illuminant = D65
  // ref_U and ref_V are constants used to convert between LUV and XYZ.
  private val ref_U: Double = ( 4.0 * 95.047 ) / ( 95.047 + ( 15 * 100.0 ) + ( 3 * 108.883 ) )
  private val ref_V: Double = ( 9.0 * 100.0 ) / ( 95.047 + ( 15 * 100.0 ) + ( 3 * 108.883 ) )

  @JSExport implicit def toLuv (xyz: XYZ): LUV = {
    val U: Double = ( 4.0 * xyz.X ) / ( xyz.X + ( 15.0 * xyz.Y ) + ( 3.0 * xyz.Z ) )
    val V: Double = ( 9.0 * xyz.Y ) / ( xyz.X + ( 15.0 * xyz.Y ) + ( 3.0 * xyz.Z ) )

    var y0: Double = xyz.Y / 100
    if ( y0 > 0.008856 ) y0 = Math.pow(y0, 1.0 / 3.0).toFloat
    else y0 = ( 7.787 * y0 ) + ( 16.0 / 116.0 )

    val L: Float = (( 116.0 * y0 ) - 16.0).toFloat
    val u: Float = (13.0 * L * ( U - ref_U )).toFloat
    val v: Float = (13.0 * L * ( V - ref_V )).toFloat

    FastFatLuv(L, u, v, xyz.argb)
  }

  @JSExport implicit def toXyz (luv: LUV): XYZ = {
    var y0: Double = ( luv.L + 16.0 ) / 116.0
    val powY0 = Math.pow(y0, 3.0)
    if ( powY0 > 0.008856 ) y0 = powY0
    else y0 = ( y0 - 16.0 / 116.0 ) / 7.787

    val var_U: Double = luv.u / ( 13.0 * luv.L ) + ref_U
    val var_V: Double = luv.v / ( 13.0 * luv.L ) + ref_V

    val Y: Float = (y0 * 100.0).toFloat
    val X: Float = -(( 9.0 * Y * var_U ) / ( ( var_U - 4.0 ) * var_V  - var_U * var_V )).toFloat
    val Z: Float = (( 9.0 * Y - ( 15.0 * var_V * Y ) - ( var_V * X ) ) / ( 3.0 * var_V )).toFloat

    SlowSlimXYZ(X, Y, Z)
  }

  @JSExport implicit def toLuv(c: Color): LUV = toLuv(toXyz(c))

  @JSExport implicit def toRgba(luv: LUV): RGBA = toXyz(luv)

  // xyz & lab conversion convenience methods:
  private def prepXyz(u: Int): Double = {
    if (u > 10) Math.pow(u * 0.003717126661090977 + 0.05213270142180095, 2.4) * 100.0
    else u * 0.03035269835488375
  }

  private def strikeXyz(v: Double): Int = {
    val u0 = (Math.pow(v * 0.01, 0.4166666666666667) - 0.05213270142180095) * 269.025
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
  /**
   * Overlays colors based on their alpha transparencies.
   * @param c1 the bottom color.
   * @param c2 the top color, overlaid on top of c1.
   * @return the color resulting from the overlay of c2 on top of c1.
   */
  @JSExport def alphaBlend(c1: RGBA, c2: RGBA): RGBA = {
    if (c1.alpha >= 255) c1.argb
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

  /**
   * Computes a weighted average of two colors in RGBA color space.
   * @param c1 the first color.
   * @param w1 the weight of the first color in the range of [0-1].
   * @param c2 the second color.
   * @param w2 the weight of the second color in the range of [0-1].
   * @return the weighted average: c1 * w1 + c2 * w2.
   */
  @JSExport def weightedAverage(c1: RGBA, w1: Float, c2: RGBA, w2: Float): RGBA = {
    RGBA(
      (w1 * c1.red + w2 * c2.red).toInt,
      (w1 * c1.green + w2 * c2.green).toInt,
      (w1 * c1.blue + w2 * c2.blue).toInt,
      Math.max(0, Math.min(255, (w1 * c1.alpha +  w2 * c2.alpha).toInt))
    )
  }

  // rgb component validation
  /**
   * convenience method to validate potential red, green, blue, and alpha intensities.
   * @param intensity the intensity with the potential to encode a red, green, blue, or alpha component in RGBA color space.  Valid values range from [0-255].
   * @return true if the component parameter lies within the range: [0-255], false if not.
   */
  def validRgbaIntensity(intensity: Int): Boolean = intensity >= 0 && intensity < 256
  private def validPercentage(percentage: Float): Boolean = percentage >= 0f && percentage <= 100f
  private def validHue(hue: Float): Boolean = hue >= 0f && hue <= 360f
  private def validWeight(weight: Float): Boolean = weight >= 0f && weight <= 1f

  // factory methods for the javascript library:
  /**
   * Factory method to create an RGBA color.  This method validates the parameters and throws
   * @param red integer value from [0-255] representing the red component in RGB space.
   * @param green integer value from [0-255] representing the green component in RGB space.
   * @param blue integer value from [0-255] representing the blue component in RGB space.
   * @param alpha optional integer value from [0-255] representing the alpha component in RGBA space.  Defaults to 255.
   * @return an instance of the RGBA class.
   * @throws ColorComponentOutOfRangeException if one or more of the parameters lies outside of the range [0-255]
   */
  @JSExport("RGBA") def rgba(red: Int, green: Int, blue: Int, alpha: Int = 255): RGBA = {
    if (!validRgbaIntensity(red)) throw ColorComponentOutOfRangeException(f"Red $red outside range [0-255]")
    if (!validRgbaIntensity(green)) throw ColorComponentOutOfRangeException(f"Green $green outside range [0-255]")
    if (!validRgbaIntensity(blue)) throw ColorComponentOutOfRangeException(f"Blue $blue outside range [0-255]")
    if (!validRgbaIntensity(alpha)) throw ColorComponentOutOfRangeException(f"Alpha $alpha outside range [0-255]")

    RGBA(red<<16|green<<8|blue|(alpha<<24))
  }

  /**
   * Factory method for creating instances of the HSV class.  This method validates input parameters and throws an exception
   * if one or more of them lie outside of their allowed ranges.
   *
   * @param saturation an angle ranging from [0-360] degrees.
   * @param hue a percentage ranging from [0-100].
   * @param value a percentage ranging from [0-100].
   * @return an instance of the HSV case class.
   * @throws ColorComponentOutOfRangeException if one or more of the parameters lies outside of their allowed ranges.
   */
  @JSExport("HSV") def hsv(hue: Float, saturation: Float, value: Float): HSV = {
    if (!validHue(hue)) throw ColorComponentOutOfRangeException(f"Hue $hue outside range [0-360]")
    if (!validPercentage(saturation)) throw ColorComponentOutOfRangeException(f"Saturation $saturation outside range [0-100]")
    if (!validPercentage(value)) throw ColorComponentOutOfRangeException(f"Value $value outside range [0-100]")

    HSV(hue, saturation, value)
  }

  /**
   * Factory method for creating instances of the HSL class.  This method validates input parameters and throws an exception
   * if one or more of them lie outside of their allowed ranges.
   *
   * @param saturation an angle ranging from [0-360] degrees.
   * @param hue a percentage ranging from [0-100].
   * @param lightness a percentage ranging from [0-100].
   * @return an instance of the HSL case class.
   * @throws ColorComponentOutOfRangeException if one or more of the parameters lies outside of their allowed ranges.
   */
  @JSExport("HSL") def hsl(hue: Float, saturation: Float, lightness: Float): HSL = {
    if (!validHue(hue)) throw ColorComponentOutOfRangeException(f"Hue $hue outside range [0-360]")
    if (!validPercentage(saturation)) throw ColorComponentOutOfRangeException(f"Saturation $saturation outside range [0-100]")
    if (!validPercentage(lightness)) throw ColorComponentOutOfRangeException(f"Lightness $lightness outside range [0-100]")
    HSL(hue, saturation, lightness)
  }

  /**
   * Factory method for creating instances of the CMYK class.  This method validates input parameters and throws an exception
   * if one or more of them lie outside of their allowed ranges.
   *
   * @param cyan a value between [0-1]
   * @param magenta a value between [0-1]
   * @param yellow a value between [0-1]
   * @param black a value between [0-1]
   * @return an instance of the CMYK class.
   * @throws ColorComponentOutOfRangeException if one or more of the parameters lies outside of their allowed ranges.
   */
  @JSExport("CMYK") def cmyk(cyan: Float, magenta: Float, yellow: Float, black: Float): CMYK = {
    if (!validWeight(cyan)) throw ColorComponentOutOfRangeException(f"Cyan $cyan outside range [0-1]")
    if (!validWeight(magenta)) throw ColorComponentOutOfRangeException(f"Magenta $magenta outside range [0-1]")
    if (!validWeight(yellow)) throw ColorComponentOutOfRangeException(f"Yellow $yellow outside range [0-1]")
    if (!validWeight(black)) throw ColorComponentOutOfRangeException(f"Black $black outside range [0-1]")
    CMYK(cyan, magenta, yellow, black)
  }

  /**
   * Factory method to create instances of the XYZ class.  This method does not validate its input parameters.
   *
   * @param x the X component of the XYZ color.
   * @param y the Y component of the XYZ color.
   * @param z the Z component of the XYZ color.
   * @return an instance of the XYZ case class.
   * @example {{{ val c = Color.xyz(22.527,38.820,26.728) }}}
   */
  @JSExport("XYZ") def xyz(x: Float, y: Float, z: Float): XYZ = XYZ(x, y, z)

  /**
   * Factory method to create instances of the LAB class.  This method does not validate its input parameters.
   *
   * @param l the L* component of the CIE L*a*b* color.
   * @param a the a* component of the CIE L*a*b* color.
   * @param b the b* component of the CIE L*a*b* color.
   * @return an instance of the SlowSlimLab case class.
   * @example {{{ val c = Color.lab(72.872, -0.531, 71.770) }}}
   */
  @JSExport("LAB") def lab(l: Float, a: Float, b: Float): LAB = SlowSlimLab(l, a, b)

  /**
   * Factory method to create instances of the LUV class.  This method does not validate its input parameters.
   *
   * @param l the L* component of the CIE L*u*v* color.
   * @param u the u* component of the CIE L*u*v* color.
   * @param v the v* component of the CIE L*u*v* color.
   * @return an instance of the SlowSlimLuv case class.
   * @example {{{ val c = Color.luv(14.756, -3.756, -58.528) }}}
   */
  @JSExport("LUV") def luv(l: Float, u: Float, v: Float): LUV = SlowSlimLuv(l, u, v)

}

case class ColorComponentOutOfRangeException(message: String) extends Exception(message)
