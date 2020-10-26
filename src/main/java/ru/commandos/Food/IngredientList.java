package ru.commandos.Food;

import java.util.HashMap;

public class IngredientList {

    private static final HashMap<String, Double> ingredientList = new HashMap<>();

    static
    {
        ingredientList.put("Сыр", 10.0);
        ingredientList.put("Тесто", 5.5);
        ingredientList.put("Баранина", 23.99);
        ingredientList.put("Перец", 4.99);
        ingredientList.put("Кабачок", 3.99);
        ingredientList.put("Лук", 1.99);
        ingredientList.put("Летучая мышь", 59.99);
        ingredientList.put("Масло", 2.99);
        ingredientList.put("Спирт этиловый", 12.99);
        ingredientList.put("Лимонный сок", 0.99);
        ingredientList.put("Какао бобы", 9.99);
        ingredientList.put("Молоко", 2.99);
    }

    public static Double getIngredientCost(String ingredient) {
        return ingredientList.get(ingredient);
    }
}
