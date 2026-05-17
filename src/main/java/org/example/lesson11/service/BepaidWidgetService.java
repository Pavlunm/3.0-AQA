package org.example.lesson11.service;

import io.qameta.allure.Step;
import org.example.lesson11.page.BepaidWidgetPage;
import org.example.lesson11.page.MtsHomePage;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;

import java.util.List;

import static org.example.lesson11.utils.Constants.CONNECTION_PHONE;
import static org.example.lesson11.utils.Constants.CONNECTION_SUM;
import static org.example.lesson11.utils.Constants.EXPECTED_CARD_FIELD_HINTS;

public class BepaidWidgetService {

    private final MtsHomePage homePage;
    private final BepaidWidgetPage bepaidPage;

    public BepaidWidgetService(MtsHomePage homePage) {
        this.homePage = homePage;
        this.bepaidPage = new BepaidWidgetPage();
    }

    @Step("Проверить виджет bePaid: заказ, сумма и форма карты")
    public BepaidWidgetService assertOrderSummaryAndCardForm() {
        bepaidPage.switchIntoPaymentIframe(homePage.paymentIframe());
        try {
            waitUntilPhoneVisible();
            waitUntilAmountOnPage();
            Assert.assertTrue(bepaidPage.amountDisplayedOnPage(CONNECTION_SUM),
                    "Сумма «" + CONNECTION_SUM + "» на странице оплаты");
            waitUntilAmountOnButton();
            Assert.assertTrue(bepaidPage.amountDisplayedOnPayButton(CONNECTION_SUM),
                    "Сумма «" + CONNECTION_SUM + "» на кнопке оплаты");
            waitUntilCardFormVisible();
            assertCardFieldHints();
            Assert.assertTrue(bepaidPage.paymentLogosDisplayed(), "Не найдены иконки платёжных систем");
        } finally {
            bepaidPage.switchToDefault();
        }
        return this;
    }

    private void waitUntilPhoneVisible() {
        try {
            bepaidPage.waitUntilPhoneVisible(CONNECTION_PHONE);
        } catch (TimeoutException e) {
            Assert.fail("В виджете bePaid не появился номер «" + CONNECTION_PHONE + "». " + bepaidPage.bodyText());
        }
    }

    private void waitUntilAmountOnPage() {
        try {
            bepaidPage.waitUntilAmountOnPage(CONNECTION_SUM);
        } catch (TimeoutException e) {
            Assert.fail("Не дождались суммы на странице «" + CONNECTION_SUM + "». " + bepaidPage.bodyText());
        }
    }

    private void waitUntilAmountOnButton() {
        try {
            bepaidPage.waitUntilAmountOnPayButton(CONNECTION_SUM);
        } catch (TimeoutException e) {
            Assert.fail("Не дождались суммы на кнопке «" + CONNECTION_SUM + "». " + bepaidPage.bodyText());
        }
    }

    private void waitUntilCardFormVisible() {
        try {
            bepaidPage.waitUntilCardFormVisible();
        } catch (TimeoutException e) {
            Assert.fail("Не появились поля карты. " + bepaidPage.bodyText());
        }
    }

    private void assertCardFieldHints() {
        List<String> actual = bepaidPage.cardFieldHints();
        Assert.assertFalse(actual.isEmpty(), "Ожидались подсказки полей карты");

        for (String expectedHint : EXPECTED_CARD_FIELD_HINTS) {
            Assert.assertTrue(bepaidPage.cardFieldLabelVisible(expectedHint),
                    "Нет надписи «" + expectedHint + "» у полей карты. hints=" + actual);
        }
    }
}
