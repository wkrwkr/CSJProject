import java.io.IOException;
import java.io.InputStream;
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
	
	public ArrayList<Worker> getList() {
		return run.list;
	}
}

class NodeManager implements Runnable{
	private static final int PORT=8000;
	private static final int LEN = 100;
	private ServerSocket accepter;
	private Socket sock;
	private InputStream in;	
	public ArrayList<Worker> list;
	
	//���Ŵ��� �ʱ�ȭ - �� ������ �ޱ� ���� �����׼��� ����
	public NodeManager() {
		try {
			accepter = new ServerSocket();
			accepter.setReuseAddress(true);
			accepter.bind(new InetSocketAddress(PORT));
		} catch (IOException e) {
			e.printStackTrace();
		}
		list = new ArrayList<Worker>();
	}
	
	//���Ŵ��� ��ƾ
	//�����׼��Ͽ��� �� ������ ��ٸ� - ������ ������ ��������� �޾Ƽ� ���
	@Override
	public void run() {
		while(true) {
			byte result[] = new byte[LEN];
			// TODO Auto-generated method stub
			try {
				System.out.println("wait..");
				sock = accepter.accept();
				if(sock == null) continue;
				
				in = sock.getInputStream();
				in.read(result);
				
				register(sock, result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//��������� ����ϴ� �Լ�.
	private void register(Socket sock, byte result[]) {
		//��������� sql�� �Է��ϴ� ��ƾ �ʿ�
		System.out.println(new String(result));
		
		list.add(new Worker(sock));
	}
}