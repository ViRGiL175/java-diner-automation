package ru.commandos;

import com.googlecode.lanterna.gui2.Label;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.tinylog.Logger;
import ru.commandos.Humans.*;
import ru.commandos.Rooms.*;

import java.util.*;

public class Diner {

    public static int slowdown = 1000;

    protected final PublishSubject<Room> cleanerCaller = PublishSubject.create();

    private final Menu menu = new Menu();
    private final Hall hall = new Hall(this);
    private final DriveThru driveThru = new DriveThru(this);
    private final Kitchen kitchen = new Kitchen(this);
    private final Barmen barmen = new Barmen(this, hall.getBar());
    private final CookController cookController = new CookController(this);
    private final WaiterController waiterController = new WaiterController(this);
    private final Bookkeeping bookkeeping = new Bookkeeping(this);
    private final Bookkeeper bookkeeper = new Bookkeeper(this, bookkeeping);
    private final Cleaner cleaner = new Cleaner(this);

    private final HashMap<Room, Integer> roomDirt = new HashMap<>();
    private final HashMap<Room, Integer> maxRoomDirt = new HashMap<>();
    private final HashMap<Room, Integer> maxRoomDirtSpeed = new HashMap<>();
    private final HashMap<String, String> roomName = new HashMap<>();
    private final HashSet<Room> callerRoom = new HashSet<>();
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

        roomName.put(Bar.class.getSimpleName(), "Counter");
        roomName.put(Bookkeeping.class.getSimpleName(), Bookkeeping.class.getSimpleName());
        roomName.put(DriveThru.class.getSimpleName(), "D-Thru");
        roomName.put(Kitchen.class.getSimpleName(), Kitchen.class.getSimpleName());
        roomName.put(Tables.class.getSimpleName(), "Canteen");
        roomName.put(Toilet.class.getSimpleName(), "Restroom");
    }

    public Diner(Observable<String> clientObservable, Observable<String> autoObservable, Observable<Date> dateObservable) {
        bookkeeping.createPayMap();
        Main.addToCmd("INFO: Diner is starting work");
        Logger.info("Diner is starting work");
        kitchen.subscribe(cookController);
        kitchen.subscribe(waiterController);
        cleanerCaller.subscribe(cleaner);
        clientObservable.subscribe(hall);
        dateObservable.subscribe(bookkeeper);
        autoObservable.subscribe(driveThru);
        driveThru.subscribe(waiterController);
    }

    public void dirtCurrentRoom(Room room) {
        int dirt = roomDirt.get(room) + new Random().nextInt(maxRoomDirtSpeed.get(room) + 1);
        roomDirt.replace(room, dirt);
        if (roomDirt.get(room) >= maxRoomDirt.get(room)/2 && !callerRoom.contains(room)) {
            Main.addToCmd("WARN: Critical pollution in " + roomName.get(room.getClass().getSimpleName()) + ": " + dirt);
            Main.updateScreen();
            Logger.warn("Critical pollution in " + room.getClass().getSimpleName() + ": " + dirt);
            callerRoom.add(room);
            cleanerCaller.onNext(room);
        }
    }

    public void clean(Room room) {
        roomDirt.replace(room, 0);
        callerRoom.remove(room);
        Main.addToCmd("INFO: Cleaner tidied up in " + roomName.get(room.getClass().getSimpleName()));
        Main.updateScreen();
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
        Main.addToFeedbackLabels((Main.calendar.get(Calendar.YEAR) - 57) + ", " + Main.calendar.get(Calendar.DAY_OF_MONTH) + " " + Main.calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("en")) + ", " + ((Main.calendar.get(Calendar.HOUR_OF_DAY) > 10) ? Main.calendar.get(Calendar.HOUR_OF_DAY) : "0" + Main.calendar.get(Calendar.HOUR_OF_DAY)) + ":" + ((Main.calendar.get(Calendar.MINUTE) > 10) ? Main.calendar.get(Calendar.MINUTE) : "0" + Main.calendar.get(Calendar.MINUTE)) + ":" + client.feedback);
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

    public WaiterController getWaiterController() {
        return waiterController;
    }

    public Barmen getBarmen() {
        return barmen;
    }

    public CookController getCookController() {
        return cookController;
    }
}
