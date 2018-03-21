import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class NodeManagerExecuter {

	private Thread th;
	private NodeManager run;
	
	public NodeManagerExecuter() {
		run = new NodeManager();
		th = new Thread(run);
		th.start();
	}
}

class NodeManager implements Runnable{
	private static final int PORT=8000;
	private static final int LEN = 100;
	private ServerSocket accepter;
	private Socket sock;
	private OutputStream out;
	
	//노드매니저 초기화 - 새 연결을 받기 위한 리스닝소켓 생성
	public NodeManager() { 
		try {
			accepter = new ServerSocket();
			accepter.setReuseAddress(true);
			accepter.bind(new InetSocketAddress(PORT));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Worker.worker_init();
	}
	
	//노드매니저 루틴
	//리스닝소켓에서 새 연결을 기다림 - 연결을 받으면 노드정보를 받아서 등록
	@Override
	public void run() {
		while(true) {
			byte result[] = new byte[LEN];
			int idx;
			// TODO Auto-generated method stub
			try {
				System.out.println("wait for node..");
				sock = accepter.accept();
				if(sock == null) continue;
				
				System.out.println("Connection for Node Established..");
				
				out = sock.getOutputStream();
				
				idx = register(sock, result);
				Packet.connPacket(out, idx);

				if(idx < 0)
					System.out.println("Node List Out of bound..");
				else
					System.out.println("New Node Index : "+idx);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//노드정보를 등록하는 함수.
	private int register(Socket sock, byte result[]) {
		//노드정보를 sql에 입력하는 루틴 필요
		System.out.println(new String(result));
		
		return Worker.add(sock);
	}
}