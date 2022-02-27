package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.testng.annotations.BeforeTest;
import ru.netology.data.DataHelper;
import ru.netology.data.DatabaseHelper;
import ru.netology.page.MainPage;
import ru.netology.page.PaymentPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentDebitCardTest {
    MainPage mainPage = open("http://localhost:8080", MainPage.class);
    PaymentPage paymentPage = mainPage.buyWithCard();

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterEach
    public void cleanBase() {
        DatabaseHelper.clearDB();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeTest
    public void openDebitPaymentPage() {
        paymentPage.checkVisibleHeadingDebitCard();
    }

    // Positive Test 1...2

    //    Test 1
    @Test
    void shouldUsualBuyWithApprovedCard() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.successMessageForm();
        assertEquals("APPROVED", DatabaseHelper.getTransactionStatusDebitCard());
        assertNotNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 2
    @Test
    void shouldUsualBuyWithDeclinedCard() throws InterruptedException {
        var cardNumber = DataHelper.getDeclinedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertEquals("DECLINED", DatabaseHelper.getTransactionStatusDebitCard());
        assertNotNull(DatabaseHelper.getTransactionTypeDebitCard());
        paymentPage.errorMessageForm();
    }

    // Negative Test 1 (1* - для воспроизведения бага)

    //    Test 1
    @Test
    void shouldUsualBuyWithAnotherCard() {
        var cardNumber = DataHelper.getAnotherCardNumber();
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageForm();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 1*
    @Test
    void shouldVisibleExtraMessageWhenUsualBuyWithAnotherCard() {
        var cardNumber = DataHelper.getAnotherCardNumber();
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageForm();
        paymentPage.closeErrorSendFormMessage();
    }

    // Negative Test 2...5

    //    Test 2
    @Test
    void shouldUsualBuyWithInvalidLengthFormatCardNumber() {
        var cardNumber = DataHelper.getInvalidFieldFormat(14, 0, 0, 0, 0);
        var month = DataHelper.getMonth(4);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidCardNumberField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 3
    @Test
    void shouldUsualBuyWithInvalidCardNumberWhenAllDigitZero() throws InterruptedException {
        var cardNumber = DataHelper.getInvalidFieldFormat(0, 0, 16, 0, 0);
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
        paymentPage.errorMessageInvalidCardNumberField();
    }

    //    Test 4
    @Test
    void shouldUsualBuyWithInvalidCardNumberIncludeSymbolsAndLetters() {
        var cardNumber = DataHelper.getInvalidFieldFormat(0, 5, 0, 6, 5);
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidCardNumberField();
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 5
    @Test
    void shouldUsualBuyWithEmptyCardNumberField() throws InterruptedException {
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutFieldsWithoutCardNumberField(month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
        paymentPage.errorMessageCardNumberFieldEmpty();
    }

    // Negative Test 6...12

    //    Test 6
    @Test
    void shouldUsualBuyWithOutOfDateMonth() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(-1);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageAboutOutOfDateMonthOrNonexistentMonth();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 7
    @Test
    void shouldUsualBuyWithNonexistentMonth() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidMonth();
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageAboutOutOfDateMonthOrNonexistentMonth();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 8
    @Test
    void shouldUsualBuyWithEmptyMonthField() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutFieldsWithoutMonthField(cardNumber, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
        paymentPage.errorMessageMonthFieldEmpty();
    }

    //    Test 9.1
    @Test
    void shouldUsualBuyWithInvalidMonthWhenAllDigitZero() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 0, 2, 0, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidMonthField();
    }

    //    Test 9.2
    @Test
    void shouldCheckRecordsInDbWhenUsualBuyWithInvalidMonthWhenAllDigitZero() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 0, 2, 0, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 10
    @Test
    void shouldUsualBuyWithInvalidLengthFormatMonth() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(1, 0, 0, 0, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidMonthField();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 11
    @Test
    void shouldUsualBuyWithInvalidMonthIncludeLetters() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 1);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidMonthField();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 12
    @Test
    void shouldUsualBuyWithInvalidMonthIncludeSymbols() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 0, 0, 2, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidMonthField();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    // Negative Test 13...20

    //    Test 13
    @Test
    void shouldUsualBuyWithOutOfDateYear() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(-1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageAboutOutOfDateYear();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 14
    @Test
    void shouldUsualBuyWithValidityPeriodExpiresInFiveYears() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(5);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.successMessageForm();
        assertEquals("APPROVED", DatabaseHelper.getTransactionStatusDebitCard());
        assertNotNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 15.1
    @Test
    void shouldUsualBuyWithValidityPeriodExpiresInSixYears() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(6);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.successMessageForm();
    }

    //    Test 15.2
    @Test
    void shouldCheckRecordsInDbWhenUsualBuyWithValidityPeriodExpiresInSixYears() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(6);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertEquals("APPROVED", DatabaseHelper.getTransactionStatusDebitCard());
        assertNotNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 16
    @Test
    void shouldUsualBuyWithEmptyYearField() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutFieldsWithoutYearField(cardNumber, month, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
        paymentPage.errorMessageYearFieldEmpty();
    }

    //    Test 17
    @Test
    void shouldUsualBuyWithInvalidYearWhenAllDigitZero() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 0, 2, 0, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageAboutOutOfDateYear();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 18
    @Test
    void shouldUsualBuyWithInvalidLengthFormatYear() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(1, 0, 0, 0, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidYearField();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 19
    @Test
    void shouldUsualBuyWithInvalidYearIncludeLetters() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidYearField();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 20
    @Test
    void shouldUsualBuyWithInvalidYearIncludeSymbols() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 0, 0, 2, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidYearField();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    // Negative Test 21...25

    //    Test 21.1
    @Test
    void shouldUsualBuyWithOwnerIncludeCyrillicLetters() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("ru");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidOwnerField();
    }

    //    Test 21.2
    @Test
    void shouldCheckRecordsInDbWhenUsualBuyWithOwnerIncludeCyrillicLetters() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("ru");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 22.1
    @Test
    void shouldUsualBuyWithOwnerFieldLengthConsistingOfOneLetter() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 0);
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidOwnerField();
    }

    //    Test 22.2
    @Test
    void shouldCheckRecordsInDbWhenUsualBuyWithOwnerFieldLengthConsistingOfOneLetter() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 0);
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 23.1
    @Test
    void shouldUsualBuyWithOwnerFieldLengthOverLimit() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getLongerOwner();
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidOwnerField();
    }

    //    Test 23.2
    @Test
    void shouldCheckRecordsInDbWhenUsualBuyWithOwnerFieldLengthOverLimit() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getLongerOwner();
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 24
    @Test
    void shouldUsualBuyWithEmptyOwnerField() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var code = DataHelper.getValidCode();
        paymentPage.fillOutFieldsWithoutOwnerField(cardNumber, month, year, code);
        paymentPage.errorMessageOwnerFieldEmptyWhenTestedOwnerField();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 25.1
    @Test
    void shouldUsualBuyWithInvalidOwnerIncludeDigitsAndSymbols() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(4, 0, 0, 4, 0);
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidOwnerField();
    }

    //    Test 25.2
    @Test
    void shouldCheckRecordsInDbWhenUsualBuyWithInvalidOwnerIncludeDigitsAndSymbols() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(4, 0, 0, 4, 0);
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    // Negative Test 26...30 (26* - для воспроизведения бага)

    //    Test 26
    @Test
    void shouldUsualBuyWithEmptyCodeFieldTypeOne() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        paymentPage.fillOutFieldsWithoutCodeField(cardNumber, month, year, owner);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
        paymentPage.errorMessageCodeFieldEmpty();
    }

    //    Test 26*
    @Test
    void shouldUsualBuyWithEmptyCodeFieldTypeTwo() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        paymentPage.fillOutFieldsWithoutCodeField(cardNumber, month, year, owner);
        paymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
    }

    //    Test 27.1
    @Test
    void shouldUsualBuyWithInvalidCodeWhenAllDigitZero() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 0, 3, 0, 0);
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidCodeField();
    }

    //    Test 27.2
    @Test
    void shouldCheckRecordsInDbWhenUsualBuyWithInvalidCodeWhenAllDigitZero() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 0, 3, 0, 0);
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
    }

    //    Test 28
    @Test
    void shouldUsualBuyWithInvalidLengthFormatCode() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(2, 0, 0, 0, 0);
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
        paymentPage.errorMessageInvalidCodeField();
    }

    //    Test 29
    @Test
    void shouldUsualBuyWithWithInvalidCodeIncludeLetters() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 2);
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
        paymentPage.errorMessageInvalidCodeField();
        paymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
    }

    //    Test 30
    @Test
    void shouldUsualBuyWithWithInvalidCodeIncludeSymbols() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 0, 0, 3, 0);
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusDebitCard());
        assertNull(DatabaseHelper.getTransactionTypeDebitCard());
        paymentPage.errorMessageInvalidCodeField();
        paymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
    }
}


