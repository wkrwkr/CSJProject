import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//import org.apache.spark.api.java;

public class Packet {
	
	private static final int PING = 0;
	private static final int RGST = 1;
	private static final int SEND = 2;
	private static final int EXEC = 3;
	private static final int LOG = 4;
	
	public static int readHeader(InputStream in) {
		byte buf[] = new byte[100];
		String header;
		int msgNum;
		int off = 0;
		int len;
		int bodylen = 0;
		
		try {
			if((len = in.available()) == 0) return -1; //읽을게 없는 경우 에러 리턴
			in.read(buf,0,4);
			
			if(new String(buf).substring(0, 4).equals("test") == false)
				return -1;
			in.read(buf,0,1);
			msgNum = Integer.parseInt(new String(buf).substring(0, 1));
			switch(msgNum) {
			case PING :
				break;
			case RGST :
				break;
			case SEND :
				in.read(buf,0,1);
				bodylen = Integer.parseInt(new String(buf).substring(0, 1));
				
				in.read(buf,0,bodylen);
				bodylen = Integer.parseInt(new String(buf).substring(0, bodylen));
				break;
			case EXEC :
				break;
			case LOG :
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bodylen;
	}
	
	public static int readBody(InputStream in, int length) {
		byte[] buf = new byte[100];
		int result = 0;
		
		try {
			do {
				result = in.read(buf, 0, 100);
				System.out.println(new String(buf).substring(0, result));
			} while(length > 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	//패킷에서 파일 정보를 읽고 저장
	public static int readFile(InputStream in, int length) {
		byte[] buf = new byte[100];
		int result = 0;
		FileWriter fw;
		
		try {
			fw = new FileWriter("./testfile");
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
	
	public static int pingPacket(OutputStream out) {
		writeHeader(out, PING, 0);
		return 0;
	}
	
	public static int sendFile(OutputStream out, String path) {
		FileReader fr;
		char[] cbuf = new char[100];
		byte[] buf = new byte[100];
		int result = 0;
		int length = 0;
		String strLength;
		
		try {
			 fr = new FileReader(path);
			 length = (int) fr.skip(100000000);	//100MB..
			 fr.close();
			 fr = new FileReader(path);
			 
			 writeHeader(out,SEND,length);
			 do {
				 result = fr.read(cbuf, 0, 100);
				 
				 buf = String.valueOf(cbuf).substring(0, result).getBytes();
				 System.out.println("Master : " + buf);
				 System.out.println(cbuf);
				 out.write(buf, 0, buf.length);
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
	
	public int sendMessage(OutputStream out, String message) {
		writeHeader(out,SEND,message.length());
		try {
			out.write(message.getBytes(), 0, message.length());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	private static int writeHeader(OutputStream out, int msgNum, int length) {
		byte buf[];
		String header = new String("");
		header += "test";
		header += msgNum;
		
		switch(msgNum) {
		case PING :
			break;
		case SEND :
			header += String.valueOf(length).length();
			header += length;
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