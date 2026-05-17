package org.example.lesson10;

import org.testng.annotations.Test;

import java.util.List;

public class OnlineRefillUITest extends BaseTest {

    private static final List<String> PAYMENT_VARIANTS = List.of(
            "Услуги связи",
            "Домашний интернет",
            "Рассрочка",
            "Задолженность"
    );

    @Test(description = "ДЗ lesson9+10: блок пополнения, плейсхолдеры 4 вариантов и виджет bePaid")
    public void onlineRefillWithoutCommission_fullScenario() {
        mtsOnlineRefillService
                .openHomeAcceptCookiesAndScrollToPay()
                .assertLesson9PayBlockChecks();

        for (String variant : PAYMENT_VARIANTS) {
            mtsOnlineRefillService.assertPlaceholdersForVariant(variant);
        }

        mtsOnlineRefillService
                .fillConnectionServicePrerequisitesAndContinue()
                .assertBepaidWidgetShowsOrderSummaryAndCardForm();
    }
}
