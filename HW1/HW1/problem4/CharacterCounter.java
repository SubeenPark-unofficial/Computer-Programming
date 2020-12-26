public class CharacterCounter {
    public static void countCharacter(String str) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.
        int[] cnt_arr = new int[26];
        int ascii_a = 'a';
        for (int i = 0; i < cnt_arr.length; i++) {
            cnt_arr[i] = cntChar(str, (char) (ascii_a + i));
        }

        for (int i = 0; i < cnt_arr.length; i++) {
            if (cnt_arr[i] != 0) {
                printCount((char) (ascii_a + i), cnt_arr[i]);
            }
        }

    }

    private static void printCount(char character, int count) {
        System.out.printf("%c: %d times\n", character, count);
    }

    public static int cntChar(String str, char alphabet) {
        int cnt = 0;
        if (str.length() == 1 && str.charAt(0) == alphabet) {
            cnt = 1;
        } else if (str.length() == 1 && str.charAt(0) != alphabet) {
            cnt = 0;
        } else {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == alphabet) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

}
