package ru.commandos.Rooms;

import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Humans.Cook;
import ru.commandos.Humans.Staff;
import ru.commandos.Humans.Waiter;

import java.util.HashMap;

public class Bookkeeping extends Room {

    private final Diner diner;
    private double budget = 1000;

    private final HashMap<Staff, Double> pay = new HashMap<>();

    public Bookkeeping(Diner diner) {
        this.diner = diner;
    }

    public void putMoneyInBudget(Double money) {
        budget += money;
        Logger.info(String.format("Diner's budget: $%.2f\n", budget));
    }

    public Double getMoneyFromBudget(Double money) {
        budget -= money;
        Logger.info(String.format("Diner's budget: $%.2f\n", budget));
        return money;
    }

    public Double checkBudget() {
        return budget;
    }

    public HashMap<Staff, Double> getStaffPayList() {
        return pay;
    }

    public void createPayMap() {
        for (Cook cook : diner.getCookController().getCooks()) {
            pay.put(cook, 500.0);
        }
        pay.put(diner.getBookkeeper(), 400.0);
        pay.put(diner.getBarmen(), 200.0);
        for (Waiter waiter : diner.getWaiterController().getWaiters()) {
            pay.put(waiter, 100.0);
        }
    }

    @Override
    public void getDirty() {
        diner.dirtCurrentRoom(this);
    }
}
