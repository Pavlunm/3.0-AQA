package org.example.lesson11.service;

import io.qameta.allure.Step;
import org.example.lesson11.page.MtsHomePage;
import org.testng.Assert;

import java.util.List;

import static org.example.lesson11.utils.Constants.CONNECTION_EMAIL;
import static org.example.lesson11.utils.Constants.CONNECTION_PHONE;
import static org.example.lesson11.utils.Constants.CONNECTION_SUM;
import static org.example.lesson11.utils.Constants.DETAILS_LINK_TEXT;
import static org.example.lesson11.utils.Constants.DETAILS_LINK_URL_PART;
import static org.example.lesson11.utils.Constants.EXPECTED_PARTNER_LOGO_ALTS;
import static org.example.lesson11.utils.Constants.EXPECTED_PLACEHOLDERS_BY_VARIANT;
import static org.example.lesson11.utils.Constants.MTS_HOME_URL;
import static org.example.lesson11.utils.Constants.PAY_BLOCK_TITLE;

public class MtsHomePageService {

    private final MtsHomePage homePage = new MtsHomePage();

    @Step("Открыть главную MTS, принять cookies и прокрутить к блоку пополнения")
    public MtsHomePageService openHomeAcceptCookiesAndScrollToPay() {
        homePage.open(MTS_HOME_URL).acceptCookiesIfVisible().scrollToPayBlock();
        return this;
    }

    @Step("Проверить блок «Онлайн пополнение без комиссии»")
    public MtsHomePageService assertPayBlockChecks() {
        Assert.assertEquals(
                PAY_BLOCK_TITLE,
                homePage.payBlockTitleTextNormalized(),
                "Название блока «Онлайн пополнение без комиссии»");

        List<String> logoAlts = homePage.partnerLogoAlts();
        Assert.assertEquals(EXPECTED_PARTNER_LOGO_ALTS.size(), logoAlts.size(),
                "Количество логотипов платёжных систем");
        for (String expectedAlt : EXPECTED_PARTNER_LOGO_ALTS) {
            Assert.assertTrue(logoAlts.contains(expectedAlt),
                    "Нет логотипа с alt «" + expectedAlt + "». Найдено: " + logoAlts);
        }

        Assert.assertEquals(DETAILS_LINK_TEXT, homePage.detailsLinkText(),
                "Текст ссылки «Подробнее о сервисе»");
        Assert.assertTrue(homePage.detailsLinkHref().contains(DETAILS_LINK_URL_PART),
                "Некорректный URL ссылки «Подробнее о сервисе»");
        homePage.clickDetailsAboutServiceLink()
                .waitDetailsHelpPageOpened(DETAILS_LINK_URL_PART)
                .navigateBackToHomePage()
                .waitPayBlockTitleVisible()
                .scrollToPayBlock();

        return this;
    }

    @Step("Проверить плейсхолдеры для варианта «{variant}»")
    public MtsHomePageService assertPlaceholdersForVariant(String variant) {
        homePage.selectPaymentVariant(variant);
        List<String> actual = homePage.placeholdersOfVisibleForm(homePage.formIdForVariant(variant));
        List<String> expected = EXPECTED_PLACEHOLDERS_BY_VARIANT.get(variant);
        Assert.assertEquals(actual, expected, "Плейсхолдеры для варианта «" + variant + "»");
        return this;
    }

    @Step("Заполнить форму «Услуги связи» и перейти к оплате")
    public BepaidWidgetService fillConnectionServiceAndContinue() {
        homePage.selectPaymentVariant("Услуги связи");
        homePage.fillConnectionService(CONNECTION_PHONE, CONNECTION_SUM, CONNECTION_EMAIL);
        homePage.clickContinueOnConnectionForm();
        homePage.waitPaymentFlowStarted();
        return new BepaidWidgetService(homePage);
    }
}
