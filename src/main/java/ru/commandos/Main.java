package ru.commandos;

import ru.virgil.OuterWorld;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        Diner.slowdown = 1000;

        OuterWorld outerWorld = OuterWorld.singleton(20 * Diner.slowdown, TimeUnit.MILLISECONDS);

        Diner diner = new Diner(outerWorld.getClientsSource(), outerWorld.getDateSource());

        outerWorld.run();
    }
}
