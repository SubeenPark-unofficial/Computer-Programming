package bank;

import security.Encryptor;
import security.Encrypted;
import security.Message;
import security.key.BankPublicKey;
import security.key.BankSymmetricKey;

import java.util.concurrent.BlockingDeque;

public class MobileApp {

    private String randomUniqueStringGen(){
        return Encryptor.randomUniqueStringGen();
    }
    private final String AppId = randomUniqueStringGen();
    public String getAppId() {
        return AppId;
    }
    private BankSymmetricKey bankSymmetricKey;

    String id, password;
    public MobileApp(String id, String password){
        this.id = id;
        this.password = password;
    }

    public Encrypted<BankSymmetricKey> sendSymKey(BankPublicKey publickey){
        //TODO: Problem 1.3
        bankSymmetricKey = new BankSymmetricKey(randomUniqueStringGen());
        Encrypted<BankSymmetricKey> bankSymmetricKeyEncrypted = new Encrypted<BankSymmetricKey>(bankSymmetricKey, publickey);
        return bankSymmetricKeyEncrypted;
    }

    public Encrypted<Message> deposit(int amount){
        //TODO: Problem 1.3
        Message message = new Message("deposit", id, password, amount);
        Encrypted<Message> messageEncrypted = new Encrypted<Message>(message, bankSymmetricKey);
        return messageEncrypted;
    }

    public Encrypted<Message> withdraw(int amount){
        //TODO: Problem 1.3
        Message message = new Message("withdraw", id, password, amount);
        Encrypted<Message> messageEncrypted = new Encrypted<Message>(message, bankSymmetricKey);
        return messageEncrypted;
    }

    public boolean processResponse(Encrypted<Boolean> obj){
        //TODO: Problem 1.3

        if (obj == null){
            return false;
        }

        Boolean objDecrypted = obj.decrypt(bankSymmetricKey);
        if (objDecrypted != null){
            return objDecrypted;
        }

        return false;
    }

}

