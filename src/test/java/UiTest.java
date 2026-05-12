import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

class UiTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = DriverManager.getDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        driver.get("https://www.mts.by/");
    }

    @Test
    void verifyOnlineTopUpBlock() {
        final By BLOCK_TITLE = By.xpath("//section[contains(@class, 'pay')]//h2");
        final By PAYMENT_LOGOS = By.xpath("//div[contains(@class, 'pay__partners')]//img");
        final By DETAILS_LINK = By.xpath("//section[contains(@class, 'pay')]//a[contains(text(), 'Подробнее о сервисе')]");
        final By PHONE_INPUT = By.xpath("//*[@id='connection-phone']");
        final By AMOUNT_INPUT = By.xpath("//*[@id='connection-sum']");
        final By EMAIL_INPUT = By.xpath("//*[@id='connection-email']");
        final By CONTINUE_BUTTON = By.xpath("//form[@id='pay-connection']//button[contains(text(), 'Продолжить')]");
        final By PAYMENT_FRAME = By.xpath(
                "//iframe[contains(@class, 'bepaid-iframe') or contains(@src, 'bepaid') or contains(@src, 'payment')]"
        );

        acceptCookies();

        WebElement blockTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(BLOCK_TITLE));
        scrollToElement(blockTitle);

        String actualTitle = blockTitle.getText().replace("\n", " ").trim();
        Assertions.assertEquals(
                "Онлайн пополнение без комиссии",
                actualTitle,
                "Block title is incorrect"
        );

        List<WebElement> paymentLogos = driver.findElements(PAYMENT_LOGOS);
        Assertions.assertEquals(5, paymentLogos.size(), "Payment systems logos count is incorrect");

        Assertions.assertTrue(hasLogo(paymentLogos, "Visa"), "Visa logo is missing");
        Assertions.assertTrue(hasLogo(paymentLogos, "Verified By Visa"), "Verified By Visa logo is missing");
        Assertions.assertTrue(hasLogo(paymentLogos, "MasterCard"), "MasterCard logo is missing");
        Assertions.assertTrue(hasLogo(paymentLogos, "MasterCard Secure Code"), "MasterCard Secure Code logo is missing");
        Assertions.assertTrue(hasLogo(paymentLogos, "Белкарт"), "Belkart logo is missing");

        WebElement detailsLink = wait.until(ExpectedConditions.elementToBeClickable(DETAILS_LINK));
        Assertions.assertEquals("Подробнее о сервисе", detailsLink.getText(), "Details link text is incorrect");
        Assertions.assertTrue(
                detailsLink.getAttribute("href").contains("/help/poryadok-oplaty-i-bezopasnost-internet-platezhey/"),
                "Details link URL is incorrect"
        );
        detailsLink.click();
        wait.until(ExpectedConditions.urlContains("/help/poryadok-oplaty-i-bezopasnost-internet-platezhey/"));
        driver.navigate().back();
        wait.until(ExpectedConditions.visibilityOfElementLocated(BLOCK_TITLE));

        driver.findElement(PHONE_INPUT).sendKeys("297777777");
        driver.findElement(AMOUNT_INPUT).sendKeys("10");
        driver.findElement(EMAIL_INPUT).sendKeys("test@test.com");
        driver.findElement(CONTINUE_BUTTON).click();

        Assertions.assertTrue(
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.urlContains("checkout"),
                        ExpectedConditions.urlContains("payment"),
                        ExpectedConditions.presenceOfElementLocated(PAYMENT_FRAME)
                )),
                "Continue button did not open payment page"
        );
    }

    @AfterEach
    void tearDown() {
        DriverManager.quitDriver();
    }

    private void acceptCookies() {
        final By COOKIE_BUTTON = By.id("cookie-agree");

        List<WebElement> cookies = driver.findElements(COOKIE_BUTTON);
        if (!cookies.isEmpty() && cookies.get(0).isDisplayed()) {
            cookies.get(0).click();
        }
    }

    private boolean hasLogo(List<WebElement> logos, String altText) {
        return logos.stream()
                .anyMatch(logo -> altText.equals(logo.getAttribute("alt")));
    }

    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }
}
