package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import ru.commandos.Diner;
import ru.commandos.Order;
import ru.commandos.Rooms.Bar;


public class Barmen extends Staff implements Observer<Order> {

    private final Bar bar;

    public Barmen(Diner diner, Bar bar) {
        super(diner);
        this.bar = bar;
    }

    public void shake(Order order) {
        order.doneDrinks.addAll(order.drinks);
        System.out.println("Бармен сделал напитки");
        bar.transferDrinks(order);
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        System.out.println("Бармен готов спаивать посетителей");
    }

    @Override
    public void onNext(@NonNull Order order) {
        shake(order);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("Бармен больше не наливает");
    }
}
