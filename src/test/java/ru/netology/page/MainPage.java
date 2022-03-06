package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$$;

public class MainPage {
    private SelenideElement buttonBuy = $$("[class='button__content']").first();
    private SelenideElement buttonBuyOnCredit = $$("[class='button__content']").last();


    public DebitCardPage buyWithCard() {
        buttonBuy.click();
        return new DebitCardPage();
    }

    public CreditCardPage buyWithCardOnCredit() {
        buttonBuyOnCredit.click();
        return new CreditCardPage();
    }
}
