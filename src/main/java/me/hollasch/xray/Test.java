package me.hollasch.xray;

/**
 * Created by Connor on 4/21/17.
 */
public class Test {

    public static void main(String... args) {
        for (int i = 5000; i < 10000; ++i) {
            double halton = halton(i, Math.PI);
            if (halton > 1 ) {
                continue;
            }
            System.out.println(halton + ",");
        }
    }

    private static double halton(double i, double b) {
        double r = 0, f = 1;

        while (i > 0) {
            f = f / b;
            r = r + f * (i % b);
            i = i / b;
        }

        return r;
    }
}
