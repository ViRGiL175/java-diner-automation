package ru.commandos.Humans;

import com.googlecode.lanterna.gui2.Label;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Main;
import ru.commandos.Rooms.Bar;
import ru.commandos.Rooms.Kitchen;
import ru.commandos.Rooms.Room;
import ru.commandos.Rooms.Tables;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Cleaner extends Staff implements Observer<Room> {

    private boolean isFree = true;
    private long actionCount;
    private final Deque<Long> action = new ArrayDeque<>();

    public Cleaner(Diner diner) {
        super(diner);
    }

    @Override
    public void useToilet() {
        if (new Random().nextInt(10) < 2) {
            ArrayDeque<Human> queue = diner.getHall().getToilet().queue;
            final boolean[] waiting = {true};
            Observable.interval(1 * Diner.slowdown, TimeUnit.MILLISECONDS).takeWhile(l1 -> waiting[0]).subscribe(l2 -> {
                if ((queue.size() - 1) / 4 == 0) {
                    queue.addLast(this);
                    int place = queue.size() - 1;
                    waiting[0] = false;
                    Main.restRoomPlaces.get(place).setText((place + 1) + ".Cleaner ");
                    Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
                        Logger.info(this.getClass().getSimpleName() + " used Toilet");
                        queue.remove(this);
                        diner.getHall().getToilet().getDirty();
                        isFree = true;
                        if (action.isEmpty()) {
                            Main.kitchenPlaces.get(2).setText("Cleaner");
                        }
                    });
                }
            });

        } else {
            isFree = true;
            if (action.isEmpty()) {
                Main.kitchenPlaces.get(2).setText("Cleaner");
            }
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        Logger.info("Cleaner is ready to work");
    }

    @Override
    public void onNext(@NonNull Room room) {
        if (action.isEmpty() && actionCount > 30) {
            actionCount = 1;
        }
        long actionNumber = actionCount++;
        action.addLast(actionNumber);
        Observable.interval(1 * Diner.slowdown, TimeUnit.MILLISECONDS).takeWhile(l1 -> !action.isEmpty() && action.peekFirst() <= actionNumber).subscribe(l2 -> {
            if (isFree && action.peekFirst() == actionNumber && room.hasFreePlace()) {
                action.pollFirst();
                isFree = false;
                Label label;
                int table = -1;
                if (room == diner.getKitchen()) {
                    label = Main.kitchenPlaces.get(2);
                } else if (room == diner.getHall().getToilet()) {
                    label = Main.restRoomPlaces.get(3);
                } else if (room == diner.getHall().getBar()) {
                    table = diner.getHall().getBar().getFreePlace();
                    label = Main.counterPlaces.get(table);
                } else if (room == diner.getHall().getTables()) {
                    table = diner.getHall().getTables().getFreePlace();
                    label = Main.canteenPlaces.get(table);
                } else {
                    label = Main.cleanerPlace;
                }
                label.setText("Cleaner");
                Main.updateScreen();
                int finalTable = table;
                Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
                    currentRoom = room;
                    diner.clean(currentRoom);
                    if (room == diner.getKitchen() || room == diner.getDriveThru()) {
                        label.setText("       ");
                    } else if (room == diner.getHall().getBar() && finalTable != -1) {
                        ((Bar) room).cleanerGone(finalTable);
                    } else if (room == diner.getHall().getTables() && finalTable != -1) {
                        ((Tables) room).cleanerGone(finalTable);
                    } else {
                        label.setText("4.        ");
                    }

                    useToilet();
                });
            }
        });
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        Logger.warn("Cleaner is sleeping");
    }
}
