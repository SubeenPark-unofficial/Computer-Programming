public class MatrixFlip {
    public static void printFlippedMatrix(char[][] matrix) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.
        int n = matrix.length;
        int m = matrix[0].length;

        char[][] flipped = new char[n][m];

        if (n == 1 && m == 1){
            flipped[0][0] = matrix[0][0];
        }
        else if (n == 1) {
            for (int j = 0; j < m; j++) {
                flipped[0][m - 1 - j] = matrix[0][j];
            }
        }
        else if (m == 1){
            for (int i = 0; i < n; i++){
                flipped[n-1-i][0] = matrix[i][0];
            }
        }
        else {
            for (int i = 0; i < n; i++){
                for (int j = 0; j < m; j++){
                    flipped[n-1-i][m-1-j] = matrix[i][j];
                }
            }
        }

        for (int i = 0; i < n; i++){
            for (int j = 0; j <m; j++){
                System.out.print(flipped[i][j]);
            }
            System.out.println();
        }
    }
}