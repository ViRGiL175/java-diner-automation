package ru.commandos.Humans;

import ru.commandos.Food.Dish.Dish;
import ru.commandos.Food.Drink.Drink;
import ru.commandos.Menu;
import ru.commandos.Order;
import ru.commandos.Rooms.Room;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Client extends Human {

    private Room.orderPlace orderPlace;
    private Integer table;
    private Menu menu;
    private String uuid;
    private Order order;

    public void setOrderPlace(Room.orderPlace orderPlace) {
        this.orderPlace = orderPlace;
    }

    public void setTable(Integer table) {
        this.table = table;
        System.out.println("Клиент " + this + " сел за стол №" + table);
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
        System.out.println("Клиент " + this + " получил меню");
    }

    public Order getOrder() {
        Random random = new Random();
        int foods = 0;
        int drink = 0;
        while (foods == 0 && drink == 0) {
            foods = random.nextInt(menu.food.size() + 1);
            drink = random.nextInt(menu.drinks.size() + 1);
        }

        ArrayList<Dish> menuFood = new ArrayList<>(menu.food.keySet());
        ArrayList<Drink> menuDrinks = new ArrayList<>(menu.drinks.keySet());

        HashSet<Dish> orderFood = new HashSet<>();
        HashSet<Drink> orderDrinks = new HashSet<>();
        Double cost = 0.;

        for (int i = 0; i < foods; i++) {
            int number = random.nextInt(menu.food.size());
            if (menu.food.get(menuFood.get(number)) <= getDoubleMoney() - cost) {
                orderFood.add(menuFood.get(number));
                cost += menu.food.get(menuFood.get(number));
            }
        }
        for (int i = 0; i < drink; i++) {
            int number = random.nextInt(menu.drinks.size());
            if (menu.drinks.get(menuDrinks.get(number)) <= getDoubleMoney() - cost) {
                orderDrinks.add(menuDrinks.get(number));
                cost += menu.drinks.get(menuDrinks.get(number));
            }
        }
        order = new Order(orderFood, orderDrinks, menu, orderPlace, table, cost);
        return order;
    }

    public void setOrder(Order order) {
        if (!this.order.equals(order)) {
            System.err.println("Официант ошибся с заказом :(");
        } else {
            System.out.println("Клиент получил заказ");
        }
    }

    public Double pay() {
        changeMoney(-order.cost);
        System.out.println("Клиент оплатил заказ");
        return order.cost;
    }

    @Override
    public String toString() {
        return "Client{" +
                "table=" + table +
                ", menu=" + menu +
                ", uuid='" + uuid + '\'' +
                ", order=" + order +
                ", money='" + money + '\'' +
                '}';
    }
}
