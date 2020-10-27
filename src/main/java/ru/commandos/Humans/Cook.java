package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Food.Dish.Dish;
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

    public Cook(Diner diner, Kitchen kitchen) {
        super(diner);
        this.kitchen = kitchen;
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

        Logger.info("ингредиентов осталось на кухне: " + kitchen.checkIngredients());
        Logger.debug("Повар приготовил блюда " + order);
        kitchen.transferDish(order);

        useToilet();
    }

    @Override
    public void useToilet() {
        if (new Random().nextInt(10) < 2) {
            Observable.timer(1, TimeUnit.SECONDS).subscribe(v -> {
                Logger.info(this.getClass().getSimpleName() + " воспользовался туалетом");
                diner.getHall().getToilet().getDirty();
                isFree = true;
            });
        }
        else {
            isFree = true;
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        Logger.info("Повар готов творить чудеса кулинарии");
    }

    @Override
    public void onNext(@NonNull Order order) {
        if (action.isEmpty() && actionCount > 30) {
            actionCount = 1;
        }
        long actionNumber = actionCount++;
        action.addLast(actionNumber);
        Observable.interval(1, TimeUnit.SECONDS).takeWhile(l1 -> !action.isEmpty() && action.peekFirst() <= actionNumber).subscribe(l2 -> {
            if (isFree && action.peekFirst() == actionNumber) {
                action.pollFirst();
                isFree = false;
                Logger.debug("Повар готовит " + order);
                Observable.timer(5, TimeUnit.SECONDS).subscribe(v -> cook(order));
            }
        });
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        Logger.warn("Повар больше не может готовить");
    }
}
