package ru.commandos.Rooms;

import io.reactivex.rxjava3.core.Observable;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Humans.*;
import ru.commandos.Main;

import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;

public class Toilet extends Room {

    private final Diner diner;

    public final ArrayDeque<Human> queue = new ArrayDeque<>();

    public Toilet(Diner diner) {
        this.diner = diner;
    }

    @Override
    public boolean hasFreePlace() {
        return queue.size() < 4;
    }

    @Override
    public void getDirty() {
        diner.dirtCurrentRoom(this);
    }
}
