package ru.commandos.Food;

import java.util.HashMap;

public class IngredientList {

    private static final HashMap<String, Double> ingredientList = new HashMap<>();

    static
    {
        ingredientList.put("Cheese", 10.0);
        ingredientList.put("Dough", 5.5);
        ingredientList.put("Mutton", 23.99);
        ingredientList.put("Pepper", 4.99);
        ingredientList.put("Zucchini", 3.99);
        ingredientList.put("Onion", 1.99);
        ingredientList.put("Bat", 59.99);
        ingredientList.put("Oil", 2.99);
        ingredientList.put("Ethanol", 12.99);
        ingredientList.put("Lemon juice", 0.99);
        ingredientList.put("Cocoa beans", 9.99);
        ingredientList.put("Milk", 2.99);
    }

    public static Double getIngredientCost(String ingredient) {
        return ingredientList.get(ingredient);
    }
}
