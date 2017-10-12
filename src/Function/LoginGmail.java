package Function;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

public class LoginGmail {
	WebDriver driver = testCondition.driver;
	CaptureScreen captureSc = new CaptureScreen();
	
	@FindBy(id="identifierId")
	WebElement identifierId;
	
	@FindBy(id="identifierNext")
	WebElement identifierNext;
	
	@FindBy(name="password")
	WebElement password;
	
	@FindBy(id="passwordNext")
	WebElement passwordNext;
	
	
	public LoginGmail()
	{
		PageFactory.initElements(driver, this);
	}
	
	public void set_identifierId(String id) {
		identifierId.clear();
		identifierId.sendKeys(id);
	}
	
	public void  set_password(String pw) {
		password.clear();
		password.sendKeys(pw);
	}
	
	public void click_btnid() {
		identifierNext.click();
	}
	public boolean click_btnpw() throws InterruptedException {
		passwordNext.click();
		Thread.sleep(2000);
		captureSc.captureScreen("Password");
		return !checkPassword() ? false : true;
	}
	
	
	@BeforeTest
	public boolean checkLoginGmail(String accounts[][]) throws InterruptedException, ClassNotFoundException, SQLException
	{
		//タイムアウトを60に変更
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.get("https://www.gmail.com");
		for (String[] account : accounts) {
			if(!LoginAccount(account))
			{
				continue;
			}
			return true;
		}
		return false;
	}
	
	public boolean isValidationMsgNotExist() {
		return !driver.findElement(By.className("LXRPh")).getText().isEmpty() ? false : true;
	}
	
	public boolean checkPassword()
	{
		if(!driver.getCurrentUrl().equals("https://mail.google.com/mail/u/0/#inbox"))
		{
			driver.get("https://www.gmail.com");
			return false;
		}
		
		return true;
	}

	private boolean LoginAccount(String[] acc) throws InterruptedException, ClassNotFoundException, SQLException {
		set_identifierId(acc[0]);
		click_btnid();
		captureSc.captureScreen("ID");
		Thread.sleep(2000);
		if(isValidationMsgNotExist())
		{
			set_password(acc[1]);
			if(!click_btnpw()){
				return false;
			 }else{
				 testCondition.saveAccountLogin(acc);
				 return true;
			 }
		}
		return false;
	}
	
	
	@AfterTest
	public void close()
	{
		driver.close();
	}
}
