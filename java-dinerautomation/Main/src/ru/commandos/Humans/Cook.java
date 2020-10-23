package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Food.Dish.Dish;
import ru.commandos.Order;
import ru.commandos.Rooms.Kitchen;

import java.util.Random;


public class Cook extends Staff implements Observer<Order> {

    private Kitchen kitchen;

    public Cook(Diner diner, Kitchen kitchen) {
        super(diner);
        this.kitchen = kitchen;
        currentRoom = kitchen;
    }

    private void cook(Order order) {
        Logger.debug("Повар готовит");
        for (Dish dish : order.dishes) {
            for (String ingredient : dish.getIngredients().keySet()) {
                kitchen.getIngredients(ingredient, dish.getIngredients().get(ingredient));
            }
            order.doneDishes.add(dish);
        }
        currentRoom.getDirty();

        useToilet();

        Logger.info("Ингредиентов осталось на кухне: " + kitchen.checkIngredients());
        Logger.debug("Повар приготовил блюда");
        kitchen.transferDish(order);
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
        Logger.info("Повар готов творить чудеса кулинарии");
    }

    @Override
    public void onNext(@NonNull Order order) {
        cook(order);
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
