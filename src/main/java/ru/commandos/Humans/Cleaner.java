package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Rooms.Room;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Cleaner extends Staff implements Observer<Room> {

    public Cleaner(Diner diner) {
        super(diner);
    }

    @Override
    public void useToilet() {
        if (new Random().nextInt(10) < 2) {
            Observable.timer(1, TimeUnit.SECONDS).subscribe(v -> {
                Logger.info(this.getClass().getSimpleName() + " воспользовался туалетом");
                diner.getHall().getToilet().getDirty();
            });
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        Logger.info("Уборщик готов драить до блеска");
    }

    @Override
    public void onNext(@NonNull Room room) {
        Observable.timer(1, TimeUnit.SECONDS).subscribe(v -> {
            currentRoom = room;
            diner.clean(currentRoom);

            useToilet();
        });
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        Logger.warn("Уборщик наелся и спит");
    }
}
