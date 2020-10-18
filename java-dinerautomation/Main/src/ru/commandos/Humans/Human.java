package ru.commandos.Humans;

abstract class Human {

    protected String money = "$0";;

    protected Double getMoney() {
        return Double.valueOf(new StringBuffer(money).delete(0, 1).toString());
    }

    protected void changeMoney(Double cost) {
        Double moneyAfterPay = Double.valueOf(new StringBuffer(money).delete(0, 1).toString()) + cost;
        money = "$" + moneyAfterPay.toString();
    }
}
