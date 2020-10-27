package ru.commandos.Food.Drink;

public class CocoaSchool extends Drink {

    {
        ingredients.put("Cocoa beans", 2);
        ingredients.put("Milk", 1);
    }

    @Override
    public String toString() {
        return "School cocoa";
    }
}
