package ru.commandos.Rooms;

import io.reactivex.rxjava3.subjects.PublishSubject;
import ru.commandos.Humans.Barmen;
import ru.commandos.Humans.Client;
import ru.commandos.Humans.Waiter;
import ru.commandos.Order;

import java.util.*;

public class Bar extends Room {

    private final HashMap<String, Integer> ingredients = new HashMap<>();

    {
        ingredients.put("Спирт этиловый", 10);
        ingredients.put("Лимонный сок", 10);
        ingredients.put("Какао бобы", 10);
        ingredients.put("Молоко", 10);
    }

    private final ArrayList<Client> chairs = new ArrayList<>();
    private final HashSet<Integer> freePlace = new HashSet<>();

    {
        for (int i = 0; i < 10; i++) {
            chairs.add(null);
            freePlace.add(i);
        }
    }

    private final ArrayDeque<Order> waitOrder = new ArrayDeque<>();
    private final ArrayDeque<Order> readyOrder = new ArrayDeque<>();

    private final PublishSubject<String> barmenCaller = PublishSubject.create();
    private final PublishSubject<String> waiterCaller = PublishSubject.create();

    public void acceptOrder(Order order) {
        waitOrder.add(order);
        barmenCaller.onNext(Waiter.class.getSimpleName());
    }

    public void transfer(Order order) {
        readyOrder.add(order);
        waiterCaller.onNext(Bar.class.getSimpleName());
    }

    public void setClient(Client client) {
        if (!freePlace.isEmpty()) {
            int random = new Random().nextInt(freePlace.size());
            Integer chair = new ArrayList<>(freePlace).get(random);
            client.setOrderPlace(orderPlace.BAR);
            client.setTable(chair);
            chairs.set(chair, client);
            freePlace.remove(chair);
            System.out.println("Клиент " + client + " готов сделать заказ в баре!");
            barmenCaller.onNext(Bar.class.getSimpleName() + chair);
        } else {
            System.out.println("Мест нет!");
        }
    }

    public void clientGone(Integer chair) {
        chairs.set(chair, null);
        freePlace.add(chair);
        System.out.println("Клиент ушёл, стул №" + chair + " освободился");
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

    public Client getClient(Integer tableNumber) {
        return chairs.get(tableNumber);
    }

    public Order getWaitOrder() {
        return waitOrder.pollFirst();
    }

    public Order getReadyOrder() {
        return readyOrder.pollFirst();
    }

    public void subscribe(Waiter waiter) {
        waiterCaller.subscribe(waiter);
    }

    public void subscribe(Barmen barmen) {
        barmenCaller.subscribe(barmen);
    }
}
