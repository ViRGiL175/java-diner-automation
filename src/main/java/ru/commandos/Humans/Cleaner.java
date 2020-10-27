package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Rooms.Room;

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
            Observable.timer(Diner.timeConst, TimeUnit.SECONDS).subscribe(v -> {
                Logger.info(this.getClass().getSimpleName() + " used Toilet");
                diner.getHall().getToilet().getDirty();
                isFree = true;
            });
        }
        else {
            isFree = true;
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
        Observable.interval(1, TimeUnit.SECONDS).takeWhile(l1 -> !action.isEmpty() && action.peekFirst() <= actionNumber).subscribe(l2 -> {
            if (isFree && action.peekFirst() == actionNumber) {
                action.pollFirst();
                isFree = false;
                Observable.timer(Diner.timeConst, TimeUnit.SECONDS).subscribe(v -> {
                    currentRoom = room;
                    diner.clean(currentRoom);

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
