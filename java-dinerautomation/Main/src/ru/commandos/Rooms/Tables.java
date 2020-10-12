package ru.commandos.Rooms;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import ru.commandos.Humans.Client;
import ru.commandos.Humans.Waiter;

import java.util.ArrayList;

public class Tables extends Room {

    private ArrayList<Client> clients = new ArrayList<>();

    private ReplaySubject<Integer> caller = ReplaySubject.create();

    public void subscribe(Waiter waiter) {
        caller.subscribe(waiter);
    }

    public Client getClient(Integer tableNumber) {
        return clients.get(tableNumber);
    }

    public void setClient(Client client) {
        if (clients.size() <= 10) {
            client.setTable(clients.size());
            clients.add(client);
            System.out.println("Клиент " + client + " готов сделать заказ!");
            caller.onNext(clients.size()-1);
        } else {
            System.out.println("Мест нет!");
        }
    }
}
