import static spark.Spark.*;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class Main {

    public static void main(String[] args) {
    	
    	
    	NodeManagerExecuter m = new NodeManagerExecuter(); //府胶醋家南 积己
		
		//GUI 悸诀
		try {
			GUI frame = new GUI();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
    }
}
