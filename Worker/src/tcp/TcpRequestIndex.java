package tcp;

import io.netty.channel.ChannelHandlerContext;

public class TcpRequestIndex extends TcpMsgRoot {

	public TcpRequestIndex(int index) {
		super(index);
	}

	@Override
	public int write(String... str) {
		tcpWrite("Connect!");
		return 0;
	}

	@Override
	public int read(ChannelHandlerContext ctx, String str) {
		int idx;
		if(str.equals("Failed")) {
			idx = -2;
		} else if(str.substring(0, 7).equals("Success")) {
			try {
				idx = Integer.parseInt(str.substring(7));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				idx = -2;
			}
		} else {
			System.out.println("Unknown Packet");
			idx = -2;
		}
		
		if (idx < 0) {
			System.out.println("conn failed..");
		}
		teHandler.packetHandler.idx = idx;
		System.out.println("idx : "+teHandler.packetHandler.idx);
		return 1;
	}
}
