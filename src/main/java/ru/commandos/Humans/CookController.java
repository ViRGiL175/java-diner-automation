package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Order;
import ru.commandos.Rooms.Kitchen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CookController implements Observer<Order> {

    private final Diner diner;
    private final HashMap<Integer, PublishSubject<Order>> cookCaller = new HashMap<>();

    public ArrayList<Cook> cooks = new ArrayList<>();

    public CookController(Diner diner) {
        this.diner = diner;
        for (int i = 0; i < 2; i++) {
            cooks.add(new Cook(diner, diner.getKitchen(), i + 1));
            cookCaller.put(i, PublishSubject.create());
            cookCaller.get(i).subscribe(cooks.get(i));
        }
    }

    public ArrayList<Cook> getCooks() {
        return cooks;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
    }

    @Override
    public void onNext(@NonNull Order order) {
        if (cooks.get(0).getActionSize() == cooks.get(1).getActionSize()) {
            cookCaller.get(new Random().nextInt(2)).onNext(order);
        }
        else if (cooks.get(0).getActionSize() < cooks.get(1).getActionSize()) {
            cookCaller.get(0).onNext(order);
        }
        else {
            cookCaller.get(1).onNext(order);
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
    }
}
