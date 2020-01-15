package clustering;

import java.util.ArrayList;
import java.util.Random;

public class ClusterCentre extends Point {
    public static final int MIN_CLUSTER_AREA = 10;
    public static final int MAX_CLUSTER_AREA = 100;

    public ClusterCentre(int dimension) {
        super(dimension);
    }

    public Point getRandomPointInClusterArea() {
        ArrayList<ArrayList<Double>> dimensionLimits = new ArrayList<>();

        for (int i = 0; i < dimension; i++) {
            double areaLength = Generator.getRandomInstance().nextDouble() * (MAX_CLUSTER_AREA - MIN_CLUSTER_AREA) + MIN_CLUSTER_AREA;
            double areaMin = coords.get(i) - (areaLength / 2.0);
            double areaMax = coords.get(i) + (areaLength / 2.0);

            if (areaMin < Generator.MIN_ENV_AREA) {
                areaMin = Generator.MIN_ENV_AREA;
            }

            if (areaMax > Generator.MAX_ENV_AREA) {
                areaMax = Generator.MAX_ENV_AREA;
            }

            ArrayList<Double> limits = new ArrayList<>();
            limits.add(areaMin);
            limits.add(areaMax);
            dimensionLimits.add(limits);
        }

        Point p = new Point(dimension);

        for (int i = 0; i < p.dimension; i++) {
            p.updateCoordinateAt(i, Generator.getRandomInstance().nextDouble() * (dimensionLimits.get(i).get(1) - dimensionLimits.get(i).get(0)) + dimensionLimits.get(i).get(0));
        }

        return p;
    }
}
