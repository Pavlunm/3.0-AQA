package org.example.lesson10.utils;

import java.util.List;
import java.util.Map;

public final class Constants {

    public static final String MTS_HOME_URL = "https://www.mts.by/";

    public static final String CONNECTION_PHONE = "297777777";
    public static final String CONNECTION_SUM = "10";
    public static final String CONNECTION_EMAIL = "test@test.com";

    public static final Map<String, List<String>> EXPECTED_PLACEHOLDERS_BY_VARIANT = Map.of(
            "Услуги связи", List.of("Номер телефона", "Сумма", "E-mail для отправки чека"),
            "Домашний интернет", List.of("Номер абонента", "Сумма", "E-mail для отправки чека"),
            "Рассрочка", List.of("Номер счета на 44", "Сумма", "E-mail для отправки чека"),
            "Задолженность", List.of("Номер счета на 2073", "Сумма", "E-mail для отправки чека")
    );

    public static final List<String> EXPECTED_CARD_FIELD_HINTS = List.of(
            "ММ / ГГ",
            "CVC",
            "карт"
    );

    private Constants() {
    }
}
