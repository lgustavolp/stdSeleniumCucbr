package base;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.google.common.io.Files;

import pages.HomePage;

public class BaseTests {
	
	private static WebDriver driver;
	protected HomePage homePage;
	
	@BeforeAll // JUnit 5
//	@BeforeClass // Junit 4
	public static void inicializar() {
		System.setProperty("webdriver.chrome.driver","C:\\webdrivers\\chromedriver\\84\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}
	
	@BeforeEach
	public void carregarPaginaInicial() {
		driver.get("https://marcelodebittencourt.com/demoprestashop/");
		homePage = new HomePage(driver);
	}
	
	public void capturarTela(String nomeTeste, String resultado) {
		var camera = (TakesScreenshot) driver;
		File capturaDeTela = camera.getScreenshotAs(OutputType.FILE);
		try {
			Files.move(capturaDeTela, new File("./resources/screenshots/" + nomeTeste + "_" + resultado + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@AfterAll // JUnit 5
//	@AfterClass // JUnit 4
		public static void finalizar() {
		driver.quit();
	}
}
