package SpringStudy.Chapter2;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class PingPongDuplexHandler extends ChannelDuplexHandler {
	

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
            		JSONObject requestJson = new JSONObject();
    				requestJson.put("type", "ping");
    				
            		ctx.writeAndFlush(new TextWebSocketFrame(requestJson.toJSONString())  );  		
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}