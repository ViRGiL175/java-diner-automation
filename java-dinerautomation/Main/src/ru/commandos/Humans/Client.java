package ru.commandos.Humans;

import ru.commandos.Menu;
import ru.commandos.Order;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Client extends Human {

    private Integer table;
    private Menu menu;
    private String uuid;
    private Order order;

    public void setTable(Integer table) {
        this.table = table;
        System.out.println("Клиент " + this + " сел за стол №" + (table + 1));
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
        System.out.println("Клиент " + this + " получил меню");
    }


    public Order getOrder() {
        Random random = new Random();
        Integer foods = random.nextInt(3) + 1;
        ArrayList<String> menuFood = new ArrayList<>(menu.menu.keySet());
        HashSet<String> orderFood = new HashSet<>();
        for (int i = 0; i < foods; i++) {
            orderFood.add(menuFood.get(random.nextInt(menu.menu.size()-1)));
        }
        order = new Order(orderFood, menu, table);
        return order;
    }

    public void setOrder(Order order) {
        if(!this.order.equals(order)) {
            System.out.println("Официант ошибся с заказом :(");
        }
        else {
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
