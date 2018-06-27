package main;


import tcp.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import io.netty.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
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
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.redis.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
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
	public static final int TCP_SZ = 100;
	
	private static final String version = "0.01";
	
	///////////////configure 파일 생성 이후 삭제////////////////////
	private static final int PORT = 9190;
	private static final String IP_DB = "127.0.0.1";
	private static final int PORT_DB = 6379;
	private static final int MSG_SZ = 256;
	///////////////configure 파일 생성 이후 삭제////////////////////
	
	private int session_index;
	private JedisPool jedisPool = null;
	
	private TcpMsgRoot[] tcpList;
	
	private ResourceManager rmHandler;
	
	private EventLoopGroup pGroup;
	private EventLoopGroup cGroup;
	private ServerBootstrap sb;
	
	public Packet(ResourceManager rm) {
		super();
		this.rmHandler = rm;
		
		jedisInit();
		listInit();
		
		session_index = tcpWrite(GET_SESSION_NUM);
		tcpWrite(DB_WRITE, "Session"+session_index, "0");
		
		InetAddress localhost;
		try {
			localhost = InetAddress.getLocalHost();
			
			String ip_str = localhost.getHostAddress();
			System.out.println("ip address : "+ip_str);
			String[] ip_str_splited = ip_str.split("\\.");
			int ip = Integer.parseInt(ip_str_splited[0]) << 24 +
					 Integer.parseInt(ip_str_splited[1]) << 16 +
	   				 Integer.parseInt(ip_str_splited[2]) << 8 +
					 Integer.parseInt(ip_str_splited[3]);
			
			tcpWrite(DB_WRITE, "Session"+session_index+"_Info", ""+ip);
			
			System.out.println("Session DB Initialized.");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//inc, get, set, pop
	
	public int sockInit(ChannelHandler ci) {
		pGroup = new NioEventLoopGroup(1);
		cGroup = new NioEventLoopGroup();
		
		sb = new ServerBootstrap();

		sb.group(pGroup, cGroup)
		  .channel(NioServerSocketChannel.class)
		  .option(ChannelOption.SO_BACKLOG, 100)
		  .handler(new LoggingHandler(LogLevel.INFO))
		  .childHandler(ci);
		
		ChannelFuture cf;
		try {
			System.out.println("init complete.");
			cf = sb.bind(PORT).sync();
			System.out.println("bind complete.");
			cf.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			pGroup.shutdownGracefully();
			cGroup.shutdownGracefully();
		}
		//////////////////////////////
		
		//WorkerNode.worker_init();
		TcpMsgRoot.setPacket(rmHandler);
		return 1;
	}
	
	private int jedisInit() {
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
				jedisPool.getResource();
			} catch (JedisConnectionException e) {
				jedisPool = null;
			}
		} while(jedisPool == null);
		
		TcpMsgRoot.setRedis(jedisPool);
		System.out.println("Connection with Redis DB Server is Established Successfully.");
		
		return 1;
	}
	
	private int listInit() {
		tcpList = new TcpMsgRoot[TCP_SZ];

		register(new TcpSetSession(GET_SESSION_NUM));
		register(new TcpConnectSession(CONN_SESSION));
		register(new TcpDBWrite(DB_WRITE));
		
		return 1;
	}

	public int tcpWrite(int msgType, String... str) {
		return tcpList[msgType].write(str);
	}
	
	private int register(TcpMsgRoot newClass) {
		int msgType = newClass.getIndex();
		if(msgType < 0) System.err.println("Tcp List Registration Error");
		
		tcpList[msgType] = newClass;
		return 1;
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
	
	 //1. 입력된 데이터를 처리하는 이벤트 핸들러 상속
	 
	//////////////////////데이터 수신시 channelRead가 자동호출됨
	//////////////////////1024바이트씩 read를 자동으로 수행하며, 0을 리턴하거나 1024 미만의 값이 리턴될 경우 channelReadComplete함수가 자동호출됨 
	
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
	     
	    	tcpList[msgNum].read(ctx,readMessage.substring(2));
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
