package ru.commandos.Food;

import java.util.HashMap;
import java.util.Objects;

abstract public class Food {

    protected HashMap<String, Integer> ingredients = new HashMap<>();

    public HashMap<String, Integer> getIngredients() {
        return ingredients;
    }
}
