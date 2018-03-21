import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class NodeModule {

	private static final int TARGET_PORT = 8000;
	private static final int PORT = 8002;

	private static final String url = "http://localhost:4567"; //test url
	
	private Socket socket;
	private InputStream in; 
	private HttpClient httpclnt;
	private int index;
	
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
			socket = new Socket();
			socket.setReuseAddress(true);
			System.out.println("Socket Initialized..");
			socket.bind(new InetSocketAddress("localhost", PORT));
			System.out.println("Bind IP Address & Port..");
			System.out.println("Trying to Connect..");
			socket.connect(new InetSocketAddress("localhost", TARGET_PORT));
			System.out.println("Connected..");
			System.out.println("user : "+System.getProperty("user.name"));
			in = socket.getInputStream();
		
			while((result = Packet.readHeader(in)) == null);
			if(result[0] == 0) {
				System.out.println("Connection Failed : Cannot Register more Node..");
				socket.close();
				socket = null;
			}
			index = Packet.readIdx(in, result[0]);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isConnected() {
		return (socket != null);
	}
	
	//���ѷ����� ����Ǵ� ��ƾ�Լ�
	//idle ������ �� �������� ping �޽����� ��� ���ƿ�
	//ó���ؾ��ϴ� �޽����� ������ ������ ����
	//1. ping �޽��� : ��尣 ������ �����ǰ��ִ��� Ȯ���ϴ� �뵵 + �ְ�޴� �޽����� ���� OS���� �ڵ����� ������ �ݴ� ���� ����
	//2. send �޽��� : ������ ���ƿ���, ��Ŀ���� ����� �ִ� �ڵ带 ������ http�� ������ ��ȿ�� ������������ Ȯ�� �� ����
	
	public void routine() {
		int[] len;
		String filename;
		
		//���ʸ��� ���ƿ� �޽����� �ִ��� Ȯ����
		try {
			Thread.sleep(1000);
			if( (len = Packet.readHeader(in)) == null) return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		//send�޽����� ���ϸ�, ���ϳ����� ���� ���
		//�����ͳ�忡�� ���� �� �̹� �����ϴ� ���Ϸ� �ǴܵǴ°��, ���ϳ��� ���̸� 0���� �� �� �ִ�.
		//���ϳ��� ���̰� 0�ε� ���� ������ ��� ���� ��û �޽����� ��������.
		filename = Packet.readName(in, len[0]);
		System.out.println("filename : " + filename);

		Packet.readFile(in, filename, len[1]);
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
				if( (len = Packet.readHeader(in)) == null);
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
		post.addHeader("taskNum", "1");
		try {
			HttpResponse httpres = httpclnt.execute(post);
			System.out.println("post result : " + EntityUtils.toString(httpres.getEntity(), "UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}