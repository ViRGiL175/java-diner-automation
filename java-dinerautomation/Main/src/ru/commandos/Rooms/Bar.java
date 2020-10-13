package ru.commandos.Rooms;

import ru.commandos.Humans.Barmen;
import ru.commandos.Order;

import java.util.HashSet;

public class Bar extends Room {

    public Barmen barmen;

    private HashSet<String> drinks = new HashSet<>();
    {
        drinks.add("Шампанское \"Советское\"");
        drinks.add("Какао \"Школьное\"");
    }

    public void setBarmen(Barmen barmen) {
        this.barmen = barmen;
        System.out.println("Бармен готов спаивать посетителей");
    }

    public Boolean canDo(String s) {
        return drinks.contains(s);
    }

    public void acceptOrder(Order order) {
        barmen.shake(order);
    }
}
