import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LookUpTable implements CommonInterface{
    public Map<Pair<Double, Double>, Double> map;

    public LookUpTable(){
        this.map = new HashMap<Pair<Double, Double>, Double>();
    }

    public double outputFor(double [] X){
        return map.get(new Pair<Double, Double>(X[0], X[1]));
    }

    public double train(double [] X, double argValue){
        map.put(new Pair<Double, Double>(X[0], X[1]), argValue);
        return 0;
    }

    public void save(File argFile){
        return;
    }

    public void load(String argFileName) throws IOException{
        return;
    }
}
