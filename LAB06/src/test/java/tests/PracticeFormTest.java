package tests;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;
import pages.PracticeFormPage;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class PracticeFormTest {
    private WebDriver driver;
    private PracticeFormPage formPage;
    private final String EXPECTED_URL = "https://demoqa.com/automation-practice-form";

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        
        driver.get(EXPECTED_URL);
        formPage = new PracticeFormPage(driver);
    }

    @DataProvider(name = "formDataProvider")
    public Object[][] getFormData() {
        return new Object[][]{
            {"Anh", "Nguyen", "anh.nguyen@example.com", "Male", "0901234567", "15", "May", "2000", "Sports", "Ho Chi Minh City"},
            {"Thu", "Tran", "thu.tran@example.com", "Female", "0987654321", "20", "October", "2003", "Reading", "Binh Duong"},
            {"Minh", "Le", "minh.le@example.com", "Other", "0911223344", "01", "January", "1999", "Music", "Dong Nai"}
        };
    }

    @Test(dataProvider = "formDataProvider")
    public void testCompletePracticeForm(
            String fName, String lName, String email, String gender,
            String phone, String day, String month, String year,
            String hobby, String address) throws IOException {

        // 1. Kiểm tra URL & Tiêu đề
        Assert.assertEquals(driver.getCurrentUrl(), EXPECTED_URL, "URL không đúng!");
        Assert.assertFalse(driver.getTitle().isEmpty(), "Tiêu đề trang bị rỗng!");

        // 2. Điền thông tin
        formPage.enterFirstName(fName);
        formPage.enterLastName(lName);
        formPage.enterEmail(email);
        formPage.selectGender(gender);
        formPage.enterMobile(phone);
        formPage.selectDateOfBirth(day, month, year);
        formPage.selectHobby(hobby);
        formPage.uploadFile("test-data/sample.txt");
        formPage.enterCurrentAddress(address);

        // Chụp ảnh màn hình: TRƯỚC SUBMIT
        captureScreen("BeforeSubmit_" + fName);

        // 3. Submit Form
        formPage.clickSubmit();

        // Chụp ảnh màn hình: SAU SUBMIT
        captureScreen("AfterSubmit_" + fName);

        // 4. Kiểm tra modal và đối chiếu dữ liệu
        Assert.assertTrue(formPage.isSuccessModalDisplayed(), "Modal xác nhận không hiển thị!");
        Assert.assertEquals(formPage.getModalTitle(), "Thanks for submitting the form");

        Map<String, String> submittedData = formPage.getResultTableData();

        // Log kết quả ra Console
        System.out.println("========== KẾT QUẢ TEST CASE: " + fName + " " + lName + " ==========");
        submittedData.forEach((key, value) -> System.out.println(key + " : " + value));
        System.out.println("===============================================================\n");

        // Assert dữ liệu phản hồi
        Assert.assertEquals(submittedData.get("Student Name"), fName + " " + lName);
        Assert.assertEquals(submittedData.get("Student Email"), email);
        Assert.assertEquals(submittedData.get("Gender"), gender);
        Assert.assertEquals(submittedData.get("Mobile"), phone);
        Assert.assertEquals(submittedData.get("Picture"), "sample.txt");
        Assert.assertEquals(submittedData.get("Address"), address);
    }

    @AfterMethod
    public void tearDown(ITestResult result) throws IOException {
        if (result.getStatus() == ITestResult.FAILURE) {
            captureScreen("FAILED_" + result.getName());
        }
        if (driver != null) {
            driver.quit();
        }
    }

    public void captureScreen(String prefix) throws IOException {
        File dir = new File("screenshots");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File dest = new File("screenshots/" + prefix + "_" + time + ".png");
        FileUtils.copyFile(src, dest);
    }
}