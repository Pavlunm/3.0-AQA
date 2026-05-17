package org.example.lesson10.page;

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

    public boolean amountDisplayed(String amount) {
        String text = bodyText();
        return text.contains(amount)
                || text.contains(amount + ",00")
                || text.contains(amount + ".00")
                || text.contains(amount + " BYN");
    }

    public List<String> inputHints() {
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

    public boolean cardFormDisplayed() {
        return !inputHints().isEmpty();
    }

    public boolean hasExpiryHint() {
        for (String hint : inputHints()) {
            if (hint.contains("ММ") || hint.contains("ГГ")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasCardNumberHint() {
        for (String hint : inputHints()) {
            if (hint.contains("карт") || hint.contains("Карт")
                    || hint.contains("card") || hint.contains("номер") || hint.contains("Номер")) {
                return true;
            }
        }
        String text = bodyText();
        return text.contains("номер") || text.contains("карт") || text.contains("Card");
    }

    public boolean hasCvcHint() {
        for (String hint : inputHints()) {
            if (hint.contains("cvc") || hint.contains("CVC")
                    || hint.contains("cvv") || hint.contains("CVV") || hint.contains("код")) {
                return true;
            }
        }
        String text = bodyText();
        return text.contains("CVC") || text.contains("CVV") || text.contains("cvc");
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

    public void waitUntilAmountVisible(String amount) {
        for (int i = 0; i < 12; i++) {
            if (amountDisplayed(amount)) {
                return;
            }
            pause();
        }
        throw new TimeoutException("Не дождались сумму «" + amount + "»");
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
