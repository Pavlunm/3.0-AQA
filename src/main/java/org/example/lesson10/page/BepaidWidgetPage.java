package org.example.lesson10.page;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Только фрейм bePaid и чтение DOM — без бизнес-проверок (они в {@link org.example.lesson10.service.MtsOnlineRefillService}).
 */
public class BepaidWidgetPage extends BasePage {

    private static final int NESTED_IFRAME_DEPTH = 2;
    private static final int SHALLOW_BODY_LEN = 40;
    private static final int PAGE_SNIP = 300_000;

    public BepaidWidgetPage switchIntoPaymentIframe(WebElement iframe) {
        driver.switchTo().defaultContent();
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframe));
        drillNestedIfNeeded();
        return this;
    }

    public void switchToDefault() {
        driver.switchTo().defaultContent();
    }

    public String bodyText() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        return driver.findElement(By.tagName("body")).getText().replace("\n", " ");
    }

    /** Текст страницы + обрезанный HTML — для поиска телефона/суммы/подписей в сервисе. */
    public String textAndHtmlSnippet() {
        return visibleText() + "\n" + snip(pageSource(), PAGE_SNIP);
    }

    public String visibleTextDebug(int maxChars) {
        String s = visibleText();
        return s.length() <= maxChars ? s : s.substring(0, maxChars) + "...";
    }

    public List<String> inputHintTexts() {
        List<String> out = new ArrayList<>();
        for (By locator : new By[]{
                By.cssSelector("input:not([type='hidden']):not([type='submit'])"),
                By.cssSelector("textarea"),
                By.xpath("//*[@placeholder and not(self::script)]"),
                By.cssSelector("[data-placeholder]")
        }) {
            for (WebElement el : driver.findElements(locator)) {
                if (!usableField(el)) {
                    continue;
                }
                Stream.of(
                        el.getAttribute("placeholder"),
                        el.getAttribute("aria-label"),
                        el.getAttribute("title"),
                        el.getAttribute("data-placeholder")
                ).filter(v -> v != null && !v.isBlank()).forEach(out::add);
            }
        }
        return out.stream().map(String::trim).filter(s -> !s.isEmpty()).distinct().collect(Collectors.toList());
    }

    public boolean hasPaymentLogoImages() {
        String html = pageSource().toLowerCase();
        if (html.contains("visa") && (html.contains("master") || html.contains("mastercard"))
                || html.contains("belkart") || html.contains("белкарт")) {
            return true;
        }
        return driver.findElements(By.cssSelector("img")).stream()
                .filter(WebElement::isDisplayed)
                .map(img -> nz(img.getAttribute("alt")) + nz(img.getAttribute("src")))
                .map(String::toLowerCase)
                .filter(s -> s.contains("visa") || s.contains("master") || s.contains("belkart")
                        || s.contains("belcard") || s.contains("mir"))
                .count() >= 2;
    }

    private void drillNestedIfNeeded() {
        for (int i = 0; i < NESTED_IFRAME_DEPTH; i++) {
            if (!shallowBody()) {
                return;
            }
            List<WebElement> inner = driver.findElements(By.cssSelector("iframe"));
            if (inner.isEmpty()) {
                return;
            }
            try {
                driver.switchTo().frame(inner.get(0));
            } catch (Exception e) {
                return;
            }
        }
    }

    private boolean shallowBody() {
        try {
            String t = driver.findElement(By.tagName("body")).getText();
            return t == null || t.replaceAll("\\s+", "").length() < SHALLOW_BODY_LEN;
        } catch (Exception e) {
            return true;
        }
    }

    private String visibleText() {
        try {
            String body = driver.findElement(By.tagName("body")).getText().replace("\n", " ");
            Object o = ((JavascriptExecutor) driver).executeScript(
                    "return document.documentElement && document.documentElement.innerText"
                            + " ? document.documentElement.innerText : '';");
            String doc = o != null ? o.toString() : "";
            return body + "\n" + doc;
        } catch (Exception e) {
            return "";
        }
    }

    private String pageSource() {
        try {
            return driver.getPageSource();
        } catch (Exception e) {
            return "";
        }
    }

    private static String snip(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max);
    }

    private static String nz(String s) {
        return s != null ? s : "";
    }

    private static boolean usableField(WebElement el) {
        try {
            if (el.isDisplayed()) {
                return true;
            }
            String ph = el.getAttribute("placeholder");
            return ph != null && !ph.isBlank() && el.getRect().getWidth() > 0 && el.getRect().getHeight() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
