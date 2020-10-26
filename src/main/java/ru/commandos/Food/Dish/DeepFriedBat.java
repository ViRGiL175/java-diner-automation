package ru.commandos.Food.Dish;

public class DeepFriedBat extends Dish {

    {
        ingredients.put("Летучая мышь", 1);
        ingredients.put("Масло", 1);
    }

    @Override
    public String toString() {
        return "Летучая мышь во фритюре";
    }
}
