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

    public void use(Human human) {
        final boolean[] waiting = {true};
        Observable.interval(1 * Diner.slowdown, TimeUnit.MILLISECONDS).takeWhile(l1 -> waiting[0]).subscribe(l2 -> {
            if ((queue.size() - 1) / 4 == 0) {
                queue.addLast(human);
                int place = queue.size() - 1;
                waiting[0] = false;
                if (human instanceof Cook) {
                    Main.restRoomPlaces.get(place).setText((place + 1) + ".Cook    ");
                } else if (human instanceof Barmen) {
                    Main.restRoomPlaces.get(place).setText((place + 1) + ".Barmen  ");
                } else if (human instanceof Waiter) {
                    Main.restRoomPlaces.get(place).setText((place + 1) + ".Waiter  ");
                } else if (human instanceof Bookkeeper) {
                    Main.restRoomPlaces.get(place).setText((place + 1) + ".Bookkeeper");
                } else if (human instanceof Client) {
                    Main.restRoomPlaces.get(place).setText((place + 1) + ".Client  ");
                } else {
                    Main.restRoomPlaces.get(place).setText((place + 1) + ".Cleaner ");
                }
            }
        });
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
