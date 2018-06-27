package tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import main.*;
import redis.clients.jedis.JedisPool;

public abstract class TcpMsgRoot {
	protected static JedisPool jedisPool;
	protected static ResourceManager rmHandler;
	protected int index = -1;
	protected String index_str;
	
	public TcpMsgRoot(int index) {
		this.index = index;
		this.index_str = ""+index;
		if(index<10) this.index_str = "0"+this.index_str;
	}
	
	public abstract int write(String... str);
	public abstract int read(ChannelHandlerContext ctx, String str);
	
	public int getIndex() {
		return index;
	}
	
	public static void setRedis(JedisPool jedisPool) {
		TcpMsgRoot.jedisPool = jedisPool;
	}
	
	public static void setPacket(ResourceManager handler) {
		TcpMsgRoot.rmHandler = handler;
	}
	
	//소켓에 메시지를 보내기 위한 랩핑함수
	protected int tcpWrite(Channel ctx, String msg) {
		//netty는 1024크기단위로 패킷을 전송하기때문에, 패킷 크기가 1024가 넘는 경우 패킷을 나누고 헤더를 각각 달아서 보내야함
		//메시지넘버 크기는 현재 2로 고정 (0~99까지 100종류의 메시지 사용 가능)
		if(msg.length() > 1022) {
			System.err.println("tcpWrite : packet size exceed 1024");
			return -1;
		}
		
		//netty는 ByteBuf 형태로만 메시지를 주고받을 수 있기 때문에, String을 ByteBuf로 변환해야함
		ByteBuf buf = Unpooled.copiedBuffer((index_str+msg).getBytes(CharsetUtil.UTF_8));
		
		try {
			//현재 연결된 소켓에 패킷을 쓰고 Flush하는 함수. 동작이 완료될때까지 쓰레드가 블록됨
			ctx.writeAndFlush(buf).sync();
		} catch (InterruptedException e) {
			//쓰레드가 블록된 중에 인터럽트때문에 강제로 블록이 해제되었을때
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}
}