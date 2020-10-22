package ru.commandos.Rooms;

import com.google.gson.Gson;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import ru.commandos.Humans.Client;
import ru.commandos.Humans.Waiter;

import java.util.ArrayDeque;

public class DriveThru extends Room implements Observer<String> {

    private final ArrayDeque<Client> cars = new ArrayDeque<>();
    private final PublishSubject<String> caller = PublishSubject.create();

    public Client getCar() {
        return cars.getFirst();
    }

    public Client carGone() {
        return cars.pollFirst();
    }

    public void subscribe(Waiter waiter) {
        caller.subscribe(waiter);
        System.out.println("Официант готов принимать заказы на Драйв-тру");
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        System.out.println("Драйв-тру открыт");
    }

    @Override
    public void onNext(@NonNull String s) {
        Gson gson = new Gson();
        Client client = gson.fromJson(s, Client.class);
        cars.add(client);
        client.setOrderPlace(orderPlace.DRIVETHRU);
        caller.onNext(DriveThru.class.getSimpleName());
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("Питстоп закрыт");
    }
}
