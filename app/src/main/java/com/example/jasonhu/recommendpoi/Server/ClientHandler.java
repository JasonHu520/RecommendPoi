package com.example.jasonhu.recommendpoi.Server;

import com.example.jasonhu.recommendpoi.Information;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by JasonHu on 19-12-25.
 */

class ClientHandler extends SimpleChannelInboundHandler<Information.Student> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Information.Student msg) throws Exception {
        System.out.println("[Server]: "+msg);
    }
}