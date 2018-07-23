package main;


import tcp.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class Packet {
	
	public static final int PING = 0;
	public static final int TcpSetSession = 1;
	public static final int TcpGetSessionIP = 2;
	public static final int TcpGetSessionNum = 3;
	public static final int TcpRequestIndex = 4;
	public static final int TcpDBWrite = 5;
	public static final int TCP_SZ = 100;
	
	private static final String version = "0.01";
	
	///////////////configure ���� ���� ���� ����////////////////////
	private static final int PORT = 9190;
	private static final String IP_DB = "127.0.0.1";
	private static final int PORT_DB = 6379;
	private static final int MSG_SZ = 256;
	///////////////configure ���� ���� ���� ����////////////////////
	
	private int session_index;
	private JedisPool jedisPool = null;
	
	private TcpMsgRoot[] tcpList;
	
	private ResourceManager rmHandler;
	
	private ChannelFuture cf;
	private EventLoopGroup pGroup;
	private EventLoopGroup cGroup;
	private ServerBootstrap sb;
	
	public Packet(ResourceManager rm) {
		super();
		this.rmHandler = rm;
		
		jedisInit();
		listInit();
		
		session_index = tcpWrite(TcpSetSession);
		tcpWrite(TcpDBWrite, "Session"+session_index, "0");
		
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
			
			tcpWrite(TcpDBWrite, "Session"+session_index+"_Info", ""+ip);
			
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
		
		try {
			System.out.println("init complete.");
			cf = sb.bind(PORT).sync();
			System.out.println("bind complete.");
			//cf.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//////////////////////////////
		
		//WorkerNode.worker_init();
		TcpMsgRoot.setPacket(rmHandler);
		return 1;
	}
	
	public void sockTerminate() {
		pGroup.shutdownGracefully();
		cGroup.shutdownGracefully();
	}
	
	private int jedisInit() {
		int tries = 0;
		JedisPoolConfig jpConfig = new JedisPoolConfig();
		
		do { //DB������ ����. ���н�, ������������ ���� ��õ�
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

		register(new TcpSetSession(TcpSetSession));
		register(new TcpRequestIndex(TcpRequestIndex));
		register(new TcpDBWrite(TcpDBWrite));
		
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
			//������ ���� ��� ���� ����
			if( sock.read(buf) <= 0) return null;
			
			//CSJ ����� �ƴ� ��� ���� ����
			header = StandardCharsets.UTF_8.decode(buf).toString();
			System.out.println("header : " + header + ".");
			if(header.substring(0, 3).equals("CSJ") == false)
			{
				System.out.println("false : "+header.substring(0, 3));
				return null;
			}
			
			//���������� ����
			len = Integer.parseInt(header.substring(3, 4));
			version = header.substring(4, 4+len);
			off = 4+len;
			System.out.println("Master Version : "+Packet.version);
			System.out.println("Worker Version : "+version);
			
			//�޽��� Ÿ���� ����
			msgNum = Integer.parseInt(header.substring(off, off+1));
			off++;
			
			switch(msgNum) {
			case PING :
				//ping �޽���
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
	
	//��Ŷ���� ���� ������ �а� ����
	//length�� 0��  ��� �̹� ������ �����Ѵٴ� ����.
	//length�� 0�ε� �������� �ʴ� �����̸� ����ó��
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
	
	 //1. �Էµ� �����͸� ó���ϴ� �̺�Ʈ �ڵ鷯 ���
	 
	//////////////////////������ ���Ž� channelRead�� �ڵ�ȣ���
	//////////////////////1024����Ʈ�� read�� �ڵ����� �����ϸ�, 0�� �����ϰų� 1024 �̸��� ���� ���ϵ� ��� channelReadComplete�Լ��� �ڵ�ȣ��� 
	
	public int tcpRead(int msgNum, ChannelHandlerContext ctx, String str) {
		return tcpList[msgNum].read(ctx,str);
	}
}
