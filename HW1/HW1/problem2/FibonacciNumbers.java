public class FibonacciNumbers {
    public static void printFibonacciNumbers(int n) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.

        if (n >= 2){
            int[] fib_arr = new int[n];

            fib_arr[0] = 0;
            fib_arr[1] = 1;

            if (n >= 3){
                for (int i = 2; i < n; i++){
                    fib_arr[i] = fib_arr[i-1] + fib_arr[i-2];
                }
            }

            for (int i = 0; i < n; i++){
                System.out.print(fib_arr[i] + " ");
            }
        }
        else{
            System.out.println(0);
        }



    }
}
