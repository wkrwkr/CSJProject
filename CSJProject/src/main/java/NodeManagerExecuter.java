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
	
	//���Ŵ��� �ʱ�ȭ - �� ������ �ޱ� ���� �����׼��� ����
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
	
	//���Ŵ��� ��ƾ
	//�����׼��Ͽ��� �� ������ ��ٸ� - ������ ������ ��������� �޾Ƽ� ���
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
	
	//��������� ����ϴ� �Լ�.
	private int register(Socket sock, byte result[]) {
		//��������� sql�� �Է��ϴ� ��ƾ �ʿ�
		System.out.println(new String(result));
		
		return Worker.add(sock);
	}
}