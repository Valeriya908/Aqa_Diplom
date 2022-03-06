package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.testng.annotations.BeforeTest;
import ru.netology.data.DataHelper;
import ru.netology.data.DatabaseHelper;
import ru.netology.page.DebitCardPage;
import ru.netology.page.MainPage;


import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentDebitCardTest {
    MainPage mainPage = open("http://localhost:8080", MainPage.class);
    DebitCardPage debitPaymentPage = mainPage.buyWithCard();

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void cleanBase() {
        DatabaseHelper.clearDB();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeTest
    public void openDebitPaymentPage() {
        debitPaymentPage.checkVisibleHeadingDebitCard();
    }

    // Positive Test 1...2

    //    Test 1
    @SneakyThrows
    @Test
    void shouldUsualBuyWithApprovedCard() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.successMessageForm();
        assertEquals("APPROVED", DatabaseHelper.getTransactionStatusDebitCard());
        assertNotNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 2
    @SneakyThrows
    @Test
    void shouldUsualBuyWithDeclinedCard() {
        var cardNumber = DataHelper.getDeclinedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        assertEquals("DECLINED", DatabaseHelper.getTransactionStatusDebitCard());
        assertNotNull(DatabaseHelper.getTransactionTypeDebitCard());
        debitPaymentPage.errorMessageForm();
    }

    // Negative Test 1 (1* - для воспроизведения бага)

    //    Test 1
    @SneakyThrows
    @Test
    void shouldUsualBuyWithAnotherCard() {
        var cardNumber = DataHelper.getAnotherCardNumber();
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageForm();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 1*
    @SneakyThrows
    @Test
    void shouldVisibleExtraMessageWhenUsualBuyWithAnotherCard() {
        var cardNumber = DataHelper.getAnotherCardNumber();
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageForm();
        debitPaymentPage.closeErrorSendFormMessage();
    }

    // Negative Test 2...5

    //    Test 2
    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidLengthFormatCardNumber() {
        var cardNumber = DataHelper.getInvalidFieldFormat(14, 0, 0, 0, 0);
        var month = DataHelper.getMonth(4);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidCardNumberField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 3
    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidCardNumberWhenAllDigitZero() {
        var cardNumber = DataHelper.getInvalidFieldFormat(0, 0, 16, 0, 0);
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidCardNumberField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 4
    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidCardNumberIncludeSymbolsAndLetters() {
        var cardNumber = DataHelper.getInvalidFieldFormat(0, 5, 0, 6, 5);
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidCardNumberField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 5
    @SneakyThrows
    @Test
    void shouldUsualBuyWithEmptyCardNumberField() {
        var cardNumber = DataHelper.getInvalidFieldFormat(0, 0, 0, 0, 0);
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageCardNumberFieldEmpty();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    // Negative Test 6...12

    //    Test 6
    @SneakyThrows
    @Test
    void shouldUsualBuyWithOutOfDateMonth() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(-1);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageAboutOutOfDateMonthOrNonexistentMonth();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 7
    @SneakyThrows
    @Test
    void shouldUsualBuyWithNonexistentMonth() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidMonth();
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageAboutOutOfDateMonthOrNonexistentMonth();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 8
    @SneakyThrows
    @Test
    void shouldUsualBuyWithEmptyMonthField() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageMonthFieldEmpty();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 9
    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidMonthWhenAllDigitZero() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 0, 2, 0, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidMonthField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 10
    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidLengthFormatMonth() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(1, 0, 0, 0, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidMonthField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 11
    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidMonthIncludeLetters() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 1);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidMonthField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 12
    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidMonthIncludeSymbols() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 0, 0, 2, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidMonthField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    // Negative Test 13...20

    //    Test 13
    @SneakyThrows
    @Test
    void shouldUsualBuyWithOutOfDateYear() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(-1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageAboutOutOfDateYear();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 14
    @SneakyThrows
    @Test
    void shouldUsualBuyWithValidityPeriodExpiresInFiveYears() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(5);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.successMessageForm();
        assertEquals("APPROVED", DatabaseHelper.getTransactionStatusDebitCard());
        assertNotNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 15
    @SneakyThrows
    @Test
    void shouldUsualBuyWithValidityPeriodExpiresInSixYears() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(6);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.successMessageForm();
        assertEquals("APPROVED", DatabaseHelper.getTransactionStatusDebitCard());
        assertNotNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 16
    @SneakyThrows
    @Test
    void shouldUsualBuyWithEmptyYearField() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageYearFieldEmpty();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 17
    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidYearWhenAllDigitZero() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 0, 2, 0, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageAboutOutOfDateYear();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 18
    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidLengthFormatYear() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(1, 0, 0, 0, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidYearField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 19
    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidYearIncludeLetters() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidYearField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 20
    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidYearIncludeSymbols() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 0, 0, 2, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidYearField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    // Negative Test 21...25

    //    Test 21
    @SneakyThrows
    @Test
    void shouldUsualBuyWithOwnerIncludeCyrillicLetters() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("ru");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidOwnerField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 22
    @SneakyThrows
    @Test
    void shouldUsualBuyWithOwnerFieldLengthConsistingOfOneLetter() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 0);
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidOwnerField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 23
    @SneakyThrows
    @Test
    void shouldUsualBuyWithOwnerFieldLengthOverLimit() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getLongerOwner();
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidOwnerField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 24
    @SneakyThrows
    @Test
    void shouldUsualBuyWithEmptyOwnerField() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageOwnerFieldEmptyWhenTestedOwnerField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 25
    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidOwnerIncludeDigitsAndSymbols() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(4, 0, 0, 4, 0);
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidOwnerField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    // Negative Test 26...30

    //    Test 26
    @SneakyThrows
    @Test
    void shouldUsualBuyWithEmptyCodeField() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageCodeFieldEmpty();
        debitPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 27
    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidCodeWhenAllDigitZero() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 0, 3, 0, 0);
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidCodeField();
        debitPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 28
    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidLengthFormatCode() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(2, 0, 0, 0, 0);
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidCodeField();
        debitPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 29
    @SneakyThrows
    @Test
    void shouldUsualBuyWithWithInvalidCodeIncludeLetters() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 2);
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidCodeField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
        debitPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
    }

    //    Test 30
    @SneakyThrows
    @Test
    void shouldUsualBuyWithWithInvalidCodeIncludeSymbols() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 0, 0, 3, 0);
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidCodeField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
        debitPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
    }
}


