package org.example.lesson10.page;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class MtsHomePage extends BasePage {

    @FindBy(xpath = "//section[contains(@class,'pay')]//h2")
    private WebElement payBlockTitle;

    @FindBy(xpath = "//div[contains(@class,'pay__partners')]//img")
    private List<WebElement> partnerLogos;

    public MtsHomePage() {
        PageFactory.initElements(driver, this);
    }

    public MtsHomePage open(String url) {
        driver.get(url);
        return this;
    }

    public MtsHomePage acceptCookiesIfVisible() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement btn = shortWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(normalize-space(.),'Принять')]")));
            btn.click();
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.invisibilityOf(btn));
        } catch (Exception ignored) {
        }
        return this;
    }

    public String payBlockTitleTextNormalized() {
        wait.until(ExpectedConditions.visibilityOf(payBlockTitle));
        return payBlockTitle.getText().replace("\n", " ").trim();
    }

    public List<String> partnerLogoAlts() {
        wait.until(ExpectedConditions.visibilityOfAllElements(partnerLogos));
        return partnerLogos.stream().map(img -> img.getAttribute("alt")).collect(Collectors.toList());
    }

    public MtsHomePage scrollToPayBlock() {
        wait.until(ExpectedConditions.visibilityOf(payBlockTitle));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", payBlockTitle);
        return this;
    }

    public MtsHomePage selectPaymentVariant(String visibleOptionText) {
        String formId = formIdForVariant(visibleOptionText);
        String firstFieldId = firstInputIdForForm(formId);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("pay")));
        WebElement select = driver.findElement(By.id("pay"));
        ((JavascriptExecutor) driver).executeScript(
                "const el = arguments[0]; const text = arguments[1];"
                        + "for (let i = 0; i < el.options.length; i++) {"
                        + "  if (el.options[i].textContent.trim() === text) { el.selectedIndex = i; break; }"
                        + "}"
                        + "el.dispatchEvent(new Event('change', { bubbles: true }));"
                        + "el.dispatchEvent(new Event('input', { bubbles: true }));",
                select,
                visibleOptionText);

        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.id(firstFieldId)));
        } catch (TimeoutException e) {
            openCustomPayDropdownAndChoose(visibleOptionText);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(firstFieldId)));
        }
        return this;
    }

    private void openCustomPayDropdownAndChoose(String visibleOptionText) {
        WebElement header = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@id='pay-section']//button[contains(@class,'select__header')]")));
        header.click();
        String optionXpath = "//div[@id='pay-section']//ul[contains(@class,'select__list')]"
                + "//li[contains(normalize-space(.), \"" + visibleOptionText + "\")]";
        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(optionXpath)));
        option.click();
    }

    private static String firstInputIdForForm(String formId) {
        return switch (formId) {
            case "pay-connection" -> "connection-phone";
            case "pay-internet" -> "internet-phone";
            case "pay-instalment" -> "score-instalment";
            case "pay-arrears" -> "score-arrears";
            default -> throw new IllegalArgumentException("Unknown form: " + formId);
        };
    }

    public List<String> placeholdersOfVisibleForm(String formId) {
        By formLocator = By.xpath("//form[@id='" + formId + "']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(formLocator));
        List<WebElement> inputs = driver.findElements(
                By.xpath("//form[@id='" + formId + "']//input[@placeholder]"));
        return inputs.stream()
                .filter(WebElement::isDisplayed)
                .map(input -> input.getAttribute("placeholder"))
                .collect(Collectors.toList());
    }

    public String formIdForVariant(String visibleOptionText) {
        return switch (visibleOptionText) {
            case "Услуги связи" -> "pay-connection";
            case "Домашний интернет" -> "pay-internet";
            case "Рассрочка" -> "pay-instalment";
            case "Задолженность" -> "pay-arrears";
            default -> throw new IllegalArgumentException("Unknown variant: " + visibleOptionText);
        };
    }

    public MtsHomePage fillConnectionService(String phone, String sum, String email) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("connection-phone"))).clear();
        driver.findElement(By.id("connection-phone")).sendKeys(phone);
        driver.findElement(By.id("connection-sum")).clear();
        driver.findElement(By.id("connection-sum")).sendKeys(sum);
        driver.findElement(By.id("connection-email")).clear();
        driver.findElement(By.id("connection-email")).sendKeys(email);
        return this;
    }

    public MtsHomePage clickContinueOnConnectionForm() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//form[@id='pay-connection']//button[@type='submit' and contains(.,'Продолжить')]")));
        button.click();
        return this;
    }

    public WebElement paymentIframe() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//iframe[contains(@src,'bepaid') or contains(@src,'checkout') or contains(@class,'bepaid')]")));
    }
}
