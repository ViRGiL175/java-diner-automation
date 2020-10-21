package ru.commandos.Rooms;

import ru.commandos.Diner;
import ru.commandos.Humans.Staff;

import java.util.HashMap;

public class Bookkeeping extends Room {

    private final Diner diner;
    private Double budget = 0.;

    private final HashMap<Staff, Double> pay = new HashMap<>();

    public Bookkeeping(Diner diner) {
        this.diner = diner;
        pay.put(diner.getCook(), 500.0);
        pay.put(diner.getBookkeeper(), 400.0);
        pay.put(diner.getBarmen(), 200.0);
        pay.put(diner.getWaiter(), 100.0);
    }

    public void putMoneyInBudget(Double money) {
        budget += money;
        System.out.println("Бюджет Дайнера: " + budget);
    }

    public Double getMoneyFromBudget(Double money) {
        budget -= money;
        System.out.println("Бюджет Дайнера: " + budget);
        return money;
    }

    public Double checkBudget() {
        return budget;
    }

    public HashMap<Staff, Double> getStaffPayList() {
        return pay;
    }
}
