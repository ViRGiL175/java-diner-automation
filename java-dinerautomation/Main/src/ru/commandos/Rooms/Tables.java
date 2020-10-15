package ru.commandos.Rooms;

import io.reactivex.rxjava3.subjects.ReplaySubject;
import ru.commandos.Humans.Client;
import ru.commandos.Humans.Waiter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Tables extends Room {

    private final ArrayList<Client> clients = new ArrayList<>();
    private final HashSet<Integer> freePlace = new HashSet<>();

    {
        for (int i = 0; i < 10; i++) {
            clients.add(null);
            freePlace.add(i);
        }
    }

    private final ReplaySubject<String> caller = ReplaySubject.create();

    public void subscribe(Waiter waiter) {
        caller.subscribe(waiter);
        System.out.println("Официант готов принимать заказы в зале");
    }

    public Client getClient(Integer tableNumber) {
        return clients.get(tableNumber);
    }

    public void setClient(Client client) {
        if (!freePlace.isEmpty()) {
            int random = new Random().nextInt(freePlace.size());
            Integer table = new ArrayList<>(freePlace).get(random);
            client.setTable(table);
            clients.set(table, client);
            freePlace.remove(table);
            System.out.println("Клиент " + client + " готов сделать заказ!");
            caller.onNext(Tables.class.getSimpleName() + table);
        } else {
            System.out.println("Мест нет!");
        }
    }

    public void clientGone(Integer table) {
        clients.set(table, null);
        freePlace.add(table);
        System.out.println("Клиент ушёл, столик №" + table + " освободился");
    }
}
