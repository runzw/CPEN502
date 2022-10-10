import org.junit.Assert;
import org.junit.Test;

public class MatrixTest {
    @Test
    public void testMultiply(){
        Matrix a = new Matrix(2,3, 2);
        Matrix b = new Matrix(3, 2, 3);
        Matrix c = new Matrix(2,3, 2);
        Matrix d = new Matrix(2,3,4);
        double[][] expected = new double[][]{{18,18}, {18,18}};
        double[][] actual = Matrix.multiply(a, b).m;
        Assert.assertArrayEquals(expected, actual);
        a.multiply(c);
        Assert.assertArrayEquals(a.m, d.m);
        a.multiply(0.5);
        Assert.assertArrayEquals(a.m, c.m);
    }

    @Test
    public void testAdd(){
        Matrix a = new Matrix(2,3);
        Matrix b = new Matrix(2,3);
        Matrix c = new Matrix(2,3);
        Matrix d = new Matrix(2,3, 1);
        a.add(b);
        Assert.assertArrayEquals(c.m, a.m);
        a.add(1);
        Assert.assertArrayEquals(d.m, a.m);
    }

    @Test
    public void testTranspose(){
        Matrix a = new Matrix(2,3, 1);
        Matrix b = new Matrix(3,2,1);
        Assert.assertArrayEquals(Matrix.transpose(a).m, b.m);
    }

    @Test
    public void testSigmoid(){
        Matrix a = new Matrix(2, 2);
        a.msigmoid(0, 1);
        Assert.assertArrayEquals(new double[][]{{0.5, 0.5}, {0.5, 0.5}}, a.m);

        a = new Matrix(2,2);
        a.bipolarSigmoid();
        Assert.assertArrayEquals(new double[][]{{0, 0}, {0, 0}}, a.m);
    }

    @Test
    public void testParseArray(){
        Matrix a  = new Matrix(2, 1);
        double[] actual = new double[]{0,0};
        Assert.assertArrayEquals(Matrix.parseArray(actual).m, a.m);
    }

    @Test
    public void testToArray(){
        Matrix a = new Matrix(1, 2);
        double[] expected = new double[]{0, 0};
        Assert.assertArrayEquals(expected, Matrix.toArray(a), 1e-15);
    }
}
