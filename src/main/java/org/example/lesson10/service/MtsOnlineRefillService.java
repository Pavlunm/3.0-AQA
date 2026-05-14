package org.example.lesson10.service;

import io.qameta.allure.Step;
import org.example.lesson10.page.BepaidWidgetPage;
import org.example.lesson10.page.MtsHomePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;

import static org.example.lesson10.utils.Constants.CONNECTION_EMAIL;
import static org.example.lesson10.utils.Constants.CONNECTION_PHONE;
import static org.example.lesson10.utils.Constants.CONNECTION_SUM;
import static org.example.lesson10.utils.Constants.EXPECTED_PLACEHOLDERS_BY_VARIANT;
import static org.example.lesson10.utils.Constants.MTS_HOME_URL;

public class MtsOnlineRefillService {

    private final MtsHomePage home = new MtsHomePage();
    private final BepaidWidgetPage widget = new BepaidWidgetPage();

    public String payBlockTitleNormalized() {
        return home.payBlockTitleTextNormalized();
    }

    public List<String> partnerLogoAlts() {
        return home.partnerLogoAlts();
    }

    @Step("Открыть mts.by, принять cookie, прокрутить к блоку оплаты")
    public MtsOnlineRefillService openHomeAcceptCookiesAndScrollToPay() {
        home.open(MTS_HOME_URL).acceptCookiesIfVisible().scrollToPayBlock();
        return this;
    }

    @Step("Проверить плейсхолдеры незаполненных полей: {variant}")
    public MtsOnlineRefillService assertPlaceholdersForVariant(String variant) {
        home.selectPaymentVariant(variant);
        List<String> actual = home.placeholdersOfVisibleForm(home.formIdForVariant(variant));
        List<String> expected = EXPECTED_PLACEHOLDERS_BY_VARIANT.get(variant);
        Assert.assertEquals(actual, expected, "Плейсхолдеры для варианта «" + variant + "»");
        return this;
    }

    @Step("Услуги связи: заполнить поля и нажать «Продолжить»")
    public MtsOnlineRefillService fillConnectionServicePrerequisitesAndContinue() {
        home.selectPaymentVariant("Услуги связи");
        home.fillConnectionService(CONNECTION_PHONE, CONNECTION_SUM, CONNECTION_EMAIL);
        home.clickContinueOnConnectionForm();
        return this;
    }

    @Step("В окне оплаты: сумма, телефон, поля карты, иконки платёжных систем")
    public MtsOnlineRefillService assertBepaidWidgetShowsOrderSummaryAndCardForm() {
        WebElement iframe = home.paymentIframe();
        widget.switchIntoPaymentIframe(iframe);
        try {
            try {
                widget.waitUntilNationalPhoneDetected(CONNECTION_PHONE, Duration.ofSeconds(25));
            } catch (TimeoutException e) {
                Assert.fail("В виджете bePaid не появился номер «" + CONNECTION_PHONE
                        + "» (ни в тексте, ни в разметке). Фрагмент: "
                        + widget.debugWidgetSnapshot(2000));
            }
            try {
                widget.waitUntilAmountOrPayButtonShowsSum(CONNECTION_SUM, Duration.ofSeconds(18));
            } catch (TimeoutException e) {
                Assert.fail("Не дождались суммы «" + CONNECTION_SUM + "» в виджете bePaid. "
                        + widget.debugWidgetSnapshot(2000));
            }
            Assert.assertTrue(
                    widget.isAmountVisibleSomewhere(CONNECTION_SUM) || widget.isPayActionShowingAmount(CONNECTION_SUM),
                    "Сумма «" + CONNECTION_SUM + "» не найдена в тексте/HTML и не на кнопке оплаты. body: "
                            + widget.bodyText()
            );
            try {
                widget.waitUntilCardFieldsOrHintsPresent(Duration.ofSeconds(20));
            } catch (TimeoutException e) {
                Assert.fail("Не появились поля карты / подсказки. " + widget.debugWidgetSnapshot(2000));
            }
            List<String> placeholders = widget.visibleCardFieldHints();
            boolean hintsInMarkup = widget.hasCardHintsInMarkup();
            Assert.assertTrue(!placeholders.isEmpty() || hintsInMarkup,
                    "Ожидались плейсхолдеры полей карты или их признаки в разметке. hints=" + placeholders);
            if (!placeholders.isEmpty()) {
                boolean hasCardNumberHint = placeholders.stream().anyMatch(p ->
                        containsIgnoreCase(p, "номер")
                                || containsIgnoreCase(p, "карт")
                                || containsIgnoreCase(p, "card"));
                boolean hasExpiryHint = placeholders.stream().anyMatch(p ->
                        containsIgnoreCase(p, "срок")
                                || containsIgnoreCase(p, "mm")
                                || containsIgnoreCase(p, "exp"));
                boolean hasCvcHint = placeholders.stream().anyMatch(p ->
                        containsIgnoreCase(p, "cvc")
                                || containsIgnoreCase(p, "cvv")
                                || containsIgnoreCase(p, "код"));
                Assert.assertTrue(hasCardNumberHint, "Нет плейсхолдера номера карты среди: " + placeholders);
                Assert.assertTrue(hasExpiryHint, "Нет плейсхолдера срока действия среди: " + placeholders);
                Assert.assertTrue(hasCvcHint, "Нет плейсхолдера CVC/CVV среди: " + placeholders);
            }
            Assert.assertTrue(widget.hasPaymentBrandImages(), "Не найдены иконки платёжных систем (Visa/MasterCard/Белкарт и т.п.)");
        } finally {
            widget.switchToDefault();
        }
        return this;
    }

    private static boolean containsIgnoreCase(String haystack, String needle) {
        return haystack != null && haystack.toLowerCase().contains(needle.toLowerCase());
    }
}
