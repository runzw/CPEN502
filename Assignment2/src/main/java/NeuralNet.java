import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
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
    private Matrix biasH;
    private Matrix biasO;
    private Matrix d_weightIH;
    private Matrix d_weightHO;
    private Matrix d_biasH;
    private Matrix d_biasO;


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
        this.bipolar = bipolar;
    }

    @Override
    public void zeroWeights() {
        this.weightIH = new Matrix(argNumHidden, argNumInputs);
        this.weightHO = new Matrix(ARG_NUM_OUTPUTS, argNumHidden);

        this.biasH = new Matrix(argNumHidden, 1);
        this.biasO = new Matrix(ARG_NUM_OUTPUTS, 1);

        this.d_weightIH = new Matrix(argNumHidden, argNumInputs);
        this.d_biasH = new Matrix(argNumHidden, 1);
        this.d_weightHO = new Matrix(ARG_NUM_OUTPUTS, argNumHidden);
        this.d_biasO = new Matrix(ARG_NUM_OUTPUTS, 1);
        return;
    }

    @Override
    public void initializeWeights() {
        this.weightIH = new Matrix(argNumHidden, argNumInputs, -0.5, 0.5);
        this.weightHO = new Matrix(ARG_NUM_OUTPUTS, argNumHidden, -0.5, 0.5);

        this.biasH = new Matrix(argNumHidden, 1, -0.5, 0.5);
        this.biasO = new Matrix(ARG_NUM_OUTPUTS, 1, -0.5, 0.5);

        this.d_weightIH = new Matrix(argNumHidden, argNumInputs);
        this.d_biasH = new Matrix(argNumHidden, 1);
        this.d_weightHO = new Matrix(ARG_NUM_OUTPUTS, argNumHidden);
        this.d_biasO = new Matrix(ARG_NUM_OUTPUTS, 1);

        return;
    }

    @Override
    public double outputFor(double[] X) {
        // Input to hidden
        Matrix dataI = Matrix.parseArray(X);
        Matrix dataH = Matrix.multiply(weightIH, dataI);
        dataH.add(biasH);

        // Activation function input -> hidden
        if (this.bipolar) {
            dataH.bipolarSigmoid();
        } else {
            dataH.msigmoid(this.argA, this.argB);
        }

        // Hidden to output
        Matrix dataO = Matrix.multiply(weightHO, dataH);
        dataO.add(biasO);
        if (this.bipolar) {
            dataO.bipolarSigmoid();
        } else {
            dataO.msigmoid(this.argA, this.argB);
        }

        double output = Matrix.toArray(dataO)[0];

        return output;
    }

    @Override
    public double train(double[] X, double argValue) {
        // Input to hidden
        Matrix dataI = Matrix.parseArray(X);
        Matrix dataH = Matrix.multiply(weightIH, dataI);
        dataH.add(biasH);

        // Activation function input -> hidden
        if (this.bipolar) {
            dataH.bipolarSigmoid();
        } else {
            dataH.msigmoid(this.argA, this.argB);
        }

        // Hidden to output
        Matrix dataO = Matrix.multiply(weightHO, dataH);
        dataO.add(biasO);

        if (this.bipolar) {
            dataO.bipolarSigmoid();
        } else {
            dataO.msigmoid(this.argA, this.argB);
        }

        double output = Matrix.toArray(dataO)[0];

        //loss computation
        double loss = argValue - output;
        Matrix lossM = new Matrix(1,1);
        lossM.add(loss);

        // hidden to output gradient
        Matrix gradient = this.getGradient(dataO);
        gradient.multiply(lossM);
        gradient.multiply(argLearningRate);

        //momentum
        Matrix hiddenDataTranpose = Matrix.transpose(dataH);
        Matrix deltaHiddenData = Matrix.multiply(gradient, hiddenDataTranpose);

        //update hidden to output weight
        d_weightHO.multiply(argMomentumTerm);
        d_weightHO.add(deltaHiddenData);
        d_biasO.multiply(argMomentumTerm);
        d_biasO.add(gradient);

        weightHO.add(d_weightHO);
        biasO.add(d_biasO);

        Matrix h_o_weight_transpose = Matrix.transpose(weightHO);
        // h_o_w_t 4*1 lossM 1*1
        Matrix hidden_loss = Matrix.multiply(h_o_weight_transpose, lossM);

        //gradient for input to hidden
        Matrix hidden_gradient = this.getGradient(dataH);
        // hidden_gradient 4*1 hidden_loss 4*1 -> 1*1
        hidden_gradient.multiply(hidden_loss);
        hidden_gradient.multiply(argLearningRate);

        //momentum
        Matrix inputDataTranspose = Matrix.transpose(dataI);
        Matrix deltaInputData = Matrix.multiply(hidden_gradient, inputDataTranspose);

        //update input to hidden weight
        d_weightIH.multiply(argMomentumTerm);
        d_weightIH.add(deltaInputData);
        d_biasH.multiply(argMomentumTerm);
        d_biasH.add(hidden_gradient);
        weightIH.add(d_weightIH);
        biasH.add(d_biasH);

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
        this.biasH = loadedNN.getBiasH();
        this.biasO = loadedNN.getBiasO();
        this.d_weightIH = loadedNN.getD_weightIH();
        this.d_weightHO = loadedNN.getD_weightHO();
        this.d_biasO = loadedNN.getD_biasO();
        this.d_biasH = loadedNN.getD_biasH();
        return;
    }

    @Override
    public void save(File argFile) throws IOException {
        Gson gson = new Gson();
        String jsonModel = gson.toJson(this);
        if(!argFile.exists()){
            argFile.createNewFile();
        }
        FileWriter myWriter = new FileWriter(argFile.getAbsolutePath());
        myWriter.write(jsonModel);
        myWriter.close();
        return;
    }

    private Matrix getGradient(Matrix m) {
        if (this.bipolar) {
            return m.dbipolarSigmoid();
        } else {
            return m.dsigmoid(0, 1 );
        }
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

    public Matrix getBiasH() {
        return biasH;
    }

    public Matrix getBiasO() {
        return biasO;
    }

    public Matrix getD_biasH() {
        return d_biasH;
    }

    public Matrix getD_biasO() {
        return d_biasO;
    }

    public Matrix getD_weightHO() {
        return d_weightHO;
    }

    public Matrix getD_weightIH() {
        return d_weightIH;
    }

    public static int getArgNumOutputs() {
        return ARG_NUM_OUTPUTS;
    }
}
