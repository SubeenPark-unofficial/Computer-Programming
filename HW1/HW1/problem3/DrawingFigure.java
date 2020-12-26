public class DrawingFigure {
    public static void drawFigure(int n) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.
        String[] fig_str = {"////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\",
                            "////////////////********\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\",
                            "////////////****************\\\\\\\\\\\\\\\\\\\\\\\\",
                            "////////************************\\\\\\\\\\\\\\\\",
                            "////********************************\\\\\\\\",
                            "****************************************"};

        if (n == 1){
            System.out.println(fig_str[0]);
        }
        else{
            for (int i = 0; i < n; i++){
                System.out.println(fig_str[i]);
            }
        }







    }
}
