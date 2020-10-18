package ru.commandos.Rooms;

import com.google.gson.Gson;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import ru.commandos.Diner;
import ru.commandos.Humans.Client;

public class Hall extends Room implements Observer<String> {

    private Diner diner;
    protected Tables tables;
    protected Bar bar;

    public Hall(Diner diner) {
        this.diner = diner;
        tables = new Tables();
        bar = new Bar();
    }

    public Tables getTables() {
        return tables;
    }

    public Bar getBar() {
        return bar;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        System.out.println("Зал открыт");
        bar.subscribe(diner.getBarmen());
        bar.subscribe(diner.getWaiter());
        tables.subscribe(diner.getWaiter());
    }

    @Override
    public void onNext(@NonNull String s) {
        Gson gson = new Gson();
        Client client = gson.fromJson(s, Client.class);
        tables.setClient(client);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("Зал закрыт");
    }
}
