package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.testng.annotations.BeforeTest;
import ru.netology.data.DataHelper;
import ru.netology.data.DatabaseHelper;
import ru.netology.page.CreditCardPage;
import ru.netology.page.MainPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentCreditCardTest {
    MainPage mainPage = open("http://localhost:8080", MainPage.class);
    CreditCardPage creditPaymentPage = mainPage.buyWithCardOnCredit();

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
    public void openCreditPaymentPage() {
        creditPaymentPage.checkVisibleHeadingCreditCard();
    }

//     Positive Test 1...2

    //    Test 1
    @SneakyThrows
    @Test
    void shouldCreditBuyWithApprovedCard() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.successMessageForm();
        assertEquals("APPROVED", DatabaseHelper.getTransactionStatusCreditCard());
        assertNotNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 2.1
    @SneakyThrows
    @Test
    void shouldCreditBuyWithDeclinedCard() {
        var cardNumber = DataHelper.getDeclinedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);;
        creditPaymentPage.errorMessageForm();
        assertEquals("DECLINED", DatabaseHelper.getTransactionStatusCreditCard());
        assertNotNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    // Negative Test 1 (1* - для воспроизведения бага)

    //    Test 1
    @SneakyThrows
    @Test
    void shouldCreditBuyWithAnotherCard() {
        var cardNumber = DataHelper.getAnotherCardNumber();
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageForm();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 1*
    @SneakyThrows
    @Test
    void shouldVisibleExtraMessageWhenCreditBuyWithAnotherCard() {
        var cardNumber = DataHelper.getAnotherCardNumber();
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageForm();
        creditPaymentPage.closeErrorSendFormMessage();
    }

    // Negative Test 2...5

    //    Test 2
    @SneakyThrows
    @Test
    void shouldCreditBuyWithInvalidLengthFormatCardNumber() {
        var cardNumber = DataHelper.getInvalidFieldFormat(14, 0, 0, 0, 0);
        var month = DataHelper.getMonth(4);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidCardNumberField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 3
    @SneakyThrows
    @Test
    void shouldCreditBuyWithInvalidCardNumberWhenAllDigitZero() {
        var cardNumber = DataHelper.getInvalidFieldFormat(0, 0, 16, 0, 0);
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidCardNumberField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 4
    @SneakyThrows
    @Test
    void shouldCreditBuyWithInvalidCardNumberIncludeSymbolsAndLetters() {
        var cardNumber = DataHelper.getInvalidFieldFormat(0, 5, 0, 6, 5);
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidCardNumberField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 5
    @SneakyThrows
    @Test
    void shouldCreditBuyWithEmptyCardNumberField() {
        var cardNumber = DataHelper.getInvalidFieldFormat(0, 0, 0, 0, 0);
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageCardNumberFieldEmpty();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    // Negative Test 6...12

    //    Test 6
    @SneakyThrows
    @Test
    void shouldCreditBuyWithOutOfDateMonth() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(-1);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageAboutOutOfDateMonthOrNonexistentMonth();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 7
    @SneakyThrows
    @Test
    void shouldCreditBuyWithNonexistentMonth() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidMonth();
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageAboutOutOfDateMonthOrNonexistentMonth();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 8
    @SneakyThrows
    @Test
    void shouldCreditBuyWithEmptyMonthField() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageMonthFieldEmpty();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 9
    @SneakyThrows
    @Test
    void shouldCreditBuyWithInvalidMonthWhenAllDigitZero() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 0, 2, 0, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidMonthField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 10
    @SneakyThrows
    @Test
    void shouldCreditBuyWithInvalidLengthFormatMonth() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(1, 0, 0, 0, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidMonthField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 11
    @SneakyThrows
    @Test
    void shouldCreditBuyWithInvalidMonthIncludeLetters() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 1);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidMonthField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 12
    @SneakyThrows
    @Test
    void shouldCreditBuyWithInvalidMonthIncludeSymbols() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 0, 0, 2, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidMonthField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    // Negative Test 13...20

    //    Test 13
    @SneakyThrows
    @Test
    void shouldCreditBuyWithOutOfDateYear() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(-1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageAboutOutOfDateYear();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 14
    @SneakyThrows
    @Test
    void shouldCreditBuyWithValidityPeriodExpiresInFiveYears() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(5);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.successMessageForm();
        assertEquals("APPROVED", DatabaseHelper.getTransactionStatusCreditCard());
        assertNotNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 15
    @SneakyThrows
    @Test
    void shouldCreditBuyWithValidityPeriodExpiresInSixYears() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(6);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.successMessageForm();
        assertEquals("APPROVED", DatabaseHelper.getTransactionStatusCreditCard());
        assertNotNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 16
    @SneakyThrows
    @Test
    void shouldCreditBuyWithEmptyYearField() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageYearFieldEmpty();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 17
    @SneakyThrows
    @Test
    void shouldCreditBuyWithInvalidYearWhenAllDigitZero() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 0, 2, 0, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageAboutOutOfDateYear();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 18
    @SneakyThrows
    @Test
    void shouldCreditBuyWithInvalidLengthFormatYear() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(1, 0, 0, 0, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidYearField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 19
    @SneakyThrows
    @Test
    void shouldCreditBuyWithInvalidYearIncludeLetters() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidYearField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 20
    @SneakyThrows
    @Test
    void shouldCreditBuyWithInvalidYearIncludeSymbols() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 0, 0, 2, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidYearField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    // Negative Test 21...25

    //    Test 21
    @SneakyThrows
    @Test
    void shouldCreditBuyWithOwnerIncludeCyrillicLetters() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("ru");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidOwnerField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 22
    @SneakyThrows
    @Test
    void shouldCreditBuyWithOwnerFieldLengthConsistingOfOneLetter() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 0);
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidOwnerField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 23
    @SneakyThrows
    @Test
    void shouldCreditBuyWithOwnerFieldLengthOverLimit() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getLongerOwner();
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidOwnerField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 24
    @SneakyThrows
    @Test
    void shouldCreditBuyWithEmptyOwnerField() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageOwnerFieldEmptyWhenTestedOwnerField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 25
    @SneakyThrows
    @Test
    void shouldCreditBuyWithInvalidOwnerIncludeDigitsAndSymbols() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(4, 0, 0, 4, 0);
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidOwnerField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    // Negative Test 26...30

    //    Test 26
    @SneakyThrows
    @Test
    void shouldCreditBuyWithEmptyCodeField() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageCodeFieldEmpty();
        creditPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 27
    @SneakyThrows
    @Test
    void shouldCreditBuyWithInvalidCodeWhenAllDigitZero() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 0, 3, 0, 0);
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidCodeField();
        creditPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 28
    @SneakyThrows
    @Test
    void shouldCreditBuyWithInvalidLengthFormatCode() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(2, 0, 0, 0, 0);
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidCodeField();
        creditPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 29
    @SneakyThrows
    @Test
    void shouldCreditBuyWithWithInvalidCodeIncludeLetters() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 2);
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidCodeField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
        creditPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
    }

    //    Test 30
    @SneakyThrows
    @Test
    void shouldCreditBuyWithWithInvalidCodeIncludeSymbols() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 0, 0, 3, 0);
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidCodeField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
        creditPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
    }
}
