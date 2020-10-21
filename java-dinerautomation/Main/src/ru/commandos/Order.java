package ru.commandos;

import ru.commandos.Rooms.Room;

import java.util.HashSet;

public class Order {

    public HashSet<String> food;
    public HashSet<String> drinks;
    public HashSet<String> doneFood = new HashSet<>();
    public HashSet<String> doneDrinks = new HashSet<>();
    public Room.orderPlace orderPlace;
    public Integer table;
    public Double cost = 0.;

    public Order(HashSet<String> food, HashSet<String> drinks, Menu menu, Room.orderPlace orderPlace, Integer table, Double cost) {
        this.food = food;
        this.drinks = drinks;
        this.orderPlace = orderPlace;
        this.table = table;
        this.cost = cost;
    }

    public Boolean isready() {
        return food.equals(doneFood) && drinks.equals(doneDrinks);
    }

    @Override
    public String toString() {
        return "Order{" +
                "food=" + food +
                ", drinks=" + drinks +
                ", doneFood=" + doneFood +
                ", doneDrinks=" + doneDrinks +
                ((orderPlace == Room.orderPlace.DRIVETHRU)
                        ? (", orderPlace=" + orderPlace.name())
                        : (", orderPlace=" + orderPlace.name() + ", table=" + table)) +
                ", cost=" + cost +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return food.equals(order.food) &&
                drinks.equals(order.drinks) &&
                table.equals(order.table);
    }
}
