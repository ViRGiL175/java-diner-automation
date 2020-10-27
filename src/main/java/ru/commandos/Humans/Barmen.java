package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Food.Drink.Drink;
import ru.commandos.Order;
import ru.commandos.Rooms.Bar;
import ru.commandos.Rooms.Room;

import java.util.Random;


public class Barmen extends Staff implements Observer<String> {

    private final Bar bar;

    public Barmen(Diner diner, Bar bar) {
        super(diner);
        this.bar = bar;
        currentRoom = bar;
    }

    private void shake(Order order) {
        for(Drink drink : order.drinks) {
            for (String ingredient : drink.getIngredients().keySet()) {
                bar.getIngredients(ingredient, drink.getIngredients().get(ingredient));
            }
            order.doneDrinks.add(drink);
        }
        Logger.debug("List of remaining ingredients in the Bar: " + bar.checkIngredients());
        Logger.debug("Barmen made drinks");
        currentRoom.getDirty();

        useToilet();

        if (order.orderPlace != Room.OrderPlace.BAR || !order.dishes.isEmpty()) {
            bar.transfer(order);
        }
        else {
            setReadyOrder(order);
        }
    }

    private void acceptOrder(Integer chairNumber) {
        bar.getClient(chairNumber).setMenu(diner.getMenu());
        Order order = bar.getClient(chairNumber).getOrder();
        if (order.cost == 0.) {
            Logger.info("Client hasn't ordered");
            bar.clientGone(order.table);
        } else {
            Logger.info("Barmen took Order at Bar: " + order);
            transferOrder(order);
        }
    }

    private void transferOrder(Order order) {
        if (!order.drinks.isEmpty()) {
            shake(order);
        }
        else {
            bar.transfer(order);
        }
    }

    public void setReadyOrder(Order order) {
        Logger.debug("Barmen gives the order to Client");
        bar.getClient(order.table).setOrder(order);
        changeMoney(bar.getClient(order.table).pay());
        bar.clientGone(order.table);
        givePaymentToBookkeeper();
    }

    private void givePaymentToBookkeeper() {
        diner.getBookkeeper().giveClientPayment(getMoney());
        money = "$0";
    }

    @Override
    public void useToilet() {
        if (new Random().nextInt(10) < 2) {
            Logger.info(this.getClass().getSimpleName() + " used Toilet");
            diner.getHall().getToilet().getDirty();
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        Logger.info("Barmen is ready to work");
    }

    @Override
    public void onNext(@NonNull String s) {
        if (s.equals(Waiter.class.getSimpleName())) {
            shake(bar.getWaitOrder());
        }
        else {
            Integer table = Integer.parseInt(new StringBuffer(s).delete(0, 3).toString());
            acceptOrder(table);
        }
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
