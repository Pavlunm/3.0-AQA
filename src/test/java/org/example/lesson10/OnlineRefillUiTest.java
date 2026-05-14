package org.example.lesson10;

import org.testng.annotations.Test;

import java.util.List;

import static org.example.lesson10.utils.Constants.EXPECTED_PARTNER_LOGO_ALTS;
import static org.example.lesson10.utils.Constants.PAY_BLOCK_TITLE;
import static org.testng.Assert.assertEquals;

public class OnlineRefillUiTest extends BaseTest {

    private static final List<String> PAYMENT_VARIANTS = List.of(
            "Услуги связи",
            "Домашний интернет",
            "Рассрочка",
            "Задолженность"
    );

    @Test(description = "Блок «Онлайн пополнение без комиссии»: PageObject, плейсхолдеры, сценарий оплаты")
    public void onlineRefillWithoutCommission_fullScenario() {
        mtsOnlineRefillService.openHomeAcceptCookiesAndScrollToPay();

        assertEquals(
                mtsOnlineRefillService.payBlockTitleNormalized(),
                PAY_BLOCK_TITLE,
                "Заголовок блока"
        );
        assertEquals(
                mtsOnlineRefillService.partnerLogoAlts(),
                EXPECTED_PARTNER_LOGO_ALTS,
                "Логотипы платёжных партнёров (alt)"
        );

        for (String variant : PAYMENT_VARIANTS) {
            mtsOnlineRefillService.assertPlaceholdersForVariant(variant);
        }

        mtsOnlineRefillService
                .fillConnectionServicePrerequisitesAndContinue()
                .assertBepaidWidgetShowsOrderSummaryAndCardForm();
    }
}
