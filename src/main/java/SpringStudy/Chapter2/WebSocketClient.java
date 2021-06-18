package SpringStudy.Chapter2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class WebSocketClient {

    EventLoopGroup group = new NioEventLoopGroup();
    WebSocketClientHandler handler;
    public void clientStart(){
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            URI uri = new URI("ws://127.0.0.1/study");
            handler = new WebSocketClientHandler(WebSocketClientHandshakerFactory
                    .newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));

            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 120*2000)
                    .remoteAddress(new InetSocketAddress("127.0.0.1", 5550))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192),
                                    WebSocketClientCompressionHandler.INSTANCE, handler);
                        }
                    });
            try {
                ChannelFuture ch = b.connect("127.0.0.1", 3333).sync();
                ch.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean connection(){
        return handler.connection();
    }
}
