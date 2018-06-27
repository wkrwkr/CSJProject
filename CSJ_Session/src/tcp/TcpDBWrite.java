package tcp;

import io.netty.channel.ChannelHandlerContext;
import main.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TcpDBWrite extends TcpMsgRoot {
	
	public TcpDBWrite(int index) {
		super(index);
	}

	@Override
	public int write(String ... str) {
		Jedis jedis = jedisPool.getResource();
		
		jedis.set(str[0], str[1]);
		jedis.close();
		
		return 1;
	}

	@Override
	public int read(ChannelHandlerContext ctx, String str) {
		// TODO Auto-generated method stub
		return 0;
	}
}
