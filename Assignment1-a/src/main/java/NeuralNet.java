import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class NeuralNet implements NeuralNetInterface{
    private int argNumInputs;
    private int argNumHidden;
    private static final int ARG_NUM_OUTPUTS = 1;
    private double argLearningRate;
    private double argMomentumTerm;
    private double argA;
    private double argB;
    private boolean bipolar;
    private Matrix weightIH;
    private Matrix weightHO;

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
        File modelFile = new File(argFileName);
        Scanner modelReader = new Scanner(modelFile);
        String model = modelReader.nextLine();
        modelReader.close();

        Gson gson = new Gson();
        NeuralNet loadedNN = gson.fromJson(model, NeuralNet.class);
        if(loadedNN.getArgNumInputs() != this.argNumInputs ||
            loadedNN.getArgNumHidden() != this.argNumHidden ||
            loadedNN.getArgLearningRate() != this.argLearningRate ||
            loadedNN.getArgMomentumTerm() != this.argMomentumTerm ||
            loadedNN.getArgA() != this.argA ||
            loadedNN.getArgB() != this.argB ||
            loadedNN.isBipolar() != this.bipolar
        ){
            throw new IOException("Model does not match current configuration");
        }

        this.weightIH = loadedNN.getWeightIH();
        this.weightHO = loadedNN.getWeightHO();
        return;
    }

    @Override
    public void save(File argFile) {
        Gson gson = new Gson();
        String jsonModel = gson.toJson(this);
        System.out.println("Model: " + jsonModel);
        return;
    }

    public int getArgNumInputs() {
        return argNumInputs;
    }

    public int getArgNumHidden() {
        return argNumHidden;
    }

    public double getArgLearningRate() {
        return argLearningRate;
    }

    public double getArgMomentumTerm() {
        return argMomentumTerm;
    }

    public double getArgA() {
        return argA;
    }

    public double getArgB() {
        return argB;
    }

    public Matrix getWeightIH() {
        return weightIH;
    }

    public Matrix getWeightHO() {
        return weightHO;
    }

    public boolean isBipolar() {
        return bipolar;
    }

    public static int getArgNumOutputs() {
        return ARG_NUM_OUTPUTS;
    }
}
