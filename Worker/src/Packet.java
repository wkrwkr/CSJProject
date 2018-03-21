import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Packet {
	
	private static final int PING = 0;
	private static final int SEND = 1;
	private static final int CONN = 2;
	private static final String version = "0.01";
	
	public static int[] readHeader(InputStream in) {
		byte buf[] = new byte[100];
		String header;
		String version;
		int msgNum;
		int off = 0;
		int len;
		int[] bodylen = new int[2];
		
		try {
			//읽을게 없는 경우 에러 리턴
			if((len = in.available()) == 0) return null;
			in.read(buf,0,3);
			
			//CSJ 헤더가 아닌 경우 에러 리턴
			if(new String(buf).substring(0, 3).equals("CSJ") == false)
			{
				System.out.println("false : "+new String(buf).substring(0, 3));
				return null;
			}
			
			//버전정보를 읽음
			in.read(buf,0,1);
			len = Integer.parseInt(new String(buf).substring(0, 1));
			in.read(buf,0,len);
			version = new String(buf).substring(0, len);
			System.out.println("Master Version : "+Packet.version);
			System.out.println("Worker Version : "+version);
			
			//메시지 타입을 읽음
			in.read(buf,0,1);
			msgNum = Integer.parseInt(new String(buf).substring(0, 1));
			
			switch(msgNum) {
			case PING :
				//ping 메시지
				break;
			case SEND :
				//send메시지는 파일명과 파일내용을 받음.
				in.read(buf,0,1);
				bodylen[0] = Integer.parseInt(new String(buf).substring(0, 1));
				in.read(buf,0,1);
				bodylen[1] = Integer.parseInt(new String(buf).substring(0, 1));
				
				in.read(buf,0,bodylen[0]);
				bodylen[0] = Integer.parseInt(new String(buf).substring(0, bodylen[0]));
				in.read(buf,0,bodylen[1]);
				bodylen[1] = Integer.parseInt(new String(buf).substring(0, bodylen[1]));
				break;
			case CONN :
				//connect 메시지는 리스트 내의 인덱스넘버를 받음
				in.read(buf,0,1);
				bodylen[0] = Integer.parseInt(new String(buf).substring(0, 1));
				
				in.read(buf,0,bodylen[0]);
				bodylen[0] = Integer.parseInt(new String(buf).substring(0, bodylen[0]));
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bodylen;
	}
	
	public static String readName(InputStream in, int length) {
		byte[] buf = new byte[100];
		int result = 0;
		try {
			result = in.read(buf, 0, length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(buf).substring(0, result);
	}
	
	//패킷에서 파일 정보를 읽고 저장
	//length가 0인  경우 이미 파일이 존재한다는 전제.
	//length가 0인데 존재하지 않는 파일이면 오류처리
	public static int readFile(InputStream in, String filename, int length) {
		byte[] buf = new byte[100];
		int result = 0;
		FileWriter fw;
		
		try {
			fw = new FileWriter("./"+filename);
			do {
				result = in.read(buf, 0, 100);
				fw.write(new String(buf).substring(0, result));
				length -= result;
			} while(length > 0);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public static int readIdx(InputStream in, int length) {
		byte[] buf = new byte[10];
		int result = 0;
		
		try {
			do {
				result += in.read(buf,result,length);
			} while(length > result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = Integer.parseInt(new String(buf).substring(0, length));
		System.out.println("node index : "+result);
		return result;
	}
	
	public static int pingPacket(OutputStream out) {
		writeHeader(out, PING, null);
		return 0;
	}
	
	public static int sendFile(OutputStream out, String path) {
		FileReader fr;
		char[] cbuf = new char[100];
		byte[] buf = new byte[100];
		int result = 0;
		int[] length = new int[2];
		String filename = path;
		
		System.out.println("send file..");
		
		try {
			//path에서 파일명 추출
			for(int i=0; i<filename.length(); i++) {
				if(filename.charAt(i) == '\\') {
					filename = filename.substring(i+1);
					i = 0;
				}
			}
			length[0] = filename.getBytes().length;
			
			System.out.println("filename : " + filename);
			
			//파일 크기를 알아야함(byte)
			 fr = new FileReader(path);
			 length[1] = (int) fr.skip(100000000);	//100MB..
			 fr.close();
			 fr = new FileReader(path);
			 
			 System.out.println("file size : " + length[1]);
			 
			 writeHeader(out,SEND,length);
			 
			 buf = filename.getBytes();
			 out.write(buf, 0, buf.length);
			 
			 do {
				 result = fr.read(cbuf, 0, 100);
				 
				 buf = String.valueOf(cbuf).substring(0, result).getBytes();
				 System.out.println(cbuf);
				 out.write(buf);
			 }
			 while(result == 100);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static int connPacket(OutputStream out, int idx) {
		int[] length = new int[2];
		if(idx < 0)
			length[0] = 0;
		else
			length[0] = (""+idx).length();
		writeHeader(out, CONN, length);
		try {
			out.write((""+idx).getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	private static int writeHeader(OutputStream out, int msgNum, int[] length) {
		byte buf[];
		String header = new String("");
		header += "CSJ";
		
		header += Packet.version.length();
		header += Packet.version;
		
		header += msgNum;
		
		switch(msgNum) {
		case PING :
			break;
		case SEND :
			header += String.valueOf(length[0]).length();
			header += String.valueOf(length[1]).length();
			header += length[0];
			header += length[1];
			break;
		case CONN :
			header += String.valueOf(length[0]).length();
			header += length[0];
			break;
		}
		
		buf = header.getBytes();
		try {
			out.write(buf, 0, header.length());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}
}
