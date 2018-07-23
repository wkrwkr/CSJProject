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
		int sessionNum=0, workerNum=0, min = 1073741824; //2^30���� �ʱⰪ ����
		
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
			
			sessionNum = Integer.parseInt(jedis.get("Session")); //������� ����� ���� ������ ����. ���� ����Ǿ��ִ� ���� ������ �ٸ� �� ����
			
			for(int i=sessionNum;i>0;i--) { //���� �����ϴ� ��Ŀ�� ���� ������ ã��
				getString = jedis.get("Session"+i);
				if(getString != null) { //�߰��� ������ ���� ���� ó��
					workerNum = Integer.parseInt(getString);
					if(min > workerNum) {
						min = workerNum;
						sessionNum = i;
					}
				}
			}
			
			if(min == 1073741824) // ���� ����Ǿ��ִ� ���� ��尡 ���� ���
				continue Loop;
		} while(false);

		//�����ƾ
		//�������κ��� �ε����� �޾� db�� ���
		
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
