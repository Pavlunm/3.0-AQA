package org.example.lesson10.service;

import org.example.lesson10.page.BepaidWidgetPage;
import org.example.lesson10.page.MtsHomePage;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;

import java.util.List;

import static org.example.lesson10.utils.Constants.CONNECTION_EMAIL;
import static org.example.lesson10.utils.Constants.CONNECTION_PHONE;
import static org.example.lesson10.utils.Constants.CONNECTION_SUM;
import static org.example.lesson10.utils.Constants.EXPECTED_PLACEHOLDERS_BY_VARIANT;
import static org.example.lesson10.utils.Constants.MTS_HOME_URL;

public class MtsOnlineRefillService {

    private final MtsHomePage home = new MtsHomePage();
    private final BepaidWidgetPage bepaid = new BepaidWidgetPage();

    public String payBlockTitleNormalized() {
        return home.payBlockTitleTextNormalized();
    }

    public List<String> partnerLogoAlts() {
        return home.partnerLogoAlts();
    }

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
            waitUntilAmountVisible();
            Assert.assertTrue(bepaid.amountDisplayed(CONNECTION_SUM), "Сумма «" + CONNECTION_SUM + "» на странице оплаты");
            waitUntilCardFormVisible();
            assertCardPlaceholders();
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

    private void waitUntilAmountVisible() {
        try {
            bepaid.waitUntilAmountVisible(CONNECTION_SUM);
        } catch (TimeoutException e) {
            Assert.fail("Не дождались суммы «" + CONNECTION_SUM + "». " + bepaid.bodyText());
        }
    }

    private void waitUntilCardFormVisible() {
        try {
            bepaid.waitUntilCardFormVisible();
        } catch (TimeoutException e) {
            Assert.fail("Не появились поля карты. " + bepaid.bodyText());
        }
    }

    private void assertCardPlaceholders() {
        List<String> hints = bepaid.inputHints();
        Assert.assertFalse(hints.isEmpty(), "Ожидались поля карты");
        Assert.assertTrue(bepaid.hasExpiryHint(), "Нет поля срока (ММ / ГГ). hints=" + hints);
        Assert.assertTrue(bepaid.hasCardNumberHint(), "Нет поля номера карты. hints=" + hints);
        Assert.assertTrue(bepaid.hasCvcHint(), "Нет поля CVC/CVV. hints=" + hints);
    }
}
