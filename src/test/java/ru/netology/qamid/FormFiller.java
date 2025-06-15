package ru.netology.qamid;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.qamid.DataGenerator.UserInfo;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class FormFiller {

    /**
     * Заполняет форму с возможностью выбора города из списка или через TAB.
     */
    public static void fill(UserInfo user, String date, boolean agree) {
        // Город
        SelenideElement cityInput = $("[data-test-id='city'] input");
        cityInput.setValue(user.getCity());

        ElementsCollection menuItems = $$(".menu-item");
        if (menuItems.findBy(text(user.getCity())).exists()) {
            menuItems.findBy(text(user.getCity()))
                    .shouldBe(visible, Duration.ofSeconds(3))
                    .click();
        } else {
            cityInput.sendKeys(Keys.TAB);
        }

        // Дата
        $("[data-test-id='date'] input")
                .sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(date);

        // Имя и телефон
        $("[data-test-id='name'] input").setValue(user.getName());
        $("[data-test-id='phone'] input").setValue(user.getPhone());

        // Чекбокс
        if (agree) {
            $("[data-test-id='agreement']").click();
        }
    }
}

