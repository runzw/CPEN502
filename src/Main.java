public class Main {
    public static final double LEARNING_RATE = 0.2;
    public static final double MOMENTUM = 0.0;
    public static final double LOSS = 0.05;

    public static final double [][] X = { {0, 0}, {1, 0}, {0, 1}, {1, 1} };

    public static void main(String[] args){
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
            System.out.print("Two numbers required!");
            return;
        }

        int epoch = 0;
        // Initialize a lookuptable
        LookUpTable lut = new LookUpTable();

        // Train LUT first
        for(double[] x : X){
            lut.train(x, (int)x[0] ^ (int)x[1]);
        }

        // Initialize a new neural net
        NeuralNet nn = new NeuralNet(inputNum, hiddenNum, LEARNING_RATE, MOMENTUM, 0, 1, false);

        // Initialize weights
        nn.initializeWeights();

        // Train the neural net
        double totalLoss;
        do{
            totalLoss = 0;
            for(double [] data: X){
                double singleLoss = nn.train(data, lut.outputFor(data));
                totalLoss += 0.5 * Math.pow(singleLoss, 2);
            }
            epoch++;
        }while(totalLoss > LOSS);

        System.out.println(epoch);
        // Save the neural net
        // nn.save();
    }
}
