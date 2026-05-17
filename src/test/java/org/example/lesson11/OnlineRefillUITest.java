package org.example.lesson11;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import java.util.List;

@Epic("MTS.by")
@Feature("Онлайн пополнение без комиссии")
public class OnlineRefillUITest extends BaseTest {

    private static final List<String> PAYMENT_VARIANTS = List.of(
            "Услуги связи",
            "Домашний интернет",
            "Рассрочка",
            "Задолженность"
    );

    @Test(description = "ДЗ lesson11: Selenium + Allure — блок пополнения, плейсхолдеры и виджет bePaid")
    @Description("Проверка блока «Онлайн пополнение без комиссии», плейсхолдеров 4 вариантов оплаты и виджета bePaid")
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
