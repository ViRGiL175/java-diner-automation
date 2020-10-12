package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import ru.commandos.Diner;
import ru.commandos.Order;
import ru.commandos.Rooms.Kitchen;

import java.util.ArrayList;
import java.util.Arrays;

public class Cook extends Staff implements Observer<Order> {

    private Kitchen kitchen;

    public Cook(Diner diner, Kitchen kitchen) {
        super(diner);
        this.kitchen = kitchen;
    }

    private void cook (Order order) {
        System.out.println("Повар готовит");
        for (String s : order.food) {
            if (kitchen.canDo(s)) {
                order.done.add(s);
            }
        }
        System.out.println("Повар приготовил блюда");
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        System.out.println("Повар готов творить чудеса кулинарии");
    }

    @Override
    public void onNext(@NonNull Order order) {
        cook(order);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("Повар больше не может готовить");
    }
}
