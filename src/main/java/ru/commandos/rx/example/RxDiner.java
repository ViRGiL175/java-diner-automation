package ru.commandos.rx.example;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import org.tinylog.Logger;
import ru.virgil.OuterWorld;

import java.util.Date;

class RxDiner {

    private final Observable<String> clientsSource;
    private final Observable<Date> dateSource;
    private final Observable<String> autoClientsSource;
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final RxWaiter rxWaiter = new RxWaiter();
    private final RxCooker rxCooker = new RxCooker();
    private final RxClient rxClient = new RxClient();

    public RxDiner(OuterWorld outerWorld) {
        clientsSource = outerWorld.getClientsSource();
        dateSource = outerWorld.getDateSource();
        autoClientsSource = outerWorld.getAutoClientsSource();
        disposable.add(outerWorld.getOuterWorldDisposable());

        // Needed to add to CompositeDisposable
        disposable.add(clientsSource
                .compose(rxClient.makeOrder())
                .compose(rxWaiter.acceptOrder())
                .compose(rxCooker)
                .compose(rxWaiter.transportOrder())
                .lift(rxClient.thinkNextOrder())
                // repeat
                .compose(rxClient.makeFeedback())
                .subscribe(Logger::trace));

        // Not needed to add to CompositeDisposable
        clientsSource
                .compose(rxClient.makeOrder())
                .compose(rxWaiter.acceptOrder())
                .compose(rxCooker)
                .compose(rxWaiter.transportOrder())
                .lift(rxClient.thinkNextOrder())
                // repeat
                .compose(rxClient.makeFeedback())
                .subscribe();
    }

    public void onProgramClose() {
        disposable.dispose();
    }
}
