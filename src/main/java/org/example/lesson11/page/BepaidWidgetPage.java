package org.example.lesson11.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.TimeoutException;

import java.util.ArrayList;
import java.util.List;

public class BepaidWidgetPage extends BasePage {

    private static final int PAUSE_MS = 500;

    @FindBy(xpath = "//body")
    private WebElement body;

    @FindBy(xpath = "//input[not(@type='hidden') and not(@type='submit')]")
    private List<WebElement> cardInputs;

    @FindBy(xpath = "//img")
    private List<WebElement> paymentImages;

    public BepaidWidgetPage() {
        PageFactory.initElements(driver, this);
    }

    public BepaidWidgetPage switchIntoPaymentIframe(WebElement iframe) {
        driver.switchTo().defaultContent();
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframe));
        switchToInnerIframeIfNeeded();
        PageFactory.initElements(driver, this);
        return this;
    }

    public void switchToDefault() {
        driver.switchTo().defaultContent();
    }

    public String bodyText() {
        wait.until(ExpectedConditions.visibilityOf(body));
        return body.getText().replace("\n", " ");
    }

    public boolean phoneDisplayed(String phone) {
        String text = bodyText();
        return text.contains(phone) || text.contains("375" + phone);
    }

    public boolean amountDisplayedOnPage(String amount) {
        String text = bodyText();
        return text.contains(amount)
                || text.contains(amount + ",00")
                || text.contains(amount + ".00")
                || text.contains(amount + " BYN");
    }

    public boolean amountDisplayedOnPayButton(String amount) {
        List<WebElement> buttons = driver.findElements(By.xpath(
                "//button | //input[@type='submit']"));
        for (WebElement button : buttons) {
            if (!button.isDisplayed()) {
                continue;
            }
            String buttonText = readButtonText(button);
            if (buttonText.contains(amount)
                    || buttonText.contains(amount + ",00")
                    || buttonText.contains(amount + ".00")) {
                return true;
            }
        }
        return false;
    }

    public List<String> cardFieldHints() {
        List<String> hints = new ArrayList<>();
        for (WebElement input : cardInputs) {
            if (!input.isDisplayed()) {
                continue;
            }
            addHint(hints, input.getAttribute("placeholder"));
            addHint(hints, input.getAttribute("aria-label"));
        }
        return hints;
    }

    public boolean hintsContain(List<String> hints, String expectedPart) {
        for (String hint : hints) {
            if (hint.contains(expectedPart)) {
                return true;
            }
        }
        return false;
    }

    public boolean cardFieldLabelVisible(String expectedPart) {
        List<String> hints = cardFieldHints();
        if (hintsContain(hints, expectedPart)) {
            return true;
        }
        String text = bodyText();
        if ("CVC".equals(expectedPart)) {
            return text.contains("CVC") || text.contains("CVV") || text.contains("cvc") || text.contains("код");
        }
        if ("карт".equals(expectedPart)) {
            return text.contains("карт") || text.contains("Card") || text.contains("номер");
        }
        return text.contains(expectedPart);
    }

    public boolean cardFormDisplayed() {
        return !cardFieldHints().isEmpty();
    }

    public boolean paymentLogosDisplayed() {
        int logos = 0;
        for (WebElement image : paymentImages) {
            if (!image.isDisplayed()) {
                continue;
            }
            String alt = image.getAttribute("alt");
            String src = image.getAttribute("src");
            if (alt == null) {
                alt = "";
            }
            if (src == null) {
                src = "";
            }
            String info = alt + src;
            if (info.contains("visa") || info.contains("Visa")
                    || info.contains("master") || info.contains("Master")
                    || info.contains("belkart") || info.contains("Белкарт")) {
                logos++;
            }
        }
        return logos >= 2;
    }

    public void waitUntilPhoneVisible(String phone) {
        for (int i = 0; i < 20; i++) {
            if (phoneDisplayed(phone)) {
                return;
            }
            pause();
        }
        throw new TimeoutException("Не дождались номер «" + phone + "»");
    }

    public void waitUntilAmountOnPage(String amount) {
        for (int i = 0; i < 12; i++) {
            if (amountDisplayedOnPage(amount)) {
                return;
            }
            pause();
        }
        throw new TimeoutException("Не дождались сумму на странице «" + amount + "»");
    }

    public void waitUntilAmountOnPayButton(String amount) {
        for (int i = 0; i < 12; i++) {
            if (amountDisplayedOnPayButton(amount)) {
                return;
            }
            pause();
        }
        throw new TimeoutException("Не дождались сумму на кнопке «" + amount + "»");
    }

    public void waitUntilCardFormVisible() {
        for (int i = 0; i < 12; i++) {
            if (cardFormDisplayed()) {
                return;
            }
            pause();
        }
        throw new TimeoutException("Не дождались форму карты");
    }

    private String readButtonText(WebElement button) {
        String text = button.getText();
        String value = button.getAttribute("value");
        String ariaLabel = button.getAttribute("aria-label");
        if (text == null) {
            text = "";
        }
        if (value == null) {
            value = "";
        }
        if (ariaLabel == null) {
            ariaLabel = "";
        }
        return text + value + ariaLabel;
    }

    private void addHint(List<String> hints, String value) {
        if (value != null && !value.isBlank()) {
            hints.add(value.trim());
        }
    }

    private void pause() {
        try {
            Thread.sleep(PAUSE_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void switchToInnerIframeIfNeeded() {
        try {
            String text = driver.findElement(By.xpath("//body")).getText();
            if (text != null && text.trim().length() > 40) {
                return;
            }
            List<WebElement> iframes = driver.findElements(By.xpath("//iframe"));
            if (!iframes.isEmpty()) {
                driver.switchTo().frame(iframes.get(0));
            }
        } catch (Exception ignored) {
        }
    }

}
