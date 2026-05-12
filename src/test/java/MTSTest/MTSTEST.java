package MTSTest;

import driver.DriverManager;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;
import pages.MainPage;

public class MTSTEST {

    private WebDriver driver;
    private MainPage mainPage;

    @BeforeMethod
    public void setUp() {
        driver = DriverManager.getDriver();
        mainPage = new MainPage(driver);
    }

    @AfterMethod
    public void tearDown() {
        DriverManager.quitDriver();
    }

    @Test
    public void testMtsOnlineTopUp() {
        mainPage.open();

        // 1. Проверить название указанного блока
        String actualTitle = mainPage.getBlockTitleText().replace("\n", " ");
        String expectedText = "Онлайн пополнение";
        Assert.assertTrue(actualTitle.contains(expectedText), 
            String.format("Название блока '%s' не содержит ожидаемый текст '%s'", actualTitle, expectedText));

        // 2. Проверить наличие логотипов платёжных систем
        Assert.assertTrue(mainPage.arePaymentLogosPresent(), "Логотипы платёжных систем не найдены");

        // 3. Проверить работу ссылки «Подробнее о сервисе»
        String detailsText = mainPage.getDetailsLinkText();
        Assert.assertEquals(detailsText, "Подробнее о сервисе", "Текст ссылки не совпадает");
        mainPage.clickDetailsLink();
        
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("help") || currentUrl.contains("instruction"), 
            "Ссылка 'Подробнее о сервисе' ведет на некорректную страницу: " + currentUrl);
        
        // Возвращаемся назад для продолжения теста
        driver.navigate().back();

        // 4. Заполнить поля и проверить работу кнопки «Продолжить» (вариант «Услуги связи», номер 297777777)
        mainPage.selectServicesTab();
        mainPage.fillConnectionForm("297777777", "10", "test@test.com");
        mainPage.clickContinue();
    }
}
