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
	
	//워커노드 생성자
	//http 쿼리를 보내기 위한 소켓 준비
	//1. Master Node와 통신할 소켓 생성
	//2. 자신의 H/W Specification 알림
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
		
		//소켓을 초기화하고 서버에 연결. 이 소켓은 파일(실행파일 및 데이터)을 전송받을때 사용됨
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
	
	//http로 교체 후 메시지 종류 변경 예정. 워커노드에 명령을 내리는 메시지 위주로 재편성
	//무한루프로 실행되는 루틴함수
	//idle 상태일 시 서버에서 ping 메시지가 계속 날아옴
	//처리해야하는 메시지의 종류는 다음과 같음
	//1. ping 메시지 : 노드간 연결이 유지되고있는지 확인하는 용도 + 주고받는 메시지가 없어 OS에서 자동으로 소켓을 닫는 것을 방지
	//2. send 메시지 : 파일이 날아오며, 워커노드는 헤더에 있는 코드를 서버에 http로 보내어 유효한 파일전송인지 확인 후 수신
	
	public void routine() {
		int[] len;
		String filename;
		
		//일정 간격으로 날아온 메시지가 있는지 확인함
		try {
			Thread.sleep(3000);
			//하트비트를 우한 핑 메시지. 아래 http 수정이 완료되면, 워커에서도 연결 단절을 감지하고 재연결시도 루틴이 필요
			Packet.pingPacket(socket);
			if( (len = Packet.readHeader(socket)) == null) return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		//추후 http로 수정 필요
		//각 명령 수행을 위한 새 쓰레드 생성 필요
		//send메시지로 파일명, 파일내용을 받은 경우
		//마스터노드에서 봤을 때 이미 존재하는 파일로 판단되는경우, 파일내용 길이를 0으로 줄 수 있다.
		//파일내용 길이가 0인데 없는 파일인 경우 파일 요청 메시지를 보내야함.
		filename = Packet.readName(in, len[0]);
		System.out.println("filename : " + filename);

		Packet.readFile(socket, filename, len[1]);
		execFile(filename);
	}
	
	private boolean CheckFile(String filename) {
		return false;
	}
	
	//받은 파일을 실행하는 루틴. 
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
			//명령어 종료시 하위 프로세스 제거
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