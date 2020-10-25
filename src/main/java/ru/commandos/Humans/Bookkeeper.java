package ru.commandos.Humans;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.tinylog.Logger;
import ru.commandos.Diner;
import ru.commandos.Food.IngredientList;
import ru.commandos.Rooms.Bookkeeping;

import java.util.*;

public class Bookkeeper extends Staff implements Observer<Date> {

    private final Bookkeeping bookkeeping;
    private GregorianCalendar calendar = null;

    public Bookkeeper(Diner diner, Bookkeeping bookkeeping) {
        super(diner);
        this.bookkeeping = bookkeeping;
        currentRoom = bookkeeping;
        Logger.info("Мы наняли лучшего бухгалтера, чтобы дела шли в гору!");
    }

    private void payTax() {
        double BD = 1000.0; //Базовая доходность, фиксированная на гос.уровне
        double FP = 80.0; //Физический показатель, равен площади зала обслуживания
        double K1 = 1.798; //Коэффициент, фиксированный государством
        double K2 = 1.0; //Коэффициент, фиксированный местными органами управления
        Double tax = BD * FP * K1 * K2 * 15.0 / 100.0;
        if (tax <= bookkeeping.checkBudget()) {
            bookkeeping.getMoneyFromBudget(tax);
            Logger.info("Налоги оплачены");
        } else {
            Logger.warn("Налоги не оплачены из-за нехватки денег");
        }
    }

    private void payDay() {
        Logger.info("День зарплаты!");
        HashMap<Staff, Double> pay = bookkeeping.getStaffPayList();
        for (Staff staff : pay.keySet()) {
            if (pay.get(staff) <= bookkeeping.checkBudget()) {
                Double money = bookkeeping.getMoneyFromBudget(pay.get(staff));
                staff.changeMoney(money);
                Logger.debug(staff.getClass().getSimpleName() + " получил зарплату");
            } else {
                Logger.warn(staff.getClass().getSimpleName() + " не получил зарплату");
            }
        }
    }

    public void giveClientPayment(Double payment) {
        bookkeeping.putMoneyInBudget(payment);
    }

    @Override
    public void useToilet() {
        if (new Random().nextInt(10) < 2) {
            Logger.info(this.getClass().getSimpleName() + " воспользовался туалетом");
            diner.getHall().getToilet().getDirty();
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
    }

    @Override
    public void onNext(@NonNull Date date) {
        if (this.calendar == null) {
            calendar = new GregorianCalendar();
            calendar.setTime(date);
        } else {
            GregorianCalendar calendarToday = new GregorianCalendar();
            calendarToday.setTime(date);
            if (calendarToday.get(Calendar.MONTH) > calendar.get(Calendar.MONTH)) {
                calendar = calendarToday;
                payTax();
                payDay();
            }
        }
        HashMap<String, Integer> ingredients = diner.getKitchen().checkIngredients();
        for (String ingredient : ingredients.keySet()) {
            if (ingredients.get(ingredient) < 5) {
                Integer countIngredientToBuy = 10 - ingredients.get(ingredient);
                Double money = bookkeeping.getMoneyFromBudget(countIngredientToBuy * IngredientList.getIngredientCost(ingredient));
                Logger.info("Бухгалтер купил ингредиент: " + ingredient + "X" + countIngredientToBuy);
                diner.getKitchen().setIngredients(ingredient, countIngredientToBuy);
            }
        }
        ingredients = diner.getHall().getBar().checkIngredients();
        for (String ingredient : ingredients.keySet()) {
            if (ingredients.get(ingredient) < 5) {
                Integer countIngredientToBuy = 10 - ingredients.get(ingredient);
                Double money = bookkeeping.getMoneyFromBudget(countIngredientToBuy * IngredientList.getIngredientCost(ingredient));
                Logger.info("Бухгалтер купил ингредиент: " + ingredient + "X" + countIngredientToBuy);
                diner.getHall().getBar().setIngredients(ingredient, countIngredientToBuy);
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
        Logger.warn("Бухгалтер подавился монетами");
    }
}
