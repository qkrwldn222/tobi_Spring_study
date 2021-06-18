package SpringStudy;

import SpringStudy.Chapter1.User;
import SpringStudy.Chapter2.WebSocketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest()
public class StudyApplicationTest {

    @BeforeEach
    public void testStart(){

    }


    @Test
    public void test(){
        User user1 = new User();
        user1.setName("지우");
        user1.setPassword("1234");
        user1.setId("qkrwldn");
        User user2 = user1;

        assertThat(user1.getId()).isEqualTo(user2.getId());
        assertThat(user1.getId(), is(user2.getId()));
    }
}