This cross-published Scala.js library supports conversions between color spaces in both Scala and Javascript.

From javascript:<br />
Create new colors in RGB space:<br />
<pre>
var rgba0 = Color().RGBA(155, 64, 100);  // opaque color
var rgba1 = Color().RGBA(255, 255, 255, 128);  // alpha channel
var rgba2 = Color().random();  // random rgba color
</pre>
Blend two colors according to their alpha channels:<br />
<pre>
var blended = Color().alphaBlend(rgba1, rgba0);
blended.toString() // returns "RGBA(155,64,100,255)"
</pre>
Compute a weighted average between two colors in RGB space:<br />
<pre>
var weightedAvg = Color().weightedAverage(rgba0, 0.25, rgba1, 0.75);
weightedAvg.toString(); // returns "RGBA(230,207,216,159)"
</pre>
Compute euclidean distance between two colors in RGB Space:<br />
<pre>
var distance = rgba0.distanceTo(rgba1);  // sets distance to 265.52965936030574
</pre>
Validate input values: <br />
<pre>
try {
  var rgba2 = Color().RGBA(300, 158, 33);  // throws ai.dragonfly.color.ColorComponentOutOfRangeException
} catch (e) {
  console.log(e);  // prints: "ai.dragonfly.color.ColorComponentOutOfRangeException: Red 300 outside range [0-255]"
}
</pre>
Easily format colors into useful strings for CSS, HTML, and SVG:<br />
<pre>
Color().RGBA(155, 64, 100).html() // returns '#9b4064'

Color().RGBA(155, 64, 100).svg()  // returns 'rgb(155,64,100)'
Color().RGBA(255, 255, 255, 128).svg()  // returns 'rgba(255,255,255,0.501960813999176)'
Color().HSL(22.208,62.097,51.373).svg()  // returns 'hsl(22.208,62.1%,51.4%)'

Color().LAB(26.833,11.700,0.500).hex()  // returns 'ff51393f'
</pre>
<hr />
Create colors in other spaces: <br />
<pre>
var hsv0 = Color().HSV(180, 20, 75);
var hsl0 = Color().HSL(180, 20, 75);
var cmyk0 = Color().CMYK(0.2, 0.8, 0.5, 0.1);
var xyz0 = Color().XYZ(28.586,42.301,55.374);
var lab0 = Color().LAB(86.947,-44.123,2.272);
var luv0 = Color().LUV(86.947,-44.123,2.272);
</pre>
Convert colors between spaces: <br />
<pre>
Color().toLab(hsv0).toString() // returns 'LAB(74.696,-12.612,-4.198)'
Color().toLuv(lab0).toString() // returns 'LUV(86.947,-56.998,10.683)'
Color().toInt(hsv0) // returns-6701121
Color().toRgba(xyz0).toString()  // returns 'RGBA(0,194,190,255)'
Color().toCmyk(hsl0).toString()  // returns 'CMYK(0.123,0.000,0.000,0.200)'
Color().toXyz(cmyk0).toString()  // returns 'XYZ(23.839,13.382,17.546)'
</pre>
