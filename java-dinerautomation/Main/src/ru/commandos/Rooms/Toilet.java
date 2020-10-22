package ru.commandos.Rooms;

import ru.commandos.Diner;

public class Toilet extends Room {

    private final Diner diner;

    public Toilet(Diner diner){
        this.diner = diner;
    }

    @Override
    public void getDirty() {
        diner.dirtCurrentRoom(this);
    }
}
