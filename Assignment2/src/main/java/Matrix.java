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

    public Matrix(int rows, int cols, double value) {
        m = new double[rows][cols];
        this.rows = rows;
        this.cols = cols;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                m[i][j] = value;
            }
        }
    }

    public Matrix(int rows, int cols, double min, double max) {
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
        if(a.cols != b.rows) return null;
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

    public void multiply(double a) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m[i][j] *= a;
            }
        }
    }

    public void multiply(Matrix a) {
        if(a.cols != this.cols) return;
        if(a.rows != this.rows) return;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m[i][j] *= a.m[i][j];
            }
        }
    }

    public void msigmoid(double a, double b) {
        for(int i=0;i<rows;i++)
        {
            for(int j=0;j<cols;j++)
                this.m[i][j] = NeuralNetInterface.customSigmoid(this.m[i][j], a, b);
        }
    }

    public Matrix dsigmoid(double a, double b){
        Matrix res = new Matrix(rows, cols);
        for(int i=0;i<rows;i++)
        {
            for(int j=0;j<cols;j++)
                res.m[i][j] = m[i][j] * (1 - m[i][j]);
        }
        return res;
    }

    public double bipolarSigmoid(double x) {
        return 2 / (1 + Math.exp(-x)) - 1;
    }

    public void bipolarSigmoid() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.m[i][j] = bipolarSigmoid(this.m[i][j]);
            }
        }
    }

    public Matrix dbipolarSigmoid(){
        Matrix res = new Matrix(rows, cols);
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                res.m[i][j] = (m[i][j] + 1) * (1 - m[i][j]) / 2;
            }
        }
        return res;

    }

    public static Matrix transpose(Matrix a) {
        Matrix res = new Matrix(a.cols, a.rows);
        for (int i = 0; i < a.rows; i++) {
            for (int j = 0; j < a.cols; j++) {
                res.m[j][i] = a.m[i][j];
            }
        }
        return res;
    }

    public static Matrix parseArray(double[] array){
        Matrix res = new Matrix(array.length, 1);
        for (int i = 0; i < res.rows; i++) {
            res.m[i][0] = array[i];
        }
        return res;
    }

    public static double[] toArray(Matrix mtx){
        double[] res = new double[mtx.cols];
        for (int i = 0; i < mtx.rows; i++) {
            res[i] = mtx.m[i][0];
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
