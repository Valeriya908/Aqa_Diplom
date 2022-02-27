package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.util.Locale;

@Data
public class DataHelper {

    private DataHelper() {
    }

    public static String getApprovedCardNumber() {
        return "4444 4444 4444 4441";
    }

    public static String getDeclinedCardNumber() {
        return "4444 4444 4444 4442";
    }

    public static String getAnotherCardNumber() {
        Faker faker = new Faker();
        return faker.business().creditCardNumber();
    }

    public static String getMonth(int month) {
        return YearMonth.now().plusMonths(month).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String getInvalidMonth() {
        return "15";
    }

    public static String getYear(int year) {
        return Year.now().plusYears(year).format(DateTimeFormatter.ofPattern("uu"));
    }

    public static String getOwner(String locale) {
        Faker faker = new Faker(new Locale(locale));
        return faker.name().firstName() + " " + faker.name().lastName();
    }

    public static String getLongerOwner() {
        Faker faker = new Faker();
        return faker.name().firstName() + " " + faker.name().lastName() + " " + faker.name().lastName() + " " + faker.name().lastName();
    }

    public static String getValidCode() {
        return RandomStringUtils.randomNumeric(3);
    }

    public static String getInvalidFieldFormat(int countDigit, int countLatinLetter, int countDigitZero, int countSpecialSymbol, int countCyrillicLetters) {
        return RandomStringUtils.randomNumeric(countDigit) + RandomStringUtils.randomAlphabetic(countLatinLetter) + RandomStringUtils.random(countDigitZero, '0')
                + RandomStringUtils.random(countSpecialSymbol, '$', '@', '^', ' ', '%') + RandomStringUtils.random(countCyrillicLetters, 'Ф', 'д', 'Г', 'Й', 'М', 'Щ');
    }
}

