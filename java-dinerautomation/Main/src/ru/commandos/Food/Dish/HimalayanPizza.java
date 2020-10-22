package ru.commandos.Food.Dish;

public class HimalayanPizza extends Dish {

    {
        ingredients.put("Сыр", 1);
        ingredients.put("Тесто", 1);
        ingredients.put("Баранина", 1);
    }

    @Override
    public String toString() {
        return "Пицца \"Гималайская\"";
    }
}
