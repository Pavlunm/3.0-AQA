package org.example.lesson10.page;

import org.example.lesson10.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BepaidWidgetPage extends BasePage {

    private static final int SHALLOW_BODY_THRESHOLD = 40;

    private final WebDriver rootDriver = DriverManager.getDriver();

    public BepaidWidgetPage switchIntoPaymentIframe(WebElement iframe) {
        rootDriver.switchTo().defaultContent();
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframe));
        drillIntoNestedIframeIfShallowBody(2);
        return this;
    }

    private void drillIntoNestedIframeIfShallowBody(int maxDepth) {
        for (int i = 0; i < maxDepth; i++) {
            if (!isShallowBodyText()) {
                return;
            }
            List<WebElement> inner = rootDriver.findElements(By.cssSelector("iframe"));
            if (inner.isEmpty()) {
                return;
            }
            try {
                rootDriver.switchTo().frame(inner.get(0));
            } catch (Exception e) {
                return;
            }
        }
    }

    private boolean isShallowBodyText() {
        try {
            String t = rootDriver.findElement(By.tagName("body")).getText();
            return t == null || t.replaceAll("\\s+", "").length() < SHALLOW_BODY_THRESHOLD;
        } catch (Exception e) {
            return true;
        }
    }

    public void waitUntilNationalPhoneDetected(String nationalDigits, Duration timeout) {
        new WebDriverWait(rootDriver, timeout)
                .ignoring(StaleElementReferenceException.class)
                .until(driver -> isNationalPhoneVisibleSomewhere(nationalDigits));
    }

    public boolean isNationalPhoneVisibleSomewhere(String nationalDigits) {
        String visible = bodyTextQuick() + "\n" + documentInnerText();
        String src = safePageSource();
        if (visible.contains(nationalDigits) || src.contains(nationalDigits)) {
            return true;
        }
        String digits = digitsOnly(visible + src);
        if (digits.contains(nationalDigits)) {
            return true;
        }
        if (digits.contains("375" + nationalDigits)) {
            return true;
        }
        if (nationalDigits != null && nationalDigits.length() > 1) {
            if (digits.contains("802" + nationalDigits.substring(1))) {
                return true;
            }
        }
        return digits.contains("80" + nationalDigits);
    }

    public String debugWidgetSnapshot(int maxChars) {
        String chunk = bodyTextQuick() + "\n--- innerText ---\n" + documentInnerText();
        if (chunk.length() > maxChars) {
            return chunk.substring(0, maxChars) + "...";
        }
        return chunk;
    }

    private String bodyTextQuick() {
        try {
            return rootDriver.findElement(By.tagName("body")).getText().replace("\n", " ");
        } catch (Exception e) {
            return "";
        }
    }

    private String documentInnerText() {
        try {
            Object o = ((JavascriptExecutor) rootDriver).executeScript(
                    "return document.documentElement && document.documentElement.innerText"
                            + " ? document.documentElement.innerText : '';");
            return o != null ? o.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private String safePageSource() {
        try {
            return rootDriver.getPageSource();
        } catch (Exception e) {
            return "";
        }
    }

    private String truncatedPageSource(int maxChars) {
        String ps = safePageSource();
        return ps.length() <= maxChars ? ps : ps.substring(0, maxChars);
    }

    public String aggregatedWidgetBlob() {
        return bodyTextQuick() + "\n" + documentInnerText() + "\n" + truncatedPageSource(300_000);
    }

    public boolean isAmountVisibleSomewhere(String amount) {
        if (amount == null || amount.isBlank()) {
            return false;
        }
        String blob = aggregatedWidgetBlob();
        if (blob.contains(amount + ",00") || blob.contains(amount + ".00")) {
            return true;
        }
        String lower = blob.toLowerCase();
        if (blob.contains(amount + " BYN")
                || blob.contains(amount + "\u00a0BYN")
                || lower.contains(amount + " br")
                || blob.contains(amount + " руб")) {
            return true;
        }
        return Pattern.compile("(^|\\D)" + Pattern.quote(amount) + "(\\D|$)").matcher(blob).find();
    }

    public boolean isPayActionShowingAmount(String amount) {
        return rootDriver.findElements(By.cssSelector("button, input[type='submit'], [role='button']")).stream()
                .filter(WebElement::isDisplayed)
                .anyMatch(btn -> {
                    String pack = textOrEmpty(btn.getText()) + " "
                            + textOrEmpty(btn.getAttribute("value")) + " "
                            + textOrEmpty(btn.getAttribute("aria-label")) + " "
                            + textOrEmpty(btn.getAttribute("title"));
                    return pack.contains(amount);
                });
    }

    public void waitUntilAmountOrPayButtonShowsSum(String amount, Duration timeout) {
        new WebDriverWait(rootDriver, timeout)
                .ignoring(StaleElementReferenceException.class)
                .until(driver -> isAmountVisibleSomewhere(amount) || isPayActionShowingAmount(amount));
    }

    public void waitUntilCardFieldsOrHintsPresent(Duration timeout) {
        new WebDriverWait(rootDriver, timeout)
                .ignoring(StaleElementReferenceException.class)
                .until(driver -> !visibleCardFieldHints().isEmpty() || cardHintsPresentInMarkup());
    }

    private boolean cardHintsPresentInMarkup() {
        String low = aggregatedWidgetBlob().toLowerCase();
        boolean secret = low.contains("cvc") || low.contains("cvv") || low.contains("security code")
                || low.contains("csc") || low.contains("код");
        boolean cardish = low.contains("card") || low.contains("карт") || low.contains("number")
                || low.contains("pan") || low.contains("номер");
        boolean expiry = low.contains("exp") || low.contains("mm/") || low.contains("/yy")
                || low.contains("срок") || low.contains("valid");
        return secret && (cardish || expiry);
    }

    public boolean hasCardHintsInMarkup() {
        return cardHintsPresentInMarkup();
    }

    private static String textOrEmpty(String s) {
        return s != null ? s : "";
    }

    private static String digitsOnly(String s) {
        return s.replaceAll("\\D+", "");
    }

    public void switchToDefault() {
        rootDriver.switchTo().defaultContent();
    }

    public String bodyText() {
        WebDriverWait iframeWait = new WebDriverWait(rootDriver, Duration.ofSeconds(25));
        iframeWait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        return rootDriver.findElement(By.tagName("body")).getText().replace("\n", " ");
    }

    public List<String> visibleCardFieldHints() {
        List<String> hints = new ArrayList<>();
        hints.addAll(collectFieldHints(By.cssSelector("input:not([type='hidden']):not([type='submit'])")));
        hints.addAll(collectFieldHints(By.cssSelector("textarea")));
        hints.addAll(collectFieldHints(By.xpath("//*[@placeholder and not(self::script)]")));
        hints.addAll(collectFieldHints(By.cssSelector("[data-placeholder]")));
        return hints.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty()).distinct()
                .collect(Collectors.toList());
    }

    private List<String> collectFieldHints(By locator) {
        List<String> out = new ArrayList<>();
        for (WebElement el : rootDriver.findElements(locator)) {
            if (!isPotentiallyInteractableField(el)) {
                continue;
            }
            Stream.of(
                    el.getAttribute("placeholder"),
                    el.getAttribute("aria-label"),
                    el.getAttribute("title"),
                    el.getAttribute("data-placeholder")
            ).filter(v -> v != null && !v.isBlank()).forEach(out::add);
        }
        return out;
    }

    private boolean isPotentiallyInteractableField(WebElement el) {
        try {
            if (el.isDisplayed()) {
                return true;
            }
            String ph = el.getAttribute("placeholder");
            if (ph != null && !ph.isBlank()) {
                org.openqa.selenium.Rectangle r = el.getRect();
                return r.getWidth() > 0 && r.getHeight() > 0;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean hasPaymentBrandImages() {
        String page = rootDriver.getPageSource().toLowerCase();
        boolean textMatch = page.contains("visa") && (page.contains("master") || page.contains("mastercard"))
                || page.contains("belkart") || page.contains("белкарт");
        List<WebElement> imgs = rootDriver.findElements(By.cssSelector("img"));
        long hits = imgs.stream()
                .filter(WebElement::isDisplayed)
                .map(img -> (img.getAttribute("alt") + " " + img.getAttribute("src")).toLowerCase())
                .filter(s -> s.contains("visa")
                        || s.contains("master")
                        || s.contains("belcard")
                        || s.contains("belkart")
                        || s.contains("mir")
                        || s.contains("maestro"))
                .count();
        return hits >= 2 || textMatch;
    }
}
