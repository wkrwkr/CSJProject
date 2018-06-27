package tcp;

import io.netty.channel.ChannelHandlerContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TcpSetSession extends TcpMsgRoot {
	
	public TcpSetSession(int index) {
		super(index);
	}
	
	@Override
	public int write(String ... str) {
		int ret;
		Jedis jedis = jedisPool.getResource();
		
		ret = Math.toIntExact(jedis.incr("Session"));
		jedis.close();
		
		return ret;
	}

	@Override
	public int read(ChannelHandlerContext ctx, String str) {
		// TODO Auto-generated method stub
		return 0;
	}
}
