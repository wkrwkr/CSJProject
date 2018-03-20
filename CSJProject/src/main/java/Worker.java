import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import spark.Request;

public class Worker {
	/////////////////////////////////
	//���� ������� �ʴ� ����
	private int ID;
	private String Env_Name;
	private String IP;
	private String CPU;
	private String RAM;
	private String DISC_Size;
	private double Score;
	/////////////////////////////////
	
	private boolean Heartbeat;
	private Socket sock;
	private InputStream in;
	private OutputStream out;
	
	public Worker(Socket sock) {
		this.sock = sock;

		try {
			in = sock.getInputStream();
			out = sock.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//�������� ��Ŀ�� �����°��� ���Ϲۿ� ����
	public void sendFile(String path) {
		Packet.sendFile(out, path);
	}
}
