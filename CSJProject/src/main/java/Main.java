import static spark.Spark.*;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class Main {
	static int runningtime = 0;

    public static void main(String[] args) {
    	NodeManagerExecuter m = new NodeManagerExecuter(); //�����׼��� ����
		
		//GUI �¾�
		try {
			GUI frame = new GUI();
			frame.setList(m.getList());
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//��Ʈ��Ʈ üũ ��ƾ �߰� �ʿ�
		while(true)
		{
			try {
				Thread.sleep(1000);
				runningtime++;
				get("/hello", (req, res) -> {
	        		res.body(""+runningtime);
	        		return res.body();
	        	});
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
}
