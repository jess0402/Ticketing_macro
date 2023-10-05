package Main;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class test_interpark {
    public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
    public static String WEB_DRIVER_PATH = "C:\\dev\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe";

    //    public static String url = "https://tickets.interpark.com/";
//    public static String url = "https://ticket.interpark.com/Gate/TPLogin.asp?CPage=B&MN=Y&tid1=main_gnb&tid2=right_top&tid3=login&tid4=login&GPage=https%3A%2F%2Ftickets.interpark.com";


    public static void main(String[] args) throws InterruptedException {

        Properties properties = getProperties(args[0]);

        /* 변수 세팅 */
        String url = properties.getProperty("interpark.url");
        String id = properties.getProperty("interpark.id");
        String pw = properties.getProperty("interpark.pw");
        String goodsCode = properties.getProperty("interpark.goodsCode");
        String ticketingTime = properties.getProperty("interpark.ticketingTime");
        String playDate = properties.getProperty("interpark.playDate");
        String playSeq = properties.getProperty("interpark.playSeq");
        int totalSeat = Integer.parseInt(properties.getProperty("totalSeat"));
        int startRow = Integer.parseInt(properties.getProperty("startRow"));
        int endRow = Integer.parseInt(properties.getProperty("endRow"));
        int startSeat = Integer.parseInt(properties.getProperty("startSeat"));
        int endSeat = Integer.parseInt(properties.getProperty("endSeat"));

        List<String> wishSeats = getWishSeatList(properties);


        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(options);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
//        ChromeDriver driver = new ChromeDriver();
        driver.get(url);

//        Thread.sleep(300);

        /* 전부 프로퍼티 파일로 변경하기 */
        /* 로그인 */
        driver.switchTo().frame(driver.findElement(By.xpath("//div[@class='leftLoginBox']/iframe[@title='login']")));
        WebElement userId = driver.findElement(By.id("userId"));
        userId.sendKeys(id);
        WebElement userPwd = driver.findElement(By.id("userPwd"));
        userPwd.sendKeys(pw);
        userPwd.sendKeys(Keys.ENTER);

        /* 원하는 티켓 페이지로 이동 */
        driver.get("http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=" + goodsCode);

        Thread.sleep(1000);

        /* 예매 안내 창 닫기 - 팝업창 있을 경우에만 */
        try {
            driver.findElement(By.className("popupWrap"));
            driver.findElement(By.className("popupCloseBtn")).click();
        } catch (Exception e){
            System.out.println("팝업창 없음");
        }

        /* 실제 티켓팅 할때만 주석해제 */
//        String nowTime = "";
//        while(true){
//            nowTime = getCurrentTime("yyyyMMddHHmmss");
//            if(nowTime.equals(ticketingTime)) break;
//        }


        /* 예매하기 버튼 클릭 */
        while(true){
            try{
                driver.findElement(By.xpath("//*[@id=\"productSide\"]/div/div[2]/a[1]")).click();
                break;
            } catch(Exception e){
                System.out.println("버튼 활성화 안됨");
                continue;
            }
        }

        Thread.sleep(500);

        /* 예매창으로 포커스 변경 */
        while(true){
            Set<String> set = driver.getWindowHandles();
            if(set.size() <= 1) {
                Thread.sleep(300);
                continue;
            } else {
                driver.switchTo().window(set.toArray()[1].toString());
                break;
            }
        }
        /* 포커스 변경 후 대기번호 창 뜸 */


        /* 대기번호 처리 */
        while(true) {
            try {
                driver.findElement(By.id("divBookMain"));
                // 위에 코드 때문에 팝업 처리 아래에서 한 번 더 해줘야함ㅠ.ㅠ
                break;
            } catch (Exception e) {
                //System.out.println("Error occurred: " + e.getMessage());
                System.out.println("대기");
                continue;
            }
        }

        js.executeScript("fnNextStep('P')");

        /* alert 창 처리 */
        if(isAlertPresent(driver)){
            Alert alert = driver.switchTo().alert();
            alert.accept();
            System.out.println("alert 처리 완료");
        } else {
            System.out.println("alert 없음");
        }

        /* iframe 안으로 포커스 변경 */
        driver.switchTo().frame(driver.findElement(By.id("ifrmSeat")));

        /* 예매창 안에도 팝업 있을 경우 */
        try{
            driver.findElement(By.id("divBookNoticeLayer"));
            js.executeScript("fnBookNoticeShowHide('')");
        } catch (Exception e){
            System.out.println("팝업창 없음");
        }

//        try{
//            driver.findElement(By.id("ifrmSeat"));
//            if(isAlertPresent(driver)){
//                System.out.println("있");
//                Alert alert = driver.switchTo().alert();
//                alert.accept();
//            } else {
//                System.out.println("없");
//            }
//            driver.switchTo().frame(driver.findElement(By.id("ifrmSeat")));
//        } catch(UnhandledAlertException e){
//            System.out.println("!!!!");
//            e.printStackTrace();
//        }


        /* iframe 안으로 포커스 변경 */
//        while(true) {
//            try {
//                driver.switchTo().frame(driver.findElement(By.id("ifrmSeat")));
//                break;
//            } catch (Exception e) {
//                //System.out.println("Error occurred: " + e.getMessage());
//                System.out.println("대기");
//                continue;
//            }
//        }



        /* 날짜 선택 */
        Select date = new Select(driver.findElement(By.id("PlayDate")));
        date.selectByValue(playDate);

        Thread.sleep(500);

        /* 시간 선택 */
        Select time = new Select(driver.findElement(By.id("PlaySeq")));

        int timeIdx = Integer.parseInt(playSeq);  // 회차 선택
        if(timeIdx == 1) {
            time.selectByIndex(1);
        } else { // i == 2
            time.selectByIndex(2);
        }


        Thread.sleep(500); // 좌석선택창 뜰때까지 0.5초 대기

        // 좌석선택창으로 제대로 들어가질때까지 loop
        while(true){
            try {
                driver.switchTo().frame(driver.findElement(By.id("ifrmSeatDetail")));
                break;
            } catch (Exception e){
                System.out.println("Error occurred: " + e.getMessage());
                Thread.sleep(100);
                continue;
            }
        }

        Thread.sleep(1000);
//
//
//        /** 좌석선택 코드 1
//         *
//         * 아예 내가 지정한 좌석만 선택
//         *
//         * */
//        int seatCnt = 0;
//        int idx = 0;
//        selectSeat:
//        while(true){
//            String seat = wishSeats.get(idx);
//            try{
//                driver.findElement(By.cssSelector(seat)).click();
//                idx++;
//                seatCnt++;
//                if(seatCnt == totalSeat) break;
//            } catch (Exception e){
//                System.out.println(seat + " - 좌석 선택 불가");
//                idx++;
//                continue;
//            }
//        }
//
//        /** 좌석선택 코드 2
//         *
//         * 지정한 시작열~종료열의 지정한 시작좌석~종료좌석사이에서만 좌석 선택
//         *
//         * */
//        // 좌석 형식
//        // 레베카: 신한카드 블루스퀘어) [VIP석] 객석1층-18열-12
//        // 벤허: LG아트센터 서울 LG SIGNATURE 홀) [R석] 1층-09열-5
//        // 그날들: 대구 계명아트센터) [VIP석] 1층-C블럭7열-3
//
//
//        if(seatCnt != totalSeat){
//            System.out.println("두번째 좌석 선택 시작");
//
//            List<WebElement> seats = driver.findElements(By.className("stySeat"));
//            // System.out.println(seats.get(0).getAttribute("title"));
//
//            selectSeat:
//            for(WebElement el : seats){
//                String seat = el.getAttribute("title");
//                if(seat.contains("VIP석")){
//                    if(seat.contains("1층")){
//                        for(int i = startRow; i <= endRow; i++){
//                            String row = Integer.toString(i) + "열";
//                            if(seat.contains(row)){
//                                for(int j = startSeat; j <= endSeat; j++){
//                                    String col = "-" + j;
//                                    if(seat.contains(col)){
//                                        el.click();
//                                        seatCnt++;
//                                        if(seatCnt == totalSeat){
//                                            break selectSeat;
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } else {
//            System.out.println("두번째 좌석 선택 패스");
//        }
//
//        /* 결제하기 버튼 클릭 */
//        driver.switchTo().defaultContent();
//        driver.switchTo().frame(driver.findElement(By.id("ifrmSeat")));
//        driver.findElement(By.xpath("/html/body/form[1]/div/div[1]/div[3]/div/div[4]/a[2]")).click();
//
//        driver.navigate().forward();



//        // 여기서부터는 손으로 해도 됨
//        /* 기본가로 1매 선택 */
//        driver.switchTo().frame(driver.findElement(By.id("ifrmBookStep")));
//        Select price = new Select(driver.findElement(By.xpath("//*[@id=\"PriceRow001\"]/td[3]/select")));
//        price.selectByValue(Integer.toString(totalSeat));
//
//        /* 다음단계 */
//        driver.switchTo().defaultContent();
//        js.executeScript("fnNextStep('P')");
//
//        /* 생년월일 입력 */
//        driver.switchTo().frame(driver.findElement(By.id("ifrmBookStep")));
//        driver.findElement(By.id("YYMMDD")).sendKeys("990402");
//
//        /* 다음단계 */
//        driver.switchTo().defaultContent();
//        js.executeScript("fnNextStep('P')");

//        마지막에 주석해제
//        driver.quit();
    }

    private static List<String> getWishSeatList(Properties properties) {

        List<String> wishSeatList = new ArrayList<>();

        int totalWishSeatCnt = Integer.parseInt(properties.getProperty("wish.seat.cnt"));
        for(int i = 1; i <= totalWishSeatCnt; i++){
            String seatName = "seat" + Integer.toString(i);
            wishSeatList.add("[title='" + properties.getProperty(seatName) + "']");
        }
        return wishSeatList;
    }

    private static String seat_title_checking1(String level, String block, String seat) {
        return "[title*='" + level + "석'][title*='" + block + "구역 " + seat + "열']";
    }

    public static String getCurrentTime(String timeFormat){
        return new SimpleDateFormat(timeFormat).format(System.currentTimeMillis());
    }

    /* 프로퍼티 파일 호출 */
    private static Properties getProperties(String propertiesPath){
        Properties properties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(propertiesPath), StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return properties;
    }

    private static Properties getProperties_backup(String propertiesPath){
        Properties properties = new Properties();
        FileInputStream input = null;
        try {
            input = new FileInputStream(propertiesPath);
            properties.load(input);
        }catch (IOException ex){
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }

    /* alert 창 존재하는지 확인 */
    private static boolean isAlertPresent(WebDriver driver){
        try {
            Alert alert = driver.switchTo().alert();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
