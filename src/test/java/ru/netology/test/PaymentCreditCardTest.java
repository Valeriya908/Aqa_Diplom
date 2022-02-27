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

public class PaymentCreditCardTest {
    MainPage mainPage = open("http://localhost:8080", MainPage.class);
    PaymentPage paymentPage = mainPage.buyWithCardOnCredit();

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
    public void openCreditPaymentPage() {
        paymentPage.checkVisibleHeadingCreditCard();
    }

    // Positive Test 1...2

    //    Test 1
    @Test
    void shouldCreditBuyWithApprovedCard() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.successMessageForm();
        assertEquals("APPROVED", DatabaseHelper.getTransactionStatusCreditCard());
        assertNotNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 2.1
    @Test
    void shouldCreditBuyWithDeclinedCard() {
        var cardNumber = DataHelper.getDeclinedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);;
        paymentPage.errorMessageForm();
    }

    //    Test 2.2
    @Test
    void shouldCheckRecordsInDbWhenCreditBuyWithDeclinedCard() throws InterruptedException {
        var cardNumber = DataHelper.getDeclinedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertEquals("DECLINED", DatabaseHelper.getTransactionStatusCreditCard());
        assertNotNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    // Negative Test 1 (1* - для воспроизведения бага)

    //    Test 1
    @Test
    void shouldCreditBuyWithAnotherCard() {
        var cardNumber = DataHelper.getAnotherCardNumber();
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageForm();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 1*
    @Test
    void shouldVisibleExtraMessageWhenCreditBuyWithAnotherCard() {
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
    void shouldCreditBuyWithInvalidLengthFormatCardNumber() {
        var cardNumber = DataHelper.getInvalidFieldFormat(14, 0, 0, 0, 0);
        var month = DataHelper.getMonth(4);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidCardNumberField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 3
    @Test
    void shouldCreditBuyWithInvalidCardNumberWhenAllDigitZero() throws InterruptedException {
        var cardNumber = DataHelper.getInvalidFieldFormat(0, 0, 16, 0, 0);
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
        paymentPage.errorMessageInvalidCardNumberField();
    }

    //    Test 4
    @Test
    void shouldCreditBuyWithInvalidCardNumberIncludeSymbolsAndLetters() {
        var cardNumber = DataHelper.getInvalidFieldFormat(0, 5, 0, 6, 5);
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidCardNumberField();
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 5
    @Test
    void shouldCreditBuyWithEmptyCardNumberField() throws InterruptedException {
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutFieldsWithoutCardNumberField(month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
        paymentPage.errorMessageCardNumberFieldEmpty();
    }

    // Negative Test 6...12

    //    Test 6
    @Test
    void shouldCreditBuyWithOutOfDateMonth() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(-1);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageAboutOutOfDateMonthOrNonexistentMonth();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 7
    @Test
    void shouldCreditBuyWithNonexistentMonth() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidMonth();
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageAboutOutOfDateMonthOrNonexistentMonth();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 8
    @Test
    void shouldCreditBuyWithEmptyMonthField() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutFieldsWithoutMonthField(cardNumber, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
        paymentPage.errorMessageMonthFieldEmpty();
    }

    //    Test 9.1
    @Test
    void shouldCreditBuyWithInvalidMonthWhenAllDigitZero() {
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
    void shouldCheckRecordsInDbWhenCreditBuyWithInvalidMonthWhenAllDigitZero() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 0, 2, 0, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 10
    @Test
    void shouldCreditBuyWithInvalidLengthFormatMonth() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(1, 0, 0, 0, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidMonthField();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 11
    @Test
    void shouldCreditBuyWithInvalidMonthIncludeLetters() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 1);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidMonthField();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 12
    @Test
    void shouldCreditBuyWithInvalidMonthIncludeSymbols() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 0, 0, 2, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidMonthField();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    // Negative Test 13...20

    //    Test 13
    @Test
    void shouldCreditBuyWithOutOfDateYear() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(-1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageAboutOutOfDateYear();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 14.1
    @Test
    void shouldCreditBuyWithValidityPeriodExpiresInFiveYears() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(5);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.successMessageForm();
    }

    //    Test 14.2
    @Test
    void shouldCheckRecordsInDbWhenCreditBuyWithValidityPeriodExpiresInFiveYears() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(5);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertEquals("APPROVED", DatabaseHelper.getTransactionStatusCreditCard());
        assertNotNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 15.1
    @Test
    void shouldCreditBuyWithValidityPeriodExpiresInSixYears() {
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
    void shouldCheckRecordsInDbWhenCreditBuyWithValidityPeriodExpiresInSixYears() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(6);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertEquals("APPROVED", DatabaseHelper.getTransactionStatusCreditCard());
        assertNotNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 16
    @Test
    void shouldCreditBuyWithEmptyYearField() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutFieldsWithoutYearField(cardNumber, month, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
        paymentPage.errorMessageYearFieldEmpty();
    }

    //    Test 17
    @Test
    void shouldCreditBuyWithInvalidYearWhenAllDigitZero() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 0, 2, 0, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageAboutOutOfDateYear();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 18
    @Test
    void shouldCreditBuyWithInvalidLengthFormatYear() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(1, 0, 0, 0, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidYearField();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 19
    @Test
    void shouldCreditBuyWithInvalidYearIncludeLetters() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidYearField();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 20
    @Test
    void shouldCreditBuyWithInvalidYearIncludeSymbols() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 0, 0, 2, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        paymentPage.errorMessageInvalidYearField();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    // Negative Test 21...25

    //    Test 21.1
    @Test
    void shouldCreditBuyWithOwnerIncludeCyrillicLetters() {
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
    void shouldCheckRecordsInDbWhenCreditBuyWithOwnerIncludeCyrillicLetters() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("ru");
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 22.1
    @Test
    void shouldCreditBuyWithOwnerFieldLengthConsistingOfOneLetter() {
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
    void shouldCheckRecordsInDbWhenCreditBuyWithOwnerFieldLengthConsistingOfOneLetter() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 0);
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 23.1
    @Test
    void shouldCreditBuyWithOwnerFieldLengthOverLimit() {
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
    void shouldCheckRecordsInDbWhenCreditBuyWithOwnerFieldLengthOverLimit() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getLongerOwner();
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 24
    @Test
    void shouldCreditBuyWithEmptyOwnerField() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var code = DataHelper.getValidCode();
        paymentPage.fillOutFieldsWithoutOwnerField(cardNumber, month, year, code);
        paymentPage.errorMessageOwnerFieldEmptyWhenTestedOwnerField();
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 25.1
    @Test
    void shouldCreditBuyWithInvalidOwnerIncludeDigitsAndSymbols() {
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
    void shouldCheckRecordsInDbWhenCreditBuyWithInvalidOwnerIncludeDigitsAndSymbols() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(4, 0, 0, 4, 0);
        var code = DataHelper.getValidCode();
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    // Negative Test 26...30 (26* - для воспроизведения бага)

    //    Test 26
    @Test
    void shouldCreditBuyWithEmptyCodeFieldTypeOne() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        paymentPage.fillOutFieldsWithoutCodeField(cardNumber, month, year, owner);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
        paymentPage.errorMessageCodeFieldEmpty();
    }

    //    Test 26*
    @Test
    void shouldCreditBuyWithEmptyCodeFieldTypeTwo() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        paymentPage.fillOutFieldsWithoutCodeField(cardNumber, month, year, owner);
        paymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
    }

    //    Test 27.1
    @Test
    void shouldCreditBuyWithInvalidCodeWhenAllDigitZero() {
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
    void shouldCheckRecordsInDbWhenCreditBuyWithInvalidCodeWhenAllDigitZero() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 0, 3, 0, 0);
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
    }

    //    Test 28
    @Test
    void shouldCreditBuyWithInvalidLengthFormatCode() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(2, 0, 0, 0, 0);
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
        paymentPage.errorMessageInvalidCodeField();
    }

    //    Test 29
    @Test
    void shouldCreditBuyWithWithInvalidCodeIncludeLetters() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 2);
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
        paymentPage.errorMessageInvalidCodeField();
        paymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
    }

    //    Test 30
    @Test
    void shouldCreditBuyWithWithInvalidCodeIncludeSymbols() throws InterruptedException {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 0, 0, 3, 0);
        paymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        Thread.sleep(10000);
        assertNull(DatabaseHelper.getTransactionStatusCreditCard());
        assertNull(DatabaseHelper.getTransactionTypeCreditCard());
        paymentPage.errorMessageInvalidCodeField();
        paymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
    }
}
