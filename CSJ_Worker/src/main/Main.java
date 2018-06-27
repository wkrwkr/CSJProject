package main;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		TaskExecutor te = new TaskExecutor();
		Scanner scan = new Scanner(System.in);
		String input = "init";
		
		while(true) {
			input = scan.next();
			if(input.equals("exit")) break;
			else if(input.equals("ping")) te.packetHandler.tcpWrite(Packet.PING);
			else
				System.out.println("Undefined Command.");
		}
		System.out.println("Worker Terminated..");
		// TODO 세션 및 DB에 정상 종료를 위한 뒷처리작업 구현 필요
		te.packetHandler.sockTerminate();
		System.out.println("bye");
	}
}