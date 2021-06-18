package SpringStudy.Chapter2;


import com.fasterxml.jackson.databind.type.TypeParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    StringBuffer frameBuffer = null;


    StudierDao studierDao;
    private JSONParser jsonParser;

    public WebSocketFrameHandler(){
        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(StudierDao.class);
        this.studierDao  = ctx.getBean("studierDao",StudierDao.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof TextWebSocketFrame) {
            if (frameBuffer == null) {
                frameBuffer = new StringBuffer();
                frameBuffer.append(((TextWebSocketFrame) frame).text());
            }

        }
        if (frame.isFinalFragment()) {
            handleMessageComplete(ctx, frameBuffer.toString());
            frameBuffer = null;
        }
    }

    protected void handleMessageComplete(ChannelHandlerContext ctx,String api) throws ParseException {
        jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) this.jsonParser.parse(api);
        String type= jsonObject.get("type").toString();
        if (type.equals("add")){

        }
    }
    protected void studierAdd(){

    }
}
