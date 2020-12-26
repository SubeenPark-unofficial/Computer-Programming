package hand.agent;

public class Seller extends Agent {


    //    this.priceLimit = priceLimit;
//    expectedPrice = priceLimit;
//    adjustment = 10;
//    hadTransaction = false;

    public Seller(double minimumPrice) {
        super(minimumPrice);
    }

    @Override
    public boolean willTransact(double price) {
        // TODO sub-problem 1
        if (!hadTransaction &&  price >= expectedPrice){
            return true;
        }
        return false;
    }

    @Override
    public void reflect() {
        // TODO sub-problem 1
        if (hadTransaction){
            expectedPrice += adjustment;
        }
        else {
            expectedPrice -= adjustment;
        }

        if (expectedPrice <= priceLimit){
            expectedPrice = priceLimit;
        }

        hadTransaction = false;
        return;
    }
}
