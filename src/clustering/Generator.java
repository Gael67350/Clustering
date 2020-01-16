package clustering;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Generator {
    private static final String AUTHOR = "SCION Gael & PICHARD Thomas";
    private static Random rand = new Random();

    public static final int MIN_ENV_AREA = 0;
    public static final int MAX_ENV_AREA = 1000;

    private int dimension;
    private int nbPoint;
    private int nbCluster;

    private ArrayList<Point> points = new ArrayList<>();
    private ArrayList<ClusterCentre> centres = new ArrayList<>();

    public static Random getRandomInstance() {
        return rand;
    }

    public Generator(int dimension, int nbPoint, int nbCluster) {
        if (dimension <= 0 || nbCluster < 0 || nbCluster > nbPoint) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        this.dimension = dimension;
        this.nbPoint = nbPoint;
        this.nbCluster = nbCluster;
    }

    public void generateTo(String filename) {
        StringBuilder sb = new StringBuilder();

        // Add head comment
        sb.append("/*********************************************").append(System.lineSeparator());
        sb.append("* OPL 12.6.0.0 Data").append(System.lineSeparator());
        sb.append("* Author: ").append(AUTHOR).append(System.lineSeparator());
        sb.append("* Creation Date: ").append(new Date().toString()).append(System.lineSeparator());
        sb.append("*********************************************/").append(System.lineSeparator());
        sb.append(System.lineSeparator());

        // Add parameters
        sb.append("dimension = ").append(dimension).append(";").append(System.lineSeparator());
        sb.append("nbPoints = ").append(nbPoint).append(";").append(System.lineSeparator());
        sb.append("nbCluster = ").append(nbCluster).append(";").append(System.lineSeparator());
        sb.append(System.lineSeparator());

        generateDataset();

        if (isDatasetValid()) {
            System.out.println("STATUS : OK");
        } else {
            System.err.println("STATUS : The generated data does not seem to be valid but the output file has been generated anyway.");
        }

        // Add point list
        sb.append("points = [");
        for (int i = 0; i < points.size(); i++) {
            sb.append(points.get(i).toString());

            if (i < points.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("];");
        sb.append(System.lineSeparator()).append(System.lineSeparator());

        // Add cluster centers used for generation
        sb.append("/*").append(System.lineSeparator()).append("Cluster centers used for generation :").append(System.lineSeparator());
        for (int i = 0; i < centres.size(); i++) {
            sb.append(points.get(i).toString()).append(System.lineSeparator());
        }
        sb.append("*/");

        // Write new file
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            System.err.println("An error occurred when writing result into the output file :");
            e.printStackTrace();
        }
    }

    private void generateDataset() {
        // Generate cluster centers
        for (int i = 0; i < nbCluster; i++) {
            ClusterCentre c = new ClusterCentre(dimension);
            centres.add(c);
            points.add(c);

            for (int j = 0; j < dimension; j++) {
                c.updateCoordinateAt(j, rand.nextDouble() * (MAX_ENV_AREA - MIN_ENV_AREA) + MIN_ENV_AREA);
            }
        }

        // Generate point around cluster centers
        for (int i = 0; i < (nbPoint - centres.size()); i++) {
            ClusterCentre c = centres.get(rand.nextInt(centres.size()));
            points.add(c.getRandomPointInClusterArea());
        }
    }

    private boolean isDatasetValid() {
        if (points.size() != nbPoint) {
            return false;
        }

        for (Point p : points) {
            ArrayList<Double> coords = p.getCoordinates();

            if (coords.size() != dimension) {
                return false;
            }

            for (Double coord : coords) {
                if (coord < MIN_ENV_AREA || coord > MAX_ENV_AREA) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Usage: <dimension> <nbPoint> <nbCluster> <filepath>");
            return;
        }

        int dimension, nbPoint, nbCluster;

        try {
            dimension = Integer.parseInt(args[0]);
            nbPoint = Integer.parseInt(args[1]);
            nbCluster = Integer.parseInt(args[2]);
        } catch (NumberFormatException ex) {
            System.err.println("Invalid arguments passed : " + ex.getMessage());
            System.err.println("Usage: <dimension> <nbPoint> <nbCluster> <filepath>");
            return;
        }

        Generator gen = new Generator(dimension, nbPoint, nbCluster);
        gen.generateTo(args[3]);
    }
}
