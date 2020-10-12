package ru.commandos.Humans;

import ru.commandos.Diner;
import ru.commandos.Order;
import ru.commandos.Rooms.Bar;


public class Barmen extends Staff {

    private Bar bar;

    public Barmen(Diner diner, Bar bar) {
        super(diner);
        this.bar = bar;
    }

    public void shake(Order order) {
        for (String s : order.food) {
            if (bar.canDo(s)) {
                order.done.add(s);
            }
        }
        System.out.println("Бармен сделал напитки");
    }
}
