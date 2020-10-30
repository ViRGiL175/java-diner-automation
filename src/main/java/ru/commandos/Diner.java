package ru.commandos;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.tinylog.Logger;
import ru.commandos.Humans.*;
import ru.commandos.Rooms.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class Diner {

    public static int slowdown = 1000;

    protected final PublishSubject<Room> cleanerCaller = PublishSubject.create();

    private final Menu menu = new Menu();
    private final Hall hall = new Hall(this);
    private final DriveThru driveThru = new DriveThru(this);
    private final Kitchen kitchen = new Kitchen(this);
    private final Cook cook = new Cook(this, kitchen);
    private final Barmen barmen = new Barmen(this, hall.getBar());
    private final Waiter waiter = new Waiter(this, kitchen, driveThru);
    private final Bookkeeping bookkeeping = new Bookkeeping(this);
    private final Bookkeeper bookkeeper = new Bookkeeper(this, bookkeeping);
    private final Cleaner cleaner = new Cleaner(this);

    private final HashMap<Room, Integer> roomDirt = new HashMap<>();
    private final HashMap<Room, Integer> maxRoomDirt = new HashMap<>();
    private final HashMap<Room, Integer> maxRoomDirtSpeed = new HashMap<>();
    {
        roomDirt.put(hall.getTables(), 0);
        roomDirt.put(hall.getBar(), 0);
        roomDirt.put(driveThru, 0);
        roomDirt.put(kitchen, 0);
        roomDirt.put(bookkeeping, 0);
        roomDirt.put(hall.getToilet(), 0);

        maxRoomDirt.put(hall.getTables(), 30);
        maxRoomDirt.put(hall.getBar(), 30);
        maxRoomDirt.put(driveThru, 70);
        maxRoomDirt.put(kitchen, 20);
        maxRoomDirt.put(bookkeeping, 20);
        maxRoomDirt.put(hall.getToilet(), 10);

        maxRoomDirtSpeed.put(hall.getTables(), 7);
        maxRoomDirtSpeed.put(hall.getBar(), 10);
        maxRoomDirtSpeed.put(driveThru, 10);
        maxRoomDirtSpeed.put(kitchen, 10);
        maxRoomDirtSpeed.put(bookkeeping, 2);
        maxRoomDirtSpeed.put(hall.getToilet(), 10);
    }

    public Diner(Observable<String> jsonObservable, Observable<Date> dateObservable) {
        bookkeeping.createPayMap();
        Logger.info("Diner is starting work");
        kitchen.subscribe(cook);
        kitchen.subscribe(waiter);
        cleanerCaller.subscribe(cleaner);
        jsonObservable.subscribe(hall);
        dateObservable.subscribe(bookkeeper);
//        jsonObservable.subscribe(driveThru);
//        driveThru.subscribe(waiter);
    }

    public void dirtCurrentRoom(Room room) {
        int dirt = roomDirt.get(room) + new Random().nextInt(maxRoomDirtSpeed.get(room) + 1);
        roomDirt.replace(room, dirt);
        if (roomDirt.get(room) >= maxRoomDirt.get(room)) {
            Logger.warn("Critical pollution in " + room.getClass().getSimpleName() + ": " + dirt);
            cleanerCaller.onNext(room);
        }
    }

    public void clean(Room room) {
        roomDirt.replace(room, 0);
        Logger.info("Cleaner tidied up in " + room.getClass().getSimpleName());
    }

    public void feedback(Client client) {
        double dirt;
        if (client.getOrderPlace() == Room.OrderPlace.DRIVETHRU){
            dirt = ((double) (roomDirt.get(driveThru) + roomDirt.get(kitchen) + roomDirt.get(hall.getBar()))) / (maxRoomDirt.get(driveThru) + maxRoomDirt.get(kitchen) + maxRoomDirt.get(hall.getBar()));
        }
        else {
            dirt = (double)hashMapValuesSum(roomDirt) / hashMapValuesSum(maxRoomDirt);
        }
        if (dirt <= 0.1) {
            client.feedback = Client.Feedback.PERFECT;
        } else if (dirt <= 0.3) {
            client.feedback = Client.Feedback.GOOD;
        } else if (dirt <= 0.5) {
            client.feedback = Client.Feedback.AVERAGE;
        } else if (dirt <= 0.7) {
            client.feedback = Client.Feedback.BELOW_AVERAGE;
        } else {
            client.feedback = Client.Feedback.BAD;
        }
    }

    public int hashMapValuesSum(HashMap<Room, Integer> map) {
        int sum = 0;
        for (Room room : map.keySet()) {
            sum += map.get(room);
        }
        return sum;
    }

    public Menu getMenu() {
        return menu;
    }

    public Hall getHall() {
        return hall;
    }

    public DriveThru getDriveThru() {
        return driveThru;
    }

    public Kitchen getKitchen() {
        return kitchen;
    }

    public Bookkeeping getBookkeeping() {
        return bookkeeping;
    }

    public Bookkeeper getBookkeeper() {
        return bookkeeper;
    }

    public Waiter getWaiter() {
        return waiter;
    }

    public Barmen getBarmen() {
        return barmen;
    }

    public Cook getCook() {
        return cook;
    }
}
