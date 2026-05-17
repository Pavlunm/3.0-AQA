package org.example.lesson11.utils;

import org.example.lesson11.DriverManager;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class Waiter {

    private Waiter() {
    }

    public static void waitElementToBeInvisible(WebElement element) {
        new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(5))
                .until(ExpectedConditions.invisibilityOf(element));
    }
}
