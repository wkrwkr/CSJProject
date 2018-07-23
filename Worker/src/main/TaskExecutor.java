package main;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class TaskExecutor {
	public Packet packetHandler;
	
	public TaskExecutor() {
		packetHandler = new Packet(this);
		sockInit();
	}
	
	private int sockInit() {
		ReadHandler.setHandler(packetHandler);
		ChannelHandler ci = new ChannelInitializer() {
			@Override
			protected void initChannel(Channel sc) throws Exception {
				ChannelPipeline cp = sc.pipeline();
				cp.addLast(new ReadHandler());
			}
		};
		
		if (packetHandler.sockInit(ci) > 0) {
			System.out.println("sock init finished - Node Index : " + packetHandler.idx);
		}
		else {
			System.out.println("sock init failed.");
			return -1;
		}
		return 1;
	}
	/*
	private void tbExecute(String command) {
		Runtime rt = Runtime.getRuntime();
		Process pc = null;
		InputStream es;
		InputStream is;
		InputStreamReader isr;
		BufferedReader br;
		byte[] buf = new byte[300];
		String result;
		int[] len;
		
		try {
			pc = rt.exec(command);
			
			es = pc.getErrorStream();
			is = pc.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
				
			while(pc.waitFor(-1,TimeUnit.SECONDS) == false) {
				if( (len = Packet.readHeader(socket)) == null);
				if(br.ready()) {
					result = br.readLine();
					System.out.println("result : " + result);
					postLog(result);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.out.println("done");
			pc.destroy();
		}
	}
	*/
}
