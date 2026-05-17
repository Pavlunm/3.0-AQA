package org.example.lesson11.page;

import org.example.lesson11.utils.Waiter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MtsHomePage extends BasePage {

    private final WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

    @FindBy(xpath = "//section[contains(@class,'pay')]")
    private WebElement payBlock;

    @FindBy(xpath = "//section[contains(@class,'pay')]//h2")
    private WebElement payBlockTitle;

    @FindBy(xpath = "//div[contains(@class,'pay__partners')]//img")
    private List<WebElement> partnerLogos;

    @FindBy(xpath = "//section[contains(@class,'pay')]//a[contains(normalize-space(.),'Подробнее о сервисе')]")
    private WebElement detailsAboutServiceLink;

    @FindBy(xpath = "//input[@id='connection-phone']")
    private WebElement connectionPhone;

    @FindBy(xpath = "//input[@id='connection-sum']")
    private WebElement connectionSum;

    @FindBy(xpath = "//input[@id='connection-email']")
    private WebElement connectionEmail;

    @FindBy(xpath = "//form[@id='pay-connection']//button[@type='submit' and contains(.,'Продолжить')]")
    private WebElement continueConnectionButton;

    public MtsHomePage() {
        PageFactory.initElements(driver, this);
    }

    public MtsHomePage open(String url) {
        driver.get(url);
        return this;
    }

    public MtsHomePage acceptCookiesIfVisible() {
        List<WebElement> cookieButtons = driver.findElements(
                By.xpath("//button[contains(normalize-space(.),'Принять')]"));
        if (!cookieButtons.isEmpty()) {
            WebElement cookieButton = cookieButtons.get(0);
            cookieButton.click();
            Waiter.waitElementToBeInvisible(cookieButton);
        }
        return this;
    }

    public String payBlockTitleTextNormalized() {
        wait.until(ExpectedConditions.visibilityOf(payBlockTitle));
        return payBlockTitle.getText().replace("\n", " ").trim();
    }

    public List<String> partnerLogoAlts() {
        wait.until(ExpectedConditions.visibilityOfAllElements(partnerLogos));
        List<String> alts = new ArrayList<>();
        for (WebElement logo : partnerLogos) {
            alts.add(logo.getAttribute("alt"));
        }
        return alts;
    }

    public String detailsLinkText() {
        wait.until(ExpectedConditions.visibilityOf(detailsAboutServiceLink));
        return detailsAboutServiceLink.getText().trim();
    }

    public String detailsLinkHref() {
        wait.until(ExpectedConditions.visibilityOf(detailsAboutServiceLink));
        return detailsAboutServiceLink.getAttribute("href");
    }

    public MtsHomePage clickDetailsAboutServiceLink() {
        wait.until(ExpectedConditions.elementToBeClickable(detailsAboutServiceLink)).click();
        return this;
    }

    public MtsHomePage waitDetailsHelpPageOpened(String urlPart) {
        wait.until(ExpectedConditions.urlContains(urlPart));
        return this;
    }

    public MtsHomePage navigateBackToHomePage() {
        driver.navigate().back();
        return this;
    }

    public MtsHomePage waitPayBlockTitleVisible() {
        wait.until(ExpectedConditions.visibilityOf(payBlockTitle));
        return this;
    }

    public MtsHomePage scrollToPayBlock() {
        scrollPayBlockToCenter();
        return this;
    }

    private void scrollPayBlockToCenter() {
        wait.until(ExpectedConditions.visibilityOf(payBlock));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", payBlock);
    }

    public MtsHomePage selectPaymentVariant(String visibleOptionText) {
        String firstFieldXpath = firstFieldXpathForVariant(visibleOptionText);
        scrollPayBlockToCenter();

        chooseVariantInPaySelect(visibleOptionText);
        try {
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(firstFieldXpath)));
        } catch (TimeoutException e) {
            openCustomPayDropdownAndChoose(visibleOptionText);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(firstFieldXpath)));
        }
        return this;
    }

    private void chooseVariantInPaySelect(String visibleOptionText) {
        WebElement paySelect = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//select[@id='pay']")));
        ((JavascriptExecutor) driver).executeScript(
                "var el = arguments[0]; var text = arguments[1];"
                        + "for (var i = 0; i < el.options.length; i++) {"
                        + "  if (el.options[i].textContent.trim() === text) { el.selectedIndex = i; break; }"
                        + "}"
                        + "el.dispatchEvent(new Event('change', { bubbles: true }));",
                paySelect,
                visibleOptionText);
    }

    private void openCustomPayDropdownAndChoose(String visibleOptionText) {
        WebElement header = shortWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@id='pay-section']//button[contains(@class,'select__header')]")));
        new Actions(driver).moveToElement(header).click().perform();

        String listXpath = "//div[@id='pay-section']//ul[contains(@class,'select__list')]";
        shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(listXpath)));

        String optionXpath = listXpath
                + "//li[contains(@class,'select__item')][contains(normalize-space(.),'"
                + visibleOptionText + "')]";
        WebElement option = shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(optionXpath)));
        new Actions(driver).moveToElement(option).click().perform();
    }

    public List<String> placeholdersOfVisibleForm(String formId) {
        String inputsXpath = "//form[@id='" + formId + "']//input[@placeholder]";
        shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(inputsXpath)));
        List<WebElement> inputs = driver.findElements(By.xpath(inputsXpath));
        List<String> placeholders = new ArrayList<>();
        for (WebElement input : inputs) {
            if (input.isDisplayed()) {
                placeholders.add(input.getAttribute("placeholder"));
            }
        }
        return placeholders;
    }

    public String formIdForVariant(String visibleOptionText) {
        if ("Услуги связи".equals(visibleOptionText)) {
            return "pay-connection";
        }
        if ("Домашний интернет".equals(visibleOptionText)) {
            return "pay-internet";
        }
        if ("Рассрочка".equals(visibleOptionText)) {
            return "pay-instalment";
        }
        if ("Задолженность".equals(visibleOptionText)) {
            return "pay-arrears";
        }
        throw new IllegalArgumentException("Unknown variant: " + visibleOptionText);
    }

    private String firstFieldXpathForVariant(String visibleOptionText) {
        if ("Услуги связи".equals(visibleOptionText)) {
            return "//input[@id='connection-phone']";
        }
        if ("Домашний интернет".equals(visibleOptionText)) {
            return "//input[@id='internet-phone']";
        }
        if ("Рассрочка".equals(visibleOptionText)) {
            return "//input[@id='score-instalment']";
        }
        if ("Задолженность".equals(visibleOptionText)) {
            return "//input[@id='score-arrears']";
        }
        throw new IllegalArgumentException("Unknown variant: " + visibleOptionText);
    }

    public MtsHomePage fillConnectionService(String phone, String sum, String email) {
        wait.until(ExpectedConditions.visibilityOf(connectionPhone));
        connectionPhone.clear();
        connectionPhone.sendKeys(phone);
        connectionSum.clear();
        connectionSum.sendKeys(sum);
        connectionEmail.clear();
        connectionEmail.sendKeys(email);
        return this;
    }

    public MtsHomePage clickContinueOnConnectionForm() {
        wait.until(ExpectedConditions.elementToBeClickable(continueConnectionButton)).click();
        return this;
    }

    public MtsHomePage waitPaymentFlowStarted() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("checkout"),
                ExpectedConditions.urlContains("payment"),
                ExpectedConditions.presenceOfElementLocated(paymentIframeXpath())));
        return this;
    }

    private By paymentIframeXpath() {
        return By.xpath("//iframe[contains(@class,'bepaid-iframe') or contains(@src,'bepaid') or contains(@src,'payment')]");
    }

    public WebElement paymentIframe() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//iframe[contains(@src,'bepaid') or contains(@src,'checkout') or contains(@class,'bepaid')]")));
    }
}
