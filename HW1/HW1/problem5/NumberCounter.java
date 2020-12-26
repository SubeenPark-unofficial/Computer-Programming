public class NumberCounter {
    public static void countNumbers(String str0, String str1, String str2) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.
        int[] cnt_nums = new int[10];
        String res_num;
        res_num = mul3str(str0, str1, str2);
        for (int i = 0; i < cnt_nums.length; i++){
            int cnt = 0;
            for (int j = 0; j < res_num.length(); j++){
                if (chr2int(res_num.charAt(j)) == i){
                    cnt ++;
                }
            }
            cnt_nums[i] = cnt;
        }
        for (int i = 0; i < cnt_nums.length; i++){
            if (cnt_nums[i] > 0){
                printNumberCount(i, cnt_nums[i]);
            }
        }
    }

    private static void printNumberCount(int number, int count) {
        System.out.printf("%d: %d times\n", number, count);
    }

    private static String mul3str(String str0, String str1, String str2){
        return ""+str2int(str0)*str2int(str1)*str2int(str2);
    }

    private static int str2int(String str){
        int num = 0;
        if (str.length() == 1){
            num = chr2int(str.charAt(0));
        }
        else {
            for (int i = 0; i < str.length(); i++){
                num = num + chr2int(str.charAt(i))*power(10, str.length()-1-i);
            }
        }
        return num;
    }

    private static int chr2int(char ch){
        int ascii0 = '0';
        return (int)ch - ascii0;
    }

    private static int power(int base, int exponent){
        if (exponent == 0){
            return 1;
        }
        else if (exponent == 1){
            return base;
        }
        else{
            int res = 1;
            for (int i = 0; i < exponent; i++){
                res *= base;
            }
            return res;
        }
    }

}
