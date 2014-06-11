package org.moparscape.msc.util.logviewer

import io.netty.channel.nio.NioEventLoopGroup
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.string.StringDecoder
import io.netty.util.CharsetUtil
import io.netty.handler.codec.string.StringEncoder
import org.moparscape.msc.util.logviewer.net.LogViewerChannelHandler

object LogViewer {
  def main(args: Array[String]) {
    val group = new NioEventLoopGroup

    try {
      val b = new Bootstrap
      b.group(group).channel(classOf[NioSocketChannel]).
        handler(new ChannelInitializer[SocketChannel]() {
          @throws(classOf[Exception]) override def initChannel(ch: SocketChannel) {
            val p = ch.pipeline
            p.addLast(new StringDecoder(CharsetUtil.UTF_8), new StringEncoder(CharsetUtil.UTF_8), new LogViewerChannelHandler)
          }
        })

      val f = b.connect("127.0.0.1", 8186).sync
      f.channel.closeFuture.sync
    } finally {
      group.shutdownGracefully()
    }
  }
}