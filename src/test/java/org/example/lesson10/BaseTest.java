package org.example.lesson10;

import org.example.lesson10.service.MtsOnlineRefillService;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

    protected MtsOnlineRefillService mtsOnlineRefillService;

    @BeforeMethod
    public void startBrowser() {
        mtsOnlineRefillService = new MtsOnlineRefillService();
    }

    @AfterMethod(alwaysRun = true)
    public void stopBrowser() {
        DriverManager.quitDriver();
    }
}
