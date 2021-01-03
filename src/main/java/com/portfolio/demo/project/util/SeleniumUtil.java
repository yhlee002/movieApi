package com.portfolio.demo.project.util;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SeleniumUtil {
    private static ChromeDriver driver;

    public void initializeDriver() {
// Windows 운영체제이므로 [ chromedriver.exe ] 파일의 경로
        Path path = Paths.get("C:\\\\drivers\\chromedriver.exe");
        System.out.println(path.toString());

        // WebDriver 경로 설정
        System.setProperty("webdriver.chrome.driver", path.toString());

        // WebDriver 옵션 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless", "window-size=1920x1080", "disable-gpu", "disable-infobars", "--disable-extensions");

        Map<String, Object> prefs = new HashMap<>(); // "profile.default_content_setting_values"
        prefs.put("cookies", false);
        prefs.put("images", false);
        prefs.put("plugins", false);
        prefs.put("popups", false);
        prefs.put("geolocation", false);
        prefs.put("notifications", false);
        prefs.put("auto_select_certificate", false);
        prefs.put("fullscreen", false);
        prefs.put("mouselock", false);
        prefs.put("mixed_script", false);
        prefs.put("media_stream", false);
        prefs.put("media_stream_mic", false);
        prefs.put("media_stream_camera", false);
        prefs.put("protocol_handlers", false);
        prefs.put("ppapi_broker", false);
        prefs.put("automatic_downloads", false);
        prefs.put("midi_sysex", false);
        prefs.put("push_messaging", false);
        prefs.put("ssl_cert_decisions", false);
        prefs.put("metro_switch_to_desktop", false);
        prefs.put("protected_media_identifier", false);
        prefs.put("app_banner", false);
        prefs.put("site_engagement", false);
        prefs.put("durable_storage", false);

        options.setExperimentalOption("prefs", prefs);

        // WebDriver 객체 생성
        driver = new ChromeDriver(options);

        // 빈 탭 생성
        driver.executeScript("window.open('about:blank','_blank');");

        // 탭 목록 가져오기
        List<String> tabs = new ArrayList<String>(driver.getWindowHandles());

        // 첫번째 탭으로 전환
        driver.switchTo().window(tabs.get(0));

    }

    @Synchronized
    public String getImgUrl(String movieCd) {
//        // Windows 운영체제이므로 [ chromedriver.exe ] 파일의 경로
//        Path path = Paths.get("C:\\\\drivers\\chromedriver.exe");
//        System.out.println(path.toString());
//
//        // WebDriver 경로 설정
//        System.setProperty("webdriver.chrome.driver", path.toString());
//
//        // WebDriver 옵션 설정
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("headless");
//
//        // WebDriver 객체 생성
//        driver = new ChromeDriver(options);
//
//        // 빈 탭 생성
//        driver.executeScript("window.open('about:blank','_blank');");
//
//        // 탭 목록 가져오기
//        List<String> tabs = new ArrayList<String>(driver.getWindowHandles());
//
//        // 첫번째 탭으로 전환
//        driver.switchTo().window(tabs.get(0));

        // 웹페이지 요청
        driver.get("http://kobis.or.kr/kobis/business/mast/mvie/searchMovieList.do?dtTp=movie&dtCd=" + movieCd);

        String imgUrl = null;
        WebElement imgUrlElement = driver.findElement(By.className("thumb"));
        if (imgUrl != null) {
            System.out.println("imgUrl.getAttribute(\"href\") : " + imgUrlElement.getAttribute("href"));
            imgUrl = imgUrlElement.getAttribute("href");
            log.info("imgUrl(movieCd=" + movieCd + ") : " + imgUrl);
        }

        // 웹페이지 소스 출력
        //System.out.println( driver.getPageSource() );

//        // 탭 종료
//        driver.close();

//        // 5초 후에 WebDriver 종료
//        try {
//            Thread.sleep(4000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            // WebDriver 종료
//            driver.quit();
//        }
        return imgUrl;
    }

    public void quit() {
        // 탭 종료
        driver.close();

        // 5초 후에 WebDriver 종료
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // WebDriver 종료
            driver.quit();
        }
    }

}
