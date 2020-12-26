public class IncreasingString {
    public static void printLongestIncreasingSubstringLength(String inputString) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.
        if (inputString.length() > 1){
            int maxLen = 1;
            for (int i = 0; i < inputString.length()-1; i++){
                for (int j = 1; j < inputString.length()-1-i; j++){
                    if (inputString.charAt(i+j-1) >= inputString.charAt(i+j)){
                        break;
                    }
                    else{
                        if (j+1 > maxLen){
                            maxLen = j+1;
                        }
                    }
                }
            }
            System.out.println(maxLen);
        }
        else {
            System.out.println(1);
        }


    }
}
