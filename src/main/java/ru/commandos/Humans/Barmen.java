package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Food.Drink.Drink;
import ru.commandos.Order;
import ru.commandos.Rooms.Bar;
import ru.commandos.Rooms.Room;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Barmen extends Staff implements Observer<String> {

    private final Bar bar;
    private boolean isFree = true;
    private long actionCount;
    private final Deque<Long> action = new ArrayDeque<>();

    public Barmen(Diner diner, Bar bar) {
        super(diner);
        this.bar = bar;
        currentRoom = bar;
    }

    private void shake(Order order) {
        for (Drink drink : order.drinks) {
            for (String ingredient : drink.getIngredients().keySet()) {
                bar.getIngredients(ingredient, drink.getIngredients().get(ingredient));
            }
            order.doneDrinks.add(drink);
        }
        Logger.debug("List of remaining ingredients in the Bar: " + bar.checkIngredients());
        Logger.debug("Barmen made drinks " + order);
        currentRoom.getDirty();

        if (order.orderPlace != Room.OrderPlace.BAR) {
            bar.transfer(order);
            isFree = true;
        } else if (order.dishes.isEmpty()) {
            setReadyOrder(order);
        } else {
            isFree = true;
        }
    }

    private void acceptOrder(Integer chairNumber) {
        Observable.timer(Diner.timeConst/12, TimeUnit.SECONDS).subscribe(v -> {
            bar.getClient(chairNumber).setMenu(diner.getMenu());
        });
        Observable.timer(Diner.timeConst/6, TimeUnit.SECONDS).subscribe(v -> {
            Order order = bar.getClient(chairNumber).getOrder();
            if (order.cost == 0.) {
                Logger.info("Client hasn't ordered");
                bar.clientGone(order.table);
                isFree = true;
            } else {
                Logger.info("Barmen took Order at Bar: " + order);
                transferOrder(order);
            }
        });
    }

    private void transferOrder(Order order) {
        if (!order.drinks.isEmpty()) {
            if (!order.dishes.isEmpty()) {
                Logger.debug("Barmen transferred Order to the Kitchen");
                bar.transfer(order);
            }
            Logger.debug("Barmen is shaking drinks");
            Observable.timer(5*Diner.timeConst/12, TimeUnit.SECONDS).subscribe(v -> shake(order));
        } else {
            bar.transfer(order);
            isFree = true;
        }
    }

    public void setReadyOrder(Order order) {
        Observable.timer(Diner.timeConst, TimeUnit.SECONDS).subscribe(v -> {
            Logger.debug("Barmen gives the order to Client " + order);
            bar.getClient(order.table).setOrder(order);
        });
        Observable.timer(Diner.timeConst/6, TimeUnit.SECONDS).subscribe(v -> {
            changeMoney(bar.getClient(order.table).pay());
            bar.clientGone(order.table);
        });
        Observable.timer(Diner.timeConst/4, TimeUnit.SECONDS).subscribe(v -> {
            givePaymentToBookkeeper();

            useToilet();
            isFree = true;
        });
    }

    private void givePaymentToBookkeeper() {
        diner.getBookkeeper().giveClientPayment(getMoney());
        money = "$0";
    }

    @Override
    public void useToilet() {
        if (new Random().nextInt(10) < 2) {
            Observable.timer(Diner.timeConst/12, TimeUnit.SECONDS).subscribe(v -> {
                Logger.info(this.getClass().getSimpleName() + " used Toilet");
                diner.getHall().getToilet().getDirty();
            });
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        Logger.info("Barmen is ready to work");
    }

    @Override
    public void onNext(@NonNull String s) {
        if (action.isEmpty() && actionCount > 30) {
            actionCount = 1;
        }
        long actionNumber;

        if (!action.isEmpty() && s.equals(Waiter.class.getSimpleName())) {
            actionNumber = action.peekFirst() - 1;
            action.addFirst(actionNumber);
        } else {
            actionNumber = actionCount++;
            action.addLast(actionNumber);
        }
        Observable.interval(1, TimeUnit.SECONDS).takeWhile(l1 -> !action.isEmpty() && action.peekFirst() <= actionNumber).subscribe(l2 -> {
            if (isFree && action.peekFirst() == actionNumber) {
                action.pollFirst();
                isFree = false;
                if (s.equals(Waiter.class.getSimpleName())) {
                    Order order = bar.getWaitOrder();
                    if (order.orderPlace.equals(Room.OrderPlace.BAR)) {
                        setReadyOrder(order);
                    } else {
                        Logger.debug("Barmen is shaking drinks");
                        Observable.timer(5*Diner.timeConst/12, TimeUnit.SECONDS).subscribe(v -> shake(order));
                    }
                } else {
                    Integer table = Integer.parseInt(new StringBuffer(s).delete(0, 3).toString());
                    acceptOrder(table);
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
        Logger.warn("Barmen is sleeping");
    }
}