package org.example.lesson11.page;

import org.example.lesson11.DriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {

    protected final WebDriver driver = DriverManager.getDriver();
    protected final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
}
