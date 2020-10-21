package ru.commandos.Humans;

abstract class Human {

    protected String money = "$0";

    protected Double getMoney() {
        return Double.parseDouble(new StringBuffer(money).delete(0, 1).toString());
    }

    protected void changeMoney(Double cost) {
        double moneyAfterPay = Double.parseDouble(new StringBuffer(money).delete(0, 1).toString()) + cost;
        money = "$" + moneyAfterPay;
    }
}
