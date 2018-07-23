package tcp;

import io.netty.channel.ChannelHandlerContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TcpGetSessionIP extends TcpMsgRoot {
	
	public TcpGetSessionIP(int index) {
		super(index);
	}
	
	@Override
	public int write(String ... str) {
		Jedis jedis = jedisPool.getResource();
		int ret;
		
		long result = jedis.incr("Session"+str[0]);
		System.out.println(result);
		jedis.close();
		
		String tmp = jedis.get("Session"+str[0]+"_Info");
		System.out.println(tmp);
		ret = Integer.parseInt(tmp);
		return ret;
	}

	@Override
	public int read(ChannelHandlerContext ctx, String str) {
		// TODO Auto-generated method stub
		return 0;
	}
}
