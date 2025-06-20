package ru.netology.qamid;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.qamid.DataGenerator.UserInfo;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

/**
 * Утилитный класс для заполнения формы бронирования.
 */
public class FormFiller {

    /**
     * Заполняет форму на основе переданных данных.
     * Если город найден в списке — выбирает его кликом.
     * Если город не найден — оставляет как есть (валидация произойдёт при отправке).
     *
     * @param user  объект с полями: город, имя, телефон
     * @param date  дата встречи
     * @param agree ставить ли галочку согласия
     */
    public static void fill(UserInfo user, String date, boolean agree) {
        // Город
        SelenideElement cityInput = $("[data-test-id='city'] input");
        cityInput.setValue(user.getCity());

        // Дата
        SelenideElement dateInput = $("[data-test-id='date'] input");
        dateInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        dateInput.setValue(date);

        // Имя и телефон
        $("[data-test-id='name'] input").setValue(user.getName());
        $("[data-test-id='phone'] input").setValue(user.getPhone());

        // Чекбокс согласия
        if (agree) {
            $("[data-test-id='agreement']").click();
        }
    }
}

