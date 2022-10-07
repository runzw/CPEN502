public class Matrix {
    double[][] m;
    int rows, cols;

    public Matrix(int rows,int cols) {
        m = new double[rows][cols];
        this.rows = rows;
        this.cols = cols;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                m[i][j] = 0;
            }
        }
    }

    public Matrix(int rows,int cols, double min, double max) {
        m = new double[rows][cols];
        double range = max - min;
        this.rows = rows;
        this.cols = cols;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                m[i][j] = Math.random() * range + min;
            }
        }
    }

    public void add(double c) {
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                this.m[i][j] += c;
            }
        }
    }

    public void add(Matrix mtx) {
        if(cols!=mtx.cols || rows!=mtx.rows) {
            System.out.println("Shape Mismatch!");
            return;
        }

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                this.m[i][j] += mtx.m[i][j];
            }
        }
    }

    public static Matrix subtract(Matrix a, Matrix b) {
        Matrix res = new Matrix(a.rows, a.cols);
        for(int i = 0; i < a.rows; i++) {
            for(int j = 0; j < a.cols; j++) {
                res.m[i][j] = a.m[i][j] - b.m[i][j];
            }
        }
        return res;
    }

    public static Matrix multiply(Matrix a, Matrix b) {
        Matrix res = new Matrix(a.rows,b.cols);
        for(int i = 0; i < res.rows; i++){
            for(int j = 0; j < res.cols; j++){
                double ele = 0;
                for(int k=0;k<a.cols;k++){
                    ele += a.m[i][k] * b.m[k][j];
                }
                res.m[i][j] = ele;
            }
        }
        return res;
    }

    public void msigmoid(double a, double b) {
        for(int i=0;i<rows;i++)
        {
            for(int j=0;j<cols;j++)
                this.m[i][j] = NeuralNetInterface.customSigmoid(this.m[i][j], a, b);
        }
    }

    public static Matrix parseArray(double[] array){
        Matrix res = new Matrix(1, array.length);
        for(int i = 0; i < res.cols; i++){
            res.m[0][i] = array[i];
        }
        return res;
    }

    public static double[] toArray(Matrix mtx){
        double[] res = new double[mtx.cols];
        for(int i = 0; i < mtx.cols; i++){
            res[i] = mtx.m[0][i];
        }
        return res;
    }

    public static void print(Matrix p) {
        for(int i = 0; i < p.rows; i++){
            for(int j = 0; j < p.cols; j++){
                System.out.print(p.m[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}
