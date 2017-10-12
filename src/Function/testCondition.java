package Function;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

public class testCondition {

	public static WebDriver driver = null;
	private static Statement st = null;
	public static Connection con = null;
	private final static String file_account = "gmail_account.xlsx";
	
	public static void main(String[] args) throws InterruptedException, IOException, SQLException, ClassNotFoundException 
	{
		String accounts[][] =  testCondition.readExcelData(file_account);
		System.setProperty("webdriver.gecko.driver", "E:\\WORK\\Selenium\\244\\geckodriver\\geckodriver.exe");
		driver = new FirefoxDriver();
		Thread.sleep(2000);
		getConnect();
		LoginGmail lg = new LoginGmail();
		if(lg.checkLoginGmail(accounts))
		{
			getSite();
			readExcelInsetToDatabase();
			writeExcel();
		}
	}
	
	private static void getSite() {
		//driver = swichWindow();
		
		//driver = seachAutoWebDriver();
		//driver = searchForSeleniumWebsite();
		
		//System.setProperty("webdriver.chrome.driver",  "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
		//WebDriver driver = new ChromeDriver();
		//driver.get("https://www.nikkei.com/");
		
		String sTile = driver.getTitle();
		int iTitleLenght = driver.getTitle().length();
		
		
		System.out.println("tile : "+sTile);
		System.out.println("lenght:"+iTitleLenght);
		
		sTile = driver.getCurrentUrl();
		iTitleLenght = driver.getCurrentUrl().length();
		
		System.out.println("tile : "+sTile);
		System.out.println("lenght:"+iTitleLenght);
		
		int iSrcLenght = driver.getPageSource().length();
		System.out.println("src lenht: "+iSrcLenght);
		
		//driver.close();
	}
	public static void getConnect() throws SQLException, ClassNotFoundException {
		try 
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl","system","123");
			st = con.createStatement();
			//String sql = "select COUNTRY_ID from HR.COUNTRIES";
			//ResultSet rs = st.executeQuery(sql);
			//while (rs.next())
			//{
			//	System.out.println(rs.getString(1));
			//}
			//con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	private static String Path = "./file/excel/";
	public static String[][] readExcelData(String filename) {
		String accTmp[][] = new String[5][2];
		try 
		{
			String fullpath = Path + filename;
			FileInputStream file = new FileInputStream(new File(fullpath));
			
			XSSFWorkbook wb = new XSSFWorkbook(file);
			
			XSSFSheet sheet = wb.getSheetAt(0);		
			int rowscount = sheet.getLastRowNum();
			
			for (int i = 0; i <= rowscount; i++) 
			{
				//ヘーダ読み込まない
				if(0 == i)
				{
					continue;
				}
				Row row = sheet.getRow(i);
				int cellscount =  row.getLastCellNum();
				
				for (int j = 0; j < cellscount; j++) 
				{
					Cell cell = row.getCell(j);
					
					String data = cell.getStringCellValue();
					accTmp[i-1][j] = data;
				}
			}
			wb.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return accTmp;
	}
	
	private static void writeExcel() throws IOException 
	{
		String filename = Path + "new.xlsx";
		String sheetname = "Sheet1";
		
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetname);
		for (int i = 0; i < 3; i++)
		{
			XSSFRow row = sheet.createRow(i);
			
			for (int j = 0; j < 3; j++)
			{
				XSSFCell cell = row.createCell(j);
				cell.setCellValue("Cell" +i+ " "+j);
			}
		}
		FileOutputStream fileout = new FileOutputStream(filename);
		
		wb.write(fileout);
		
		System.out.println("");
		fileout.flush();
		fileout.close();
		wb.close();
	}
	private static void readExcelInsetToDatabase() throws SQLException
	{
		//driver.get("http://www.google.co.jp/");
		//driver.manage().window().maximize();
		//WebElement searchbox = driver.findElement(By.name("q"));
		
		try{
			String fullPath = Path + "Book1.xlsx";
			FileInputStream file = new FileInputStream(new File(fullPath));
			XSSFWorkbook wb = new XSSFWorkbook(file);
			int totalSheet = wb.getNumberOfSheets();
			
			if (0 < totalSheet) 
			{
				String seid = null;
				double age = 0;
				double pacent = 0;
				String school = null;
				String station = null;
				for (int i = 0; i < totalSheet; i++) 
				{
					XSSFSheet sheet = wb.getSheetAt(i);
					Iterator<Row> rowIterator = sheet.iterator();
					while(rowIterator.hasNext())
					{
						Row row = rowIterator.next();
						Iterator<Cell> celIterator = row.cellIterator();
						int cellnum = 0;
						while(celIterator.hasNext())
						{
							Cell cel = celIterator.next();
							if(0 == cellnum)
							{
								seid = cel.getStringCellValue();
								cellnum++;
							
							}else if(1 == cellnum)
							{
								if(1 == i)
								{
									school = cel.getStringCellValue();
								}else{
									age = cel.getNumericCellValue();
								}
								cellnum++;
							}else{
								if(1 == i){
									station = cel.getStringCellValue();
								}else{
									pacent = cel.getNumericCellValue();
								}
								break;
							}
						}
						String sql = null;
						if(1 == i)
						{
							sql = "SELECT * FROM HR.SCHOOL WHERE HR.SCHOOL.NO IN('"+seid+"')";
						}else
						{
							sql = "SELECT * FROM HR.SELENIUM WHERE HR.SELENIUM.SEID IN('"+seid+"')";
						}	
						ResultSet rs = st.executeQuery(sql);
						PreparedStatement statement = null;
						if(!rs.next())
						{
							if(1 == i){
								statement = con.prepareStatement("INSERT INTO HR.SCHOOL (NO, SCHOOLNAME,STATION) VALUES ( ?, ? , ?)");
								statement.setString(1, seid);
								statement.setString(2, school );
								statement.setString(3, station );
							}else{
								statement = con.prepareStatement("INSERT INTO HR.SELENIUM (SEID, AGE,PACENT) VALUES ( ?, ? , ?)");
								statement.setString(1, seid);
								statement.setDouble(2, age );
								statement.setDouble(3, pacent );
							}
							statement.execute();
						}
					}
				}
			}
			//con.close();
			wb.close();
			file.close();
		}catch(FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveAccountLogin(String[] acc) throws SQLException {
		 PreparedStatement statement = null;
		 String sql = "SELECT COUNT(*) FROM HR.GMAIL_ACCOUNT WHERE HR.GMAIL_ACCOUNT.EMAIL ='"+acc[0]+"'";
		 ResultSet rs = st.executeQuery(sql);
		 if(rs.next() && rs.getInt("COUNT(*)") == 0)
		 {
			 sql = "INSERT INTO HR.GMAIL_ACCOUNT VALUES(? , ?)";
			 statement = con.prepareStatement(sql);
			 statement.setString(1, acc[0]);
			 statement.setString(2, acc[1]);
			 statement.execute();
		 }
		 rs.close();
	}
	private static WebDriver swichWindow() {
		driver.get("https://vdata.nikkei.com/economicdashboard/macro/");
		String handler = driver.getWindowHandle();
		System.out.println("hanlde :"+handler);
		return driver;
	}

	private static WebDriver seachAutoWebDriver() {
		driver.get("https://www.nikkei.com/");
		driver.findElement(By.linkText("アジア")).click();
		driver.navigate().back();
		driver.navigate().forward();
		driver.navigate().to("https://www.nikkei.com/");
		//driver.navigate().refresh();
		return driver;
	}

	private static WebDriver searchForSeleniumWebsite() throws InterruptedException
	{
		driver.get("http://www.google.co.jp/");
		CharSequence[] keyword = {"nikke"};
		searchFor(keyword);
		//assertThat(driver.getTitle(),is("selenium - Google 検索"));
		//driver.quit();
		
		//driver.navigate().to("https://www.nikkei.com/");
		//driver.navigate().forward();
		driver.navigate().back();
		return driver;
		 
	}

	private static void searchFor(CharSequence[] keyword) throws InterruptedException{
		WebElement searchBox = driver.findElement(By.name("q"));
		searchBox.sendKeys(keyword);
		searchBox.submit();
		driver.get("");
		//searchBox.sendKeys("\n");
		Thread.sleep(3000);
	}

}
