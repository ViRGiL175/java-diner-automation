package ru.commandos.Rooms;

import com.google.gson.Gson;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import ru.commandos.Humans.Barmen;
import ru.commandos.Humans.Client;
import ru.commandos.Humans.Waiter;
import ru.commandos.Order;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Bar extends Room {

    public Barmen barmen;

    private final ArrayList<Client> chairs = new ArrayList<>();
    private final HashSet<Integer> freePlace = new HashSet<>();

    {
        for (int i = 0; i < 10; i++) {
            chairs.add(null);
            freePlace.add(i);
        }
    }

    public ArrayDeque<Order> readyOrder = new ArrayDeque<>();

    private final ReplaySubject<String> barmenCaller = ReplaySubject.create();
    private final ReplaySubject<String> waiterCaller = ReplaySubject.create();

    public void acceptOrder(Order order) {
        String stringOrder = new Gson().toJson(order);
        barmenCaller.onNext(stringOrder);
    }

    public void transferDrinks(Order order) {
        readyOrder.add(order);
        waiterCaller.onNext(Bar.class.getSimpleName());
    }

    public void transferOrder(Order order) {
        waiterCaller.onNext(new Gson().toJson(order));
    }

    public void setClient(Client client) {
        if (!freePlace.isEmpty()) {
            int random = new Random().nextInt(freePlace.size());
            Integer chair = new ArrayList<>(freePlace).get(random);
            client.setOrderPlace(orderPlace.BAR);
            client.setTable(chair);
            chairs.set(chair, client);
            freePlace.remove(chair);
            System.out.println("Клиент " + client + " готов сделать заказ в баре!");
            barmenCaller.onNext(Bar.class.getSimpleName() + chair);
        } else {
            System.out.println("Мест нет!");
        }
    }

    public void clientGone(Integer chair) {
        chairs.set(chair, null);
        freePlace.add(chair);
        System.out.println("Клиент ушёл, стул №" + chair + " освободился");
    }

    public Client getClient(Integer tableNumber) {
        return chairs.get(tableNumber);
    }

    public void subscribe(Waiter waiter) {
        waiterCaller.subscribe(waiter);
    }

    public void subscribe(Barmen barmen) {
        barmenCaller.subscribe(barmen);
    }
}
