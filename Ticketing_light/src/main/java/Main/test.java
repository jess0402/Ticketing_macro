package Main;

import java.text.SimpleDateFormat;

public class test {
    public static void main(String[] args) throws InterruptedException {

        String nowTime ="";

        while(true){
            nowTime = getCurrentTime("yyyyMMddHHmmss");
            if(nowTime.equals("20230824135900")) break;
            System.out.println("pass");
        }
        System.out.println(nowTime);
    }

    public static String getCurrentTime(String timeFormat){
        return new SimpleDateFormat(timeFormat).format(System.currentTimeMillis());
    }
}
