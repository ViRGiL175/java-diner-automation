package ru.commandos.Humans;

import com.google.gson.Gson;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import ru.commandos.Diner;
import ru.commandos.Order;
import ru.commandos.Rooms.Bar;


public class Barmen extends Staff implements Observer<String> {

    private final Bar bar;

    public Barmen(Diner diner, Bar bar) {
        super(diner);
        this.bar = bar;
    }

    private void shake(Order order) {
        order.doneDrinks.addAll(order.drinks);
        System.out.println("Бармен сделал напитки");
        bar.transferDrinks(order);
    }

    private void acceptOrder(Integer chairNumber) {
        bar.getClient(chairNumber).setMenu(diner.getMenu());
        Order order = bar.getClient(chairNumber).getOrder();
        if (order.cost == 0.) {
            System.out.println("Клиент ничего не заказал");
            bar.clientGone(order.table);
        } else {
            System.out.println("Бармен взял заказ в баре: " + order);
            transferOrder(order);
        }
    }

    private void transferOrder(Order order) {
        shake(order);
        if (!order.food.isEmpty()) {
            System.out.println("Заказ передан в кухню");
            bar.transferOrder(order);
        }
    }

    public void setReadyOrderFromKithen(Order order) {
        bar.getClient(order.table).setOrder(order);
        changeMoney(bar.getClient(order.table).pay());
        bar.clientGone(order.table);
        givePaymentToBookkeeper();
    }

    private void givePaymentToBookkeeper() {
        diner.getBookkeeper().giveClientPayment(getDoubleMoney());
        money = "$0";
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        System.out.println("Бармен готов спаивать посетителей");
    }

    @Override
    public void onNext(@NonNull String s) {
        if (s.substring(0, 3).equals(Bar.class.getSimpleName())) {
            Integer table = Integer.parseInt(new StringBuffer(s).delete(0, 3).toString());
            acceptOrder(table);
        }
        else {
            Order order = new Gson().fromJson(s, Order.class);
            shake(order);
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("Бармен больше не наливает");
    }
}
