package ru.commandos.Rooms;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Humans.Barmen;
import ru.commandos.Humans.Client;
import ru.commandos.Humans.Waiter;
import ru.commandos.Order;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class Bar extends Room {

    private final Diner diner;
    private final HashMap<String, Integer> ingredients = new HashMap<>();

    {
        ingredients.put("Ethanol", 10);
        ingredients.put("Lemon juice", 10);
        ingredients.put("Cocoa beans", 10);
        ingredients.put("Milk", 10);
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

    public Bar(Diner diner) {
        this.diner = diner;
    }

    public void acceptOrder(Order order) {
        waitOrder.addLast(order);
        barmenCaller.onNext(Waiter.class.getSimpleName());
    }

    public void transfer(Order order) {
        readyOrder.addLast(order);
        waiterCaller.onNext(Bar.class.getSimpleName());
    }

    public void setClient(Client client) {
        client.move(this);
        if (!freePlace.isEmpty()) {
            int random = new Random().nextInt(freePlace.size());
            Integer chair = new ArrayList<>(freePlace).get(random);
            client.setOrderPlace(OrderPlace.BAR);
            client.setTable(chair);
            chairs.set(chair, client);
            freePlace.remove(chair);
            Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
                Logger.info(client + " ready to do order!");
                barmenCaller.onNext(Bar.class.getSimpleName() + chair);
            });
        } else {
            Logger.info("No place!");
        }
    }

    public void reOrder(Integer chair) {
        Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
            Logger.info(getClient(chair) + " ready to do order again!");
            barmenCaller.onNext(Bar.class.getSimpleName() + chair);
        });
    }

    public void clientGone(Integer chair) {
        Client client = getClient(chair);
        diner.feedback(client);
        chairs.set(chair, null);
        freePlace.add(chair);
        Logger.info("Client is gone (feedback: " + client.feedback + "), chair #" + chair + " is free");
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

    public Order checkWaitOrder() {
        return waitOrder.peekFirst();
    }

    public Order checkReadyOrder() {
        return readyOrder.peekFirst();
    }


    public void subscribe(Waiter waiter) {
        waiterCaller.subscribe(waiter);
    }

    public void subscribe(Barmen barmen) {
        barmenCaller.subscribe(barmen);
    }

    public Toilet getToilet() {
        return diner.getHall().getToilet();
    }

    @Override
    public void getDirty() {
        diner.dirtCurrentRoom(this);
    }
}
