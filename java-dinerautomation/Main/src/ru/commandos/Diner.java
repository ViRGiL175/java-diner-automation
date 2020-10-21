package ru.commandos;

import io.reactivex.rxjava3.core.Observable;
import ru.commandos.Humans.Barmen;
import ru.commandos.Humans.Bookkeeper;
import ru.commandos.Humans.Cook;
import ru.commandos.Humans.Waiter;
import ru.commandos.Rooms.*;

import java.util.Date;

public class Diner {

    private final Menu menu = new Menu();
    private final Hall hall = new Hall(this);
    private final DriveThru driveThru = new DriveThru();
    private final Kitchen kitchen = new Kitchen();
    private final Bookkeeping bookkeeping = new Bookkeeping(this);
    private final Cook cook = new Cook(this, kitchen);
    private final Barmen barmen = new Barmen(this, hall.getBar());
    private final Waiter waiter = new Waiter(this, kitchen, driveThru);
    private final Bookkeeper bookkeeper = new Bookkeeper(this, bookkeeping);

    public Diner(Observable<String> jsonObservable, Observable<Date> dateObservable) {
        System.out.println("Дайнер начал работу");
        kitchen.subscribe(cook);
        kitchen.subscribe(waiter);
        jsonObservable.subscribe(hall);
        dateObservable.subscribe(bookkeeper);
//        jsonObservable.subscribe(driveThru);
//        driveThru.subscribe(waiter);
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
