package Function;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.testng.annotations.AfterMethod;

public class CaptureScreen {
	@AfterMethod
	public void captureScreen(String checkValue) {
		try {
			String path = null;
			WebDriver agmDriver = new Augmenter().augment(testCondition.driver);
			File screen = ((TakesScreenshot)agmDriver).getScreenshotAs(OutputType.FILE);
			
			DateFormat dateformat = new SimpleDateFormat("dd-mm-yyyyh-m-s");
			Date date = new Date();
			String filename = "Test_" +checkValue+ "_" + dateformat.format(date) + ".png";
			
			path = "./file/screenshots/" + filename;
			
			FileUtils.copyFile(screen,new File(path));
			screen.delete();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
