package ru.commandos.Rooms;

import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Humans.Staff;

import java.util.HashMap;

public class Bookkeeping extends Room {

    private final Diner diner;
    private Double budget = 0.;

    private final HashMap<Staff, Double> pay = new HashMap<>();

    public Bookkeeping(Diner diner) {
        this.diner = diner;
    }

    public void putMoneyInBudget(Double money) {
        budget += money;
        Logger.info(String.format("Бюджет Дайнера: $%.2f\n", budget));
    }

    public Double getMoneyFromBudget(Double money) {
        budget -= money;
        Logger.info(String.format("Бюджет Дайнера: $%.2f\n", budget));
        return money;
    }

    public Double checkBudget() {
        return budget;
    }

    public HashMap<Staff, Double> getStaffPayList() {
        return pay;
    }

    public void createPayMap() {
        pay.put(diner.getCook(), 500.0);
        pay.put(diner.getBookkeeper(), 400.0);
        pay.put(diner.getBarmen(), 200.0);
        pay.put(diner.getWaiter(), 100.0);
    }
  
    @Override
    public void getDirty() {
        diner.dirtCurrentRoom(this);
    }
}
