This cross-published Scala.js library supports conversions between color spaces in both Scala and Javascript.

From javascript:<br />
Create new colors in RGB space:<br />
<pre>
var rgba0 = Color.RGBA(155, 64, 100);  // opaque color
var rgba1 = Color.RGBA(255, 255, 255, 128);  // alpha channel
var rgba2 = Color.random();  // random rgba color
</pre>
Blend two colors according to their alpha channels:<br />
<pre>
var blended = Color.alphaBlend(rgba1, rgba0);
blended.toString() // returns "RGBA(155,64,100,255)"
</pre>
Compute a weighted average between two colors in RGB space:<br />
<pre>
var weightedAvg = Color.weightedAverage(rgba0, 0.25, rgba1, 0.75);
weightedAvg.toString(); // returns "RGBA(230,207,216,159)"
</pre>
Compute euclidean distance between two colors in RGB Space:<br />
<pre>
var distance = rgba0.distanceTo(rgba1);  // sets distance to 265.52965936030574
</pre>
Validate input values: <br />
<pre>
try {
  var rgba2 = Color.RGBA(300, 158, 33);  // throws ai.dragonfly.color.ColorComponentOutOfRangeException
} catch (e) {
  console.log(e);  // prints: "ai.dragonfly.color.ColorComponentOutOfRangeException: Red 300 outside range [0-255]"
}
</pre>
<hr />
Create colors in other spaces: <br />
<pre>
var hsv0 = Color.HSV(180, 20, 75);
var hsl0 = Color.HSL(180, 20, 75);
var cmyk0 = Color.CMYK(0.2, 0.8, 0.5, 0.1);
var xyz0 = Color.XYZ(28.586,42.301,55.374);
var lab0 = Color.LAB(86.947,-44.123,2.272);
var luv0 = Color.LUV(86.947,-44.123,2.272);
</pre>
