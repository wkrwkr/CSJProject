package main;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import io.netty.channel.Channel;

public class WorkerNode {
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
	
	private static WorkerNode[] list;
	public static final int list_max = 50;
	
	private boolean Heartbeat;
	private Channel ch;
	private InputStream in;
	private OutputStream out;
	private int idx;
	private Result res;
	
	public static void worker_init() {
		list = new WorkerNode[list_max];
		for(int i=0;i<list_max; i++)
			list[i] = null;
	}
	
	public WorkerNode(Channel sock, int idx) {
		this.ch = ch;
		this.idx = idx;
		res = new Result();
	}
	//�������� ��Ŀ�� �����°��� ���Ϲۿ� ����
	public void sendFile(String path) {
		
	}
	
	public static WorkerNode[] getList() {
		return list;
	}
	
	public static WorkerNode get(int idx) {
		return list[idx];
	}
	
	public static Channel getChannel(int idx) {
		if(list[idx] == null) return null;
		return list[idx].ch;
	}
	
	public static int add(Channel ch) {
		int i;
		
		//������ ���� ��ũ���Ϳ� �����ϰ�, ������ ���� ���� �ε����� �Ҵ�
		for(i=0;i<list_max; i++) {
			if(list[i] == null) {
				list[i] = new WorkerNode(ch, i);
				break;
			}
		}
		if(i == list_max) { //��ü ����Ʈ�� �������, ��� ��� ���� �޽���
			System.out.println("Node Number Limit Exceeded..");
			return -1;
		}
		
		return i;
	}
	
	public void addResult(String result) {
		res.add(result);
	}
	
	public String toString() {
		return ""+idx+'\n';
	}
}
