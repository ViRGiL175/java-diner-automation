package ru.commandos.Humans;

import ru.commandos.Rooms.Room;

abstract public class Human {

    protected Room currentRoom;
    protected String money = "$0";

    public Double getMoney() {
        return Double.parseDouble(new StringBuffer(money).delete(0, 1).toString());
    }

    protected void changeMoney(Double cost) {
        double moneyAfterPay = getMoney() + cost;
        money = "$" + moneyAfterPay;
    }

    public void move (Room room) {
        currentRoom = room;
        currentRoom.getDirty();
    }

    abstract public void useToilet();
}
