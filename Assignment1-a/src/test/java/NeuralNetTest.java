import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class NeuralNetTest {
    @Test
    public void testZeroWeight(){
        NeuralNet nn = new NeuralNet(2,4,0.2,0,0,1, true);
        Matrix zeroIH = new Matrix(4,2);
        Matrix zeroHO = new Matrix(1,4);
        nn.zeroWeights();
        Assert.assertArrayEquals(zeroIH.m, nn.getWeightIH().m);
        Assert.assertArrayEquals(zeroHO.m, nn.getWeightHO().m);
    }

    @Test
    public void testOutputfor(){
        NeuralNet nn = new NeuralNet(2,4,0.2,0,0,1, false);
        Matrix zeroIH = new Matrix(4,2);
        Matrix zeroHO = new Matrix(1,4);
        nn.zeroWeights();
        double[] res = new double[]{2,2};
        Assert.assertEquals(0.5, nn.outputFor(res), 1e-16);
    }

    @Test
    public void testSave(){
        NeuralNet nn = new NeuralNet(2,4,0.2,0,0,1, true);
        nn.zeroWeights();
        try {
            nn.save(new File("outputs/test.json"));
        } catch (IOException e) {
            Assert.assertTrue(false);
        }
        Assert.assertTrue(true);
    }

    @Test
    public void testLoad(){
        NeuralNet nn = new NeuralNet(2,4,0.2,0,0,1, true);
        nn.initializeWeights();
        try {
            nn.load("test.json");
        } catch (IOException e) {
            Assert.assertTrue(false);
        }
        Assert.assertArrayEquals(nn.getWeightIH().m, new Matrix(4,2).m);
        Assert.assertArrayEquals(nn.getWeightHO().m, new Matrix(1,4).m);

        nn = new NeuralNet(2,3,0.2,0,0,1, true);
        try {
            nn.load("test.json");
        } catch (IOException e) {
            Assert.assertTrue(true);
            return;
        }
        Assert.assertTrue(false);
    }

    @Test
    public void testGetGradient(){

    }
}
