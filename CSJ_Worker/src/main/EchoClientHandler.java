package main;
import java.nio.charset.Charset;
 
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
 
public class EchoClientHandler extends ChannelInboundHandlerAdapter  {
  @Override
  public void channelActive(ChannelHandlerContext ctx){
    // ���� ä���� ���� Ȱ��ȭ �Ǿ����� ����
     
    String sendMessage = "Hello, Netty!";
     
    ByteBuf messageBuffer = Unpooled.buffer();
    messageBuffer.writeBytes(sendMessage.getBytes());
     
    StringBuilder builder = new StringBuilder();
    builder.append("������ ���ڿ� [");
    builder.append(sendMessage);
    builder.append("]");
     
    System.out.println(builder.toString());
    ctx.writeAndFlush(messageBuffer);
    //2.writeAndFlush()�� ���������� ��ϰ� ������ �ΰ��� �޼��� ȣ��( write(). flush()
    //2.1 write() : ä�ο� �����͸� ���
    //2.2 flush() : ä�ο� ��ϵ� �����͸� ������ ����
     
  }
 
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg){
    //3.�����κ��� ���ŵ� �����Ͱ� ���� �� ȣ��Ǵ� �޼���
     
    String readMessage = ((ByteBuf)msg).toString(Charset.defaultCharset());
    //4.������ ���� ���ŵ� �����Ͱ� ����� msg ��ü���� ���ڿ� ������ ����
     
    StringBuilder builder = new StringBuilder();
    builder.append("������ ���ڿ� [");
    builder.append(readMessage);
    builder.append("]");
     
    System.out.println(builder.toString());
     
  }
   
  @Override
  public void channelReadComplete(ChannelHandlerContext ctx){
    //5.���ŵ� �����͸� ��� �о����� ȣ��Ǵ� �̺�Ʈ �޼���
    ctx.close();//6.������ ����� ä���� ����
    //6.1 ���� ������ �ۼ��� ä���� ������ �ǰ� Ŭ���̾�Ʈ ���α׷��� �����
  }
   
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
    cause.printStackTrace();
    ctx.close();
  }
 
   
 
}