package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$$;

public class MainPage {
    private SelenideElement buttonBuy = $$("[class='button__content']").first();
    private SelenideElement buttonBuyOnCredit = $$("[class='button__content']").last();


    public PaymentPage buyWithCard() {
        buttonBuy.click();
        return new PaymentPage();
    }

    public PaymentPage buyWithCardOnCredit() {
        buttonBuyOnCredit.click();
        return new PaymentPage();
    }
}
