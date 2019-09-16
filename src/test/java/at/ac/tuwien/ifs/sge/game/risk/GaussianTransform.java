package at.ac.tuwien.ifs.sge.game.risk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class GaussianTransform {

  @Property
  public void reflexiveTransformation(double d, double n, double sigma) {
    double X = Util.transformGaussian(d, n, sigma);
    double Z = Util.standardizeGaussian(X, n, sigma);

    assertEquals(d, Z, 0.00001D);

  }

  @Property
  public void reverseReflexiveTransformation(double d, double n, double sigma) {
    double Z = Util.standardizeGaussian(d, n, sigma);
    double X = Util.transformGaussian(Z, n, sigma);

    assertEquals(d, X, 0.00001D);

  }

  @Property
  public void prop_gaussianIntMinMax(double d, int a, int b) {
    int min = Math.min(a, b);
    int max = Math.max(a, b);
    int r = Util.gaussianInt(d, a, b);
    assertTrue(min <= r && r <= max);
  }

}
