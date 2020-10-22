package ru.commandos.Humans;

import ru.commandos.Diner;

public abstract class Staff extends Human {

    protected Diner diner;

    Staff(Diner diner) {
        this.diner = diner;
    }
}
