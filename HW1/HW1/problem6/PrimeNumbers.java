public class PrimeNumbers {
    public static void printPrimeNumbers(int n) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.
        int idx = 2;
        int cnt = 0;

        while (cnt < n){
            if (isPrime(idx)){
                System.out.print(idx+" ");
                cnt++;
            }
            idx++;
        }

    }


    private static boolean isPrime(int num){
        boolean isP = true;
        if (num == 1){
            isP = false;
        }
        else{
            for (int i = 2; i < num; i++){
                if (num%i == 0){
                    isP = false;
                    break;
                }
            }
        }
        return isP;
    }
}


