package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import ru.commandos.Diner;
import ru.commandos.Order;
import ru.commandos.Rooms.Kitchen;

public class Waiter extends Staff implements Observer<Integer> {

    Order order;
    Kitchen kitchen;

    public Waiter(Diner diner, Kitchen kitchen) {
        super(diner);
        this.kitchen = kitchen;
        money = "$0";
    }

    public void acceptOrder(Integer tableNumber) {
        diner.tables.getClient(tableNumber).setMenu(diner.getMenu());
        order = diner.tables.getClient(tableNumber).getOrder();
        System.out.println("Официант взял заказ: " + order);
        transferOrder(order);
    }

    private void transferOrder(Order order) {
        for (String s : order.food) {
            if (kitchen.canDo(s)) {
                System.out.println("Заказ передан в кухню");
                kitchen.acceptOrder(order);
                break;
            }
        }
        if (!order.isready()) {
            for (String s : order.food) {
                if (diner.bar.canDo(s)) {
                    System.out.println("Заказ передан в бар");
                    diner.bar.acceptOrder(order);
                    break;
                }
            }
        }
        System.out.println("Официант взял готовый заказ");
        diner.tables.getClient(order.table).setOrder(order);
        changeMoney(diner.tables.getClient(order.table).pay());
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        System.out.println("Официант готов принимать заказы");
    }

    @Override
    public void onNext(@NonNull Integer integer) {
        acceptOrder(integer);
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
