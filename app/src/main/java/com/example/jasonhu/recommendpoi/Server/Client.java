package com.example.jasonhu.recommendpoi.Server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.jasonhu.recommendpoi.Information;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by JasonHu on 19-1-21.
 */

public class Client extends Service {

    public static String host = "103.46.128.41";

    public static int port = 23371;
    Channel channel;


    public void run(){

        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap  = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
//                            ch.pipeline().addLast(new StringEncoder());
//                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new ProtobufDecoder(Information.Student.getDefaultInstance()));
                            ch.pipeline().addLast(new ProtobufEncoder());
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            ch.pipeline().addLast(new ClientHandler()); //加入自己的处理器
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channelFuture.addListener((ChannelFutureListener) future -> {
                if(future.isSuccess()){
                    channel = channelFuture.channel();
                }
                else{
                    worker.shutdownGracefully();
                    Toast.makeText(this,"服务器异常",Toast.LENGTH_SHORT).show();
                }
                if(future.isCancelled()){
                    Toast.makeText(this,"服务器异常",Toast.LENGTH_SHORT).show();
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void sendMessagetoServer(String message, Context context){
        //发送数据
        if (channel!=null)
            channel.writeAndFlush(message+ "\r\n");
        else
            Toast.makeText(context,"未建立连接",Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}