//마스터노드는 현재 3개의 실행 흐름이 있을 예정
//1. 노드 매니저의 리스닝 소켓
//2. GUI
//3. 하트비트 체크
public class Main {
	public static void main(String []args) {
		
		//GUI 셋업
		try {
			GUI frame = new GUI();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		while(true)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
