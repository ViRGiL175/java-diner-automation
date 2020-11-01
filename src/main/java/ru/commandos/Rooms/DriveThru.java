package ru.commandos.Rooms;

import com.google.gson.Gson;
import com.googlecode.lanterna.gui2.Label;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Humans.Cleaner;
import ru.commandos.Humans.Client;
import ru.commandos.Humans.WaiterController;
import ru.commandos.Main;

import java.util.ArrayDeque;

public class DriveThru extends Room implements Observer<String> {

    private final Diner diner;
    private final ArrayDeque<Client> cars = new ArrayDeque<>();
    private final PublishSubject<String> caller = PublishSubject.create();

    public DriveThru(Diner diner) {
        this.diner = diner;
    }

    public Client getCar() {
        return cars.getFirst();
    }

    public void carGone() {
        diner.feedback(getCar());
        cars.pollFirst();
        for (int i = 0; i < Main.driveThruPlaces.size(); i++) {
            if (i >= cars.size()) {
                Main.driveThruPlaces.get(i).setText((i + 1) + ".        ");
            }
        }
        Main.updateScreen();
        Logger.info("Auto is gone");
    }

    public void subscribe(WaiterController waiterController) {
        caller.subscribe(waiterController);
        Logger.info("Waiters is ready to take orders from Drive-Thru");
    }

    @Override
    public boolean hasFreePlace() {
        return true;
    }

    @Override
    public void getDirty() {
        diner.dirtCurrentRoom(this);
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        System.out.println("Drive-Thru is open");
    }

    @Override
    public void onNext(@NonNull String s) {
        if (cars.size() < Main.driveThruPlaces.size()) {
            Gson gson = new Gson();
            Client client = gson.fromJson(s, Client.class);
            client.move(this);
            cars.add(client);
            client.setOrderPlace(OrderPlace.DRIVETHRU);
            Main.driveThruPlaces.get(cars.size() - 1).setText(cars.size() + ".Auto    ");
            Main.updateScreen();
            caller.onNext(DriveThru.class.getSimpleName());
        } else {
            Logger.info("No places!");
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        Logger.warn("Drive-Thru is close");
    }
}
