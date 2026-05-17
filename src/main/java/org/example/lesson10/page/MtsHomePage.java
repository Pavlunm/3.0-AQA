package org.example.lesson10.page;

import org.example.lesson10.utils.Waiter;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class MtsHomePage extends BasePage {

    @FindBy(xpath = "//section[contains(@class,'pay')]//h2")
    private WebElement payBlockTitle;

    @FindBy(xpath = "//div[contains(@class,'pay__partners')]//img")
    private List<WebElement> partnerLogos;

    @FindBy(xpath = "//input[@id='connection-phone']")
    private WebElement connectionPhone;

    @FindBy(xpath = "//input[@id='connection-sum']")
    private WebElement connectionSum;

    @FindBy(xpath = "//input[@id='connection-email']")
    private WebElement connectionEmail;

    @FindBy(xpath = "//form[@id='pay-connection']//button[@type='submit' and contains(.,'Продолжить')]")
    private WebElement continueConnectionButton;

    @FindBy(xpath = "//select[@id='pay']")
    private WebElement payVariantSelect;

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

    public MtsHomePage scrollToPayBlock() {
        wait.until(ExpectedConditions.visibilityOf(payBlockTitle));
        new Actions(driver).moveToElement(payBlockTitle).perform();
        return this;
    }

    public MtsHomePage selectPaymentVariant(String visibleOptionText) {
        String formId = formIdForVariant(visibleOptionText);
        String formXpath = "//form[@id='" + formId + "']";

        try {
            wait.until(ExpectedConditions.visibilityOf(payVariantSelect));
            new Select(payVariantSelect).selectByVisibleText(visibleOptionText);
        } catch (TimeoutException e) {
            openCustomPayDropdownAndChoose(visibleOptionText);
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(formXpath)));
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

    public List<String> placeholdersOfVisibleForm(String formId) {
        String formXpath = "//form[@id='" + formId + "']";
        String inputsXpath = formXpath + "//input[@placeholder]";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(formXpath)));
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

    public WebElement paymentIframe() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//iframe[contains(@src,'bepaid') or contains(@src,'checkout') or contains(@class,'bepaid')]")));
    }
}
