package ru.netology.web.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;


class MoneyTransferTest {
    @BeforeEach
    public void shouldLogin() {
        Configuration.holdBrowserOpen = true;
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
    }



    @Test
    void shouldTransferMoneyFromTheSecondCardToTheFirstCard() {  //перевод со второй карты на первую
        var dashboardPage = new DashboardPage();

        String idRechargeCard = DataHelper.Card1().getIdCard();
        int sumTransfer = 200;
        String numberTransferCard = DataHelper.Card2().getNumberCard();

        int balanceFirstCard = dashboardPage.getCardBalance(DataHelper.Card1().getIdCard());
        int balanceSecondCard = dashboardPage.getCardBalance(DataHelper.Card2().getIdCard());

        dashboardPage
                .choiceRechargeCard(idRechargeCard)
                .transferMoney(sumTransfer,numberTransferCard);

        Assertions.assertEquals(balanceFirstCard + sumTransfer, dashboardPage.getCardBalance(DataHelper.Card1().getIdCard()));
        Assertions.assertEquals(balanceSecondCard - sumTransfer, dashboardPage.getCardBalance(DataHelper.Card2().getIdCard()));

    }

    @Test
    void shouldTransferMoneyFromTheFirstCardToTheSecondCard() {  //перевод с первой карты на вторую
        var dashboardPage = new DashboardPage();

        String idRechargeCard = DataHelper.Card2().getIdCard();
        int sumTransfer = 200;
        String numberTransferCard = DataHelper.Card1().getNumberCard();

        int balanceFirstCard = dashboardPage.getCardBalance(DataHelper.Card1().getIdCard());
        int balanceSecondCard = dashboardPage.getCardBalance(DataHelper.Card2().getIdCard());

        dashboardPage
                .choiceRechargeCard(idRechargeCard)
                .transferMoney(sumTransfer,numberTransferCard);

        Assertions.assertEquals(balanceFirstCard - sumTransfer, dashboardPage.getCardBalance(DataHelper.Card1().getIdCard()));
        Assertions.assertEquals(balanceSecondCard + sumTransfer, dashboardPage.getCardBalance(DataHelper.Card2().getIdCard()));

    }

    @Test
    void shouldReturnErrorWithInvalidCard(){         //перевод на не существующую карту
        var dashboardPage = new DashboardPage();

        String idRechargeCard = DataHelper.Card2().getIdCard();
        int sumTransfer = 200;
        String numberTransferCard = DataHelper.invalidCard().getNumberCard();

        var page = dashboardPage.choiceRechargeCard(idRechargeCard);
        page.transferMoney(sumTransfer, numberTransferCard);
        page.getErrorNotification();

    }

    @Test
    void shouldReturnErrorWithInsufficientFunds(){           //попытка перевода суммы превышающей баланс
        var dashboardPage = new DashboardPage();

        String idRechargeCard = DataHelper.Card1().getIdCard();
        int sumTransfer = 200 + dashboardPage.getCardBalance(DataHelper.Card2().getIdCard());
        String numberTransferCard = DataHelper.Card2().getNumberCard();

        var page = dashboardPage.choiceRechargeCard(idRechargeCard);
        page.transferMoney(sumTransfer, numberTransferCard);
        page.getErrorNotification();

    }

    @Test
    void shouldReturnErrorInvalidRechargeCard(){               //попытка перевода с карты на туже карту
        var dashboardPage = new DashboardPage();

        String idRechargeCard = DataHelper.Card2().getIdCard();
        int sumTransfer = 200;
        String numberTransferCard = DataHelper.Card2().getNumberCard();

        var page = dashboardPage.choiceRechargeCard(idRechargeCard);
        page.transferMoney(sumTransfer, numberTransferCard);
        page.getErrorNotification();

    }

}
