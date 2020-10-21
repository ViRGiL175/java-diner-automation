package ru.commandos.Humans;

import com.google.gson.Gson;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import ru.commandos.Diner;
import ru.commandos.Order;
import ru.commandos.Rooms.*;

public class Waiter extends Staff implements Observer<String> {

    Order order;
    Kitchen kitchen;
    DriveThru driveThru;

    public Waiter(Diner diner, Kitchen kitchen, DriveThru driveThru) {
        super(diner);
        this.kitchen = kitchen;
        this.driveThru = driveThru;
    }

    public void acceptTablesOrder(Integer tableNumber) {
        diner.getHall().getTables().getClient(tableNumber).setMenu(diner.getMenu());
        order = diner.getHall().getTables().getClient(tableNumber).getOrder();
        if (order.cost == 0.) {
            System.out.println("Клиент ничего не заказал");
            diner.getHall().getTables().clientGone(order.table);
        } else {
            System.out.println("Официант взял заказ в Зале: " + order);
            transferOrder(order);
        }
    }

    public void acceptPitOrder() {
        driveThru.getCar().setMenu(diner.getMenu());
        order = driveThru.getCar().getOrder();
        if (order.cost == 0.) {
            System.out.println("Клиент ничего не заказал");
            driveThru.carGone();
        } else {
            System.out.println("Официант взял заказ на Драйв-тру: " + order);
            transferOrder(order);
        }
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
        if (order.orderPlace == Room.orderPlace.DRIVETHRU) {
            driveThru.getCar().setOrder(order);
            changeMoney(driveThru.carGone().pay());
        } else if (order.orderPlace == Room.orderPlace.TABLES) {
            diner.getHall().getTables().getClient(order.table).setOrder(order);
            changeMoney(diner.getHall().getTables().getClient(order.table).pay());
            diner.getHall().getTables().clientGone(order.table);
        } else {
            diner.getBarmen().setReadyOrderFromKithen(order);
        }
    }

    private void transferOrderFromBar(Order order) {
        System.out.println("Заказ передан в кухню");
        kitchen.acceptOrder(order);
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
    }

    @Override
    public void onNext(@NonNull String s) {
        if (s.equals(DriveThru.class.getSimpleName())) {
            acceptPitOrder();
        } else if (s.equals(Kitchen.class.getSimpleName())) {
            order = kitchen.readyOrder.pollFirst();
            if (order.isready()) {
                carryOrder(order);
            }
        } else if (s.equals(Bar.class.getSimpleName())) {
            order = diner.getHall().getBar().readyOrder.pollFirst();
            if (order.isready()) {
                carryOrder(order);
            }
        } else if (s.substring(0, 6).equals(Tables.class.getSimpleName())) {
            Integer table = Integer.parseInt(new StringBuffer(s).delete(0, 6).toString());
            acceptTablesOrder(table);
        } else {
            transferOrderFromBar(new Gson().fromJson(s, Order.class));
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
