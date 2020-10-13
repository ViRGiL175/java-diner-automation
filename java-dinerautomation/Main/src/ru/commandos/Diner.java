package ru.commandos;

import com.google.gson.Gson;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import ru.commandos.Humans.Barmen;
import ru.commandos.Humans.Client;
import ru.commandos.Humans.Cook;
import ru.commandos.Humans.Waiter;
import ru.commandos.Rooms.Bar;
import ru.commandos.Rooms.Kitchen;
import ru.commandos.Rooms.Tables;
import ru.virgil.OuterWorld;

public class Diner implements Observer<String> {

    private Menu menu = new Menu();
    public Tables tables = new Tables();
    public Bar bar = new Bar();
    private Kitchen kitchen = new Kitchen();
    private Cook cook = new Cook(this, kitchen);
    private Barmen barmen = new Barmen(this, bar);
    private Waiter waiter = new Waiter(this, kitchen);

    public Menu getMenu() {
        return menu;
    }

    public static void main(String[] args) throws InterruptedException {

        OuterWorld outerWorld = OuterWorld.singleton();

        Observable subject = outerWorld.getClientsSource();
        Diner diner = new Diner();
        subject.subscribe(diner);

        outerWorld.run();
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        System.out.println("Дайнер начал работу");
        bar.setBarmen(barmen);
        kitchen.subscribe(cook);
        tables.subscribe(waiter);
    }

    @Override
    public void onNext(@NonNull String o) {
        Gson gson = new Gson();
        Client client = gson.fromJson(o, Client.class);
        tables.setClient(client);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("Дайнер закончил работу");
    }
}
