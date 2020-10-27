package ru.commandos;

import ru.virgil.OuterWorld;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        OuterWorld outerWorld = OuterWorld.singleton(1, TimeUnit.SECONDS);

        Diner diner = new Diner(outerWorld.getClientsSource(), outerWorld.getDateSource());

        outerWorld.run();
    }
}
