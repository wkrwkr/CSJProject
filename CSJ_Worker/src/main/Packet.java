package main;

import tcp.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import io.netty.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.redis.*;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class Packet extends ChannelInboundHandlerAdapter{
	
	public static final int PING = 0;
	public static final int SET_SESSION = 1;
	public static final int GET_SESSION_IP = 2;
	public static final int GET_SESSION_NUM = 3;
	public static final int CONN_SESSION = 4;
	public static final int DB_WRITE = 5;
	private static final int TCP_SZ = 100;
	
	private static final String version = "0.01";
	
	///////////////configure 파일 생성 이후 삭제////////////////////
	private static final String IP_DB = "127.0.0.1";
	private static final int PORT_DB = 6379;
	private static final int MSG_SZ = 256;
	private static final int PORT = 9190;
	private String hostIP = "127.0.0.1";
	///////////////configure 파일 생성 이후 삭제////////////////////
	
	private JedisPool jedisPool = null;
	
	private int PORT_Session;
	private String IP_Session;
	private TcpMsgRoot[] tcpList;
	
	private TaskExecutor teHandler;

	private EventLoopGroup group;
	private ChannelFuture cf;
	private Bootstrap b;
	
	private int session_num;
	private int idx;
	
	public Packet(TaskExecutor teHandler) {
		
		
		this.teHandler = teHandler;
		
		jedisInit();
		listInit();
		
		
	}

	public int sockInit(ChannelHandler ci) {
		int hostIP_Integer;
		group = new NioEventLoopGroup();
		
		b = new Bootstrap();
		b.group(group)
		 .channel(NioSocketChannel.class)
		//b.option(ChannelOption.TCP_NODELAY, true);
		 .handler(ci);

		session_num = tcpWrite(GET_SESSION_NUM);
		hostIP_Integer = tcpWrite(GET_SESSION_IP,""+session_num);
		hostIP = String.format("%d.%d.%d.%d",
				(hostIP_Integer & 0xff),
				(hostIP_Integer >> 8 & 0xff),
				(hostIP_Integer >> 16 & 0xff),
				(hostIP_Integer >> 24 & 0xff));
		try {
			cf = b.connect(hostIP, PORT).sync();
			TcpMsgRoot.setSock(cf);
			System.out.println("Connected.");
			
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		////////////////////////////////////////////////////
		
		
		return 1;
	}
	
	private int jedisInit() {
		Jedis jedis = null;
		int tries = 0;
		JedisPoolConfig jpConfig = new JedisPoolConfig();
		
		do { //DB서버에 연결. 실패시, 일정간격으로 연결 재시도
			tries++;
			System.out.println("Establish Connection to Redis DB Server : "+ tries + " times.");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jedisPool = new JedisPool(jpConfig, IP_DB, PORT_DB);
			try {
				jedis = jedisPool.getResource();
			} catch (JedisConnectionException e) {
				jedisPool = null;
			}
		} while(jedisPool == null);
		
		jedis.close();
		
		TcpMsgRoot.setRedis(jedisPool);
		
		System.out.println("Connection with Redis DB Server is Established Successfully. : " + jedisPool);
		
		return 1;
	}
	
	private int listInit() {
		tcpList = new TcpMsgRoot[TCP_SZ];
		
		register(new TcpGetSessionNum(GET_SESSION_NUM));
		register(new TcpGetSessionIP(GET_SESSION_IP));
		register(new TcpConnectSession(CONN_SESSION));
		
		return 1;
	}

	public int tcpWrite(int msgType, String... str) {
		return tcpList[msgType].write(str);
	}
/*
	public int tcpRead(LoadBalancer lb, SocketChannel sock) {
		//tcp 소켓을 읽는 경우에는, 상대방이 메시지를 보내지 않은 경우까지 대비하여 타임아웃을 두어야함
		int msgType = -1;
		ByteBuffer bufByteBuffer = ByteBuffer.allocate(4);
		try {
			sock.read(bufByteBuffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bufByteBuffer.putInt(msgType);
		return tcpList[msgType].read(lb, this);
	}
*/
	private int register(TcpMsgRoot newClass) {
		int msgType = newClass.getIndex();
		if(msgType < 0) System.err.println("Tcp List Registration Error");
		
		tcpList[msgType] = newClass;
		return 1;
	}

	public void sockTerminate() {
		try {
			
			//TODO 위 주석을 수정해서 Worker의 정상종료를 알리는 메시지를 보내야함. 혹은 tcpWrite 활용
			cf.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //closeFuture의 리턴값으로 sync를 호출하면, 소켓이 닫힐때까지 쓰레드가 블록됨
	}
	
	
	/*
	public static int[] readHeader(SocketChannel sock) {
		ByteBuffer buf = ByteBuffer.allocate(100); 
		String header;
		String version;
		int msgNum;
		int off = 0;
		int len;
		int[] bodylen = new int[2];
		
		try {
			//읽을게 없는 경우 에러 리턴
			if( sock.read(buf) <= 0) return null;
			
			//CSJ 헤더가 아닌 경우 에러 리턴
			header = StandardCharsets.UTF_8.decode(buf).toString();
			System.out.println("header : " + header + ".");
			if(header.substring(0, 3).equals("CSJ") == false)
			{
				System.out.println("false : "+header.substring(0, 3));
				return null;
			}
			
			//버전정보를 읽음
			len = Integer.parseInt(header.substring(3, 4));
			version = header.substring(4, 4+len);
			off = 4+len;
			System.out.println("Master Version : "+Packet.version);
			System.out.println("Worker Version : "+version);
			
			//메시지 타입을 읽음
			msgNum = Integer.parseInt(header.substring(off, off+1));
			off++;
			
			switch(msgNum) {
			case PING :
				//ping 메시지
				break;
			
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bodylen;
	}
	
	public static String readName(InputStream in, int length) {
		
		return "";
	}
	
	//패킷에서 파일 정보를 읽고 저장
	//length가 0인  경우 이미 파일이 존재한다는 전제.
	//length가 0인데 존재하지 않는 파일이면 오류처리
	public static int readFile(SocketChannel sock, String filename, int length) {
		
		return 0;
	}
	
	public static int readIdx(SocketChannel sock, int length) {
		ByteBuffer buf[] = new ByteBuffer[1];
		buf[0] = ByteBuffer.allocate(10);
		int result = 0;
		
		try {
			do {
				result += sock.read(buf,0,10);
			} while(length > result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = Integer.parseInt(StandardCharsets.UTF_8.decode(buf[0]).toString());
		System.out.println("node index : "+result);
		return result;
	}
	
	public static int pingPacket(SocketChannel sock) {
		writeHeader(sock, PING, null);
		return 0;
	}
	
	public static int sendFile(OutputStream out, String path) {
	
		return 0;
	}
	
	public static int connPacket(SocketChannel sock, int idx) {
		int[] length = new int[2];
		ByteBuffer buf;
		
		if(idx < 0)
			length[0] = 0;
		else
			length[0] = (""+idx).length();
		writeHeader(sock, CONN, length);
		
		buf = ByteBuffer.allocate(length[0]);
		buf.put((""+idx).getBytes());
		try {
			sock.write(buf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	private static int writeHeader(SocketChannel sock, int msgNum, int[] length) {
		ByteBuffer buf = ByteBuffer.allocate(100);
		String header = new String("");
		
		header += "CSJ";
		
		header += Packet.version.length();
		header += Packet.version;
		
		header += msgNum;
		
		switch(msgNum) {
		case PING :
			break;
		case SEND :
			header += String.valueOf(length[0]).length();
			header += String.valueOf(length[1]).length();
			header += length[0];
			header += length[1];
			break;
		case CONN :
			header += String.valueOf(length[0]).length();
			header += length[0];
			break;
		}
		
		buf.put(header.getBytes());
		try {
			sock.write(buf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}
	*/
	
	@Override
	  public void channelActive(ChannelHandlerContext ctx){
	    // 소켓 채널이 최초 활성화 되었을때 실행
		tcpWrite(CONN_SESSION);
	  }
	 
	  @Override
	  public void channelRead(ChannelHandlerContext ctx, Object msg){
		  String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());
	    	//Netty를 통해 송수신되는 모든 데이터는 ByteBuf형을 사용함 
	    	
	    	System.out.println("수신한 문자열 ["+readMessage +"]");
	    	int msgNum = Integer.parseInt(readMessage.substring(0, 2));
	    	System.out.println(msgNum);
	     
	    	tcpList[msgNum].read(ctx,readMessage.substring(2));
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
