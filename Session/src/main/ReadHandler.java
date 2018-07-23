package main;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ReadHandler extends ChannelInboundHandlerAdapter{
	
	private static Packet handler;
	
	public static void setHandler(Packet packetHandler) {
		handler = packetHandler;
	}
	
	@Override
	  public void channelActive(ChannelHandlerContext ctx){
	    // 소켓 채널이 최초 활성화 되었을때 실행
		System.out.println("called!");
	  }
	
	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg)  {
	    	String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());
	    	//Netty를 통해 송수신되는 모든 데이터는 ByteBuf형을 사용함 
	    	
	    	System.out.println("수신한 문자열 ["+readMessage +"]");
	    	int msgNum = Integer.parseInt(readMessage.substring(0, 2));
	    	System.out.println(msgNum);
	     
	    	handler.tcpRead(msgNum,ctx,readMessage.substring(2));
	    	//발신자에게 응답을 하고 싶은 경우 ctx 객체 사용 (ctx.write())
	    }
	   
	    @Override
	    public void channelReadComplete(ChannelHandlerContext ctx){
	    	ctx.flush();
	    	//채널 파이프 라인에 남은 버퍼를 전송
	    }
	   
	 
	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	    	cause.printStackTrace();
	    	ctx.close();
	    }
}
