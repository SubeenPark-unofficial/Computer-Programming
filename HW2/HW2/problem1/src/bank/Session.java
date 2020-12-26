package bank;

public class Session {

    private String sessionKey;
    private Bank bank;
    private boolean valid;
    Session(String sessionKey,Bank bank){
        this.sessionKey = sessionKey;
        this.bank = bank;
        valid = true;
    }

    public boolean deposit(int amount) {
        //TODO: Problem 1.2
        if (valid){
            bank.deposit(sessionKey, amount);
            return true;
        }
        return false;
    }

    public boolean withdraw(int amount) {
        //TODO: Problem 1.2
        if (valid){
            return bank.withdraw(sessionKey, amount);
        }
        return false;
    }

    public boolean transfer(String targetId, int amount) {
        //TODO: Problem 1.2
        if (valid){
            return bank.transfer(sessionKey, targetId, amount);
        }
        return false;
    }

    public void expireSession(){
        valid = false;
    }

}
