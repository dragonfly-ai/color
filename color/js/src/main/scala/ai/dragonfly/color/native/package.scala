package ai.dragonfly.color

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

package object native {

  @JSExportTopLevel("RGBA")
  def RGBA(args:Double*):RGBA = {
    args.length match {
      case 1 => RGBA(args(0).toInt)
      case 3 => Color.rgba(args(0).toInt, args(1).toInt, args(2).toInt)
      case 4 => Color.rgba(args(0).toInt, args(1).toInt, args(2).toInt, args(3).toInt)
      case _ => throw js.JavaScriptException( InvalidArgumentsException( s"required 1, 3, or 4 numbers, received: ${args.length}" ) )
    }
  }
}

case class InvalidArgumentsException(msg:String) extends Exception {
  override def toString: String = msg
}