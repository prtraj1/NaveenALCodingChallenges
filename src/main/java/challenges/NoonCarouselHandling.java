package challenges;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class NoonCarouselHandling {
	public static WebDriver driver;

	public static void main(String[] args) {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("https://www.noon.com/uae-en/");
		String[] headers = { "Recommended For You", "Save big on mobiles & tablets", "Top picks in laptops",
				"Bestselling fragrances", "invalid container" };

		for (String header : headers) {
			sectionName(header);
		}
		driver.quit();
	}

	public static void sectionName(String containerName) {
		String xPath = "//div[div[h3[contains(.,'" + containerName
				+ "')]]]/following-sibling::div/div/div/div/div/a/div/div[2]/div[@data-qa='product-name']/div";
		String nextBtn = "//div[div[h3[contains(.,'" + containerName
				+ "')]]]/following-sibling::div/div[contains(@class,'swiper-button-next')]";

		Set<String> products = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		int scrollCnt = 0;
		int maxScroll = 25;

		while (true) {
			js.executeScript("window.scrollBy(0, 1000)");
			scrollCnt++;
			pauseExecution(1);
			if (verifyElementVisible(By.xpath(xPath))) {
				js.executeScript("arguments[0].scrollIntoView(false);", driver.findElement(By.xpath(xPath)));
				break;
			}
			if (scrollCnt == maxScroll) {
				System.out.println("Maximum allowed scroll reached for finding carousel- "+containerName);
				break;
			}
		}
		
		if (verifyElementVisible(By.xpath(xPath))) {
			do {
				List<WebElement> prodList = driver.findElements(By.xpath(xPath));
				for (WebElement elm : prodList) {
					products.add(elm.getText().trim());
				}
				driver.findElement(By.xpath(nextBtn)).click();
			} while (!driver.findElement(By.xpath(nextBtn)).getAttribute("class").contains("swiper-button-disabled"));
		}

		products.forEach(System.out::println);
		js.executeScript("window.scrollTo(0,0);");

	}

	public static boolean verifyElementVisible(final By locator) {
		try {
			new WebDriverWait(driver, 1).ignoring(NoSuchElementException.class).pollingEvery(Duration.ofMillis(5))
					.until(ExpectedConditions.presenceOfElementLocated(locator));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void pauseExecution(long seconds) {
		try {
			TimeUnit.SECONDS.sleep(seconds);
		} catch (InterruptedException e) {

		}
	}
}
