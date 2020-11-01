package ru.commandos.Rooms;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Humans.Client;
import ru.commandos.Humans.Waiter;
import ru.commandos.Humans.WaiterController;
import ru.commandos.Main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Tables extends Room {

    private final Diner diner;
    private final ArrayList<Client> tables = new ArrayList<>();
    private final HashSet<Integer> freePlace = new HashSet<>();

    {
        for (int i = 0; i < 10; i++) {
            tables.add(null);
            freePlace.add(i);
        }
    }

    private final PublishSubject<String> waiterCaller = PublishSubject.create();

    public Tables(Diner diner) {
        this.diner = diner;
    }

    public void subscribe(WaiterController waiterController) {
        waiterCaller.subscribe(waiterController);
        Logger.info("Waiters is ready to work");
    }

    public Client getClient(Integer tableNumber) {
        return tables.get(tableNumber);
    }

    public void setClient(Client client) {
        client.move(this);
        if (!freePlace.isEmpty()) {
            int random = new Random().nextInt(freePlace.size());
            Integer table = new ArrayList<>(freePlace).get(random);
            client.setOrderPlace(OrderPlace.TABLES);
            client.setTable(table);
            tables.set(table, client);
            freePlace.remove(table);
            Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
                Logger.info(client + " ready to do order!");
                waiterCaller.onNext(Tables.class.getSimpleName() + table);
            });
        } else {
            Logger.warn("No place!");
        }
    }

    public void reOrder(Integer table) {
        Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
            Logger.info(getClient(table) + " ready to do order again!");
            waiterCaller.onNext(Tables.class.getSimpleName() + table);
        });
    }

    public void clientGone(Integer table) {
        Client client = getClient(table);
        diner.feedback(client);
        tables.set(table, null);
        if (table < 5 || table == 9) {
            Main.canteenPlaces.get(table).setText((table + 1) + ".         ");
        }
        else {
            Main.canteenPlaces.get(table).setText(" " + (table + 1) + ".         ");
        }
        Main.updateScreen();
        freePlace.add(table);
        Logger.info("Client is gone (feedback: " + client.feedback + "), chair #" + table + " is free");
    }

    public Toilet getToilet() {
        return diner.getHall().getToilet();
    }

    @Override
    public void getDirty() {
        diner.dirtCurrentRoom(this);
    }
}
