package ru.commandos.Rooms;

import io.reactivex.rxjava3.subjects.PublishSubject;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Humans.Cook;
import ru.commandos.Humans.CookController;
import ru.commandos.Humans.Waiter;
import ru.commandos.Humans.WaiterController;
import ru.commandos.Order;

import java.util.ArrayDeque;
import java.util.HashMap;

public class Kitchen extends Room {

    private final Diner diner;
    private final HashMap<String, Integer> ingredients = new HashMap<>();

    {
        ingredients.put("Cheese", 10);
        ingredients.put("Dough", 10);
        ingredients.put("Mutton", 10);
        ingredients.put("Pepper", 10);
        ingredients.put("Zucchini", 10);
        ingredients.put("Onion", 10);
        ingredients.put("Bat", 10);
        ingredients.put("Oil", 10);
    }

    private final ArrayDeque<Order> readyOrder = new ArrayDeque<>();

    private final PublishSubject<Order> dashboard = PublishSubject.create();
    private final PublishSubject<String> bell = PublishSubject.create();

    public Kitchen(Diner diner) {
        this.diner = diner;
    }

    public void subscribe(CookController cookController) {
        dashboard.subscribe(cookController);
        Logger.info("Cooks is ready to work");
    }

    public void subscribe(WaiterController waiterController) {
        bell.subscribe(waiterController);
    }

    public void acceptOrder(Order order) {
        dashboard.onNext(order);
    }

    public void transferDish(Order order) {
        readyOrder.add(order);
        bell.onNext(Kitchen.class.getSimpleName());
    }

    public Order getReadyOrder() {
        return readyOrder.pollFirst();
    }

    public void getIngredients(String ingredient, Integer count) {
        ingredients.replace(ingredient, ingredients.get(ingredient) - count);
    }

    public Order checkReadyOrder() {
        return readyOrder.pollFirst();
    }

    public void setIngredients(String ingredient, Integer count) {
        ingredients.replace(ingredient, ingredients.get(ingredient) + count);
    }

    public HashMap<String, Integer> checkIngredients() {
        return ingredients;
    }

    @Override
    public boolean hasFreePlace() {
        return true;
    }

    @Override
    public void getDirty() {
        diner.dirtCurrentRoom(this);
    }
}
