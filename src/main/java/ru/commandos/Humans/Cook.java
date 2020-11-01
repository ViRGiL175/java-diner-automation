package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Food.Dish.Dish;
import ru.commandos.Main;
import ru.commandos.Order;
import ru.commandos.Rooms.Kitchen;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Cook extends Staff implements Observer<Order> {

    private final Kitchen kitchen;
    private boolean isFree = true;
    private long actionCount;
    private final Deque<Long> action = new ArrayDeque<>();

    private final int number;

    public Cook(Diner diner, Kitchen kitchen, int number) {
        super(diner);
        this.kitchen = kitchen;
        this.number = number;
        currentRoom = kitchen;
    }

    private void cook(Order order) {
        for (Dish dish : order.dishes) {
            for (String ingredient : dish.getIngredients().keySet()) {
                kitchen.getIngredients(ingredient, dish.getIngredients().get(ingredient));
            }
            order.doneDishes.add(dish);
        }
        currentRoom.getDirty();

        Logger.info("List of remaining ingredients in the Kitchen: " + kitchen.checkIngredients());
        Logger.debug("Cook " + number + " cooked dishes " + order);
        Main.cookPlaces.get(number).setText("Cook   ");
        Main.updateScreen();
        kitchen.transferDish(order);

        useToilet();
    }

    public int getActionSize() {
        return action.size();
    }

    @Override
    public void useToilet() {
        if (new Random().nextInt(10) < 2) {
            ArrayDeque<Human> queue = diner.getHall().getToilet().queue;
            final boolean[] waiting = {true};
            Observable.interval(1 * Diner.slowdown, TimeUnit.MILLISECONDS).takeWhile(l1 -> waiting[0]).subscribe(l2 -> {
                if ((queue.size() - 1) / 4 == 0) {
                    queue.addLast(this);
                    int place = queue.size() - 1;
                    waiting[0] = false;
                    Main.cookPlaces.get(number).setText("       ");
                    Main.restRoomPlaces.get(place).setText((place + 1) + ".Cook    ");
                    Main.updateScreen();
                    Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
                        Logger.info(this.getClass().getSimpleName() + " used Toilet");
                        queue.remove(this);
                        Main.restRoomPlaces.get(place).setText((place + 1) + ".        ");
                        Main.cookPlaces.get(number).setText("Cook   ");
                        Main.updateScreen();
                        diner.getHall().getToilet().getDirty();
                        isFree = true;
                    });
                }
            });
        }
        else {
            isFree = true;
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
    }

    @Override
    public void onNext(@NonNull Order order) {
        if (action.isEmpty() && actionCount > 30) {
            actionCount = 1;
        }
        long actionNumber = actionCount++;
        action.addLast(actionNumber);
        Observable.interval(1 * Diner.slowdown, TimeUnit.MILLISECONDS).takeWhile(l1 -> !action.isEmpty() && action.peekFirst() <= actionNumber).subscribe(l2 -> {
            if (isFree && action.peekFirst() == actionNumber) {
                action.pollFirst();
                isFree = false;
                Logger.debug("Cook " + number + " is cooking " + order);
                Main.cookPlaces.get(number).setText("Cook(C)");
                Main.updateScreen();
                Observable.timer(5 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> cook(order));
            }
        });
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        Logger.warn("Cook is sleeping");
    }
}