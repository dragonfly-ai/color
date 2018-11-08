package ai.dragonfly.color.experiments

import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import ai.dragonfly.color.Color
import ai.dragonfly.color.stats.LabSpace

/**
 * Created by clifton on 1/6/17.
 */

object TestLabSampleSpace extends App {

  val bi = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB)
  val bi1 = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB)

  for (y <- 0 until bi.getHeight) {
    for (x <- 0 until bi.getWidth) {
      bi.setRGB(x, y, LabSpace())
      bi1.setRGB(x, y, Color.random())
    }
  }

  val tmp = System.getProperty("java.io.tmpdir")
  println(tmp)
  val timestamp = System.currentTimeMillis()
  ImageIO.write(bi, "PNG", new File(tmp+"/randomImage"+timestamp+"LabSampleSpace.png"))
  ImageIO.write(bi1, "PNG", new File(tmp+"/randomImage"+timestamp+"RgbSampleSpace.png"))
}