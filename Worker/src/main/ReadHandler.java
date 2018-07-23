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
		handler.tcpWrite(Packet.TcpRequestIndex);
	  }
	 
	  @Override
	  public void channelRead(ChannelHandlerContext ctx, Object msg){
		  String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());
	    	//Netty를 통해 송수신되는 모든 데이터는 ByteBuf형을 사용함 
	    	
	    	System.out.println("수신한 문자열 ["+readMessage +"]");
	    	int msgNum = Integer.parseInt(readMessage.substring(0, 2));
	    	System.out.println(msgNum);
	     
	    	handler.tcpRead(msgNum, ctx, readMessage.substring(2));
	    	//발신자에게 응답을 하고 싶은 경우 ctx 객체 사용 (ctx.write())
	     
	  }
	   
	  @Override
	  public void channelReadComplete(ChannelHandlerContext ctx){
	    //5.수신된 데이터를 모두 읽었을때 호출되는 이벤트 메서드
	    ctx.close();//6.서버와 연결된 채널을 닫음
	    //6.1 이후 데이터 송수신 채널은 닫히게 되고 클라이언트 프로그램은 종료됨
	  }
	   
	  @Override
	  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
	    cause.printStackTrace();
	    ctx.close();
	  }
}
