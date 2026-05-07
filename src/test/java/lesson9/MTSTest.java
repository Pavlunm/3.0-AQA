package lesson9;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MTSTest {

    private static final String BASE_URL = "https://www.mts.by";
    private static final String BLOCK_TITLE = "Онлайн пополнение без комиссии";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldValidateOnlineTopUpBlock() {
        driver.get(BASE_URL);
        closeCookieBannerIfPresent();

        WebElement blockHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(normalize-space(.),'" + BLOCK_TITLE + "')]")));
        Assertions.assertTrue(blockHeader.isDisplayed(), "Заголовок блока не отображается");

        WebElement paymentSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(normalize-space(.),'" + BLOCK_TITLE + "')]/ancestor::section[1]")));
        List<WebElement> paymentLogos = paymentSection.findElements(By.xpath(".//img[@src and @alt]"));
        Assertions.assertFalse(paymentLogos.isEmpty(), "Логотипы платежных систем не найдены");

        WebElement detailsLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//*[contains(normalize-space(.),'" + BLOCK_TITLE + "')]/ancestor::section[1]//a[contains(.,'Подробнее о сервисе')]")));
        String expectedLink = detailsLink.getAttribute("href");
        Assertions.assertNotNull(expectedLink, "У ссылки 'Подробнее о сервисе' отсутствует href");
        Assertions.assertFalse(expectedLink.isBlank(), "У ссылки 'Подробнее о сервисе' пустой href");
        openAndVerifyServiceLink(detailsLink, expectedLink);

        WebElement serviceTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(normalize-space(.),'" + BLOCK_TITLE + "')]/ancestor::section[1]//*[self::button or self::li or self::a][contains(.,'Услуги связи')]")));
        serviceTab.click();

        WebElement phoneInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(normalize-space(.),'" + BLOCK_TITLE + "')]/ancestor::section[1]//input[contains(@placeholder,'Номер')]")));
        phoneInput.clear();
        phoneInput.sendKeys("297777777");

        WebElement amountInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(normalize-space(.),'" + BLOCK_TITLE + "')]/ancestor::section[1]//input[contains(@placeholder,'Сумма')]")));
        amountInput.clear();
        amountInput.sendKeys("10");

        WebElement emailInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(normalize-space(.),'" + BLOCK_TITLE + "')]/ancestor::section[1]//input[contains(@placeholder,'E-mail')]")));
        emailInput.clear();
        emailInput.sendKeys("Pavlun_m@icloud.com");

        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(normalize-space(.),'" + BLOCK_TITLE + "')]/ancestor::section[1]//button[contains(.,'Продолжить')]")));
        continueButton.click();

        By paymentIframe = By.xpath("//iframe[contains(@name,'pay') or contains(@src,'pay')]");
        closePaymentWidget(paymentIframe);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(normalize-space(.),'" + BLOCK_TITLE + "')]")));
    }

    private void closeCookieBannerIfPresent() {
        List<By> cookieButtons = List.of(
                By.xpath("//button[contains(.,'Принять')]"),
                By.xpath("//*[contains(text(),'Принять все cookie')]/ancestor::button[1]")
        );
        for (By locator : cookieButtons) {
            List<WebElement> elements = driver.findElements(locator);
            if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                elements.get(0).click();
                break;
            }
        }
    }

    private void openAndVerifyServiceLink(WebElement detailsLink, String expectedLink) {
        String currentWindow = driver.getWindowHandle();
        int windowsBeforeClick = driver.getWindowHandles().size();
        detailsLink.click();

        wait.until(d -> d.getWindowHandles().size() >= windowsBeforeClick);
        if (driver.getWindowHandles().size() > windowsBeforeClick) {
            List<String> windows = new ArrayList<>(driver.getWindowHandles());
            windows.remove(currentWindow);
            driver.switchTo().window(windows.get(0));
            wait.until(ExpectedConditions.urlContains(expectedLink));
            Assertions.assertTrue(driver.getCurrentUrl().contains(expectedLink),
                    "Ссылка 'Подробнее о сервисе' открыла неверную страницу");
            driver.close();
            driver.switchTo().window(currentWindow);
        } else {
            wait.until(ExpectedConditions.urlContains(expectedLink));
            Assertions.assertTrue(driver.getCurrentUrl().contains(expectedLink),
                    "Ссылка 'Подробнее о сервисе' открыла неверную страницу");
            driver.navigate().back();
        }
        closeCookieBannerIfPresent();
    }

    private void closePaymentWidget(By paymentIframe) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(7));
            shortWait.until(ExpectedConditions.presenceOfElementLocated(paymentIframe));
            shortWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(paymentIframe));
            By closeButtonBy = By.xpath("//div[contains(@class,'header__close-button')]");
            WebElement closeButton = shortWait.until(ExpectedConditions.presenceOfElementLocated(closeButtonBy));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeButton);
        } catch (TimeoutException ignored) {
            // Не падаем тестом, если платежный iframe/крестик не успели появиться.
        } finally {
            driver.switchTo().defaultContent();
        }
    }
}
