package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import java.time.Duration;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PaymentPage {
    private SelenideElement headingCardType = $("[class='heading heading_size_m heading_theme_alfa-on-white']");
    private SelenideElement cardNumberField = $$(".input__control").get(0);
    private SelenideElement monthField = $$(".input__control").get(1);
    private SelenideElement yearField = $$(".input__control").get(2);
    private SelenideElement ownerField = $$(".input__control").get(3);
    private SelenideElement codeField = $$(".input__control").get(4);
    private SelenideElement buttonContinue = $(byText("Продолжить"));
    private SelenideElement successSendFormMessage = $$(".notification").first();
    private SelenideElement errorSendFormMessage = $$(".notification").last();
    private SelenideElement closeButtonErrorSendFormMessage = $$(".notification").last().$$("button[class*=notification__closer]").get(0);
    private SelenideElement errorMessageCardNumberField = $$(".input__sub").first();
    private SelenideElement errorMessageMonthField = $$(".input__sub").first();
    private SelenideElement errorMessageYearField = $$(".input__sub").first();
    private SelenideElement errorMessageOwnerField = $$(".input__sub").first();
    private SelenideElement errorMessageCodeField = $$(".input__sub").last();

    public void checkVisibleHeadingDebitCard() {
        headingCardType.shouldBe(visible).shouldHave(text("Оплата по карте"));
    }

    public void checkVisibleHeadingCreditCard() {
        headingCardType.shouldBe(visible).shouldHave(text("Кредит по данным карты"));
    }

    //Form

    public void successMessageForm() {
        successSendFormMessage.shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text("Операция одобрена Банком."));
    }

    public void errorMessageForm() {
        errorSendFormMessage.shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text("Ошибка! Банк отказал в проведении операции."));
    }

    //CardNumber Field

    public void errorMessageInvalidCardNumberField() {
        errorMessageCardNumberField.shouldBe(visible).shouldHave(text("Неверный формат"));
    }

    public void errorMessageCardNumberFieldEmpty() {
        errorMessageCardNumberField.shouldBe(visible).shouldHave(text("Поле обязательно для заполнения"));
    }

    //Month Field

    public void errorMessageInvalidMonthField() {
        errorMessageMonthField.shouldBe(visible).shouldHave(text("Неверный формат"));
    }

    public void errorMessageAboutOutOfDateMonthOrNonexistentMonth() {
        errorMessageMonthField.shouldBe(visible).shouldHave(text("Неверно указан срок действия карты"));
    }

    public void errorMessageMonthFieldEmpty() {
        errorMessageMonthField.shouldBe(visible).shouldHave(text("Поле обязательно для заполнения"));
    }

    //Year Field

    public void errorMessageInvalidYearField() {
        errorMessageYearField.shouldBe(visible).shouldHave(text("Неверный формат"));
    }

    public void errorMessageAboutOutOfDateYear() {
        errorMessageYearField.shouldBe(visible).shouldHave(text("Истёк срок действия карты"));
    }

    public void errorMessageYearFieldEmpty() {
        errorMessageYearField.shouldBe(visible).shouldHave(text("Поле обязательно для заполнения"));
    }

    //Owner Field

    public void errorMessageInvalidOwnerField() {
        errorMessageOwnerField.shouldBe(visible).shouldHave(text("Неверный формат"));
    }

    public void errorMessageOwnerFieldEmptyWhenTestedOwnerField() {
        errorMessageOwnerField.shouldBe(visible).shouldHave(text("Поле обязательно для заполнения"));
    }

    public void errorMessageOwnerFieldEmptyWhenTestedCodeField() {
        errorMessageOwnerField.shouldNotBe(visible);
    }

    //Code Field

    public void errorMessageInvalidCodeField() {
        errorMessageCodeField.shouldBe(visible).shouldHave(text("Неверный формат"));
    }

    public void errorMessageCodeFieldEmpty() {
        errorMessageCodeField.shouldBe(visible).shouldHave(text("Поле обязательно для заполнения"));
    }


    public void fillOutAllFields(String cardNumber, String month, String year, String owner, String code) {
        cardNumberField.setValue(cardNumber);
        monthField.setValue(month);
        yearField.setValue(year);
        ownerField.setValue(owner);
        codeField.setValue(code);
        buttonContinue.click();
    }

    public void fillOutFieldsWithoutCardNumberField(String month, String year, String owner, String code) {
        monthField.setValue(month);
        yearField.setValue(year);
        ownerField.setValue(owner);
        codeField.setValue(code);
        buttonContinue.click();
    }

    public void fillOutFieldsWithoutMonthField(String cardNumber, String year, String owner, String code) {
        cardNumberField.setValue(cardNumber);
        yearField.setValue(year);
        ownerField.setValue(owner);
        codeField.setValue(code);
        buttonContinue.click();
    }

    public void fillOutFieldsWithoutYearField(String cardNumber, String month, String owner, String code) {
        cardNumberField.setValue(cardNumber);
        monthField.setValue(month);
        ownerField.setValue(owner);
        codeField.setValue(code);
        buttonContinue.click();
    }

    public void fillOutFieldsWithoutOwnerField(String cardNumber, String month, String year, String code) {
        cardNumberField.setValue(cardNumber);
        monthField.setValue(month);
        yearField.setValue(year);
        codeField.setValue(code);
        buttonContinue.click();
    }

    public void fillOutFieldsWithoutCodeField(String cardNumber, String month, String year, String owner) {
        cardNumberField.setValue(cardNumber);
        monthField.setValue(month);
        yearField.setValue(year);
        ownerField.setValue(owner);
        buttonContinue.click();
    }

    public void closeErrorSendFormMessage() {
        closeButtonErrorSendFormMessage.click();
        successSendFormMessage.shouldNotBe(visible);
    }
}
