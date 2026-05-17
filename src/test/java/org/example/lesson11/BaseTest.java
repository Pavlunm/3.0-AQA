package org.example.lesson11;

import org.example.lesson11.service.MtsHomePageService;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

    protected MtsHomePageService mtsHomePageService;

    @BeforeMethod
    public void setUp() {
        mtsHomePageService = new MtsHomePageService();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }
}
