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
	    // ���� ä���� ���� Ȱ��ȭ �Ǿ����� ����
		System.out.println("called!");
	  }
	
	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg)  {
	    	String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());
	    	//Netty�� ���� �ۼ��ŵǴ� ��� �����ʹ� ByteBuf���� ����� 
	    	
	    	System.out.println("������ ���ڿ� ["+readMessage +"]");
	    	int msgNum = Integer.parseInt(readMessage.substring(0, 2));
	    	System.out.println(msgNum);
	     
	    	handler.tcpRead(msgNum,ctx,readMessage.substring(2));
	    	//�߽��ڿ��� ������ �ϰ� ���� ��� ctx ��ü ��� (ctx.write())
	    }
	   
	    @Override
	    public void channelReadComplete(ChannelHandlerContext ctx){
	    	ctx.flush();
	    	//ä�� ������ ���ο� ���� ���۸� ����
	    }
	   
	 
	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	    	cause.printStackTrace();
	    	ctx.close();
	    }
}
