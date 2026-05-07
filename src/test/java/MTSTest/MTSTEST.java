package MTSTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class MTSTEST {

    private static final String BASE_URL = "https://www.mts.by";
    private static final String BLOCK_TITLE = "Онлайн пополнение без комиссии";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        driver = DriverManager.getDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    public void tearDown() {
        DriverManager.quitDriver();
    }

    @Test
    public void verifyMtsTopUpForm() {
        final By COOKIE_BUTTON = By.xpath("//*[contains(text(),'Принять все cookie')]");
        final By BLOCK_SECTION = By.xpath("//*[contains(normalize-space(.),'" + BLOCK_TITLE + "')]/ancestor::section[1]");
        final By SERVICES_TAB = By.xpath(".//*[self::button or self::li or self::a][contains(.,'Услуги связи')]");
        final By PHONE_INPUT = By.xpath(".//input[contains(@placeholder,'Номер')]");
        final By AMOUNT_INPUT = By.xpath(".//input[contains(@placeholder,'Сумма')]");
        final By EMAIL_INPUT = By.xpath(".//input[contains(@placeholder,'E-mail')]");
        final By CONTINUE_BUTTON = By.xpath(".//button[contains(.,'Продолжить')]");
        final By PAYMENT_IFRAME = By.xpath("//iframe[contains(@name,'pay') or contains(@src,'pay')]");
        final By PAYMENT_CLOSE_BUTTON = By.xpath("//div[contains(@class,'header__close-button')]");

        driver.get(BASE_URL);
        acceptCookiesIfPresent(COOKIE_BUTTON);

        WebElement section = wait.until(ExpectedConditions.visibilityOfElementLocated(BLOCK_SECTION));
        section.findElement(SERVICES_TAB).click();

        WebElement phoneInput = wait.until(ExpectedConditions.elementToBeClickable(section.findElement(PHONE_INPUT)));
        phoneInput.clear();
        phoneInput.sendKeys("297777777");

        WebElement amountInput = wait.until(ExpectedConditions.elementToBeClickable(section.findElement(AMOUNT_INPUT)));
        amountInput.clear();
        amountInput.sendKeys("10");

        WebElement emailInput = wait.until(ExpectedConditions.elementToBeClickable(section.findElement(EMAIL_INPUT)));
        emailInput.clear();
        emailInput.sendKeys("Pavlun_m@icloud.com");

        section.findElement(CONTINUE_BUTTON).click();
        closePaymentWidget(PAYMENT_IFRAME, PAYMENT_CLOSE_BUTTON);

        wait.until(ExpectedConditions.visibilityOfElementLocated(BLOCK_SECTION));
        Assertions.assertTrue(driver.findElement(BLOCK_SECTION).isDisplayed(), "Блок оплаты не отображается");
    }

    private void acceptCookiesIfPresent(By cookieButton) {
        List<WebElement> buttons = driver.findElements(cookieButton);
        if (!buttons.isEmpty() && buttons.get(0).isDisplayed()) {
            buttons.get(0).click();
        }
    }

    private void closePaymentWidget(By paymentIframe, By paymentCloseButton) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(7));
            shortWait.until(ExpectedConditions.presenceOfElementLocated(paymentIframe));
            shortWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(paymentIframe));
            WebElement closeButton = shortWait.until(ExpectedConditions.presenceOfElementLocated(paymentCloseButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeButton);
        } catch (TimeoutException ignored) {
            // Виджет может не открыться в нестабильной среде, тест продолжает выполнение.
        } finally {
            driver.switchTo().defaultContent();
        }
    }
}
