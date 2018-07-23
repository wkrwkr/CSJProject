package main;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import tcp.TcpSetSession;
import tcp.TcpDBWrite;
import tcp.TcpMsgRoot;

public class ResourceManager {
	private Packet packetHandler;
	
	public ResourceManager() {
		packetHandler = new Packet(this);
		WorkerNode.worker_init();
		sockInit();
	}
	
	private int sockInit() {
		int tries = 0;
		ReadHandler.setHandler(packetHandler);
		ChannelHandler ci = new ChannelInitializer() {
			@Override
			protected void initChannel(Channel sc) throws Exception {
				ChannelPipeline cp = sc.pipeline();
				cp.addLast(new ReadHandler());
			}
		};
		
		packetHandler.sockInit(ci);
		
		return 1;
	}
	/*
	public int rmSchedule(int cpuCore, int ramSize, int diskSize) {
		int worker_idx;
			
		if((worker_idx = schedule_worker(cpuCore, ramSize, diskSize)) == -1)
			return -1;
			
		if((rmAlloc(session_idx, worker_idx, cpuCore, ramSize, diskSize)) == -1)
			return -1;

		return 1;
	}

	private int schedule_session() {
		int min = SessionNode.MAX_WNODE;
		int wNode;
		int ret = -1;
			
		for(int i=list_s.length; i>=0; i--) {
			if(list_s[i] != null) {
				wNode = list_s[i].getWNode();
				if(wNode < min) {
					min = wNode;
					ret = i;
				}
			}
		}
			
		return ret;
	}

	private int schedule_worker(int cpuCore, int ramSize, int diskSize) {
		int core;
		int qIndex = 0;
		int ret = -1;
			
		if(cpuCore < 3) core = cpuCore;
		else core = ((cpuCore-1)/2)*4;
			
		for(int i=core; core>1; core/=2)
			qIndex++;
			
		for(; ret < 0 && queue_w[qIndex] != null; qIndex++)
			ret = queue_w[qIndex].find(ramSize, diskSize);
			
		return ret;
	}

	private int rmAlloc(int s_idx, int w_idx, int cpuCore, int ramSize, int diskSize) {
		int ret;

		ret = pk.tcpWrite(pk.TCP_CONN, ""+w_idx, ""+s_idx, ""+cpuCore, ""+ramSize, ""+diskSize);
		queueUpdate(w_idx,cpuCore);
		
		return ret;
	}*/
}
