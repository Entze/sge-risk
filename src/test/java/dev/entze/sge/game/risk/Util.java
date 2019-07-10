package dev.entze.sge.game.risk;

public class Util {

  public static double transformGaussian(double Z, double n, double sigma) {
    return Z * sigma + n;
  }

  public static double standardizeGaussian(double X, double n, double sigma) {
    return (X - n) / sigma;
  }

  public static int gaussianInt(double gaussian) {
    return (int) Math.round(gaussian);
  }

  public static int gaussianInt(double gaussian, int max) {
    return gaussianInt(gaussian, Integer.MIN_VALUE, max);
  }

  public static int gaussianInt(double gaussian, int min, int max) {
    if (min > max) {
      return gaussianInt(gaussian, max, min);
    }
    long longdiff = Math.abs((long) min - (long) max);
    int halfDiff = (int) (longdiff / 2);
    double n = min + halfDiff;
    double sigma = 1D / 3D * halfDiff;
    double X = transformGaussian(gaussian, n, sigma);
    return (int) Math.max(min, Math.min(max, X));
  }

}
