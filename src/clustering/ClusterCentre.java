package clustering;

import java.util.Random;

public class ClusterCentre extends Point {
    public static final int MIN_CLUSTER_AREA = 10;
    public static final int MAX_CLUSTER_AREA = 100;

    public ClusterCentre(int dimension) {
        super(dimension);
    }

    public Point getRandomPointInClusterArea() {
        Random rand = new Random();

        double areaLength = rand.nextDouble() * (MAX_CLUSTER_AREA - MIN_CLUSTER_AREA) + MIN_CLUSTER_AREA;
        double areaMin = coords.get(0) - (areaLength / 2.0);
        double areaMax = coords.get(0) + (areaLength / 2.0);

        if (areaMin < Generator.MIN_ENV_AREA) {
            areaMin = Generator.MIN_ENV_AREA;
        }

        if (areaMax > Generator.MAX_ENV_AREA) {
            areaMax = Generator.MAX_ENV_AREA;
        }

        Point p = new Point(dimension);

        for (int i = 0; i < p.dimension; i++) {
            p.updateCoordinateAt(i, rand.nextDouble() * (areaMax - areaMin) + areaMin);
        }

        return p;
    }
}
