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
                String blob = widget.aggregatedWidgetBlob();
                boolean hasExpiryHint = placeholders.stream().anyMatch(MtsOnlineRefillService::isExpiryPlaceholderText)
                        || containsExpiryInBlob(blob);
                boolean hasCardNumberHint = placeholders.stream().anyMatch(MtsOnlineRefillService::isCardNumberPlaceholderText)
                        || containsCardNumberInBlob(blob);
                boolean hasCvcHint = placeholders.stream().anyMatch(MtsOnlineRefillService::isCvcPlaceholderText)
                        || containsCvcInBlob(blob);
                Assert.assertTrue(hasCardNumberHint,
                        "Нет подсказки номера карты (список: " + placeholders + "). Фрагмент виджета: "
                                + shorten(blob, 400));
                Assert.assertTrue(hasExpiryHint,
                        "Нет подсказки срока (ожидается в т.ч. «ММ / ГГ»). Список: " + placeholders);
                Assert.assertTrue(hasCvcHint,
                        "Нет подсказки CVC/CVV. Список: " + placeholders);
            }
            Assert.assertTrue(widget.hasPaymentBrandImages(), "Не найдены иконки платёжных систем (Visa/MasterCard/Белкарт и т.п.)");
        } finally {
            widget.switchToDefault();
        }
        return this;
    }

    private static boolean isExpiryPlaceholderText(String p) {
        if (p == null || p.isBlank()) {
            return false;
        }
        if (containsIgnoreCase(p, "срок") || containsIgnoreCase(p, "exp") || containsIgnoreCase(p, "valid")) {
            return true;
        }
        boolean mm = p.contains("ММ") || p.contains("мм") || containsIgnoreCase(p, "mm");
        boolean yy = p.contains("ГГ") || p.contains("гг") || containsIgnoreCase(p, "yy");
        return mm && yy;
    }

    private static boolean isCardNumberPlaceholderText(String p) {
        if (p == null) {
            return false;
        }
        return containsIgnoreCase(p, "номер")
                || containsIgnoreCase(p, "карт")
                || containsIgnoreCase(p, "card");
    }

    private static boolean isCvcPlaceholderText(String p) {
        if (p == null) {
            return false;
        }
        return containsIgnoreCase(p, "cvc")
                || containsIgnoreCase(p, "cvv")
                || containsIgnoreCase(p, "код")
                || containsIgnoreCase(p, "csc");
    }

    private static boolean containsExpiryInBlob(String blob) {
        if (blob == null) {
            return false;
        }
        return containsIgnoreCase(blob, "mm/yy") || containsIgnoreCase(blob, "exp date")
                || (blob.contains("ММ") && blob.contains("ГГ"));
    }

    private static boolean containsCardNumberInBlob(String blob) {
        if (blob == null) {
            return false;
        }
        return (containsIgnoreCase(blob, "номер") && containsIgnoreCase(blob, "карт"))
                || containsIgnoreCase(blob, "card number")
                || containsIgnoreCase(blob, "номер карты")
                || containsIgnoreCase(blob, "pan")
                || containsIgnoreCase(blob, "ccnumber")
                || containsIgnoreCase(blob, "cc-number")
                || containsIgnoreCase(blob, "cardnumber");
    }

    private static boolean containsCvcInBlob(String blob) {
        if (blob == null) {
            return false;
        }
        return containsIgnoreCase(blob, "cvc") || containsIgnoreCase(blob, "cvv") || containsIgnoreCase(blob, "csc")
                || containsIgnoreCase(blob, "security code");
    }

    private static String shorten(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }

    private static boolean containsIgnoreCase(String haystack, String needle) {
        return haystack != null && haystack.toLowerCase().contains(needle.toLowerCase());
    }
}
