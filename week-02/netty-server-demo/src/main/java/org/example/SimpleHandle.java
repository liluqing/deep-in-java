/**
 * fshows.com
 * Copyright (C) 2013-2021 All Rights Reserved.
 */
package
        org.example;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author liluqing
 * @version SimpleHandle.java, v 0.1 2021-01-02 11:04
 */
public class SimpleHandle extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        String readStr = in.toString(CharsetUtil.US_ASCII);
        System.out.println("会话:" +ctx.channel().id()+ ";客户端请求报文：" + readStr);
        ctx.writeAndFlush(Unpooled.copiedBuffer("yes\r\n".getBytes()));
    }
}