package ru.commandos.Food.Drink;

public class CocoaSchool extends Drink {

    {
        ingredients.put("Какао бобы", 2);
        ingredients.put("Молоко", 1);
    }

    @Override
    public String toString() {
        return "Какао \"Школьное\"";
    }
}
