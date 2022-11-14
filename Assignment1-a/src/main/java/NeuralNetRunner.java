import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;


public class NeuralNetRunner {
    public static final double LEARNING_RATE = 0.2;
    public static final double MOMENTUM = 0.9;
    public static final double LOSS = 0.05;
    public static final double TRIALS = 10;

    //public static final double [][] X = { {0, 0}, {1, 0}, {0, 1}, {1, 1} };
    public static final double [][] X = { {-1, -1}, {-1, 1}, {1, -1}, {1, 1} };

    public static void main(String[] args) throws IOException, PythonExecutionException {
        if(args.length != 3){
            System.out.print("Three arguments required!");
            return;
        }

        int inputNum;
        int hiddenNum;
        boolean bipolar;
        try{
            inputNum = Integer.parseInt(args[0]);
            hiddenNum = Integer.parseInt(args[1]);
            bipolar = Boolean.parseBoolean(args[2]);
        }catch(Exception e){
            System.out.print("Arguments not in the right type!");
            return;
        }

        System.out.println("Input layer number of neurons:" + inputNum);
        System.out.println("Hidden layer number of neurons:" + hiddenNum);

        // Initialize a new neural net
        NeuralNet nn = new NeuralNet(inputNum, hiddenNum, LEARNING_RATE, MOMENTUM, 0, 1, bipolar);

        // Train the neural net
        int trial = 0;
        int sumEpoch = 0;
        int trialToPlot = 9;
        while(trial < TRIALS){
            int epoch = 0;
            Map<Integer, Double> map = new HashMap<>();
            double totalLoss;
            // Initialize weights
            nn.initializeWeights();
            System.out.println("=================================================================");
            System.out.println("Trail: "+trial);
            do{
                totalLoss = 0;
                for(double [] data: X){
                    double singleLoss = nn.train(data, /*(int)data[0] ^ (int)data[1]*/ data[0] == data[1] ? -1 : 1);
                    totalLoss += 0.5 * Math.pow(singleLoss, 2);
                }
                map.put(epoch, totalLoss);
                epoch++;
            }while(totalLoss > LOSS);
            trial++;
            sumEpoch += epoch;
            System.out.println("Epochs: "+epoch);
            System.out.println("Final loss: "+totalLoss);
            if(trial == trialToPlot){
                Plot plt = Plot.create();
                List<Integer> epochs = new ArrayList<>();
                List<Double> losses = new ArrayList<>();
                for(int e : map.keySet()){
                    epochs.add(e);
                    losses.add(map.get(e));
                }
                plt.plot().add(epochs, losses);
                plt.xlabel("Epoch");
                plt.ylabel("Loss");
                plt.title("Total Error");
                plt.show();
            }
        }
        System.out.println("=================================================================");
        System.out.println("Training finished!");
        System.out.println("Average Epoch: " + (double)sumEpoch/trial);
    }
}
