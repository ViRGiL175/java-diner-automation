package ru.commandos.Food.Dish;

public class Ratatouille extends Dish {

    {
        ingredients.put("Pepper", 2);
        ingredients.put("Zucchini", 1);
        ingredients.put("Onion", 1);
    }

    @Override
    public String toString() {
        return "Ratatouille";
    }
}
