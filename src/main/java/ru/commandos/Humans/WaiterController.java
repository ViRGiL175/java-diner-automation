package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import ru.commandos.Diner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class WaiterController implements Observer<String> {

    private final Diner diner;
    private final HashMap<Integer, PublishSubject<String>> waiterCaller = new HashMap<>();

    public ArrayList<Waiter> waiters = new ArrayList<>();

    public WaiterController(Diner diner) {
        this.diner = diner;
        for (int i = 0; i < 2; i++) {
            waiters.add(new Waiter(diner, diner.getKitchen(), diner.getDriveThru(), i + 1));
            waiterCaller.put(i, PublishSubject.create());
            waiterCaller.get(i).subscribe(waiters.get(i));
        }
    }

    public ArrayList<Waiter> getWaiters() {
        return waiters;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
    }

    @Override
    public void onNext(@NonNull String s) {
        if (waiters.get(0).getActionSize() == waiters.get(1).getActionSize()) {
            waiterCaller.get(new Random().nextInt(2)).onNext(s);
        }
        else if (waiters.get(0).getActionSize() < waiters.get(1).getActionSize()) {
            waiterCaller.get(0).onNext(s);
        }
        else {
            waiterCaller.get(1).onNext(s);
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
