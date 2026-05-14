package org.example.lesson10.page;

import org.example.lesson10.DriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {

    protected final WebDriver driver = DriverManager.getDriver();
    protected final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
}
