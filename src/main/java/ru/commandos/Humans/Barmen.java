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
import java.util.ArrayList;
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
        Logger.debug("ингредиентов осталось в баре: " + bar.checkIngredients());
        Logger.debug("Бармен сделал напитки " + order);
        currentRoom.getDirty();

        if (order.orderPlace != Room.OrderPlace.BAR) {
            bar.transfer(order);
            isFree = true;
        } else if (order.drinks.isEmpty()){
            setReadyOrder(order);
        } else {
            isFree = true;
        }
    }

    private void acceptOrder(Integer chairNumber) {
        Observable.timer(1, TimeUnit.SECONDS).subscribe(v -> {
            bar.getClient(chairNumber).setMenu(diner.getMenu());
        });
        Observable.timer(2, TimeUnit.SECONDS).subscribe(v -> {
            Order order = bar.getClient(chairNumber).getOrder();
            if (order.cost == 0.) {
                Logger.info("Клиент ничего не заказал");
                bar.clientGone(order.table);
                isFree = true;
            } else {
                Logger.info("Бармен взял заказ в баре: " + order);
                transferOrder(order);
            }
        });
    }

    private void transferOrder(Order order) {
        if (!order.drinks.isEmpty()) {
            if (!order.dishes.isEmpty()) {
                bar.transfer(order);
            }
            Logger.debug("Бармен начал готовить напитки");
            Observable.timer(5, TimeUnit.SECONDS).subscribe(v -> shake(order));
        } else {
            bar.transfer(order);
            isFree = true;
        }
    }

    public void setReadyOrder(Order order) {
        Observable.timer(1, TimeUnit.SECONDS).subscribe(v -> {
            Logger.debug("Бармен отдаёт заказ клиенту " + order);
            bar.getClient(order.table).setOrder(order);
        });
        Observable.timer(2, TimeUnit.SECONDS).subscribe(v -> {
            changeMoney(bar.getClient(order.table).pay());
            bar.clientGone(order.table);
        });
        Observable.timer(3, TimeUnit.SECONDS).subscribe(v -> {
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
            Observable.timer(1, TimeUnit.SECONDS).subscribe(v -> {
                Logger.info(this.getClass().getSimpleName() + " воспользовался туалетом");
                diner.getHall().getToilet().getDirty();
            });
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        Logger.info("Бармен готов спаивать посетителей");
    }

    @Override
    public void onNext(@NonNull String s) {
        if (action.isEmpty() && actionCount > 30) {
            actionCount = 1;
        }
        long actionNumber = actionCount++;

        if (s.equals(Waiter.class.getSimpleName()) && bar.checkWaitOrder().orderPlace.equals(Room.OrderPlace.BAR))
            action.addFirst(actionNumber);
        else {
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
                        Logger.debug("Бармен начал готовить напитки");
                        Observable.timer(5, TimeUnit.SECONDS).subscribe(v -> shake(order));
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
        Logger.warn("Бармен больше не наливает");
    }
}
