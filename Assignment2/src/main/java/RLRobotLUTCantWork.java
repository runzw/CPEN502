import java.io.*;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class RLRobotLUTCantWork implements LUTInterface {
    private int level1;
    private int level2;
    private int level3;
    private int level4;
    private int level5;
    private double[][][][][] lut;

    public RLRobotLUTCantWork(int level1, int level2, int level3, int level4, int level5) {
        this.level1 = level1;
        this.level2 = level2;
        this.level3 = level3;
        this.level4 = level4;
        this.level5 = level5;

        lut = new double[level1][level2][level3][level4][level5];
    }

    @Override
    public void initialiseLUT() {
        for (int a = 0; a < level1; a++) {
            for (int b = 0; b < level2; b++) {
                for (int c = 0; c < level3; c++) {
                    for (int d = 0; d < level4; d++) {
                        Arrays.fill(lut[a][b][c][d], Math.random());
                    }
                }
            }
        }
    }

    @Override
    public int indexFor(double[] X) {
        return 0;
    }

    @Override
    public double outputFor(double[] x){
        return lut[(int)x[0]][(int)x[1]][(int)x[2]][(int)x[3]][(int)x[4]];
    }

    @Override
    public double train(double[] x, double target){
        lut[(int)x[0]][(int)x[1]][(int)x[2]][(int)x[3]][(int)x[4]] = target;
        return 0;
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

    @Override
    public void load(String argFileName) throws IOException {
        File modelFile = new File(argFileName);
        Scanner modelReader = new Scanner(modelFile);
        String model = modelReader.nextLine();
        modelReader.close();

        Gson gson = new Gson();
        RLRobotLUT loadedLUT = gson.fromJson(model, RLRobotLUT.class);

        this.lut = loadedLUT.getLUT();
        this.level1 = loadedLUT.getLevel1();
        this.level2 = loadedLUT.getLevel2();
        this.level3 = loadedLUT.getLevel3();
        this.level4 = loadedLUT.getLevel4();
        this.level5 = loadedLUT.getLevel5();
        return;
    }

    public double[][][][][] getLut() {
        return lut;
    }

    public int getLevel1() {
        return level1;
    }

    public int getLevel2() {
        return level2;
    }

    public int getLevel3() {
        return level3;
    }

    public int getLevel4() {
        return level4;
    }

    public int getLevel5() {
        return level5;
    }
}
