package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Food.IngredientList;
import ru.commandos.Main;
import ru.commandos.Rooms.Bookkeeping;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Bookkeeper extends Staff implements Observer<Date> {

    private final Bookkeeping bookkeeping;
    private GregorianCalendar calendar = null;

    public Bookkeeper(Diner diner, Bookkeeping bookkeeping) {
        super(diner);
        this.bookkeeping = bookkeeping;
        currentRoom = bookkeeping;
        Main.addToCmd("INFO: Bookkeeper is ready to work");
        Logger.info("Bookkeeper is ready to work");
    }

    private void payTax() {
        double BD = 1000.0; //Базовая доходность, фиксированная на гос.уровне
        double FP = 80.0; //Физический показатель, равен площади зала обслуживания
        double K1 = 1.798; //Коэффициент, фиксированный государством
        double K2 = 1.0; //Коэффициент, фиксированный местными органами управления
        Double tax = BD * FP * K1 * K2 * 15.0 / 100.0;
        if (tax <= bookkeeping.checkBudget()) {
            bookkeeping.getMoneyFromBudget(tax);
            Main.addToEconomicLabels((Main.calendar.get(Calendar.YEAR) - 57) + ", " + Main.calendar.get(Calendar.DAY_OF_MONTH) + " " + Main.calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("en")) + ", " + ((Main.calendar.get(Calendar.HOUR_OF_DAY) > 10) ? Main.calendar.get(Calendar.HOUR_OF_DAY) : "0" + Main.calendar.get(Calendar.HOUR_OF_DAY)) + ":" + ((Main.calendar.get(Calendar.MINUTE) > 10) ? Main.calendar.get(Calendar.MINUTE) : "0" + Main.calendar.get(Calendar.MINUTE)) + ":" + " Tax = -$" + String.format("%.2f", tax));
            Main.addToCmd("INFO: Taxes paid");
            Main.updateScreen();
            Logger.info("Taxes paid");
        } else {
            Main.addToCmd("WARN: Taxes not paid due to lack of money");
            Main.updateScreen();
            Logger.warn("Taxes not paid due to lack of money");
        }
    }

    private void payDay() {
        Main.addToCmd("INFO: Payday!");
        Logger.info("Payday!");
        HashMap<Staff, Double> pay = bookkeeping.getStaffPayList();
        for (Staff staff : pay.keySet()) {
            if (pay.get(staff) <= bookkeeping.checkBudget()) {
                Double money = bookkeeping.getMoneyFromBudget(pay.get(staff));
                Main.addToEconomicLabels((Main.calendar.get(Calendar.YEAR) - 57) + ", " + Main.calendar.get(Calendar.DAY_OF_MONTH) + " " + Main.calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("en")) + ", " + ((Main.calendar.get(Calendar.HOUR_OF_DAY) > 10) ? Main.calendar.get(Calendar.HOUR_OF_DAY) : "0" + Main.calendar.get(Calendar.HOUR_OF_DAY)) + ":" + ((Main.calendar.get(Calendar.MINUTE) > 10) ? Main.calendar.get(Calendar.MINUTE) : "0" + Main.calendar.get(Calendar.MINUTE)) + ":" + staff.getClass().getSimpleName() + " receive a salary = -$" + String.format("%.2f", money));
                staff.changeMoney(money);
                Main.addToCmd("DEBUG: " + staff.getClass().getSimpleName() + " receive a salary");
                Main.updateScreen();
                Logger.debug(staff.getClass().getSimpleName() + " receive a salary");
            } else {
                Main.addToEconomicLabels((Main.calendar.get(Calendar.YEAR) - 57) + ", " + Main.calendar.get(Calendar.DAY_OF_MONTH) + " " + Main.calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("en")) + ", " + ((Main.calendar.get(Calendar.HOUR_OF_DAY) > 10) ? Main.calendar.get(Calendar.HOUR_OF_DAY) : "0" + Main.calendar.get(Calendar.HOUR_OF_DAY)) + ":" + ((Main.calendar.get(Calendar.MINUTE) > 10) ? Main.calendar.get(Calendar.MINUTE) : "0" + Main.calendar.get(Calendar.MINUTE)) + ":" + staff.getClass().getSimpleName() + " didn't receive a salary");
                Main.addToCmd("WARN: " + staff.getClass().getSimpleName() + " didn't receive a salary");
                Main.updateScreen();
                Logger.warn(staff.getClass().getSimpleName() + " didn't receive a salary");
            }
        }
    }

    public void giveClientPayment(Double payment) {
        Main.addToEconomicLabels((Main.calendar.get(Calendar.YEAR) - 57) + ", " + Main.calendar.get(Calendar.DAY_OF_MONTH) + " " + Main.calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("en")) + ", " + ((Main.calendar.get(Calendar.HOUR_OF_DAY) > 10) ? Main.calendar.get(Calendar.HOUR_OF_DAY) : "0" + Main.calendar.get(Calendar.HOUR_OF_DAY)) + ":" + ((Main.calendar.get(Calendar.MINUTE) > 10) ? Main.calendar.get(Calendar.MINUTE) : "0" + Main.calendar.get(Calendar.MINUTE)) + ": Serve a client = +$" + String.format("%.2f", payment));
        bookkeeping.putMoneyInBudget(payment);
    }

    @Override
    public void useToilet() {
        if (new Random().nextInt(10) < 2) {
            ArrayDeque<Human> queue = diner.getHall().getToilet().queue;
            final boolean[] waiting = {true};
            Observable.interval(1 * Diner.slowdown, TimeUnit.MILLISECONDS).takeWhile(l1 -> waiting[0]).subscribe(l2 -> {
                if ((queue.size() - 1) / 4 == 0) {
                    queue.addLast(this);
                    int place = queue.size() - 1;
                    waiting[0] = false;
                    Main.restRoomPlaces.get(place).setText((place + 1) + ".Bookkeeper");
                    Main.updateScreen();
                    Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
                        Main.addToCmd("INFO: Bookkeepet used Restroom");
                        Logger.info(this.getClass().getSimpleName() + " used Toilet");
                        queue.remove(this);
                        Main.restRoomPlaces.get(place).setText((place + 1) + ".        ");
                        Main.updateScreen();
                        diner.getHall().getToilet().getDirty();
                    });
                }
            });
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
    }

    @Override
    public void onNext(@NonNull Date date) {
        Main.updateDate(date);
        if (this.calendar == null) {
            calendar = new GregorianCalendar();
            calendar.setTime(date);
        } else {
            GregorianCalendar calendarToday = new GregorianCalendar();
            calendarToday.setTime(date);
            if (calendarToday.get(Calendar.MONTH) > calendar.get(Calendar.MONTH)) {
                Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
                    calendar = calendarToday;
                    payTax();
                    payDay();
                    bookkeeping.countDinamic();
                });
            }
        }
        HashMap<String, Integer> ingredients = diner.getKitchen().checkIngredients();
        for (String ingredient : ingredients.keySet()) {
            if (ingredients.get(ingredient) < 5) {
                HashMap<String, Integer> finalIngredients = ingredients;
                Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
                    Integer countIngredientToBuy = 10 - finalIngredients.get(ingredient);
                    Double money = bookkeeping.getMoneyFromBudget(countIngredientToBuy * IngredientList.getIngredientCost(ingredient));
                    Main.addToCmd("INFO: Bookkeeper bought an ingredient: " + ingredient + "X" + countIngredientToBuy);
                    Main.updateScreen();
                    Logger.info("Bookkeeper bought an ingredient: " + ingredient + "X" + countIngredientToBuy);
                    diner.getKitchen().setIngredients(ingredient, countIngredientToBuy);
                });
            }
        }
        ingredients = diner.getHall().getBar().checkIngredients();
        for (String ingredient : ingredients.keySet()) {
            if (ingredients.get(ingredient) < 5) {
                HashMap<String, Integer> finalIngredients1 = ingredients;
                Observable.timer(1 * Diner.slowdown, TimeUnit.MILLISECONDS).subscribe(v -> {
                    Integer countIngredientToBuy = 10 - finalIngredients1.get(ingredient);
                    Double money = bookkeeping.getMoneyFromBudget(countIngredientToBuy * IngredientList.getIngredientCost(ingredient));
                    Main.addToCmd("INFO: Bookkeeper bought an ingredient: " + ingredient + "X" + countIngredientToBuy);
                    Main.updateScreen();
                    Logger.info("Bookkeeper bought an ingredient: " + ingredient + "X" + countIngredientToBuy);
                    diner.getHall().getBar().setIngredients(ingredient, countIngredientToBuy);
                });
            }
        }
        currentRoom.getDirty();

        useToilet();

    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        Logger.warn("Bookkeeper is sleeping");
    }
}
