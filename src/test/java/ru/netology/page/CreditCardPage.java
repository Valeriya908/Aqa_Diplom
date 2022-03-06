package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import java.time.Duration;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class CreditCardPage {
    private SelenideElement headingCardType = $("[class='heading heading_size_m heading_theme_alfa-on-white']");
    private SelenideElement cardNumberField =$(byText("Номер карты")).parent().$(".input__control");
    private SelenideElement monthField = $(byText("Месяц")).parent().$(".input__control");
    private SelenideElement yearField = $(byText("Год")).parent().$(".input__control");
    private SelenideElement ownerField = $(byText("Владелец")).parent().$(".input__control");
    private SelenideElement codeField = $(byText("CVC/CVV")).parent().$(".input__control");
    private SelenideElement buttonContinue = $(byText("Продолжить"));
    private SelenideElement successSendFormMessage = $$(".notification").first();
    private SelenideElement errorSendFormMessage = $$(".notification").last();
    private SelenideElement closeButtonErrorSendFormMessage = $$(".notification").last().$$("button[class*=notification__closer]").get(0);
    private SelenideElement errorMessageCardNumberField = $(byText("Номер карты")).parent().$(".input__sub");
    private SelenideElement errorMessageMonthField = $(byText("Месяц")).parent().$(".input__sub");
    private SelenideElement errorMessageYearField = $(byText("Год")).parent().$(".input__sub");
    private SelenideElement errorMessageOwnerField = $(byText("Владелец")).parent().$(".input__sub");
    private SelenideElement errorMessageCodeField = $(byText("CVC/CVV")).parent().$(".input__sub");

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
        errorMessageCardNumberField.shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text("Неверный формат"));
    }

    public void errorMessageCardNumberFieldEmpty() {
        errorMessageCardNumberField.shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text("Поле обязательно для заполнения"));
    }

    //Month Field

    public void errorMessageInvalidMonthField() {
        errorMessageMonthField.shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text("Неверный формат"));
    }

    public void errorMessageAboutOutOfDateMonthOrNonexistentMonth() {
        errorMessageMonthField.shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text("Неверно указан срок действия карты"));
    }

    public void errorMessageMonthFieldEmpty() {
        errorMessageMonthField.shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text("Поле обязательно для заполнения"));
    }

    //Year Field

    public void errorMessageInvalidYearField() {
        errorMessageYearField.shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text("Неверный формат"));
    }

    public void errorMessageAboutOutOfDateYear() {
        errorMessageYearField.shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text("Истёк срок действия карты"));
    }

    public void errorMessageYearFieldEmpty() {
        errorMessageYearField.shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text("Поле обязательно для заполнения"));
    }

    //Owner Field

    public void errorMessageInvalidOwnerField() {
        errorMessageOwnerField.shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text("Неверный формат"));
    }

    public void errorMessageOwnerFieldEmptyWhenTestedOwnerField() {
        errorMessageOwnerField.shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text("Поле обязательно для заполнения"));
    }

    public void errorMessageOwnerFieldEmptyWhenTestedCodeField() {
        errorMessageOwnerField.shouldNotBe(visible);
    }

    //Code Field

    public void errorMessageInvalidCodeField() {
        errorMessageCodeField.shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text("Неверный формат"));
    }

    public void errorMessageCodeFieldEmpty() {
        errorMessageCodeField.shouldBe(visible, Duration.ofSeconds(15)).shouldHave(text("Поле обязательно для заполнения"));
    }


    public void fillOutAllFields(String cardNumber, String month, String year, String owner, String code) {
        cardNumberField.setValue(cardNumber);
        monthField.setValue(month);
        yearField.setValue(year);
        ownerField.setValue(owner);
        codeField.setValue(code);
        buttonContinue.click();
    }

    public void closeErrorSendFormMessage() {
        closeButtonErrorSendFormMessage.click();
        successSendFormMessage.shouldNotBe(visible);
    }
}
















