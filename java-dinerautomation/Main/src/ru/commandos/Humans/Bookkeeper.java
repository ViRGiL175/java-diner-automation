package ru.commandos.Humans;

import ru.commandos.Diner;
import ru.commandos.Rooms.Bookkeeping;

public class Bookkeeper extends Staff {

    private final Bookkeeping bookkeeping;

    public Bookkeeper(Diner diner, Bookkeeping bookkeeping) {
        super(diner);
        this.bookkeeping = bookkeeping;
    }

    public void giveClientPayment(Double payment) {
        bookkeeping.putMoneyInBudget(payment);
    }
}
