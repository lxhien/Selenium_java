package Function;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
			
			String path = "./file/screenshots/";
			WebDriver agmDriver = new Augmenter().augment(testCondition.driver);
			File screen = ((TakesScreenshot)agmDriver).getScreenshotAs(OutputType.FILE);
			
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate localdate = LocalDate.now();
			File folder = new File(path + dtf.format(localdate));
			
			if(!folder.exists())
			{
				folder.mkdir();
			}
			
			DateFormat dateformat = new SimpleDateFormat("dd-mm-yyyyh-m-s");
			Date date = new Date();
			String filename = checkValue + "_" + dateformat.format(date) + ".png";
			
			path = folder + "/" + filename;
			FileUtils.copyFile(screen,new File(path));
			screen.delete();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
