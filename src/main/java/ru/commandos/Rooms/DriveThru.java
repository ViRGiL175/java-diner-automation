package ru.commandos.Rooms;

import com.google.gson.Gson;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Humans.Client;
import ru.commandos.Humans.Waiter;

import java.util.ArrayDeque;

public class DriveThru extends Room implements Observer<String> {

    private final Diner diner;
    private final ArrayDeque<Client> cars = new ArrayDeque<>();
    private final PublishSubject<String> caller = PublishSubject.create();

    public DriveThru(Diner diner) {
        this.diner = diner;
    }

    public Client getCar() {
        return cars.getFirst();
    }

    public Client carGone() {
        return cars.pollFirst();
    }

    public void subscribe(Waiter waiter) {
        caller.subscribe(waiter);
        Logger.info("Официант готов принимать заказы на Драйв-тру");
    }

    @Override
    public void getDirty() {
        diner.dirtCurrentRoom(this);
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        System.out.println("Драйв-тру открыт");
    }

    @Override
    public void onNext(@NonNull String s) {
        Gson gson = new Gson();
        Client client = gson.fromJson(s, Client.class);
        client.move(this);
        cars.add(client);
        client.setOrderPlace(OrderPlace.DRIVETHRU);
        caller.onNext(DriveThru.class.getSimpleName());
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        Logger.warn("Драйв-тру закрыт");
    }
}
