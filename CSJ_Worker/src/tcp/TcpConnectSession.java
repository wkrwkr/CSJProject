package tcp;

import io.netty.channel.ChannelHandlerContext;

public class TcpConnectSession extends TcpMsgRoot {

	public TcpConnectSession(int index) {
		super(index);
	}

	@Override
	public int write(String... str) {
		tcpWrite("Connect!");
		return 0;
	}

	@Override
	public int read(ChannelHandlerContext ctx, String str) {
		int ret;
		if(str.equals("Failed")) {
			ret = -1;
		} else if(str.substring(0, 7).equals("Success")) {
			try {
				ret = Integer.parseInt(str.substring(7));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				ret = -1;
			}
		} else {
			System.out.println("Unknown Packet");
			ret = -1;
		}
		return ret;
	}
}
