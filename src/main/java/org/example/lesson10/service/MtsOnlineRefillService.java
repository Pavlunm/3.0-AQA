package org.example.lesson10.service;

import org.example.lesson10.page.BepaidWidgetPage;
import org.example.lesson10.page.MtsHomePage;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;

import java.util.List;

import static org.example.lesson10.utils.Constants.CONNECTION_EMAIL;
import static org.example.lesson10.utils.Constants.CONNECTION_PHONE;
import static org.example.lesson10.utils.Constants.CONNECTION_SUM;
import static org.example.lesson10.utils.Constants.EXPECTED_CARD_FIELD_HINTS;
import static org.example.lesson10.utils.Constants.EXPECTED_PLACEHOLDERS_BY_VARIANT;
import static org.example.lesson10.utils.Constants.MTS_HOME_URL;

public class MtsOnlineRefillService {

    private final MtsHomePage home = new MtsHomePage();
    private final BepaidWidgetPage bepaid = new BepaidWidgetPage();

    public MtsOnlineRefillService openHomeAcceptCookiesAndScrollToPay() {
        home.open(MTS_HOME_URL).acceptCookiesIfVisible().scrollToPayBlock();
        return this;
    }

    public MtsOnlineRefillService assertPlaceholdersForVariant(String variant) {
        home.selectPaymentVariant(variant);
        List<String> actual = home.placeholdersOfVisibleForm(home.formIdForVariant(variant));
        List<String> expected = EXPECTED_PLACEHOLDERS_BY_VARIANT.get(variant);
        Assert.assertEquals(actual, expected, "Плейсхолдеры для варианта «" + variant + "»");
        return this;
    }

    public MtsOnlineRefillService fillConnectionServicePrerequisitesAndContinue() {
        home.selectPaymentVariant("Услуги связи");
        home.fillConnectionService(CONNECTION_PHONE, CONNECTION_SUM, CONNECTION_EMAIL);
        home.clickContinueOnConnectionForm();
        return this;
    }

    public MtsOnlineRefillService assertBepaidWidgetShowsOrderSummaryAndCardForm() {
        bepaid.switchIntoPaymentIframe(home.paymentIframe());
        try {
            waitUntilPhoneVisible();
            waitUntilAmountOnPage();
            Assert.assertTrue(bepaid.amountDisplayedOnPage(CONNECTION_SUM),
                    "Сумма «" + CONNECTION_SUM + "» на странице оплаты");
            waitUntilAmountOnButton();
            Assert.assertTrue(bepaid.amountDisplayedOnPayButton(CONNECTION_SUM),
                    "Сумма «" + CONNECTION_SUM + "» на кнопке оплаты");
            waitUntilCardFormVisible();
            assertCardFieldHints();
            Assert.assertTrue(bepaid.paymentLogosDisplayed(), "Не найдены иконки платёжных систем");
        } finally {
            bepaid.switchToDefault();
        }
        return this;
    }

    private void waitUntilPhoneVisible() {
        try {
            bepaid.waitUntilPhoneVisible(CONNECTION_PHONE);
        } catch (TimeoutException e) {
            Assert.fail("В виджете bePaid не появился номер «" + CONNECTION_PHONE + "». " + bepaid.bodyText());
        }
    }

    private void waitUntilAmountOnPage() {
        try {
            bepaid.waitUntilAmountOnPage(CONNECTION_SUM);
        } catch (TimeoutException e) {
            Assert.fail("Не дождались суммы на странице «" + CONNECTION_SUM + "». " + bepaid.bodyText());
        }
    }

    private void waitUntilAmountOnButton() {
        try {
            bepaid.waitUntilAmountOnPayButton(CONNECTION_SUM);
        } catch (TimeoutException e) {
            Assert.fail("Не дождались суммы на кнопке «" + CONNECTION_SUM + "». " + bepaid.bodyText());
        }
    }

    private void waitUntilCardFormVisible() {
        try {
            bepaid.waitUntilCardFormVisible();
        } catch (TimeoutException e) {
            Assert.fail("Не появились поля карты. " + bepaid.bodyText());
        }
    }

    private void assertCardFieldHints() {
        List<String> actual = bepaid.cardFieldHints();
        Assert.assertFalse(actual.isEmpty(), "Ожидались подсказки полей карты");

        for (String expectedHint : EXPECTED_CARD_FIELD_HINTS) {
            Assert.assertTrue(bepaid.cardFieldLabelVisible(expectedHint),
                    "Нет надписи «" + expectedHint + "» у полей карты. hints=" + actual);
        }
    }
}
