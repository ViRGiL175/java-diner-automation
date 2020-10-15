package ru.commandos.Rooms;

import io.reactivex.rxjava3.subjects.ReplaySubject;
import ru.commandos.Humans.Cook;
import ru.commandos.Humans.Waiter;
import ru.commandos.Order;

import java.util.ArrayDeque;

public class Kitchen extends Room {

    public ArrayDeque<Order> readyOrder = new ArrayDeque<>();

    private final ReplaySubject<Order> dashboard = ReplaySubject.create();
    private final ReplaySubject<String> bell = ReplaySubject.create();

    public void subscribe(Cook cook) {
        dashboard.subscribe(cook);
    }

    public void subscribe(Waiter waiter) {
        bell.subscribe(waiter);
    }

    public void acceptOrder(Order order) {
        dashboard.onNext(order);
    }

    public void transferDish(Order order) {
        readyOrder.add(order);
        bell.onNext(Kitchen.class.getSimpleName());
    }
}
