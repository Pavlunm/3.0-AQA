package org.example.lesson11;

import io.qameta.allure.Description;
import org.testng.annotations.Test;

import java.util.List;

public class UiTest extends BaseTest {

    private static final List<String> PAYMENT_VARIANTS = List.of(
            "Услуги связи",
            "Домашний интернет",
            "Рассрочка",
            "Задолженность"
    );

    @Test(testName = "Онлайн пополнение без комиссии")
    @Description("Проверка блока пополнения, плейсхолдеров 4 вариантов оплаты и виджета bePaid")
    public void onlineRefillWithoutCommission_fullScenario() {
        mtsHomePageService
                .openHomeAcceptCookiesAndScrollToPay()
                .assertPayBlockChecks();

        for (String variant : PAYMENT_VARIANTS) {
            mtsHomePageService.assertPlaceholdersForVariant(variant);
        }

        mtsHomePageService
                .fillConnectionServiceAndContinue()
                .assertOrderSummaryAndCardForm();
    }
}
