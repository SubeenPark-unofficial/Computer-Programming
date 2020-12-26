public class FractionalNumberCalculator {
    public FractionalNumber operand1, operand2;
    public char operator;

    FractionalNumberCalculator(String equation){
        String[] eq_split = equation.split(" ");
        this.operand1 = new FractionalNumber(eq_split[0]);
        //System.out.println(this.operand1.formatAsAnswer()); %%%
        //this.operand1.printAsString();
        this.operand2 = new FractionalNumber(eq_split[2]);
        //this.operand2.printAsString();
        //System.out.println(this.operand2.formatAsAnswer()); %%%
        this.operator = eq_split[1].charAt(0);


    }

    private FractionalNumber evaluateExpression(){
        int num_res = 1;
        int denom_res = 1;
        int n1, n2, d1, d2;
        n1 = this.operand1.getNumerator();
        d1 = this.operand1.getDenominator();
        n2 = this.operand2.getNumerator();
        d2 = this.operand2.getDenominator();


        if (this.operator == '+'){
            num_res = n1*d2 + n2*d1;
            denom_res = d1*d2;
        }
        else if (this.operator == '-'){
            num_res = n1*d2 - n2*d1;
            denom_res = d1*d2;
        }
        else if (this.operator == '*'){
            num_res = n1*n2;
            denom_res = d1*d2;
        }
        else if (this.operator == '/'){
            num_res = n1*d2;
            denom_res = d1*n2;
        }


        return new FractionalNumber(num_res, denom_res);
    }

    public static void printCalculationResult(String equation) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.
        FractionalNumberCalculator calc = new FractionalNumberCalculator(equation);
        FractionalNumber pre_answer = calc.evaluateExpression();
        //pre_answer.printAsString();
        System.out.println(pre_answer.formatAsAnswer());






    }
}

class FractionalNumber {
    private int numerator;
    private int denominator;
    private int sign;

    public int getNumerator(){
        return this.numerator;
    }

    public int getDenominator(){
        return this.denominator;
    }



    FractionalNumber(int numerator, int denominator){
        this.numerator = numerator;
        this.denominator = denominator;
        this.sign =  (numerator*denominator < 0) ? -1:1;
    }

    FractionalNumber(String str){
        if (str.contains("/")){
            this.numerator = Integer.parseInt(str.split("/")[0]);
            this.denominator = Integer.parseInt(str.split("/")[1]);
            this.sign =  (this.numerator*this.denominator < 0) ? -1:1;
        }
        else {
            this.numerator = Integer.parseInt(str);
            this.denominator = 1;
            this.sign =  (this.numerator*this.denominator < 0) ? -1:1;
        }
    }

    public void printAsString(){
        System.out.println(this.getNumerator()+"/"+this.getDenominator());

    }

    public String formatAsAnswer(){
        if (this.numerator == 0){
            return "0";
        } else {
            this.reduceFrac();
            if (this.denominator == 1){
                return this.numerator+" ";
            } else {
                return this.numerator + "/" + this.denominator;
            }
        }

    }


    // Reduce Fraction and make denominator positive
    public void reduceFrac(){

        int num_abs = Math.abs(numerator);
        int denom_abs = Math.abs(denominator);

        int gcd = findGCD(num_abs, denom_abs);

        this.numerator = this.sign*num_abs/gcd;
        this.denominator = denom_abs/gcd;

    }

    private static int findGCD(int n1, int n2){
        int gcd, r;
        gcd = Math.min(n1, n2);
        r = Math.max(n1, n2)%gcd;

        while (r != 0){
            int temp1 = gcd;
            int temp2 = r;
            gcd = temp2;
            r = temp1%temp2;
        }
        return gcd;

    }


}





