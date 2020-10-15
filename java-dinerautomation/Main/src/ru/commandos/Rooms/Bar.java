package ru.commandos.Rooms;

import io.reactivex.rxjava3.subjects.ReplaySubject;
import ru.commandos.Humans.Barmen;
import ru.commandos.Humans.Waiter;
import ru.commandos.Order;

import java.util.ArrayDeque;

public class Bar extends Room {

    public Barmen barmen;

    public ArrayDeque<Order> readyOrder = new ArrayDeque<>();

    private final ReplaySubject<Order> dashboard = ReplaySubject.create();
    private final ReplaySubject<String> bell = ReplaySubject.create();

    public void acceptOrder(Order order) {
        dashboard.onNext(order);
    }

    public void transferDrinks(Order order) {
        readyOrder.add(order);
        bell.onNext(Bar.class.getSimpleName());
    }

    public void subscribe(Waiter waiter) {
        bell.subscribe(waiter);
    }

    public void subscribe(Barmen barmen) {
        dashboard.subscribe(barmen);
    }
}
