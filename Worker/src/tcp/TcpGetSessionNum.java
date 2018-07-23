package tcp;

import io.netty.channel.ChannelHandlerContext;
import main.Packet;
import main.TaskExecutor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TcpGetSessionNum extends TcpMsgRoot {
	
	public TcpGetSessionNum(int index) {
		super(index);
	}
	
	@Override
	public int write(String... str) {
		Jedis jedis = jedisPool.getResource();
		String getString;
		int sessionNum=0, workerNum=0, min = 1073741824; //2^30으로 초기값 설정
		
		Loop : do {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getString = jedis.get("Session");
			
			if (getString == null)
				continue Loop;
			
			sessionNum = Integer.parseInt(jedis.get("Session")); //현재까지 연결된 세션 개수를 받음. 현재 연결되어있는 세션 개수와 다를 수 있음
			
			for(int i=sessionNum;i>0;i--) { //가장 관리하는 워커가 적은 세션을 찾음
				getString = jedis.get("Session"+i);
				if(getString != null) { //중간에 연결이 끊긴 세션 처리
					workerNum = Integer.parseInt(getString);
					if(min > workerNum) {
						min = workerNum;
						sessionNum = i;
					}
				}
			}
			
			if(min == 1073741824) // 현재 연결되어있는 세션 노드가 없는 경우
				continue Loop;
		} while(false);

		//연결루틴
		//세션으로부터 인덱스를 받아 db에 등록
		
		System.out.println("session "+sessionNum+" selected.");
		jedis.close();
		return sessionNum;
	}

	@Override
	public int read(ChannelHandlerContext ctx, String str) {
		// TODO Auto-generated method stub
		return 0;
	}
}
