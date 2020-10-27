package ru.commandos;

import ru.commandos.Food.Dish.Dish;
import ru.commandos.Food.Drink.Drink;
import ru.commandos.Rooms.Room;

import java.util.HashSet;

public class Order {

    public HashSet<Dish> dishes;
    public HashSet<Drink> drinks;
    public HashSet<Dish> doneDishes = new HashSet<>();
    public HashSet<Drink> doneDrinks = new HashSet<>();
    public Room.OrderPlace orderPlace;
    public Integer table;
    public double cost = 0;
    public boolean placed = false;

    public Order(HashSet<Dish> dishes, HashSet<Drink> drinks, Menu menu, Room.OrderPlace orderPlace, Integer table, Double cost) {
        this.dishes = dishes;
        this.drinks = drinks;
        this.orderPlace = orderPlace;
        this.table = table;
        this.cost = cost;
    }

    public Boolean isready() {
        return dishes.equals(doneDishes) && drinks.equals(doneDrinks);
    }

    @Override
    public String toString() {
        return "Order{" +
                "food=" + dishes +
                ", drinks=" + drinks +
                ", doneFood=" + doneDishes +
                ", doneDrinks=" + doneDrinks +
                ((orderPlace == Room.OrderPlace.DRIVETHRU)
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
        return dishes.equals(order.dishes) &&
                drinks.equals(order.drinks) &&
                table.equals(order.table);
    }
}
