import static spark.Spark.get;
import static spark.Spark.post;

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
		run.initHTTP();
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
	
	public void initHTTP() {
		post("/log", (req, res) -> {
			String log = req.headers("log");
			String taskNum = req.headers("taskNum");
			System.out.println("task "+taskNum+"'s log : "+log);
			return "done";
		});
		
		post("/list", (req, res) -> {
			Worker[] list;
			String ret = "";
			String result = "";
			int idx;
			
			list = Worker.getList();
			result = req.attribute("idx");
			
			if(result.equals("all")) {
				//return all node
				for(int i=0;i<Worker.list_max;i++)
					if(list[i] != null)
						ret += list[i].toString();
			} else {
				//return selected node
				
				try {
					idx = Integer.parseInt(result);
					if(idx > Worker.list_max || idx < 0)
						return "ERROR";
					if(list[idx] == null)
						ret += "ERROR";
					else 
						ret += list[idx].toString();
				} catch(NumberFormatException e) {
					return "ERROR";
				}
			}
			return ret;
		});
		
		
	}
	
	private void addHTTP(int idx) {
		get("/Node/"+idx, (req, res) -> {
			return "good";
		});
		
		get("/Node/"+idx+"/send", (req, res) -> {
			Worker.get(idx).sendFile("C:\\Users\\LHS\\eclipse-workspace/test.py");
			return "good";
		});
		
		get("/Node/"+idx+"/exec", (req, res) -> {
			Worker.get(idx).sendFile("C:\\Users\\LHS\\eclipse-workspace/test.py");
			return "good";
		});
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
				else {
					System.out.println("New Node Index : "+idx);
					addHTTP(idx);
				}
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