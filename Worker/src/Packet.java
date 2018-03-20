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
	
	public static int[] readHeader(InputStream in) {
		byte buf[] = new byte[100];
		String header;
		int msgNum;
		int off = 0;
		int len;
		int[] bodylen = new int[2];
		
		try {
			//������ ���� ��� ���� ����
			if((len = in.available()) == 0) return null;
			in.read(buf,0,3);
			
			//CSJ ����� �ƴ� ��� ���� ����
			if(new String(buf).substring(0, 3).equals("CSJ") == false)
			{
				System.out.println("false : "+new String(buf).substring(0, 3));
				return null;
			}
			
			//�޽��� Ÿ���� ����
			in.read(buf,0,1);
			msgNum = Integer.parseInt(new String(buf).substring(0, 1));
			
			switch(msgNum) {
			case PING :
				//ping �޽���
				break;
			case SEND :
				//send�޽����� ���ϸ�� ���ϳ����� ����.
				in.read(buf,0,1);
				bodylen[0] = Integer.parseInt(new String(buf).substring(0, 1));
				in.read(buf,0,1);
				bodylen[1] = Integer.parseInt(new String(buf).substring(0, 1));
				
				in.read(buf,0,bodylen[0]);
				bodylen[0] = Integer.parseInt(new String(buf).substring(0, bodylen[0]));
				in.read(buf,0,bodylen[1]);
				bodylen[1] = Integer.parseInt(new String(buf).substring(0, bodylen[1]));
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
			System.out.println("?.. " + length);
			result = in.read(buf, 0, length);
			System.out.println("?");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(buf).substring(0, result);
	}
	
	/*
	public static int readBodyl(InputStream in, int length) {
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
	}*/
	
	//��Ŷ���� ���� ������ �а� ����
	//length�� 0��  ��� �̹� ������ �����Ѵٴ� ����.
	//length�� 0�ε� �������� �ʴ� �����̸� ����ó��
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
			//path���� ���ϸ� ����
			for(int i=0; i<filename.length(); i++) {
				if(filename.charAt(i) == '\\') {
					filename = filename.substring(i+1);
					i = 0;
				}
			}
			length[0] = filename.getBytes().length;
			
			System.out.println("filename : " + filename);
			
			//���� ũ�⸦ �˾ƾ���(byte)
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
	/*
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
	*/
	private static int writeHeader(OutputStream out, int msgNum, int[] length) {
		byte buf[];
		String header = new String("");
		header += "CSJ";
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
