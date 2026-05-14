package org.example.lesson10.service;

import org.example.lesson10.DriverManager;
import org.example.lesson10.page.BepaidWidgetPage;
import org.example.lesson10.page.MtsHomePage;
import org.example.lesson10.utils.Waiter;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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
        WebElement iframe = home.paymentIframe();
        bepaid.switchIntoPaymentIframe(iframe);
        try {
            waitPhoneOrFail();
            waitAmountOrFail();
            assertAmountOnPageOrButton();
            waitCardHintsOrFail();
            assertCardHints();
            Assert.assertTrue(bepaid.hasPaymentLogoImages(), "Не найдены иконки платёжных систем");
        } finally {
            bepaid.switchToDefault();
        }
        return this;
    }

    private void waitPhoneOrFail() {
        try {
            Waiter.untilTrue(DriverManager.getDriver(), Duration.ofSeconds(25),
                    () -> phoneVisible(bepaid.textAndHtmlSnippet(), CONNECTION_PHONE));
        } catch (TimeoutException e) {
            Assert.fail("В виджете bePaid не появился номер «" + CONNECTION_PHONE + "». "
                    + bepaid.visibleTextDebug(2000));
        }
    }

    private void waitAmountOrFail() {
        try {
            Waiter.untilTrue(DriverManager.getDriver(), Duration.ofSeconds(18),
                    () -> amountVisible(bepaid.textAndHtmlSnippet(), CONNECTION_SUM) || payButtonHasSum(CONNECTION_SUM));
        } catch (TimeoutException e) {
            Assert.fail("Не дождались суммы «" + CONNECTION_SUM + "». " + bepaid.visibleTextDebug(2000));
        }
    }

    private void assertAmountOnPageOrButton() {
        String blob = bepaid.textAndHtmlSnippet();
        Assert.assertTrue(amountVisible(blob, CONNECTION_SUM) || payButtonHasSum(CONNECTION_SUM),
                "Сумма «" + CONNECTION_SUM + "» не в тексте/HTML и не на кнопке. body: " + bepaid.bodyText());
    }

    private void waitCardHintsOrFail() {
        try {
            Waiter.untilTrue(DriverManager.getDriver(), Duration.ofSeconds(20),
                    () -> !bepaid.inputHintTexts().isEmpty() || cardHintsInHtml(bepaid.textAndHtmlSnippet()));
        } catch (TimeoutException e) {
            Assert.fail("Не появились поля карты / подсказки. " + bepaid.visibleTextDebug(2000));
        }
    }

    private void assertCardHints() {
        List<String> hints = bepaid.inputHintTexts();
        String blob = bepaid.textAndHtmlSnippet();
        Assert.assertTrue(!hints.isEmpty() || cardHintsInHtml(blob),
                "Ожидались подсказки полей карты. hints=" + hints);
        if (!hints.isEmpty()) {
            Assert.assertTrue(cardNumberOk(hints, blob), "Нет подсказки номера карты. hints=" + hints);
            Assert.assertTrue(expiryOk(hints, blob), "Нет подсказки срока (в т.ч. «ММ / ГГ»). hints=" + hints);
            Assert.assertTrue(cvcOk(hints, blob), "Нет подсказки CVC/CVV. hints=" + hints);
        }
    }

    private static boolean phoneVisible(String blob, String national) {
        if (blob.contains(national)) {
            return true;
        }
        String d = digits(blob);
        return d.contains(national) || d.contains("375" + national)
                || (national.length() > 1 && d.contains("802" + national.substring(1)))
                || d.contains("80" + national);
    }

    private static boolean amountVisible(String blob, String amount) {
        if (amount == null || amount.isBlank()) {
            return false;
        }
        if (blob.contains(amount + ",00") || blob.contains(amount + ".00")) {
            return true;
        }
        String low = blob.toLowerCase();
        if (blob.contains(amount + " BYN") || blob.contains(amount + "\u00a0BYN")
                || low.contains(amount + " br") || blob.contains(amount + " руб")) {
            return true;
        }
        return Pattern.compile("(^|\\D)" + Pattern.quote(amount) + "(\\D|$)").matcher(blob).find();
    }

    private boolean payButtonHasSum(String amount) {
        return DriverManager.getDriver().findElements(By.cssSelector("button, input[type='submit'], [role='button']"))
                .stream()
                .filter(WebElement::isDisplayed)
                .map(this::buttonPack)
                .anyMatch(p -> p.contains(amount));
    }

    private String buttonPack(WebElement btn) {
        return nz(btn.getText()) + nz(btn.getAttribute("value")) + nz(btn.getAttribute("aria-label"))
                + nz(btn.getAttribute("title"));
    }

    private static String nz(String s) {
        return s != null ? s : "";
    }

    private static String digits(String s) {
        return s.replaceAll("\\D+", "");
    }

    private static boolean cardHintsInHtml(String low) {
        low = low.toLowerCase();
        boolean secret = containsAny(low, "cvc", "cvv", "security code", "csc", "код");
        boolean cardish = containsAny(low, "card", "карт", "number", "pan", "номер");
        boolean expiry = containsAny(low, "exp", "mm/", "/yy", "срок", "valid");
        return secret && (cardish || expiry);
    }

    private static boolean containsAny(String haystack, String... needles) {
        return Arrays.stream(needles).anyMatch(haystack::contains);
    }

    private static boolean cardNumberOk(List<String> hints, String blob) {
        return hints.stream().anyMatch(p -> containsIc(p, "номер") || containsIc(p, "карт") || containsIc(p, "card"))
                || containsCardInBlob(blob);
    }

    private static boolean expiryOk(List<String> hints, String blob) {
        return hints.stream().anyMatch(MtsOnlineRefillService::expiryHint)
                || containsExpiryInBlob(blob);
    }

    private static boolean cvcOk(List<String> hints, String blob) {
        return hints.stream().anyMatch(p -> containsIc(p, "cvc") || containsIc(p, "cvv")
                || containsIc(p, "код") || containsIc(p, "csc"))
                || containsCvcInBlob(blob);
    }

    private static boolean expiryHint(String p) {
        if (p == null || p.isBlank()) {
            return false;
        }
        if (containsIc(p, "срок") || containsIc(p, "exp") || containsIc(p, "valid")) {
            return true;
        }
        boolean mm = p.contains("ММ") || p.contains("мм") || containsIc(p, "mm");
        boolean yy = p.contains("ГГ") || p.contains("гг") || containsIc(p, "yy");
        return mm && yy;
    }

    private static boolean containsExpiryInBlob(String blob) {
        return containsIc(blob, "mm/yy") || containsIc(blob, "exp date")
                || (blob != null && blob.contains("ММ") && blob.contains("ГГ"));
    }

    private static boolean containsCardInBlob(String blob) {
        return (containsIc(blob, "номер") && containsIc(blob, "карт"))
                || containsIc(blob, "card number")
                || containsIc(blob, "номер карты")
                || containsIc(blob, "pan")
                || containsIc(blob, "ccnumber")
                || containsIc(blob, "cc-number")
                || containsIc(blob, "cardnumber");
    }

    private static boolean containsCvcInBlob(String blob) {
        return containsIc(blob, "cvc") || containsIc(blob, "cvv") || containsIc(blob, "csc")
                || containsIc(blob, "security code");
    }

    private static boolean containsIc(String hay, String needle) {
        return hay != null && hay.toLowerCase().contains(needle.toLowerCase());
    }
}
