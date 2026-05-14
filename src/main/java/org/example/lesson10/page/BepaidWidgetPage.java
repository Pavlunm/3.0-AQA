package org.example.lesson10.page;

import org.example.lesson10.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class BepaidWidgetPage extends BasePage {

    private final WebDriver rootDriver = DriverManager.getDriver();

    public BepaidWidgetPage switchIntoPaymentIframe(WebElement iframe) {
        rootDriver.switchTo().defaultContent();
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframe));
        return this;
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
        List<WebElement> inputs = rootDriver.findElements(By.cssSelector("input"));
        return inputs.stream()
                .filter(WebElement::isDisplayed)
                .flatMap(el -> java.util.stream.Stream.of(
                                el.getAttribute("placeholder"),
                                el.getAttribute("aria-label"),
                                el.getAttribute("title")
                        )
                        .filter(v -> v != null && !v.isBlank()))
                .distinct()
                .collect(Collectors.toList());
    }

    public String findPayButtonTextContaining(String textFragment) {
        List<WebElement> buttons = rootDriver.findElements(By.cssSelector("button"));
        return buttons.stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .map(String::trim)
                .filter(t -> !t.isEmpty() && t.contains(textFragment))
                .findFirst()
                .orElseGet(() -> buttons.stream()
                        .filter(WebElement::isDisplayed)
                        .map(WebElement::getText)
                        .map(String::trim)
                        .filter(t -> !t.isEmpty())
                        .filter(t -> t.toLowerCase().contains("оплат") || t.matches(".*\\d+.*"))
                        .findFirst()
                        .orElse(""));
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
