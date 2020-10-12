package ru.commandos;

import java.util.HashMap;

public class Menu {

    public HashMap<String, Double> menu = new HashMap<>();
    {
        menu.put("Пицца \"Гималайская\"", 299.99);
        menu.put("Шампанское \"Советское\"", 399.99);
        menu.put("Рататуй", 249.99);
        menu.put("Летучая мышь во фритюре", 599.99);
        menu.put("Какао \"Школьное\"", 39.99);
    }
}
