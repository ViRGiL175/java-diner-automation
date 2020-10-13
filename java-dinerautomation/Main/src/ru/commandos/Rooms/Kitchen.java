package ru.commandos.Rooms;

import io.reactivex.rxjava3.subjects.ReplaySubject;
import ru.commandos.Humans.Cook;
import ru.commandos.Order;

import java.util.HashSet;

public class Kitchen extends Room {

    private ReplaySubject<Order> dashboard = ReplaySubject.create();

    private HashSet<String> dishes = new HashSet<>();
    {
        dishes.add("Пицца \"Гималайская\"");
        dishes.add("Рататуй");
        dishes.add("Летучая мышь во фритюре");
    }

    public Boolean canDo(String s) {
        return dishes.contains(s);
    }

    public void subscribe(Cook cook) {
        dashboard.subscribe(cook);
    }

    public void acceptOrder(Order order) {
        dashboard.onNext(order);
    }
}
