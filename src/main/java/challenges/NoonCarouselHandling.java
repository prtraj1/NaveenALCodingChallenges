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
		String[] carousels = { "Recommended For You", "Limited time offers", "Save big on mobiles & tablets",
				"Top picks in laptops", "Bestselling fragrances", "Sports shoes under 199 AED" };

		for (String sectionName : carousels) {
			System.out.println("------------" + sectionName + "------------");
			printProductList(sectionName).forEach(System.out::println);
		}
		driver.quit();
	}

	public static Set<String> printProductList(String sectionName) {
		String productsXpath = "//div[div[h3[contains(.,'" + sectionName
				+ "')]]]/following-sibling::div/div/div/div/div/a/div/div[2]/div[@data-qa='product-name']/div";
		String nextBtnXpath = "//div[div[h3[contains(.,'" + sectionName
				+ "')]]]/following-sibling::div/div[contains(@class,'swiper-button-next')]";

		Set<String> products = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		boolean carouselVisible = false;
		int scrollCnt = 0;
		int maxScroll = 25;

		while (true) {
			if (verifyElementVisible(By.xpath(productsXpath))) {
				carouselVisible = true;
				break;
			}
			js.executeScript("window.scrollBy(0, 1000)");
			scrollCnt++;
			if (scrollCnt == maxScroll) {
				System.out.println("Maximum allowed scrolling reached for finding carousel- " + sectionName);
				break;
			}
		}

		if (carouselVisible) {
			do {
				List<WebElement> prodList = driver.findElements(By.xpath(productsXpath));
				for (WebElement elm : prodList) {
					products.add(elm.getText().trim());
				}
				if (!driver.findElement(By.xpath(nextBtnXpath)).getAttribute("class")
						.contains("swiper-button-disabled"))
					driver.findElement(By.xpath(nextBtnXpath)).click();
			} while (!driver.findElement(By.xpath(nextBtnXpath)).getAttribute("class")
					.contains("swiper-button-disabled"));
		}

		js.executeScript("window.scrollTo(0,0);");

		return products;

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
