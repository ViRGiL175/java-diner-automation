package ru.commandos.Rooms;

import io.reactivex.rxjava3.subjects.PublishSubject;
import ru.commandos.Diner;
import ru.commandos.Humans.Cook;
import ru.commandos.Humans.Waiter;
import ru.commandos.Order;

import java.util.ArrayDeque;
import java.util.HashMap;

public class Kitchen extends Room {

    private final Diner diner;
    private final HashMap<String, Integer> ingredients = new HashMap<>();

    {
        ingredients.put("Сыр", 10);
        ingredients.put("Тесто", 10);
        ingredients.put("Баранина", 10);
        ingredients.put("Перец", 10);
        ingredients.put("Кабачок", 10);
        ingredients.put("Лук", 10);
        ingredients.put("Летучая мышь", 10);
        ingredients.put("Масло", 10);
    }

    private final ArrayDeque<Order> readyOrder = new ArrayDeque<>();

    private final PublishSubject<Order> dashboard = PublishSubject.create();
    private final PublishSubject<String> bell = PublishSubject.create();

    public Kitchen(Diner diner) {
        this.diner = diner;
    }

    public void subscribe(Cook cook) {
        dashboard.subscribe(cook);
    }

    public void subscribe(Waiter waiter) {
        bell.subscribe(waiter);
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

    public void setIngredients(String ingredient, Integer count) {
        ingredients.replace(ingredient, ingredients.get(ingredient) + count);
    }

    public HashMap<String, Integer> checkIngredients() {
        return ingredients;
    }

    @Override
    public void getDirty() {
        diner.dirtCurrentRoom(this);
    }
}
