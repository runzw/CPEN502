import java.io.File;
import java.io.IOException;

public class NeuralNet implements NeuralNetInterface{
    int argNumInputs;
    int argNumHidden;
    static final int ARG_NUM_OUTPUTS = 1;
    double argLearningRate;
    double argMomentumTerm;
    double argA;
    double argB;
    boolean bipolar;
    Matrix weightIH;
    Matrix weightHO;

    public NeuralNet(
        int argNumInputs,
        int argNumHidden,
        double argLearningRate,
        double argMomentumTerm,
        double argA,
        double argB,
        boolean bipolar
    ){
        this.argNumInputs = argNumInputs;
        this.argNumHidden = argNumHidden;
        this.argLearningRate = argLearningRate;
        this.argMomentumTerm = argMomentumTerm;
        this.argA = argA;
        this.argB = argB;
    }

    @Override
    public void zeroWeights() {
        this.weightIH = new Matrix(argNumInputs, argNumHidden);
        this.weightHO = new Matrix(argNumHidden, ARG_NUM_OUTPUTS);
        return;
    }

    @Override
    public void initializeWeights() {
        this.weightIH = new Matrix(argNumInputs, argNumHidden, -0.5, 0.5);
        this.weightHO = new Matrix(argNumHidden, ARG_NUM_OUTPUTS, -0.5, 0.5);
        return;
    }

    @Override
    public double outputFor(double[] X) {
        // Input to hidden
        Matrix dataI = Matrix.parseArray(X);
        Matrix dataH = Matrix.multiply(dataI, weightIH);

        // Activation function input -> hidden
        dataH.msigmoid(this.argA, this.argB);

        // Hidden to output
        Matrix dataO = Matrix.multiply(dataH, weightHO);
        dataO.msigmoid(this.argA, this.argB);
        double output = Matrix.toArray(dataO)[0];

        return output;
    }

    @Override
    public double train(double[] X, double argValue) {
        // Calculate loss
        double expected = argValue;
        double actual = outputFor(X);
        double loss = actual - expected;

        // TODO: backward propogation


        return loss;
    }

    @Override
    public void load(String argFileName) throws IOException {
        return;
    }

    @Override
    public void save(File argFile) {
        return;
    }
}
