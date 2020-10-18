package ru.commandos;

import io.reactivex.rxjava3.core.Observable;
import ru.commandos.Humans.Barmen;
import ru.commandos.Humans.Cook;
import ru.commandos.Humans.Waiter;
import ru.commandos.Rooms.*;

public class Diner {

    private final Menu menu = new Menu();
    private final Hall hall = new Hall(this);
    private final Kitchen kitchen = new Kitchen();
    private final Cook cook = new Cook(this, kitchen);
    private final Barmen barmen = new Barmen(this, hall.getBar());
    private final Waiter waiter = new Waiter(this, kitchen);

    public Diner(Observable<String> subject) {
        System.out.println("Дайнер начал работу");
        kitchen.subscribe(cook);
        kitchen.subscribe(waiter);
        subject.subscribe(this.getHall());
    }

    public Menu getMenu() {
        return menu;
    }

    public Waiter getWaiter() {
        return waiter;
    }

    public Barmen getBarmen() {
        return barmen;
    }

    public Hall getHall() {
        return hall;
    }
}
