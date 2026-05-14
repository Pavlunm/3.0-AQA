package org.example.lesson10.utils;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.BooleanSupplier;

/**
 * Явные ожидания (как в примере lesson_9).
 */
public final class Waiter {

    private Waiter() {
    }

    public static void untilTrue(WebDriver driver, Duration timeout, BooleanSupplier condition) {
        new WebDriverWait(driver, timeout)
                .ignoring(StaleElementReferenceException.class)
                .until(d -> condition.getAsBoolean());
    }
}
