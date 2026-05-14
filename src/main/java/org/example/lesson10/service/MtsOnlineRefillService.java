package org.example.lesson10.service;

import io.qameta.allure.Step;
import org.example.lesson10.page.BepaidWidgetPage;
import org.example.lesson10.page.MtsHomePage;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

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
            String text = widget.bodyText();
            String digits = text.replaceAll("\\D+", "");
            Assert.assertTrue(
                    digits.contains("375297777777") || digits.contains("297777777"),
                    "В тексте виджета должен отображаться номер телефона (цифры): " + text
            );
            Assert.assertTrue(
                    text.contains(CONNECTION_SUM) || text.contains(CONNECTION_SUM + ",00") || text.contains(CONNECTION_SUM + ".00"),
                    "В тексте виджета должна отображаться сумма пополнения: " + text
            );
            String payButtonText = widget.findPayButtonTextContaining(CONNECTION_SUM);
            Assert.assertTrue(
                    payButtonText.contains(CONNECTION_SUM),
                    "Кнопка оплаты должна содержать сумму. Текст кнопки: '" + payButtonText + "', весь текст: " + text
            );
            List<String> placeholders = widget.visibleCardFieldHints();
            Assert.assertFalse(placeholders.isEmpty(), "Ожидались плейсхолдеры полей карты");
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
