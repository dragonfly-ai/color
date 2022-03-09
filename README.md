This cross-published Scala.js library supports conversions between color spaces in both Scala and Javascript.

In addition to support for common color spaces: RGB, RGBA, HSV, HSL, and CYMK, this library
also provides convenient access to color spaces designed around human color
perception research, such as XYZ, L* a* b*, and L* u* v*.   

Its most unique feature provides random sampling of colors from the L* a* b* color space.

In many situations, we might need to generate a random color.  Generally, developers simply combine three
8-bit integers corresponding to the red, green, and blue values of the 24 bit, RGB color space.  While
easy to implement, this method suffers from the weaknesses implicit in the RGB color cube, most notably
that common similarity measures, usually some derivative of 3D Euclidian distance in RGB space, reflect
electromagnetic properties like frequency and intensity.  Unfortunately, human color perception has
highly non linear properties compared to the measurable properties of light.

On average, human vision systems perceive random samples from RGB to over represent shades of green, blue, and
magenta, leaving yellow, orange, and red relatively less expressed.  When we look at an image comprised of pixels
with independently randomized colors, we might expect to see an image resembling a scramble picture of a rainbow.
However, when sampling from RGB space, most people experience such images as biased toward cool colors.

![Image of Randomized Colors Sampled from RGB Space.](https://github.com/dragonfly-ai/color/blob/master/RGB.png "Image of Randomized Colors Sampled from RGB Space.")

By contrast, by sampling from L* a* b* color space, most people experience this kind of image as a scrambled rainbow.
![Image of Randomized Colors Sampled from L* a* b* Space.](https://github.com/dragonfly-ai/color/blob/master/L*a*b*.png "Image of Randomized Colors Sampled from L* a* b* Space.")

On a more practical basis, we often rely on random colors to distinguish two separate features in various data visualizations.
Sampling from RGB space is more likely to yield colors that differ significantly in intensity or frequency, but look the same
to the people looking at them.  By uniformly sampling from L* a* b* color space instead, we increase the likelihood that
any two colors look different from each other to the people who look at them.

##Scala:
Please view the
<a href="https://github.com/dragonfly-ai/Color/wiki/Scala-Setup">Setup</a> and
<a href="http://dragonfly.ai/doc/color/2.13/0.202/ai/dragonfly/color/index.html">Documentation</a> pages.<br />

Demo:<br />
<pre>
import ai.dragonfly.color.*
import Color.*

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
