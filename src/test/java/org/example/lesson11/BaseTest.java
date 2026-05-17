package org.example.lesson11;

import io.qameta.allure.Allure;
import org.example.lesson11.service.MtsOnlineRefillService;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;

public abstract class BaseTest {

    protected MtsOnlineRefillService mtsOnlineRefillService;

    @BeforeMethod
    public void startTests() {
        mtsOnlineRefillService = new MtsOnlineRefillService();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            attachScreenshot("Screenshot on failure");
        }
        DriverManager.quitDriver();
    }

    private void attachScreenshot(String name) {
        try {
            WebDriver driver = DriverManager.getDriver();
            if (driver instanceof TakesScreenshot screenshotDriver) {
                byte[] screenshot = screenshotDriver.getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), "png");
            }
        } catch (Exception ignored) {
        }
    }
}
