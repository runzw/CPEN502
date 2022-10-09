import java.io.File;

public class NeuralNetRunner {
    public static final double LEARNING_RATE = 0.2;
    public static final double MOMENTUM = 0.0;
    public static final double LOSS = 0.05;

    public static final double [][] X = { {0, 0}, {1, 0}, {0, 1}, {1, 1} };

    public static void main(String[] args){
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

        int epoch = 0;

        // Initialize a new neural net
        NeuralNet nn = new NeuralNet(inputNum, hiddenNum, LEARNING_RATE, MOMENTUM, 0, 1, bipolar);

        // Initialize weights
        nn.initializeWeights();

        // Train the neural net
        double totalLoss;
        do{
            System.out.println("=================================================================");
            System.out.println("Epoch: "+epoch);
            totalLoss = 0;
            System.out.println("Start training...");
            for(double [] data: X){
                System.out.println("Training with " + "{" + data[0] + "," + data[1] + "}:");
                nn.train(data, (int)data[0] ^ (int)data[1]);
                double singleLoss = nn.outputFor(data) - ((int)data[0] ^ (int)data[1]);
                System.out.println("loss: " + singleLoss);
                totalLoss += 0.5 * Math.pow(singleLoss, 2);
            }
            epoch++;
            System.out.println("Average Loss: " + totalLoss);
        }while(totalLoss > LOSS);
        System.out.println("Training finished!");
        System.out.println("Total number of epochs:" + epoch);

        // Save the neural net
//        System.out.println("Saving the model...");
//        nn.save(new File("model.json"));
//        System.out.println("Model saved!");
    }
}
