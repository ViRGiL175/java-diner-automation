package ru.commandos.Humans;

import io.reactivex.rxjava3.core.Observable;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Food.Dish.Dish;
import ru.commandos.Food.Drink.Drink;
import ru.commandos.Main;
import ru.commandos.Menu;
import ru.commandos.Order;
import ru.commandos.Rooms.Bar;
import ru.commandos.Rooms.Room;
import ru.commandos.Rooms.Tables;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Client extends Human {

    public enum Feedback {
        PERFECT, GOOD, AVERAGE, BELOW_AVERAGE, BAD
    }

    private Room.OrderPlace orderPlace;
    private Menu menu;
    private String uuid;
    private Order order;

    public Integer table;
    public Feedback feedback;

    public void setOrderPlace(Room.OrderPlace orderPlace) {
        this.orderPlace = orderPlace;
    }

    public void setTable(Integer table) {
        this.table = table;
        if (orderPlace == Room.OrderPlace.TABLES) {
            if (table < 5 || table == 9) {
                Main.canteenPlaces.get(table).setText((table + 1) + ".Client   ");
            } else {
                Main.canteenPlaces.get(table).setText(" " + (table + 1) + ".Client   ");
            }
            Main.addToCmd("INFO: " + this + " sat at the table #" + table);
            Logger.info(this + " sat at the table #" + table);
        } else {
            Main.counterPlaces.get(table).setText((table + 1) + ".Client   ");
            Main.addToCmd("INFO: " + this + " sat at the chair #" + table);
            Logger.info(this + " sat at the chair #" + table);
        }
        Main.updateScreen();
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
        Main.addToCmd("INFO: " + this + " got a menu");
        Logger.info(this + " got a menu");
    }

    public Room.OrderPlace getOrderPlace() {
        return orderPlace;
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
            if (menu.food.get(menuFood.get(number)) <= getMoney() - cost) {
                orderFood.add(menuFood.get(number));
                cost += menu.food.get(menuFood.get(number));
            }
        }
        for (int i = 0; i < drink; i++) {
            int number = random.nextInt(menu.drinks.size());
            if (menu.drinks.get(menuDrinks.get(number)) <= getMoney() - cost) {
                orderDrinks.add(menuDrinks.get(number));
                cost += menu.drinks.get(menuDrinks.get(number));
            }
        }
        order = new Order(orderFood, orderDrinks, menu, orderPlace, table, cost);
        return order;
    }

    public void setOrder(Order order) {
        if (!this.order.equals(order)) {
            Logger.warn("Waiter made a mistake with the order :(");
        } else {
            Main.addToCmd("INFO: " + this + " got order");
            Logger.info("Client got order");
        }
        currentRoom.getDirty();

        useToilet();

    }

    public Double pay() {
        changeMoney(-order.cost);
        Main.addToCmd("INFO: " + this + " paid for order");
        Logger.info("Client paid for order");
        return order.cost;
    }

    @Override
    public void useToilet() {
        if (orderPlace != Room.OrderPlace.DRIVETHRU && new Random().nextInt(10) < 2) {
            ArrayDeque<Human> queue;
            if (currentRoom instanceof Tables) {
                queue = ((Tables) currentRoom).getToilet().queue;
            } else {
                queue = ((Bar) currentRoom).getToilet().queue;
            }
            final boolean[] waiting = {true};
            Observable.interval(1 * Diner.slowdown, TimeUnit.MILLISECONDS).takeWhile(l1 -> waiting[0]).subscribe(l2 -> {
                if ((queue.size() - 1) / 4 == 0) {
                    queue.addLast(this);
                    int place = queue.size() - 1;
                    waiting[0] = false;
                    if (orderPlace == Room.OrderPlace.TABLES) {
                        if (table < 5 || table == 9) {
                            Main.canteenPlaces.get(table).setText((table + 1) + ".         ");
                        } else {
                            Main.canteenPlaces.get(table).setText(" " + (table + 1) + ".         ");
                        }
                    } else {
                        Main.counterPlaces.get(table).setText((table + 1) + ".        ");
                    }
                    Main.restRoomPlaces.get(place).setText((place + 1) + ".Client   ");
                    Main.updateScreen();
                    Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
                        Main.addToCmd(this + " used Restroom");
                        Logger.info(this.getClass().getSimpleName() + " used Toilet");
                        queue.remove(this);
                        Main.restRoomPlaces.get(place).setText((place + 1) + ".        ");
                        if (orderPlace == Room.OrderPlace.TABLES) {
                            if (table < 5 || table == 9) {
                                Main.canteenPlaces.get(table).setText((table + 1) + ".Client   ");
                            } else {
                                Main.canteenPlaces.get(table).setText(" " + (table + 1) + ".Client   ");
                            }
                        } else {
                            Main.counterPlaces.get(table).setText((table + 1) + ".Client   ");
                        }
                        if (currentRoom instanceof Tables) {
                            ((Tables) currentRoom).getToilet().getDirty();
                        } else if (currentRoom instanceof Bar) {
                            ((Bar) currentRoom).getToilet().getDirty();
                        }
                        Main.updateScreen();
                    });
                }
            });
        }
    }

    @Override
    public String toString() {
        return "Client{" +
                "uuid='" + new StringBuilder(uuid).delete(18, 36) +
                '}';
    }
}
