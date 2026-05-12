package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

public class MainPage extends BasePage {

    private final By blockTitle = By.xpath("//section[contains(@class, 'pay')]//h2 | //div[contains(@class, 'pay__wrapper')]//h2");
    private final By paymentLogos = By.xpath("//div[contains(@class, 'pay__partners')]//img | //ul[contains(@class, 'pay__partners')]//img");
    private final By detailsLink = By.xpath("//a[contains(normalize-space(.), 'Подробнее о сервисе')]");
    
    // Locators for the top-up form
    private final By servicesTab = By.xpath("//button[contains(normalize-space(.), 'Услуги связи')]");
    private final By phoneInput = By.xpath("//input[contains(@placeholder, 'Номер')]");
    private final By amountInput = By.xpath("//input[contains(@placeholder, 'Сумма')]");
    private final By emailInput = By.xpath("//input[contains(@placeholder, 'E-mail')]");
    private final By continueButton = By.xpath("//button[contains(normalize-space(.), 'Продолжить')]");
    
    private final By cookieButton = By.xpath("//button[contains(., 'Принять')] | //button[contains(., 'Ок')] | //button[@id='cookie-agree']");

    public MainPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get("https://www.mts.by");
        acceptCookies();
    }

    public void acceptCookies() {
        try {
            List<WebElement> buttons = driver.findElements(cookieButton);
            for (WebElement button : buttons) {
                if (button.isDisplayed()) {
                    button.click();
                    return;
                }
            }
        } catch (Exception ignored) {}
    }

    public String getBlockTitleText() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(blockTitle));
        scrollToElement(element);
        return element.getText().replace("\u00a0", " ").trim();
    }

    public boolean arePaymentLogosPresent() {
        List<WebElement> logos = driver.findElements(paymentLogos);
        return !logos.isEmpty();
    }

    public String getDetailsLinkText() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(detailsLink));
        scrollToElement(element);
        return element.getText().trim();
    }

    public void clickDetailsLink() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(detailsLink));
        scrollToElement(element);
        element.click();
    }

    public void selectServicesTab() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(servicesTab));
        scrollToElement(element);
        element.click();
    }

    public void fillConnectionForm(String phone, String amount, String email) {
        WebElement phoneEl = wait.until(ExpectedConditions.visibilityOfElementLocated(phoneInput));
        scrollToElement(phoneEl);
        phoneEl.sendKeys(phone);
        driver.findElement(amountInput).sendKeys(amount);
        driver.findElement(emailInput).sendKeys(email);
    }

    public void clickContinue() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        scrollToElement(element);
        element.click();
    }

    private void scrollToElement(WebElement element) {
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        try {
            Thread.sleep(500); // Даем время для завершения анимации прокрутки
        } catch (InterruptedException ignored) {}
    }
}
