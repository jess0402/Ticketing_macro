package Main;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class Main {

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

        String nowTime = "";

        /* 실제 티켓팅 할때만 주석해제 */
//        while(true){
//            nowTime = getCurrentTime("yyyyMMddHHmmss");
//            if(nowTime.equals(ticketingTime)) break;
//        }

        /* 예매하기 버튼 클릭 */
        driver.findElement(By.xpath("//*[@id=\"productSide\"]/div/div[2]/a[1]")).click();

        Thread.sleep(500);



        /* 예매창으로 포커스 변경 */
        Set<String> set = driver.getWindowHandles();
        driver.switchTo().window(set.toArray()[1].toString());

        /* iframe 안으로 포커스 변경 */
        driver.switchTo().frame(driver.findElement(By.id("ifrmSeat")));

        /* 날짜 선택 */
        Select date = new Select(driver.findElement(By.id("PlayDate")));
        date.selectByValue(playDate);

        Thread.sleep(500);

        /* 시간 선택 */
        Select time = new Select(driver.findElement(By.id("PlaySeq")));

        int timeIdx = Integer.parseInt(playSeq);  // 회차 선택
        if(time.getOptions().size() != 1){ // 옵션이 두 개 인 경우
            // 첫 번째 옵션 || 두 번째 옵션
            if(timeIdx == 1) {
                time.selectByIndex(1);
            } else { // i == 2
                time.selectByIndex(2);
            }
        }

        /* 좌석 선택 - 몽땅 프로퍼티로 바꾸기 */
        int seatCnt = 0;

        driver.switchTo().frame(driver.findElement(By.id("ifrmSeatDetail")));
        Thread.sleep(500);

        // 좌석 형식
        // 레베카) [VIP석] 객석1층-18열-12
        // 벤허) [R석] 1층-09열-5
        List<WebElement> seats = driver.findElements(By.className("stySeat"));
//        System.out.println(seats.get(0).getAttribute("title"));

        // 23~26번째 좌석만 select
        /* 좌석선택 코드 1 */
        selectSeat:
        for(WebElement el : seats){
            String seat = el.getAttribute("title");
            if(seat.contains("VIP석")){
                if(seat.contains("1층")){
                    for(int i = startRow; i <= endRow; i++){
                        String row = Integer.toString(i) + "열";
                        if(seat.contains(row)){
                            for(int j = startSeat; j <= endSeat; j++){
                                String col = "-" + j;
                                if(seat.contains(col)){
                                    el.click();
                                    seatCnt++;
                                    if(seatCnt == totalSeat){
                                        break selectSeat;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

//        /* 좌석선택 코드 2 - 미완성 */
//        WebElement seatsAll = driver.findElement(By.xpath("//*[@id=\"TmgsTable\"]/tbody/tr/td"));
//        seatsAll.findElement(By.)

        /* 결제하기 버튼 클릭 */
        driver.switchTo().defaultContent();
        driver.switchTo().frame(driver.findElement(By.id("ifrmSeat")));
        driver.findElement(By.xpath("/html/body/form[1]/div/div[1]/div[3]/div/div[4]/a[2]")).click();

        driver.navigate().forward();


        // 여기서부터는 손으로 해도 됨
        /* 기본가로 1매 선택 */
        driver.switchTo().frame(driver.findElement(By.id("ifrmBookStep")));
        Select price = new Select(driver.findElement(By.xpath("//*[@id=\"PriceRow001\"]/td[3]/select")));
        price.selectByValue(Integer.toString(totalSeat));

        /* 다음단계 */
        driver.switchTo().defaultContent();
        js.executeScript("fnNextStep('P')");

        /* 생년월일 입력 */
        driver.switchTo().frame(driver.findElement(By.id("ifrmBookStep")));
        driver.findElement(By.id("YYMMDD")).sendKeys("990402");

        /* 다음단계 */
        driver.switchTo().defaultContent();
        js.executeScript("fnNextStep('P')");

//        마지막에 주석해제
//        driver.quit();
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

}