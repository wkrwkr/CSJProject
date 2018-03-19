import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Worker {
	private int ID;
	private String Env_Name;
	private String IP;
	private String CPU;
	private String RAM;
	private String DISC_Size;
	private double Score; //알고리즘 실행 후 평가값
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
	
	public void sendFile(String path) {
		Packet.sendFile(out, path);
	}
	
	public void sendMessage() {
		byte tmp[];
		try {
			String a = "Hello Worker";
			tmp = a.getBytes();
			System.out.println(new String(tmp));
			out.write(tmp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
