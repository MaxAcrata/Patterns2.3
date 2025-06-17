package ru.netology.qamid;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.qamid.DataGenerator.UserInfo;
import org.junit.jupiter.api.Disabled;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

//java -jar ./artifacts/app-replan-delivery.jar &

/**
 * Класс содержит авто тесты для формы бронирования встречи.
 * Используются данные, сгенерированные с помощью класса DataGenerator.
 */
public class BankFormTest {

    private static final int seconds = 10;      // Время ожидания для валидации
    private static final int addedDays = 3;     // Смещение даты для валидной заявки

    /**
     * Выполняется один раз перед всеми тестами.
     * Устанавливает конфигурацию Selenide.
     */
    @BeforeAll
    static void setup() {
        Configuration.baseUrl = "http://localhost:9999";
        Configuration.browserSize = "1920x1080";
        System.setProperty("file.encoding", "UTF-8"); // Принудительно задаем кодировку
    }

    /**
     * Выполняется перед каждым тестом.
     * Открывает главную страницу приложения.
     */
    @BeforeEach
    void openForm() {
        open("/");
    }

    /**
     * Позитивный сценарий — форма успешно отправляется с валидными данными.
     */
    @Test
    void formSuccessfully() {
        UserInfo user = DataGenerator.generateUserWithOverrides(null, null, null);
        String expectedDate = DataGenerator.generateDate(addedDays);

        FormFiller.fill(user, expectedDate, true); // Используем метод из FormFiller

        $("button.button").shouldBe(enabled).click(); // Нажатие кнопки

        // Проверка, что появилось всплывающее окно с заголовком "Успешно"
        $(".notification__title")
                // Ожидаем, что заголовок станет видимым в течение заданного времени
                .shouldBe(visible, Duration.ofSeconds(seconds))
                .shouldHave(text("Успешно"));

        // Проверка, что содержимое уведомления содержит ожидаемую дату встречи
        $(".notification__content")
                .shouldHave(text("Встреча успешно запланирована на " + expectedDate));
    }

    /**
     * Негативный сценарий — не установлен флажок согласия.
     * Ожидаем подсветку поля "Согласие" как обязательного.
     */
    @Test
    void rejectUnchecked() {
        UserInfo user = DataGenerator.generateUserWithOverrides(null, null, null);
        String date = DataGenerator.generateDate(addedDays);

        FormFiller.fill(user, date, false); // Без установки флажка

        $("button.button").click();

        $("[data-test-id='agreement'].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(seconds));
    }

    /**
     * Негативный сценарий — введён невалидный номер телефона.
     */
    @Disabled("Известный баг: форма принимает неверный номер телефона") // Деактивация теста
    @Test
    void invalidPhone() {
        UserInfo user = DataGenerator.generateUserWithOverrides(null, null, "1234"); // Невалидный номер
        String date = DataGenerator.generateDate(addedDays);

        FormFiller.fill(user, date, true);

        $("button.button").click();

        $("[data-test-id='phone'].input_invalid .input__sub")
                .shouldBe(visible, Duration.ofSeconds(seconds))
                .shouldHave(text("Телефон указан неверно"));
    }

    /**
     * Негативный сценарий — введено недопустимое имя (латиницей).
     */
    @Test
    void invalidName() {
        UserInfo user = DataGenerator.generateUserWithOverrides(null, "John Smith", null);
        String date = DataGenerator.generateDate(addedDays);

        FormFiller.fill(user, date, true);

        $("button.button").click();

        $("[data-test-id='name'].input_invalid .input__sub")
                .shouldBe(visible, Duration.ofSeconds(seconds))
                .shouldHave(text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    /**
     * Негативный сценарий — введён город, отсутствующий в списке.
     */
    @Test
    void invalidCity() {
        UserInfo user = DataGenerator.generateUserWithOverrides("Ура", null, null); // Невалидный город
        String date = DataGenerator.generateDate(addedDays);

        FormFiller.fill(user, date, true);

        $("button.button").click();

        $("[data-test-id='city'].input_invalid .input__sub")
                .shouldBe(visible, Duration.ofSeconds(seconds))
                .shouldHave(text("Доставка в выбранный город недоступна"));
    }
}
