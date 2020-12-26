package bank;

import bank.event.*;

class BankAccount {
    private Event[] events = new Event[maxEvents];
    final static int maxEvents = 100;



    // Added
    private String id;
    private String password;
    private int balance;
    private int numEvents;


    BankAccount(String id, String password, int balance) {
        //TODO: Problem 1.1
        this.id = id;
        this.password = password;
        this.balance = balance;
        this.numEvents = 0;
    }

    public Event[] getEvents() {
        Event[] eventsWithoutNull = new Event[numEvents];
        for (int i = 0; i < numEvents; i++){
            eventsWithoutNull[i] = events[i];
        }
        return eventsWithoutNull;
    }

    public int getBalance() {
        return balance;
    }

    boolean authenticate(String password) {
        //TODO: Problem 1.1
        return this.password.equals(password);
    }

    void deposit(int amount) {
        //TODO: Problem 1.1
        balance += amount;
        events[numEvents++] = new DepositEvent();
    }

    boolean withdraw(int amount) {
        //TODO: Problem 1.1
        if (balance >= amount){
            balance -= amount;
            events[numEvents++] = new WithdrawEvent();
            return true;
        }
        return false;
    }

    void receive(int amount) {
        //TODO: Problem 1.1
        balance += amount;
        events[numEvents++] = new ReceiveEvent();
    }

    boolean send(int amount) {
        //TODO: Problem 1.1
        if (balance >= amount){
            balance -= amount;
            events[numEvents++] = new SendEvent();
            return true;
        }
        return false;
    }

}
