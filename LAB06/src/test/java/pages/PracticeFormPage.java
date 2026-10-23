package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PracticeFormPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By firstNameInput = By.id("firstName");
    private By lastNameInput = By.id("lastName");
    private By emailInput = By.id("userEmail");
    private By mobileInput = By.id("userNumber");
    private By dateOfBirthInput = By.id("dateOfBirthInput");
    private By monthSelect = By.className("react-datepicker__month-select");
    private By yearSelect = By.className("react-datepicker__year-select");
    private By uploadPictureInput = By.id("uploadPicture");
    private By currentAddressInput = By.id("currentAddress");
    private By submitButton = By.id("submit");

    // Modal Locators
    private By modalDialog = By.className("modal-content");
    private By modalTitle = By.id("example-modal-sizes-title-lg");
    private By tableRows = By.xpath("//div[@class='table-responsive']//tbody/tr");

    public PracticeFormPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void enterFirstName(String firstName) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput)).sendKeys(firstName);
    }

    public void enterLastName(String lastName) {
        driver.findElement(lastNameInput).sendKeys(lastName);
    }

    public void enterEmail(String email) {
        driver.findElement(emailInput).sendKeys(email);
    }

    public void selectGender(String gender) {
        By genderOption = By.xpath("//label[text()='" + gender + "']");
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(genderOption));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public void enterMobile(String mobile) {
        driver.findElement(mobileInput).sendKeys(mobile);
    }

    public void selectDateOfBirth(String day, String month, String year) {
        WebElement dob = wait.until(ExpectedConditions.elementToBeClickable(dateOfBirthInput));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", dob);
        dob.click();

        new Select(driver.findElement(yearSelect)).selectByValue(year);
        new Select(driver.findElement(monthSelect)).selectByVisibleText(month);

        String formattedDay = day.length() == 1 ? "0" + day : day;
        By dayLocator = By.xpath("//div[contains(@class,'react-datepicker__day--0" + formattedDay + "') and not(contains(@class,'outside-month'))]");
        driver.findElement(dayLocator).click();
    }

    public void selectHobby(String hobby) {
        By hobbyCheckbox = By.xpath("//label[text()='" + hobby + "']");
        WebElement element = driver.findElement(hobbyCheckbox);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public void uploadFile(String relativeFilePath) {
        File file = new File(relativeFilePath);
        driver.findElement(uploadPictureInput).sendKeys(file.getAbsolutePath());
    }

    public void enterCurrentAddress(String address) {
        driver.findElement(currentAddressInput).sendKeys(address);
    }

    public void clickSubmit() {
        WebElement submit = wait.until(ExpectedConditions.presenceOfElementLocated(submitButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submit);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submit);
    }

    public boolean isSuccessModalDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(modalDialog)).isDisplayed();
    }

    public String getModalTitle() {
        return driver.findElement(modalTitle).getText();
    }

    public Map<String, String> getResultTableData() {
    	List<WebElement> rows = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(tableRows));
        Map<String, String> resultData = new HashMap<>();
        for (WebElement row : rows) {
            List<WebElement> cols = row.findElements(By.tagName("td"));
            if (cols.size() == 2) {
                resultData.put(cols.get(0).getText().trim(), cols.get(1).getText().trim());
            }
        }
        return resultData;
    }
}