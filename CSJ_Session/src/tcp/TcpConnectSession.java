package tcp;

import io.netty.channel.ChannelHandlerContext;
import main.WorkerNode;

public class TcpConnectSession extends TcpMsgRoot {

	public TcpConnectSession(int index) {
		super(index);
	}

	@Override
	public int write(String... str) {
		//ȣ��� �� ����
		tcpWrite(WorkerNode.getChannel(Integer.parseInt(str[0])),"Connect!");
		return 0;
	}

	@Override
	public int read(ChannelHandlerContext ctx, String str) {
		int result = 0;
		String msg;
		
		result = WorkerNode.add(ctx.channel());
		//if(result < 0) msg = "Failed";
		//else msg = "Success"+result;
		msg = "Failed";
				
		tcpWrite(ctx.channel(), msg);
		// TODO Auto-generated method stub
		return 0;
	}
}
