package SpringStudy.Chapter2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketServer extends Thread{




    EventLoopGroup workerGroup = new NioEventLoopGroup();

    public void start(){
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 120*10)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.SO_LINGER, 0)
                    .childHandler(new WebSocketServerInitializer());
            Channel ch = b.bind(3333).sync().channel();
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    public void shutdown(){
        this.workerGroup.shutdownGracefully();
    }
    public int returnTest(){
        return 1;
    }
}
