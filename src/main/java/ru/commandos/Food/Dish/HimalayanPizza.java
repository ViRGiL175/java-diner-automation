package ru.commandos.Food.Dish;

public class HimalayanPizza extends Dish {

    {
        ingredients.put("Cheese", 1);
        ingredients.put("Dough", 1);
        ingredients.put("Mutton", 1);
    }

    @Override
    public String toString() {
        return "Himalayan pizza";
    }
}
