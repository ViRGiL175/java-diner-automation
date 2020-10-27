package ru.commandos.Food.Dish;

public class DeepFriedBat extends Dish {

    {
        ingredients.put("Bat", 1);
        ingredients.put("Oil", 1);
    }

    @Override
    public String toString() {
        return "Deep fried bat";
    }
}
