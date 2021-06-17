/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package SpringStudy.Chapter2;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.timeout.IdleStateHandler;


/**
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

	private static final String WEBSOCKET_PATH = "/drone";

	public WebSocketServerInitializer() {
		
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		
		
		pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new HttpObjectAggregator(65536));
		pipeline.addLast(new WebSocketServerCompressionHandler());
		
		
		pipeline.addLast(new ByteToMessageDecoder() {
			@Override
			protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) { // (2)
		        if (in.readableBytes() < 4) {
		            return; 
		        }
		        out.add(in.readBytes(4)); // (4)
		    }
		});
		pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));


		pipeline.addLast("idleStateHandler", new IdleStateHandler(90, 60, 0));
		pipeline.addLast("myHandler", new PingPongDuplexHandler());

		pipeline.addLast(new WebSocketFrameHandler());
	}
}
