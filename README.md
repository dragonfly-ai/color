This cross-published Scala.js library supports conversions between color spaces in both Scala and Javascript.

##Scala:
Please view the
<a href="https://github.com/dragonfly-ai/Color/wiki/Scala-Setup">Setup</a> and
<a href="http://dragonfly.ai/doc/color/2.13/0.201/ai/dragonfly/color/index.html">Documentation</a>
pages.<br />
Demo:<br />
<pre>
import ai.dragonfly.color._
import Color._

// implicit conversions of colors from any color space to any other color space.
val argb: RGBA = RGBA(0xFF23EE8b)
rgba.toString //returns "RGBA(35,238,139,255)"
rgba.hex  // returns "ff23ee8b"
val lab: LAB = rgba
lab.toString  // returns "LAB(83.637,-67.948,35.092)"
val hsv: HSV = lab
hsv.toString  // returns "HSV(150.739,85.294,93.333)"
val cmyk: CMYK = hsv
cmyk.toString  // returns "CMYK(0.853,0.000,0.416,0.067)"
val xyz: XYZ = cmyk
xyz.toString  // returns "XYZ(35.928,63.370,34.764)"
val luv: LUV = xyz
luv.toString  // returns "LUV(83.637,-71.857,59.293)"
val hsl: HSL = luv
hsl.toString  // returns "HSL(150.739,85.654,53.529)"
val argbInt: Int = hsl
integer.toHexString(rgbaInt) // returns "ff23ee8b"
</pre>

##Javascript:
Please view the
<a href="https://github.com/dragonfly-ai/Color/wiki/Javascript-Setup">Setup</a> and 
<a href="https://github.com/dragonfly-ai/Color/wiki/dragonfly.ai-Color-Javascript-Documentation">Documentation</a>
pages.<br />
