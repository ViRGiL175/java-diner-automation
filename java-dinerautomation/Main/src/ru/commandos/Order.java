package ru.commandos;

import java.util.HashSet;
import java.util.Objects;

public class Order {

    public HashSet<String> food;
    public HashSet<String> done = new HashSet<>();
    public Integer table;
    public Double cost = 0.;

    public Order(HashSet<String> food, Menu menu, Integer table) {
        this.food = food;
        for (String s : food) {
            cost += menu.menu.get(s);
        }
        this.table = table;
    }

    public Boolean isready() {
        return food.equals(done);
    }

    @Override
    public String toString() {
        return "Order{" +
                "food=" + food +
                ", table=" + (table + 1) +
                ", cost=" + cost +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return food.equals(order.food) &&
                done.equals(order.done) &&
                table.equals(order.table) &&
                cost.equals(order.cost);
    }
}
