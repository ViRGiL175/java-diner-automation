package ru.commandos.Rooms;

import io.reactivex.rxjava3.subjects.ReplaySubject;
import ru.commandos.Humans.Client;
import ru.commandos.Humans.Waiter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Tables extends Room {

    private final ArrayList<Client> tables = new ArrayList<>();
    private final HashSet<Integer> freePlace = new HashSet<>();

    {
        for (int i = 0; i < 10; i++) {
            tables.add(null);
            freePlace.add(i);
        }
    }

    private final ReplaySubject<String> caller = ReplaySubject.create();

    public void subscribe(Waiter waiter) {
        caller.subscribe(waiter);
        System.out.println("Официант готов принимать заказы в зале");
    }

    public Client getClient(Integer tableNumber) {
        return tables.get(tableNumber);
    }

    public void setClient(Client client) {
        if (!freePlace.isEmpty()) {
            int random = new Random().nextInt(freePlace.size());
            Integer table = new ArrayList<>(freePlace).get(random);
            client.setOrderPlace(orderPlace.TABLES);
            client.setTable(table);
            tables.set(table, client);
            freePlace.remove(table);
            System.out.println("Клиент " + client + " готов сделать заказ в зале!");
            caller.onNext(Tables.class.getSimpleName() + table);
        } else {
            System.out.println("Мест нет!");
        }
    }

    public void clientGone(Integer table) {
        tables.set(table, null);
        freePlace.add(table);
        System.out.println("Клиент ушёл, столик №" + table + " освободился");
    }
}
