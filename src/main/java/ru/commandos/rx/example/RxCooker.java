package ru.commandos.rx.example;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;

import java.util.concurrent.TimeUnit;

class RxCooker implements ObservableTransformer<RxOrder, RxMeal> {

    @Override
    public @NonNull ObservableSource<RxMeal> apply(@NonNull Observable<RxOrder> upstream) {
        return upstream.delay(15, TimeUnit.SECONDS).flatMap(rxOrder -> Observable.just(new RxMeal(rxOrder)));
    }
}
