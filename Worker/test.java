import java.util.Calendar;
import java.util.Scanner;

public class test {

   public static void main(String[] args) {
      // TODO Auto-generated method stub

      long curtime = System.currentTimeMillis();
      
      Scanner in = new Scanner(System.in);
	String msg = "";
        String msg2 = formatTime(curtime);
        
	for(int i=0;i<100;i++) {
curtime = System.currentTimeMillis();
msg = "";
msg2 = formatTime(curtime);

        msg += in.nextLine();
msg += msg2;
        
        System.out.println(msg);
}
      
   }
   
   public static String formatTime(long curTime)
   {
      Calendar c = Calendar.getInstance();
      c.setTimeInMillis(curTime);
      return ("["+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND)+":"+c.get(Calendar.MILLISECOND)+"]");
   }

}