package clustering;

import java.util.Random;

public class ClusterCentre extends Point {
    public static final int MIN_CLUSTER_AREA = 10;
    public static final int MAX_CLUSTER_AREA = 50;

    private double areaMin;
    private double areaMax;

    public ClusterCentre(int dimension) {
        super(dimension);

        Random rand = new Random();
        double areaLength = rand.nextDouble() * (MAX_CLUSTER_AREA - MIN_CLUSTER_AREA) + MIN_CLUSTER_AREA;

        this.areaMin = coords.get(0) - (areaLength / 2.0);
        this.areaMax = coords.get(0) + (areaLength / 2.0);

        if (this.areaMin < Generator.MIN_ENV_AREA) {
            this.areaMin = Generator.MIN_ENV_AREA;
        }

        if (this.areaMax > Generator.MAX_ENV_AREA) {
            this.areaMax = Generator.MAX_ENV_AREA;
        }
    }

    public double getAreaMin() {
        return areaMin;
    }

    public double getAreaMax() {
        return areaMax;
    }

    public Point getRandomPointInClusterArea() {
        Random rand = new Random();
        Point p = new Point(dimension);

        for (int i = 0; i < p.dimension; i++) {
            p.updateCoordinateAt(i, rand.nextDouble() * (areaMax - areaMin) + areaMin);
        }

        return p;
    }
}
