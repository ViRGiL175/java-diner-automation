package ru.commandos;

import java.util.HashMap;

public class Menu {

    public HashMap<String, Double> food = new HashMap<>();
    public HashMap<String, Double> drinks = new HashMap<>();

    {
        food.put("Пицца \"Гималайская\"", 99.99);
        food.put("Рататуй", 79.99);
        food.put("Летучая мышь во фритюре", 199.99);
        drinks.put("Шампанское \"Советское\"", 149.99);
        drinks.put("Какао \"Школьное\"", 39.99);
    }
}
