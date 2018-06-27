package tcp;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.netty.channel.ChannelHandlerContext;

public class TcpPingMessage extends TcpMsgRoot{

	public TcpPingMessage(int index) {
		super(index);
	}

	@Override
	public int write(String... str) {
		InetAddress localhost;
		try {
			localhost = InetAddress.getLocalHost();
			String ip_str = localhost.getHostAddress();
			tcpWrite(ip_str);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int read(ChannelHandlerContext ctx, String str) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
