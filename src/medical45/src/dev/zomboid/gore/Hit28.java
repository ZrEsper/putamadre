/*
 * Decompiled with CFR 0.152.
 */
package dev.zomboid.gore;

import dev.zomboid.gore.Gore;
import dev.zomboid.gore.R;

public final class Hit28 {
    static final int[] REGIONS = new int[]{0, 2, 3, 4, 5, 6};

    public static double[] local(double d, double d2, double d3, double d4, double d5) {
        double d6 = Math.cos(d4);
        double d7 = Math.sin(d4);
        return new double[]{(d * d6 + d3 * d7) * 16.0 / d5, d2 * 16.0 / d5, (d * d7 - d3 * d6) * 16.0 / d5};
    }

    public static double[] world(double d, double d2, double d3, double d4, double d5) {
        double d6 = Math.cos(d4);
        double d7 = Math.sin(d4);
        return new double[]{(d6 * d + d7 * d3) * d5 / 16.0, d2 * d5 / 16.0, (d7 * d - d6 * d3) * d5 / 16.0};
    }

    static double[] box(int n) {
        double[] dArray;
        switch (n) {
            case 0: {
                double[] dArray2 = new double[6];
                dArray2[0] = -4.0;
                dArray2[1] = 23.2;
                dArray2[2] = -4.0;
                dArray2[3] = 4.0;
                dArray2[4] = 31.2;
                dArray = dArray2;
                dArray2[5] = 4.0;
                break;
            }
            case 2: {
                double[] dArray3 = new double[6];
                dArray3[0] = 3.0;
                dArray3[1] = 11.2;
                dArray3[2] = -2.0;
                dArray3[3] = 7.0;
                dArray3[4] = 23.2;
                dArray = dArray3;
                dArray3[5] = 2.0;
                break;
            }
            case 3: {
                double[] dArray4 = new double[6];
                dArray4[0] = -7.0;
                dArray4[1] = 11.2;
                dArray4[2] = -2.0;
                dArray4[3] = -3.0;
                dArray4[4] = 23.2;
                dArray = dArray4;
                dArray4[5] = 2.0;
                break;
            }
            case 4: {
                double[] dArray5 = new double[6];
                dArray5[0] = 0.0;
                dArray5[1] = 0.0;
                dArray5[2] = -2.0;
                dArray5[3] = 4.0;
                dArray5[4] = 12.0;
                dArray = dArray5;
                dArray5[5] = 2.0;
                break;
            }
            case 5: {
                double[] dArray6 = new double[6];
                dArray6[0] = -4.0;
                dArray6[1] = 0.0;
                dArray6[2] = -2.0;
                dArray6[3] = 0.0;
                dArray6[4] = 12.0;
                dArray = dArray6;
                dArray6[5] = 2.0;
                break;
            }
            default: {
                double[] dArray7 = new double[6];
                dArray7[0] = -4.0;
                dArray7[1] = 11.2;
                dArray7[2] = -2.0;
                dArray7[3] = 4.0;
                dArray7[4] = 23.2;
                dArray = dArray7;
                dArray7[5] = 2.0;
            }
        }
        return dArray;
    }

    static double clamp(double d) {
        return Math.max(0.0, Math.min(1.0, d));
    }

    public static Hit classify(double d, double d2, double d3, boolean bl) {
        int n = 6;
        double d4 = Double.POSITIVE_INFINITY;
        for (int n2 : REGIONS) {
            double d5;
            double d6;
            double[] dArray = Hit28.box(n2);
            double d7 = Math.max(dArray[0] - d, Math.max(0.0, d - dArray[3]));
            double d8 = d7 * d7 + (d6 = Math.max(dArray[1] - d2, Math.max(0.0, d2 - dArray[4]))) * d6 + (d5 = Math.max(dArray[2] - d3, Math.max(0.0, d3 - dArray[5]))) * d5;
            if (!(d8 < d4 - 1.0E-7) && (!(Math.abs(d8 - d4) < 1.0E-7) || !(n2 >= 2 && n2 <= 5 && Math.abs(d) >= 3.2) && (n2 != 6 || !(Math.abs(d) < 3.2)))) continue;
            d4 = d8;
            n = n2;
        }
        if (bl && d2 >= 22.5 && d4 < 4.0) {
            n = 0;
        }
        double[] dArray = Hit28.box(n);
        return new Hit(n, Hit28.clamp((d - dArray[0]) / (dArray[3] - dArray[0])), Hit28.clamp((dArray[4] - d2) / (dArray[4] - dArray[1])), Hit28.clamp((d3 - dArray[2]) / (dArray[5] - dArray[2])));
    }

    public static Hit locate(Gore.Shot shot) {
        Object object = shot.entity();
        Object object2 = shot.point();
        double d2 = BodyCoordinates45.height(object) / 1.95;
        double d3 = Math.toRadians(R.num(R.get(object, "yBodyRot")));
        double[] dArray = BodyCoordinates45.local(object,object2,d3);
        Hit hit = Hit28.classify(dArray[0], dArray[1], dArray[2], shot.head());
        if (hit.region() == 0) {
            try {
                double d4 = Math.toRadians(R.num(R.get(object, "yHeadRot")));
                double[] dArray2 = BodyCoordinates45.local(object,object2,d4);
                return new Hit(0, Hit28.clamp(dArray2[0] / 8.0 + 0.5), Hit28.clamp((31.2 - dArray2[1]) / 8.0), Hit28.clamp(dArray2[2] / 8.0 + 0.5));
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
        }
        return hit;
    }

    public static int zone(Gore.Shot shot) {
        return Hit28.locate(shot).region();
    }

    public record Hit(int region, double x, double y, double z) {
    }
}

