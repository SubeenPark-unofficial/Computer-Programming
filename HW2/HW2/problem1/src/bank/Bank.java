package bank;

import bank.event.*;
import security.*;
import security.key.*;

import java.util.HashMap;

public class Bank {
    private int numAccounts = 0;
    final static int maxAccounts = 100;
    private BankAccount[] accounts = new BankAccount[maxAccounts];
    //private String[] ids = new String[maxAccounts];
    private HashMap<String, BankAccount> accountInfo = new HashMap<>(maxAccounts);

    public void createAccount(String id, String password) {
        createAccount(id, password, 0);
    }

    public void createAccount(String id, String password, int initBalance) {
        accountInfo.put(id, new BankAccount(id, password, initBalance));
        numAccounts+=1;
    }

    public boolean deposit(String id, String password, int amount) {
        //TODO: Problem 1.1
        if (accountInfo.containsKey(id) && accountInfo.get(id).authenticate(password)){
            accountInfo.get(id).deposit(amount);
            return true;
        }
        return false;
    }

    public boolean withdraw(String id, String password, int amount) {
        //TODO: Problem 1.1
        if (accountInfo.containsKey(id) && accountInfo.get(id).authenticate(password)){
            return accountInfo.get(id).withdraw(amount);
        }
        return false;
    }

    public boolean transfer(String sourceId, String password, String targetId, int amount) {
        //TODO: Problem 1.1
        if (accountInfo.containsKey(sourceId) && accountInfo.get(sourceId).authenticate(password) && accountInfo.containsKey(targetId)){
            if (accountInfo.get(sourceId).send(amount)){
                accountInfo.get(targetId).receive(amount);
                return true;
            }
        }
        return false;
    }

    public Event[] getEvents(String id, String password) {
        //TODO: Problem 1.1
        if (accountInfo.containsKey(id) && accountInfo.get(id).authenticate(password)){
            return accountInfo.get(id).getEvents();
        }
        return null;
    }

    public int getBalance(String id, String password) {
        //TODO: Problem 1.1
        if (accountInfo.containsKey(id) && accountInfo.get(id).authenticate(password)){
            return accountInfo.get(id).getBalance();
        }
        return -1;
    }

    private static String randomUniqueStringGen(){
        return Encryptor.randomUniqueStringGen();
    }

//    private BankAccount find(String id) {
//        for (int i = 0; i < numAccounts; i++) {
//            if(ids[i].equals(id)){return accounts[i];};
//        }
//        return null;
//    }

    final static int maxSessionKey = 100;
    int numSessionKey = 0;
    String[] sessionKeyArr = new String[maxSessionKey];
    BankAccount[] bankAccountmap = new BankAccount[maxSessionKey];
    String generateSessionKey(String id, String password){
        BankAccount account = accountInfo.get(id);
        if(account == null || !account.authenticate(password)){
            return null;
        }
        String sessionkey = randomUniqueStringGen();
        sessionKeyArr[numSessionKey] = sessionkey;
        bankAccountmap[numSessionKey] = account;
        numSessionKey += 1;
        return sessionkey;
    }
    BankAccount getAccount(String sessionkey){
        for(int i = 0 ;i < numSessionKey; i++){
            if(sessionKeyArr[i] != null && sessionKeyArr[i].equals(sessionkey)){
                return bankAccountmap[i];
            }
        }
        return null;
    }

    boolean deposit(String sessionkey, int amount) {
        //TODO: Problem 1.2
        BankAccount bankAccount = getAccount(sessionkey);
        if (bankAccount != null){
            bankAccount.deposit(amount);
            return true;
        }
        return false;
    }

    boolean withdraw(String sessionkey, int amount) {
        //TODO: Problem 1.2
        BankAccount bankAccount = getAccount(sessionkey);
        if (bankAccount != null){
            return bankAccount.withdraw(amount);
        }
        return false;
    }

    boolean transfer(String sessionkey, String targetId, int amount) {
        //TODO: Problem 1.2
        BankAccount bankAccount = getAccount(sessionkey);
        if (bankAccount != null && accountInfo.containsKey(targetId)){
            if (bankAccount.send(amount)){
                accountInfo.get(targetId).receive(amount);
                return true;
            }
        }

        return false;
    }

    private BankSecretKey secretKey;
    public BankPublicKey getPublicKey(){
        BankKeyPair keypair = Encryptor.publicKeyGen();
        secretKey = keypair.deckey;
        return keypair.enckey;
    }

    final static int maxHandShake = 10000;
    private HashMap<String , BankSymmetricKey> idKeyPair = new HashMap<>(maxHandShake);
    public void fetchSymKey(Encrypted<BankSymmetricKey> encryptedKey, String AppId){
        //TODO: Problem 1.3
        if (encryptedKey == null){
            return;
        }
        BankSymmetricKey bankSymmetricKey = encryptedKey.decrypt(secretKey);
        if (bankSymmetricKey == null){
            return;
        }
        idKeyPair.put(AppId, bankSymmetricKey);
    }

    public Encrypted<Boolean> processRequest(Encrypted<Message> messageEnc, String AppId){
        //TODO: Problem 1.3
        if (idKeyPair.containsKey(AppId) && idKeyPair.get(AppId) != null){
            BankSymmetricKey bankSymmetricKey = idKeyPair.get(AppId);

            if (messageEnc == null || messageEnc.decrypt(bankSymmetricKey) == null){
                return null;
            }

            Message message = messageEnc.decrypt(bankSymmetricKey);
            String request = message.getRequestType();
            String id = message.getId();
            String password = message.getPassword();
            int amount = message.getAmount();

            boolean result;

            if (request.equals("withdraw")){
                result = withdraw(id, password, amount);
                return new Encrypted(result, bankSymmetricKey);
            } else if (request.equals("deposit")){
                result =  deposit(id, password, amount);
                return new Encrypted(result, bankSymmetricKey);
            } else {
                return null;
            }

        }

        return null;
    }


}