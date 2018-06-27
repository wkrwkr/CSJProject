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
	
	//���Ͽ� �޽����� ������ ���� �����Լ�
	protected int tcpWrite(Channel ctx, String msg) {
		//netty�� 1024ũ������� ��Ŷ�� �����ϱ⶧����, ��Ŷ ũ�Ⱑ 1024�� �Ѵ� ��� ��Ŷ�� ������ ����� ���� �޾Ƽ� ��������
		//�޽����ѹ� ũ��� ���� 2�� ���� (0~99���� 100������ �޽��� ��� ����)
		if(msg.length() > 1022) {
			System.err.println("tcpWrite : packet size exceed 1024");
			return -1;
		}
		
		//netty�� ByteBuf ���·θ� �޽����� �ְ���� �� �ֱ� ������, String�� ByteBuf�� ��ȯ�ؾ���
		ByteBuf buf = Unpooled.copiedBuffer((index_str+msg).getBytes(CharsetUtil.UTF_8));
		
		try {
			//���� ����� ���Ͽ� ��Ŷ�� ���� Flush�ϴ� �Լ�. ������ �Ϸ�ɶ����� �����尡 ��ϵ�
			ctx.writeAndFlush(buf).sync();
		} catch (InterruptedException e) {
			//�����尡 ��ϵ� �߿� ���ͷ�Ʈ������ ������ ����� �����Ǿ�����
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}
}