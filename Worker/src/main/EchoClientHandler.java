package main;
import java.nio.charset.Charset;
 
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
 
public class EchoClientHandler extends ChannelInboundHandlerAdapter  {
  @Override
  public void channelActive(ChannelHandlerContext ctx){
    // 소켓 채널이 최초 활성화 되었을때 실행
     
    String sendMessage = "Hello, Netty!";
     
    ByteBuf messageBuffer = Unpooled.buffer();
    messageBuffer.writeBytes(sendMessage.getBytes());
     
    StringBuilder builder = new StringBuilder();
    builder.append("전송한 문자열 [");
    builder.append(sendMessage);
    builder.append("]");
     
    System.out.println(builder.toString());
    ctx.writeAndFlush(messageBuffer);
    //2.writeAndFlush()은 내부적으로 기록과 전송의 두가지 메서드 호출( write(). flush()
    //2.1 write() : 채널에 데이터를 기록
    //2.2 flush() : 채널에 기록된 데이터를 서버로 전송
     
  }
 
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg){
    //3.서버로부터 수신된 데이터가 있을 때 호출되는 메서드
     
    String readMessage = ((ByteBuf)msg).toString(Charset.defaultCharset());
    //4.서버로 부터 수신된 데이터가 저장된 msg 객체에서 문자열 데이터 추출
     
    StringBuilder builder = new StringBuilder();
    builder.append("수신한 문자열 [");
    builder.append(readMessage);
    builder.append("]");
     
    System.out.println(builder.toString());
     
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