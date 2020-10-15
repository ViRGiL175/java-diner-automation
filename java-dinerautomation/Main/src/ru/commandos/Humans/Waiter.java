package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import ru.commandos.Diner;
import ru.commandos.Order;
import ru.commandos.Rooms.Bar;
import ru.commandos.Rooms.Kitchen;

public class Waiter extends Staff implements Observer<String> {

    Order order;
    Kitchen kitchen;

    public Waiter(Diner diner, Kitchen kitchen) {
        super(diner);
        this.kitchen = kitchen;
    }

    public void acceptTablesOrder(Integer tableNumber) {
        diner.getHall().getTables().getClient(tableNumber).setMenu(diner.getMenu());
        order = diner.getHall().getTables().getClient(tableNumber).getOrder();
        System.out.println("Официант взял заказ в Зале: " + order);
        transferOrder(order);
    }

    private void transferOrder(Order order) {
        if (!order.food.isEmpty()) {
            System.out.println("Заказ передан в кухню");
            kitchen.acceptOrder(order);
        }
        if (!order.drinks.isEmpty()) {
            System.out.println("Заказ передан в бар");
            diner.getHall().getBar().acceptOrder(order);
        }
    }

    private void carryOrder(Order order) {
        System.out.println("Официант взял готовый заказ");
        diner.getHall().getTables().getClient(order.table).setOrder(order);
        changeMoney(diner.getHall().getTables().getClient(order.table).pay());
        diner.getHall().getTables().clientGone(order.table);
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
    }

    @Override
    public void onNext(@NonNull String s) {
        if (s.equals(Kitchen.class.getSimpleName())) {
            order = kitchen.readyOrder.pollFirst();
            if (order.isready()) {
                carryOrder(order);
            }
        } else if (s.equals(Bar.class.getSimpleName())) {
            order = diner.getHall().getBar().readyOrder.pollFirst();
            if (order.isready()) {
                carryOrder(order);
            }
        } else {
            Integer table = Integer.valueOf(new StringBuffer(s).delete(0, 6).toString());
            acceptTablesOrder(table);
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("Официант больше не может принимать заказы");
    }
}
