package ru.commandos.Rooms;

public abstract class Room {

    public enum OrderPlace {
        TABLES, BAR, DRIVETHRU
    }

    public abstract void getDirty();
}
