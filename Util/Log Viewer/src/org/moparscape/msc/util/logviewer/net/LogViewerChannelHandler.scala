package org.moparscape.msc.util.logviewer.net

import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import org.moparscape.msc.util.logviewer.gui.GUI
import java.net.URLDecoder
import org.moparscape.msc.util.logviewer.util.Conversions

class LogViewerChannelHandler extends ChannelInboundHandlerAdapter {
  override def channelActive(ctx: ChannelHandlerContext) {
    val gui = new GUI(ctx)
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: Object) {
    val m = URLDecoder.decode(msg.toString, "UTF-8")
    Integer.parseInt(m.head + "") match {
      case 0 => println("Error")
      case 1 => println("Success")
      case 2 => {
        val msg = m.drop(2).replace(',', ' ')
        var args = msg.split(" ")
        args.sliding(4).foreach {
          e =>
          println(Conversions.hashToUsername(e(0).toLong) + " - " + (e(1) -> e(2)) + " - " + e(3))
        }
      }
      case 3 => {
        val msg = m.drop(2).replace(',', ' ')
        var args = msg.split(" ")
        args(0) match {
          case "login" => {
            args = args.drop(1)
            print(args.mkString("\n").replace("-", " - ").replace("_", " "))
          }
          
          // Implement other log types
        }
      }
      case 4 => {
        println(m.drop(2))
      }
    }
  }

  override def channelReadComplete(ctx: ChannelHandlerContext) {
    ctx.flush
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
    cause.printStackTrace
    ctx.close
  }
}