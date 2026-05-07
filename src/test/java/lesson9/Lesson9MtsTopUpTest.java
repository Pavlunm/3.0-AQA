package lesson9;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Lesson9MtsTopUpTest {

    private static final String BASE_URL = "https://www.mts.by";
    private static final String BLOCK_TITLE = "Онлайн пополнение без комиссии";
    private static final String TEST_PHONE = "297777777";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void verifyMtsTopUpBlock() {
        final By COOKIE_BUTTON = By.xpath("//*[contains(text(), 'Принять все cookie')]");
        final By BLOCK_HEADER = By.xpath("//*[contains(normalize-space(.), 'Онлайн пополнение без комиссии')]");
        final By BLOCK_SECTION = By.xpath("//*[contains(normalize-space(.), 'Онлайн пополнение без комиссии')]/ancestor::section[1]");
        final By PAYMENT_LOGOS = By.xpath(".//img[@src and @alt]");
        final By MORE_DETAILS_LINK = By.xpath(".//a[contains(., 'Подробнее о сервисе')]");
        final By SERVICES_TAB = By.xpath(".//*[self::button or self::li or self::a][contains(., 'Услуги связи')]");
        final By PHONE_INPUT = By.xpath(".//input[contains(@placeholder, 'Номер')]");
        final By CONTINUE_BUTTON = By.xpath(".//button[contains(., 'Продолжить')]");
        final By PAYMENT_FORM = By.xpath("//iframe[contains(@name, 'pay') or contains(@src, 'pay')] | //form[contains(@action, 'pay')]");

        driver.get(BASE_URL);
        acceptCookiesIfPresent(COOKIE_BUTTON);

        WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(BLOCK_HEADER));
        Assertions.assertEquals(BLOCK_TITLE, header.getText().trim(), "Неверное название блока");

        WebElement section = wait.until(ExpectedConditions.visibilityOfElementLocated(BLOCK_SECTION));
        List<WebElement> logos = section.findElements(PAYMENT_LOGOS);
        Assertions.assertFalse(logos.isEmpty(), "Логотипы платежных систем не найдены");

        WebElement detailsLink = section.findElement(MORE_DETAILS_LINK);
        Assertions.assertTrue(detailsLink.isDisplayed(), "Ссылка 'Подробнее о сервисе' не отображается");
        String currentWindow = driver.getWindowHandle();
        int windowsBeforeClick = driver.getWindowHandles().size();
        detailsLink.click();
        wait.until(d -> d.getWindowHandles().size() >= windowsBeforeClick);

        if (driver.getWindowHandles().size() > windowsBeforeClick) {
            List<String> windows = new ArrayList<>(driver.getWindowHandles());
            windows.remove(currentWindow);
            driver.switchTo().window(windows.get(0));
            wait.until(ExpectedConditions.urlContains("poryadok-oplaty-i-bezopasnost-internet-platezhey"));
            Assertions.assertTrue(driver.getCurrentUrl().contains("poryadok-oplaty-i-bezopasnost-internet-platezhey"),
                    "Ссылка 'Подробнее о сервисе' открыла неверную страницу");
            driver.close();
            driver.switchTo().window(currentWindow);
        } else {
            wait.until(ExpectedConditions.urlContains("poryadok-oplaty-i-bezopasnost-internet-platezhey"));
            Assertions.assertTrue(driver.getCurrentUrl().contains("poryadok-oplaty-i-bezopasnost-internet-platezhey"),
                    "Ссылка 'Подробнее о сервисе' открыла неверную страницу");
            driver.navigate().back();
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(BLOCK_SECTION));
        acceptCookiesIfPresent(COOKIE_BUTTON);
        section = wait.until(ExpectedConditions.visibilityOfElementLocated(BLOCK_SECTION));

        section.findElement(SERVICES_TAB).click();
        WebElement phoneInput = wait.until(ExpectedConditions.elementToBeClickable(section.findElement(PHONE_INPUT)));
        phoneInput.clear();
        phoneInput.sendKeys(TEST_PHONE);
        section.findElement(CONTINUE_BUTTON).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(PAYMENT_FORM));
    }

    private void acceptCookiesIfPresent(By cookieButton) {
        List<WebElement> buttons = driver.findElements(cookieButton);
        if (!buttons.isEmpty() && buttons.get(0).isDisplayed()) {
            buttons.get(0).click();
        }
    }
}
