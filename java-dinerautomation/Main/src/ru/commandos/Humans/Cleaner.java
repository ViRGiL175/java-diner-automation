package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import ru.commandos.Diner;
import ru.commandos.Rooms.Room;

import java.util.Random;

public class Cleaner extends Staff implements Observer<Room> {

    public Cleaner(Diner diner) {
        super(diner);
    }

    @Override
    public void useToilet() {
        if (new Random().nextInt(10) < 2) {
            System.out.println(this.getClass().getSimpleName() + " воспользовался туалетом");
            diner.getHall().getToilet().getDirty();
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        System.out.println("Уборщик готов драить до блеска");
    }

    @Override
    public void onNext(@NonNull Room room) {
        currentRoom = room;
        diner.clean(currentRoom);

        useToilet();

    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("Уборщик наелся и спит");
    }
}
