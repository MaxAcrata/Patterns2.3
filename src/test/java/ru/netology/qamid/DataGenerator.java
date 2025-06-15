package ru.netology.qamid;

import com.github.javafaker.Faker;
import com.google.gson.*;
import lombok.Getter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Генератор тестовых данных: город, имя, телефон, дата.
 */
public class DataGenerator {

    private DataGenerator() {}

    private static final Faker faker = new Faker(new Locale("ru"));

    /**
     * Генерирует дату, сдвинутую на указанное количество дней вперёд.
     */
    public static String generateDate(int days) {
        return LocalDate.now().plusDays(days)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    /**
     * Загружает случайный город из файла cities.json.
     */
    public static String generateCity() {
        try (Reader reader = new InputStreamReader(
                new FileInputStream("src/test/resources/cities.json"), StandardCharsets.UTF_8)) {

            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray jsonArray = jsonObject.getAsJsonArray("cities");

            if (jsonArray == null || jsonArray.isEmpty()) {
                throw new RuntimeException("Список городов пуст или отсутствует в JSON");
            }

            List<String> cities = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                cities.add(element.getAsString());
            }

            return cities.get(new Random().nextInt(cities.size()));

        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл cities.json", e);
        }
    }

    /**
     * Генерирует полное имя (Фамилия Имя) на русском языке.
     */
    public static String generateName() {
        return faker.name().lastName() + " " + faker.name().firstName();
    }

    /**
     * Генерирует номер телефона в формате +7XXXXXXXXXX.
     */
    public static String generatePhone() {
        String digits = faker.phoneNumber().subscriberNumber(10);
        return "+7" + digits;
    }

    /**
     * Генерирует пользователя, позволяя переопределить один или несколько параметров.
     *
     * @param city  город (если null — выбирается случайный)
     * @param name  ФИО (если null — генерируется)
     * @param phone телефон (если null — генерируется)
     * @return объект UserInfo
     */
    public static UserInfo generateUserWithOverrides(String city, String name, String phone) {
        return new UserInfo(
                city != null ? city : generateCity(),
                name != null ? name : generateName(),
                phone != null ? phone : generatePhone()
        );
    }

    /**
     * Класс-контейнер для пользователя.
     */
    @Getter
    public static class UserInfo {
        private final String city;
        private final String name;
        private final String phone;

        public UserInfo(String city, String name, String phone) {
            this.city = city;
            this.name = name;
            this.phone = phone;
        }
    }
}
