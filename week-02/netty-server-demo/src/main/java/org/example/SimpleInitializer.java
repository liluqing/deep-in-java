/**
 * fshows.com
 * Copyright (C) 2013-2021 All Rights Reserved.
 */
package
        org.example;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 *
 * @author liluqing
 * @version SimpleInitializer.java, v 0.1 2021-01-02 10:57
 */
public class SimpleInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new LineBasedFrameDecoder(1024 * 1024));
        p.addLast(new SimpleHandle());
    }
}