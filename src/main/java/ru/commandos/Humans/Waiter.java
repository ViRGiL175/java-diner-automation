package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Order;
import ru.commandos.Rooms.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Waiter extends Staff implements Observer<String> {

    Order order;
    Kitchen kitchen;
    DriveThru driveThru;
    private boolean isFree = true;
    private long actionCount;
    private final Deque<Long> action = new ArrayDeque<>();

    public Waiter(Diner diner, Kitchen kitchen, DriveThru driveThru) {
        super(diner);
        this.kitchen = kitchen;
        this.driveThru = driveThru;
    }

    public void acceptTablesOrder(Integer tableNumber) {
        Observable.timer(1, TimeUnit.SECONDS).subscribe(v -> {
            move(diner.getHall().getTables());
            diner.getHall().getTables().getClient(tableNumber).setMenu(diner.getMenu());
            order = diner.getHall().getTables().getClient(tableNumber).getOrder();
            if (order.cost == 0.) {
                Logger.info("Client hasn't ordered");
                diner.getHall().getTables().clientGone(order.table);
                isFree = true;
            } else {
                Logger.info("Waiter took Order at Tables: " + order);
                transferOrder(order);
            }
        });
    }

    public void acceptDriveThruOrder() {
        Observable.timer(1, TimeUnit.SECONDS).subscribe(v -> {
            move(diner.getDriveThru());
            driveThru.getCar().setMenu(diner.getMenu());
            order = driveThru.getCar().getOrder();
            if (order.cost == 0.) {
                Logger.info("Client hasn't ordered");
                driveThru.carGone();
                isFree = true;
            } else {
                Logger.info("Waiter took Order at Drive-Thru: " + order);
                transferOrder(order);
            }
        });
    }

    private void transferOrder(Order order) {
        Observable.timer(1, TimeUnit.SECONDS).subscribe(v -> {
            if (!order.dishes.isEmpty()) {
                move(kitchen);
                Logger.debug("Order has been transferred to Kitchen " + order);
                kitchen.acceptOrder(order);
            }
            if (!order.drinks.isEmpty()) {
                move(diner.getHall().getBar());
                Logger.debug("Order has been transferred to Bar " + order);
                diner.getHall().getBar().acceptOrder(order);
            }
            isFree = true;
        });
    }

    private void carryOrder(Order order) {
        Logger.debug("Waiter took the ready Order");
        Observable.timer(1, TimeUnit.SECONDS).subscribe(v -> {
            if (order.orderPlace == Room.OrderPlace.DRIVETHRU) {
                move(driveThru);
                driveThru.getCar().setOrder(order);
                Observable.timer(1, TimeUnit.SECONDS).subscribe(s -> {
                    changeMoney(driveThru.carGone().pay());
                });
                Observable.timer(2, TimeUnit.SECONDS).subscribe(s -> {
                    givePaymentToBookkeeper();
                });
            } else if (order.orderPlace == Room.OrderPlace.TABLES) {
                move(diner.getHall().getTables());
                Client client = diner.getHall().getTables().getClient(order.table);
                client.setOrder(order);
                Observable.timer(1, TimeUnit.SECONDS).subscribe(s -> {
                    changeMoney(client.pay());
                    if ((client.getMoney() < diner.getMenu().food.values().stream().min(Double::compare).get()
                            && client.getMoney() < diner.getMenu().drinks.values().stream().min(Double::compare).get())
                            || new Random().nextInt(10) > 3) {
                        diner.getHall().getTables().clientGone(order.table);
                    } else {
                        diner.getHall().getTables().reOrder(order.table);
                    }
                });
                Observable.timer(2, TimeUnit.SECONDS).subscribe(s -> {
                    givePaymentToBookkeeper();
                });
            } else {
                diner.getHall().getBar().acceptOrder(order);
            }

            useToilet();
        });
    }

    private void transferOrderFromBar(Order order) {
        Observable.timer(1, TimeUnit.SECONDS).subscribe(v -> {
            move(kitchen);
            Logger.debug("Order has been transferred to Kitchen " + order);
            kitchen.acceptOrder(order);
            isFree = true;
        });
    }

    private void givePaymentToBookkeeper() {
        Observable.timer(1, TimeUnit.SECONDS).subscribe(v -> {
            move(diner.getBookkeeping());
            diner.getBookkeeper().giveClientPayment(getMoney());
            money = "$0";
        });
    }

    @Override
    public void useToilet() {
        if (new Random().nextInt(10) < 2) {
            Observable.timer(1, TimeUnit.SECONDS).subscribe(v -> {
                Logger.info(this.getClass().getSimpleName() + " used Toilet");
                diner.getHall().getToilet().getDirty();
                isFree = true;
            });
        } else {
            isFree = true;
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
    }

    @Override
    public void onNext(@NonNull String s) {
        if (action.isEmpty() && actionCount > 30) {
            actionCount = 1;
        }
        long actionNumber = actionCount++;
        action.addLast(actionNumber);
        Observable.interval(1, TimeUnit.SECONDS).takeWhile(l1 -> !action.isEmpty() && action.peekFirst() <= actionNumber).subscribe(l2 -> {
            if (isFree && action.peekFirst() == actionNumber) {
                action.pollFirst();
                isFree = false;
                if (s.equals(DriveThru.class.getSimpleName())) {
                    acceptDriveThruOrder();
                } else if (s.equals(Kitchen.class.getSimpleName())) {
                    order = kitchen.getReadyOrder();
                    if (order.isready() && !order.placed) {
                        carryOrder(order);
                    } else {
                        isFree = true;
                    }
                } else if (s.equals(Bar.class.getSimpleName())) {
                    order = diner.getHall().getBar().getReadyOrder();
                    if (order.orderPlace == Room.OrderPlace.BAR) {
                        transferOrderFromBar(order);
                    } else if (order.isready() && !order.placed) {
                        carryOrder(order);
                    } else {
                        isFree = true;
                    }
                } else {
                    Integer table = Integer.parseInt(new StringBuffer(s).delete(0, 6).toString());
                    acceptTablesOrder(table);
                }
            }
        });
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        Logger.warn("Waiter is sleeping");
    }
}