package ru.commandos.rx.example;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOperator;
import io.reactivex.rxjava3.core.ObservableTransformer;

import java.util.concurrent.TimeUnit;

class RxClient {

    public ObservableTransformer<String, String> makeOrder() {
        return upstream -> upstream;
    }

    public ObservableOperator<RxMeal, RxMeal> thinkNextOrder() {
        return observer -> observer;
    }

    public ObservableTransformer<RxMeal, String> makeFeedback() {
        return upstream -> upstream.delay(10, TimeUnit.SECONDS).flatMap(rxMeal -> Observable.just(rxMeal.rxOrder.content));
    }
}
