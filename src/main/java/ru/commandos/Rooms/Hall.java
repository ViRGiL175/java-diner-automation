package ru.commandos.Rooms;

import com.google.gson.Gson;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Humans.Client;

import java.util.Random;

public class Hall implements Observer<String> {

    private final Diner diner;
    private final Tables tables;
    private final Bar bar;
    private final Toilet toilet;

    public Hall(Diner diner) {
        this.diner = diner;
        tables = new Tables(diner);
        bar = new Bar(diner);
        toilet = new Toilet(diner);
    }

    public Tables getTables() {
        return tables;
    }

    public Bar getBar() {
        return bar;
    }

    public Toilet getToilet() {
        return toilet;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        Logger.info("Hall is open");
        bar.subscribe(diner.getBarmen());
        bar.subscribe(diner.getWaiterController());
        tables.subscribe(diner.getWaiterController());
    }

    @Override
    public void onNext(@NonNull String s) {
        Gson gson = new Gson();
        Client client = gson.fromJson(s, Client.class);
        if (client.getMoney() >= diner.getMenu().food.values().stream().min(Double::compare).get()
                || client.getMoney() >= diner.getMenu().drinks.values().stream().min(Double::compare).get()) {
            if (new Random().nextInt(10) > 3) {
                tables.setClient(client);
            } else {
                bar.setClient(client);
            }
        } else {
            Logger.info(client + " did't enter, because he haven't money");
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        Logger.warn("Hall is close");
    }
}
