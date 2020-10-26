package ru.commandos.Food.Dish;

public class Ratatouille extends Dish {

    {
        ingredients.put("Перец", 2);
        ingredients.put("Кабачок", 1);
        ingredients.put("Лук", 1);
    }

    @Override
    public String toString() {
        return "Рататуй";
    }
}
