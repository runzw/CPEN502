import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import sun.rmi.runtime.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NeuralNetRunner {
    public static final double LEARNING_RATE = 0.01;
    public static final double MOMENTUM = 0.9;

    public static void main(String[] args) throws IOException, PythonExecutionException {
        if(args.length != 2){
            System.out.print("Two arguments required!");
            return;
        }

        int inputNum;
        int hiddenNum;
        try{
            inputNum = Integer.parseInt(args[0]);
            hiddenNum = Integer.parseInt(args[1]);
        }catch(Exception e){
            System.out.print("Arguments not in the right type!");
            return;
        }

        System.out.println("Input layer number of neurons:" + inputNum);
        System.out.println("Hidden layer number of neurons:" + hiddenNum);

        // Initialize a new neural net
        NeuralNet nn = new NeuralNet(inputNum, hiddenNum, LEARNING_RATE, MOMENTUM, 0, 1, false);

        // Initialize the LUT
        RLRobotLUT lut = new RLRobotLUT(5, 5, 5, 5, 5);
        lut.load("E:/CPEN502/Assignment2/figures/RLRobot.txt");
        lut.normalize();

        // Train the neural net
        Map<Integer, Double> map = new HashMap<>();

        double totalLoss;
        //nn.zeroWeights();
        // Initialize weights
        nn.initializeWeights();

        List<Integer> epochs = new ArrayList<>();
        List<Double> losses = new ArrayList<>();
        int epoch = 0;
        while (true) {
            totalLoss = 0;
            for (int a = 0; a < 5; a++) {
                for (int b = 0; b < 5; b++) {
                    for (int c = 0; c < 5; c++) {
                        for (int d = 0; d < 5; d++) {
                            for (int e = 0; e < 5; e++) {
                                double[] X = normalizeX(a,b,c,d,e);
                                double singleLoss = nn.train(X, lut.outputFor(X));
                                totalLoss += Math.pow(singleLoss, 2);
                            }
                        }
                    }
                }
            }
            epoch++;
            epochs.add(epoch);
            losses.add(Math.pow(totalLoss/3125, 0.5));
            System.out.print(epoch + ": ");
            System.out.println(Math.pow(totalLoss/3125, 0.5));
            // Change here if you want to evaluate on a mini batch
            if (epoch > 1000) {
                try {
                    nn.save(new File("E:/CPEN502/Assignment2/figures/nn.txt"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        BufferedWriter outputWriter = null;
        outputWriter = new BufferedWriter(new FileWriter("E:/CPEN502/Assignment2/figures/nn_para_n_15.txt"));
        outputWriter.write("n");
        outputWriter.newLine();
        outputWriter.write("15");
        outputWriter.newLine();
        for(int i = 0; i < epochs.size(); i++){
            outputWriter.write(epochs.get(i)+":"+losses.get(i));
            outputWriter.newLine();
        }

        outputWriter.flush();
        outputWriter.close();

        System.out.println("=================================================================");
        System.out.println("Training finished!");
    }

    private static double[] normalizeX(int energy1, int dist1, int energy2, int dist2, int action){
        Map<Integer, Double> normalizedE = new HashMap<Integer, Double>(){{
            put(0, 0.0);
            put(1, 0.2);
            put(2, 0.4);
            put(3, 0.6);
            put(4, 1.0);
        }};

        Map<Integer, Double> normalizedD = new HashMap<Integer, Double>(){{
            put(0, 0.0);
            put(1, 0.05);
            put(2, 0.25);
            put(3, 0.5);
            put(4, 0.75);
        }};

        Map<Integer, Double> normalizedA = new HashMap<Integer, Double>(){{
            put(0, 0.0);
            put(1, 0.25);
            put(2, 0.5);
            put(3, 0.75);
            put(4, 1.0);
        }};

        return new double[]{
                normalizedE.get(energy1),
                normalizedE.get(dist1),
                normalizedE.get(energy2),
                normalizedE.get(dist2),
                normalizedE.get(action)
        };
    }
}