package ru.commandos.Humans;

abstract class Human {

    protected String money;

    protected void changeMoney(Double cost) {
        Double moneyAfterPay = Double.valueOf(new StringBuffer(money).delete(0,1).toString()) + cost;
        money = "$" + moneyAfterPay.toString();
    }
}
