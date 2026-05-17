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

    @FindBy(xpath = "//body")
    private WebElement body;

    @FindBy(xpath = "//input[@placeholder and not(@type='hidden') and not(@type='submit')]")
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

    public List<String> inputPlaceholders() {
        List<String> placeholders = new ArrayList<>();
        for (WebElement input : cardInputs) {
            if (!input.isDisplayed()) {
                continue;
            }
            String placeholder = input.getAttribute("placeholder");
            if (placeholder != null && !placeholder.isBlank()) {
                placeholders.add(placeholder.trim());
            }
        }
        return placeholders;
    }

    public boolean cardFormDisplayed() {
        return !inputPlaceholders().isEmpty();
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
        for (int i = 0; i < 25; i++) {
            if (phoneDisplayed(phone)) {
                return;
            }
            pauseOneSecond();
        }
        throw new TimeoutException("Не дождались номер «" + phone + "»");
    }

    public void waitUntilAmountVisible(String amount) {
        for (int i = 0; i < 18; i++) {
            if (amountDisplayed(amount)) {
                return;
            }
            pauseOneSecond();
        }
        throw new TimeoutException("Не дождались сумму «" + amount + "»");
    }

    public void waitUntilCardFormVisible() {
        for (int i = 0; i < 20; i++) {
            if (cardFormDisplayed()) {
                return;
            }
            pauseOneSecond();
        }
        throw new TimeoutException("Не дождались форму карты");
    }

    private void pauseOneSecond() {
        try {
            Thread.sleep(1000);
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
