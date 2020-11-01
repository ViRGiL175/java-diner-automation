package ru.commandos.rx.example;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableTransformer;

import java.util.concurrent.TimeUnit;

class RxWaiter {

    public ObservableTransformer<String, RxOrder> acceptOrder() {
        return upstream1 -> upstream1.delay(4, TimeUnit.SECONDS).flatMap(s -> Observable.just(new RxOrder(s)));
    }

    public ObservableTransformer<RxMeal, RxMeal> transportOrder() {
        return upstream -> upstream.delay(15, TimeUnit.SECONDS);
    }
}
