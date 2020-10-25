package ru.commandos.Food.Drink;

public class ChampagneSoviet extends Drink {

    {
        ingredients.put("Спирт этиловый", 3);
        ingredients.put("Лимонный сок", 1);
    }

    @Override
    public String toString() {
        return "Шампанское \"Советское\"";
    }
}
