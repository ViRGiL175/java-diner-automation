package ru.commandos;

import ru.virgil.OuterWorld;

public class Main {

    public static void main(String[] args) {

        OuterWorld outerWorld = OuterWorld.singleton();

        Diner diner = new Diner(outerWorld.getClientsSource(), outerWorld.getDateSource());

        outerWorld.run();
    }
}
