package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Main;
import ru.commandos.Order;
import ru.commandos.Rooms.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Waiter extends Staff implements Observer<String> {

    private Order order;
    private final Kitchen kitchen;
    private final DriveThru driveThru;
    private boolean isFree = true;
    private long actionCount;
    private final Deque<Long> action = new ArrayDeque<>();

    private final int number;
    private double clientMoney;

    public Waiter(Diner diner, Kitchen kitchen, DriveThru driveThru, int number) {
        super(diner);
        this.kitchen = kitchen;
        this.number = number;
        this.driveThru = driveThru;
    }

    public void acceptTablesOrder(Integer tableNumber) {
        if (tableNumber < 5 || tableNumber == 9) {
            Main.canteenPlaces.get(tableNumber).setText((tableNumber + 1) + ".Client(O)");
        } else {
            Main.canteenPlaces.get(tableNumber).setText(" " + (tableNumber + 1) + ".Client(O)");
        }
        Main.updateScreen();
        Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
            move(diner.getHall().getTables());
            diner.getHall().getTables().getClient(tableNumber).setMenu(diner.getMenu());
            order = diner.getHall().getTables().getClient(tableNumber).getOrder();
            if (order.cost == 0.) {
                Main.addToCmd("INFO: Client hasn't ordered");
                Main.updateScreen();
                Logger.info("Client hasn't ordered");
                diner.getHall().getTables().clientGone(order.table);
                isFree = true;
            } else {
                Main.addToCmd("INFO: Waiter took Order at Canteen: Order{",
                        "dishes=" + order.dishes.toString(),
                        "drinks=" + order.drinks.toString(),
                        "table=" + order.table.toString(), "cost=" + String.valueOf(order.cost) + "}");
                Main.updateScreen();
                Logger.info("Waiter " + number + " took Order at Tables: " + order);
                if (tableNumber < 5 || tableNumber == 9) {
                    Main.canteenPlaces.get(tableNumber).setText((tableNumber + 1) + ".Client(W)");
                } else {
                    Main.canteenPlaces.get(tableNumber).setText(" " + (tableNumber + 1) + ".Client(W)");
                }
                Main.updateScreen();
                transferOrder(order);
            }
        });
    }

    public void acceptDriveThruOrder() {
        Main.driveThruPlaces.get(0).setText("1.Auto(O) ");
        Main.updateScreen();
        Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
            move(diner.getDriveThru());
            driveThru.getCar().setMenu(diner.getMenu());
            order = driveThru.getCar().getOrder();
            if (order.cost == 0.) {
                Main.addToCmd("INFO: Client hasn't ordered");
                Main.updateScreen();
                Logger.info("Client hasn't ordered");
                driveThru.carGone();
                isFree = true;
            } else {
                Main.addToCmd("INFO: Waiter took Order at D-Thru: Order{",
                        "dishes=" + order.dishes.toString(),
                        "drinks=" + order.drinks.toString(),
                        "table=" + ((order.orderPlace == Room.OrderPlace.DRIVETHRU) ? "D-Thru" : order.table.toString()), "cost=" + String.valueOf(order.cost) + "}");
                Main.updateScreen();
                Logger.info("Waiter " + number + " took Order at Drive-Thru: " + order);
                Main.driveThruPlaces.get(0).setText("1.Auto(W) ");
                Main.updateScreen();
                transferOrder(order);
            }
        });
    }

    private void transferOrder(Order order) {
        Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
            if (!order.dishes.isEmpty()) {
                move(kitchen);
                Logger.debug("Order has been transferred to Kitchen " + order);
                kitchen.acceptOrder(order);
            }
            if (!order.drinks.isEmpty()) {
                Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(s -> {
                    move(diner.getHall().getBar());
                    Logger.debug("Order has been transferred to Bar " + order);
                    diner.getHall().getBar().acceptOrder(order);
                });
            }
            isFree = true;
        });
    }

    private void carryOrder(Order order) {
        int k = 1;
        if (currentRoom != kitchen) {
            k = 1000;
            move(kitchen);
        }
        else {
        Logger.debug("Waiter " + number + " took the ready Order");
        }
        Observable.timer(1 * Diner.slowdown + k, TimeUnit.MILLISECONDS).subscribe(v -> {
            if (order.orderPlace == Room.OrderPlace.DRIVETHRU) {
                move(driveThru);
                driveThru.getCar().setOrder(order);
                Main.driveThruPlaces.get(0).setText("1.Auto    ");
                Main.updateScreen();
                Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(s -> {
                    clientMoney = driveThru.getCar().pay();
                    driveThru.carGone();
                });
                Observable.timer(2 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(s -> {
                    givePaymentToBookkeeper();
                });
            } else if (order.orderPlace == Room.OrderPlace.TABLES) {
                move(diner.getHall().getTables());
                Client client = diner.getHall().getTables().getClient(order.table);
                client.setOrder(order);
                if (order.table < 5 || order.table == 9) {
                    Main.canteenPlaces.get(order.table).setText((order.table + 1) + ".Client   ");
                } else {
                    Main.canteenPlaces.get(order.table).setText(" " + (order.table + 1) + ".Client   ");
                }
                Main.updateScreen();
                Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(s -> {
                    clientMoney = client.pay();
                    if ((client.getMoney() < diner.getMenu().food.values().stream().min(Double::compare).get()
                            && client.getMoney() < diner.getMenu().drinks.values().stream().min(Double::compare).get())
                            || new Random().nextInt(10) > 3) {
                        diner.getHall().getTables().clientGone(order.table);
                    } else {
                        diner.getHall().getTables().reOrder(order.table);
                    }
                });
                Observable.timer(2 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(s -> {
                    givePaymentToBookkeeper();
                });
            } else {
                diner.getHall().getBar().acceptOrder(order);
            }

            useToilet();
        });
    }

    private void transferOrderFromBar(Order order) {
        Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
            move(kitchen);
            Logger.debug("Order has been transferred to Kitchen " + order);
            kitchen.acceptOrder(order);
            isFree = true;
        });
    }

    private void givePaymentToBookkeeper() {
        Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
            move(diner.getBookkeeping());
            diner.getBookkeeper().giveClientPayment(clientMoney);
            clientMoney = 0;
        });
    }

    public int getActionSize() {
        return action.size();
    }

    @Override
    public void move(Room room) {
        if (currentRoom == kitchen) {
            Main.kitchenPlaces.get(number).setText("       ");
            Main.updateScreen();
        }
        currentRoom = room;
        if (currentRoom == kitchen) {
            Main.kitchenPlaces.get(number).setText("Waiter ");
            Main.updateScreen();
        }
        currentRoom.getDirty();
    }

    @Override
    public void useToilet() {
        if (new Random().nextInt(10) < 10) {
            ArrayDeque<Human> queue = diner.getHall().getToilet().queue;
            final boolean[] waiting = {true};
            Observable.interval(1 * Diner.slowdown, TimeUnit.MILLISECONDS).takeWhile(l1 -> waiting[0]).subscribe(l2 -> {
                if ((queue.size() - 1) / 4 == 0) {
                    queue.addLast(this);
                    int place = queue.size() - 1;
                    waiting[0] = false;
                    move(diner.getHall().getToilet());
                    Main.restRoomPlaces.get(place).setText((place + 1) + ".Waiter  ");
                    Main.updateScreen();
                    Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
                        Main.addToCmd("INFO: Waiter used Restroom");
                        Logger.info(this.getClass().getSimpleName() + " used Toilet");
                        queue.remove(this);
                        Main.restRoomPlaces.get(place).setText((place + 1) + ".        ");
                        move(kitchen);
                        Main.updateScreen();
                        diner.getHall().getToilet().getDirty();
                        isFree = true;
                    });
                }
            });
        } else {
            move(kitchen);
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
        Observable.interval(1 * Diner.slowdown, TimeUnit.MILLISECONDS).takeWhile(l1 -> !action.isEmpty() && action.peekFirst() <= actionNumber).subscribe(l2 -> {
            if (isFree && action.peekFirst() == actionNumber) {
                action.pollFirst();
                isFree = false;
                if (s.equals(DriveThru.class.getSimpleName())) {
                    acceptDriveThruOrder();
                } else if (s.equals(Kitchen.class.getSimpleName())) {
                    order = kitchen.getReadyOrder();
                    if (order.isready() && !order.placed) {
                        order.placed = true;
                        carryOrder(order);
                    } else {
                        isFree = true;
                    }
                } else if (s.equals(Bar.class.getSimpleName())) {
                    order = diner.getHall().getBar().getReadyOrder();
                    if (order.orderPlace == Room.OrderPlace.BAR) {
                        transferOrderFromBar(order);
                    } else if (order.isready() && !order.placed) {
                        order.placed = true;
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