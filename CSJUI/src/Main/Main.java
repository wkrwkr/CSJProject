//�����ͳ��� ���� 3���� ���� �帧�� ���� ����
//1. ��� �Ŵ����� ������ ����
//2. GUI
//3. ��Ʈ��Ʈ üũ
public class Main {
	public static void main(String []args) {
		
		//GUI �¾�
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
