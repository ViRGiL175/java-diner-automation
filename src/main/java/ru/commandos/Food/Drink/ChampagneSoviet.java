package ru.commandos.Food.Drink;

public class ChampagneSoviet extends Drink {

    {
        ingredients.put("Ethanol", 3);
        ingredients.put("Lemon juice", 1);
    }

    @Override
    public String toString() {
        return "Soviet champagne";
    }
}
