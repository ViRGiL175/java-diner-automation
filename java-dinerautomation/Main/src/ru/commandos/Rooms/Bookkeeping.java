package ru.commandos.Rooms;

public class Bookkeeping extends Room {

    private Double budget = 0.;

    public void putMoneyInBudget(Double money) {
        budget += money;
        System.out.println("Бюджет Дайнера: " + budget);
    }
}
