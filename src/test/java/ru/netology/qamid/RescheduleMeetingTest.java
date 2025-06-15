package ru.netology.qamid;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.qamid.DataGenerator.UserInfo;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

/**
 * Тест проверяет возможность перепланирования встречи при повторной отправке формы.
 */
public class RescheduleMeetingTest {

    private static final int seconds = 10;
    private static final int firstDays = 4;
    private static final int secondDays = 6;

    /**
     * Устанавливает параметры браузера перед запуском всех тестов.
     */
    @BeforeAll
    static void setup() {
        Configuration.baseUrl = "http://localhost:9999";
        Configuration.browserSize = "1920x1080";
        Configuration.headless = true;
        System.setProperty("file.encoding", "UTF-8"); // Принудительно задаем кодировку
    }

    /**
     * Открывает страницу формы перед каждым тестом.
     */
    @BeforeEach
    void openForm() {
        open("/");
    }

    /**
     * Проверяет поведение формы при повторной отправке с другой датой:
     * сначала подтверждение первой встречи,
     * затем предложение перепланировать,
     * после подтверждения — финальное уведомление.
     */
    @Test
    void shouldRescheduleMeeting() {
        // Генерация данных пользователя один раз в начале теста
        UserInfo user = DataGenerator.generateUserWithOverrides(null, null, null);
        String firstDate = DataGenerator.generateDate(firstDays);
        String secondDate = DataGenerator.generateDate(secondDays);

        // Первая отправка формы
        FormFiller.fill(user, firstDate, true);
        $("button.button").shouldBe(enabled).click();

        // Ожидаем первое уведомление
        $("[data-test-id='success-notification'] .notification__content")
                .shouldBe(visible, Duration.ofSeconds(seconds))
                .shouldHave(text("Встреча успешно запланирована на " + firstDate));

        // Изменяем только дату, сохраняя остальные данные пользователя
        $("[data-test-id='date'] input")
                .sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(secondDate);

        // Вторая отправка формы с теми же данными пользователя, но новой датой
        // Не заполняем форму заново, так как остальные поля уже содержат нужные значения
        $("button.button").shouldBe(enabled).click();

        // Ожидаем уведомление о перепланировании
        $("[data-test-id='replan-notification'] .notification__content")
                .shouldBe(visible, Duration.ofSeconds(seconds))
                .shouldHave(text("У вас уже запланирована встреча на другую дату"));

        // Клик по кнопке "Перепланировать"
        $$("button").findBy(text("Перепланировать"))
                .shouldBe(visible, Duration.ofSeconds(seconds))
                .click();

        // Ожидаем финальное уведомление
        $("[data-test-id='success-notification'] .notification__content")
                .shouldBe(visible, Duration.ofSeconds(seconds))
                .shouldHave(text("Встреча успешно запланирована на " + secondDate));

        // Проверяем иконку календаря
        $("[data-test-id='success-notification'] .icon_name_calendar")
                .shouldBe(visible, Duration.ofSeconds(5));
    }
}

