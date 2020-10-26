package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Order;
import ru.commandos.Rooms.*;

import java.util.Random;

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
        move(diner.getHall().getTables());
        diner.getHall().getTables().getClient(tableNumber).setMenu(diner.getMenu());
        order = diner.getHall().getTables().getClient(tableNumber).getOrder();
        if (order.cost == 0.) {
            Logger.info("Клиент ничего не заказал");
            diner.getHall().getTables().clientGone(order.table);
        } else {
            Logger.info("Официант взял заказ в Зале: " + order);
            transferOrder(order);
        }
    }

    public void acceptDriveThruOrder() {
        move(diner.getDriveThru());
        driveThru.getCar().setMenu(diner.getMenu());
        order = driveThru.getCar().getOrder();
        if (order.cost == 0.) {
            Logger.info("Клиент ничего не заказал");
            driveThru.carGone();
        } else {
            Logger.info("Официант взял заказ на Драйв-тру: " + order);
            transferOrder(order);
        }
    }

    private void transferOrder(Order order) {
        if (!order.dishes.isEmpty()) {
            move(kitchen);
            Logger.debug("Заказ передан в кухню");
            kitchen.acceptOrder(order);
        }
        if (!order.drinks.isEmpty()) {
            move(diner.getHall().getBar());
            Logger.debug("Заказ передан в бар");
            diner.getHall().getBar().acceptOrder(order);
        }
    }

    private void carryOrder(Order order) {
        Logger.debug("Официант взял готовый заказ");
        if (order.orderPlace == Room.OrderPlace.DRIVETHRU) {
            move(driveThru);
            driveThru.getCar().setOrder(order);
            changeMoney(driveThru.carGone().pay());
            givePaymentToBookkeeper();
        } else if (order.orderPlace == Room.OrderPlace.TABLES) {
            move(diner.getHall().getTables());
            diner.getHall().getTables().getClient(order.table).setOrder(order);
            changeMoney(diner.getHall().getTables().getClient(order.table).pay());
            diner.getHall().getTables().clientGone(order.table);
            givePaymentToBookkeeper();
            if (new Random().nextInt(10) > 3) {
                diner.getHall().getTables().clientGone(order.table);
            } else {
                diner.getWaiter().acceptTablesOrder(order.table);
            }
        } else {
            diner.getBarmen().setReadyOrder(order);
        }

        useToilet();

    }

    private void transferOrderFromBar(Order order) {
        move(kitchen);
        Logger.debug("Заказ передан в кухню");
        kitchen.acceptOrder(order);
    }

    private void givePaymentToBookkeeper() {
        move(diner.getBookkeeping());
        diner.getBookkeeper().giveClientPayment(getMoney());
        money = "$0";
    }

    @Override
    public void useToilet() {
        if (new Random().nextInt(10) < 2) {
            Logger.info(this.getClass().getSimpleName() + " воспользовался туалетом");
            diner.getHall().getToilet().getDirty();
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
    }

    @Override
    public void onNext(@NonNull String s) {
        if (s.equals(DriveThru.class.getSimpleName())) {
            acceptDriveThruOrder();
        } else if (s.equals(Kitchen.class.getSimpleName())) {
            order = kitchen.getReadyOrder();
            if (order.isready()) {
                carryOrder(order);
            }
        } else if (s.equals(Bar.class.getSimpleName())) {
            order = diner.getHall().getBar().getReadyOrder();
            if (order.orderPlace == Room.OrderPlace.BAR) {
                transferOrderFromBar(order);
            } else if (order.isready()) {
                carryOrder(order);
            }
        } else {
            Integer table = Integer.parseInt(new StringBuffer(s).delete(0, 6).toString());
            acceptTablesOrder(table);
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        Logger.warn("Официант больше не может принимать заказы");
    }
}
