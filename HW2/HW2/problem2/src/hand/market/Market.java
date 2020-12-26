package hand.market;

import hand.agent.Buyer;
import hand.agent.Seller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class Pair<K,V> {
    public K key;
    public V value;
    Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}

public class Market {
    public ArrayList<Buyer> buyers;
    public ArrayList<Seller> sellers;


    public Market(int nb, ArrayList<Double> fb, int ns, ArrayList<Double> fs) {
        buyers = createBuyers(nb, fb);
        sellers = createSellers(ns, fs);
    }

    private double calculatePolynomial(double x, ArrayList<Double> f){
        double res = 0;
        int l = f.size(); // l = n + 1
        for (int i = 0; i < l; i++){
            res += f.get(l-i-1)*Math.pow(x, i);
        }
        return res;
    }
    
    private ArrayList<Buyer> createBuyers(int n, ArrayList<Double> f) {
        // TODO sub-problem 3
        ArrayList<Buyer> buyerArrayList = new ArrayList<>();
        for (int i = 0; i < n; i++){
            buyerArrayList.add(i, new Buyer(calculatePolynomial((double) (i+1)/n, f)));
        }
        return buyerArrayList;
    }

    private ArrayList<Seller> createSellers(int n, ArrayList<Double> f) {
        // TODO sub-problem 3
        ArrayList<Seller> sellerArrayList = new ArrayList<>();
        for (int i = 0; i < n; i++){
            sellerArrayList.add(i, new Seller(calculatePolynomial((double) (i+1)/n, f)));
        }

        return sellerArrayList;
    }

    private ArrayList<Pair<Seller, Buyer>> matchedPairs(int day, int round) {
        ArrayList<Seller> shuffledSellers = new ArrayList<>(sellers);
        ArrayList<Buyer> shuffledBuyers = new ArrayList<>(buyers);
        Collections.shuffle(shuffledSellers, new Random(71 * day + 43 * round + 7));
        Collections.shuffle(shuffledBuyers, new Random(67 * day + 29 * round + 11));
        ArrayList<Pair<Seller, Buyer>> pairs = new ArrayList<>();
        for (int i = 0; i < shuffledBuyers.size(); i++) {
            if (i < shuffledSellers.size()) {
                pairs.add(new Pair<>(shuffledSellers.get(i), shuffledBuyers.get(i)));
            }
        }
        return pairs;
    }

    public double simulate() {
        // TODO sub-problem 2 and 3

        ArrayList<Double> prices = new ArrayList<Double>();
        double price_sum = 0;
        int num_transaction = 0;

        for (int day = 1; day <= 1000; day++) { // do not change this line
            for (int round = 1; round <= 10; round++) { // do not change this line
                ArrayList<Pair<Seller, Buyer>> pairs = matchedPairs(day, round); // do not change this line

                for (int i = 0; i < pairs.size();i++){

                    // get agents
                    Seller seller = pairs.get(i).key;
                    Buyer buyer = pairs.get(i).value;

                    // price setup
                    double price = seller.getExpectedPrice();
                    if (buyer.willTransact(price) && seller.willTransact(price)){
                        buyer.makeTransaction();
                        seller.makeTransaction();
                        // Add price to list only when transaction is made
                        if (day == 1000){
                            num_transaction++;
                            price_sum += price;
                        }
                    }
                }
            }

            // Daily Reflection

            for (int i = 0; i < buyers.size(); i++){
                buyers.get(i).reflect();
            }

            for (int i = 0; i < sellers.size(); i++){
                sellers.get(i).reflect();
            }

        }

        return price_sum/num_transaction;
    }
}
