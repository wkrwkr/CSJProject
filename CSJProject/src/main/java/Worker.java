import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import spark.Request;

public class Worker {
	/////////////////////////////////
	//현재 사용하지 않는 값들
	private int ID;
	private String Env_Name;
	private String IP;
	private String CPU;
	private String RAM;
	private String DISC_Size;
	private double Score;
	/////////////////////////////////
	
	private static Worker[] list;
	private static final int list_max = 1024;
	
	private boolean Heartbeat;
	private Socket sock;
	private InputStream in;
	private OutputStream out;
	private int idx;
	
	public static void worker_init() {
		list = new Worker[list_max];
		for(int i=0;i<list_max; i++)
			list[i] = null;
	}
	
	public Worker(Socket sock, int idx) {
		this.sock = sock;
		this.idx = idx;

		try {
			in = sock.getInputStream();
			out = sock.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//소켓으로 워커에 보내는것은 파일밖에 없음
	public void sendFile(String path) {
		Packet.sendFile(out, path);
	}
	
	public static Worker[] getList() {
		return list;
	}
	
	public static Worker get(int idx) {
		return list[idx];
	}
	
	public static int add(Socket sock) {
		int i;
		
		for(i=0;i<list_max; i++) {
			if(list[i] == null) {
				list[i] = new Worker(sock, i);
				break;
			}
		}
		if(i == list_max) {
			System.out.println("Node Number Limit Exceeded..");
			return -1;
		}
		
		return i;
	}
}
