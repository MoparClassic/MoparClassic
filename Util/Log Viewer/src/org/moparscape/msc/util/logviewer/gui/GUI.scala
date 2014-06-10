package org.moparscape.msc.util.logviewer.gui

import io.netty.channel.ChannelHandlerContext

class GUI(ctx: ChannelHandlerContext) {
	ctx.writeAndFlush("4 login")
	
	// TODO: Implement this
}