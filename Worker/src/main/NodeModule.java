package main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public class NodeModule {
/*
	private static final int TARGET_PORT = 8000;
	private static final int PORT = 8002;

	private static final String url = "http://localhost:4567"; //test url
	
	private SocketChannel socket;
	private InputStream in; 
	private OutputStream out;
	private HttpClient httpclnt;
	private int idx;
	
	//��Ŀ��� ������
	//http ������ ������ ���� ���� �غ�
	//1. Master Node�� ����� ���� ����
	//2. �ڽ��� H/W Specification �˸�
	public NodeModule() {
		int[] result;
		
		httpclnt = HttpClients.createDefault();
		
		HttpPost post = new HttpPost(url+"/worker_init");
		post.addHeader("type", "worker_init");
		
		try {
			HttpResponse httpres = httpclnt.execute(post);
			System.out.println(EntityUtils.toString(httpres.getEntity(), "UTF-8"));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//������ �ʱ�ȭ�ϰ� ������ ����. �� ������ ����(�������� �� ������)�� ���۹����� ����
		try {
			socket = SocketChannel.open();
			//socket.configureBlocking(false);
			System.out.println("Socket Initialized..");
			socket.socket().bind(new InetSocketAddress(PORT));
			System.out.println("Bind IP Address & Port..");
			System.out.println("Trying to Connect..");
			socket.connect(new InetSocketAddress("localhost", TARGET_PORT));
			System.out.println("Connected..");
			System.out.println("user : "+System.getProperty("user.name"));
			
			while((result = Packet.readHeader(socket)) == null);
			if(result[0] == 0) {
				System.out.println("Connection Failed : Cannot Register more Node..");
				socket.close();
				socket = null;
			}
			idx = Packet.readIdx(socket, result[0]);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isConnected() {
		return (socket != null);
	}
	
	//http�� ��ü �� �޽��� ���� ���� ����. ��Ŀ��忡 ����� ������ �޽��� ���ַ� ����
	//���ѷ����� ����Ǵ� ��ƾ�Լ�
	//idle ������ �� �������� ping �޽����� ��� ���ƿ�
	//ó���ؾ��ϴ� �޽����� ������ ������ ����
	//1. ping �޽��� : ��尣 ������ �����ǰ��ִ��� Ȯ���ϴ� �뵵 + �ְ�޴� �޽����� ���� OS���� �ڵ����� ������ �ݴ� ���� ����
	//2. send �޽��� : ������ ���ƿ���, ��Ŀ���� ����� �ִ� �ڵ带 ������ http�� ������ ��ȿ�� ������������ Ȯ�� �� ����
	
	public void routine() {
		int[] len;
		String filename;
		
		//���� �������� ���ƿ� �޽����� �ִ��� Ȯ����
		try {
			Thread.sleep(3000);
			//��Ʈ��Ʈ�� ���� �� �޽���. �Ʒ� http ������ �Ϸ�Ǹ�, ��Ŀ������ ���� ������ �����ϰ� �翬��õ� ��ƾ�� �ʿ�
			Packet.pingPacket(socket);
			if( (len = Packet.readHeader(socket)) == null) return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		//���� http�� ���� �ʿ�
		//�� ��� ������ ���� �� ������ ���� �ʿ�
		//send�޽����� ���ϸ�, ���ϳ����� ���� ���
		//�����ͳ�忡�� ���� �� �̹� �����ϴ� ���Ϸ� �ǴܵǴ°��, ���ϳ��� ���̸� 0���� �� �� �ִ�.
		//���ϳ��� ���̰� 0�ε� ���� ������ ��� ���� ��û �޽����� ��������.
		filename = Packet.readName(in, len[0]);
		System.out.println("filename : " + filename);

		Packet.readFile(socket, filename, len[1]);
		execFile(filename);
	}
	
	private boolean CheckFile(String filename) {
		return false;
	}
	
	//���� ������ �����ϴ� ��ƾ. 
	private void execFile(String filename) {
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
			pc = rt.exec("py -u "+filename);
			
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
			//��ɾ� ����� ���� ���μ��� ����
			pc.destroy();
		}
	}
	
	private void postLog(String log) {
		HttpPost post = new HttpPost(url+"/log");
		post.addHeader("log", log);
		post.addHeader("idx", ""+idx);
		try {
			HttpResponse httpres = httpclnt.execute(post);
			System.out.println("post result : " + EntityUtils.toString(httpres.getEntity(), "UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
}