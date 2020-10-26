package ru.commandos;

import ru.commandos.Food.Dish.*;
import ru.commandos.Food.Drink.*;

import java.util.HashMap;

public class Menu {

    public HashMap<Dish, Double> food = new HashMap<>();
    public HashMap<Drink, Double> drinks = new HashMap<>();

    {
        food.put(new HimalayanPizza(), 99.99);
        food.put(new Ratatouille(), 79.99);
        food.put(new DeepFriedBat(), 199.99);
        drinks.put(new ChampagneSoviet(), 149.99);
        drinks.put(new CocoaSchool(), 39.99);
    }
}
