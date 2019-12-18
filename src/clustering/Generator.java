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

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 1000;

    private int dimension;
    private int nbPoint;
    private int nbCluster;

    private ArrayList<Point> points = new ArrayList<>();

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

        // Add point list
        sb.append("points = [");
        for (int i = 0; i < points.size(); i++) {
            sb.append(points.get(i).toString());

            if (i < points.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("];");

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
        Random rand = new Random();

        for (int i = 0; i < nbCluster; i++) {
            Point p = new Point(dimension);
            points.add(p);

            for (int j = 0; j < dimension; j++) {
                p.addCoordinate(rand.nextDouble() * (MAX_VALUE - MIN_VALUE) + MIN_VALUE);
            }
        }

        // Generate point around cluster centers
        for (int i = 0; i < (nbPoint - nbCluster); i++) {
            // TODO : Utiliser des zones carrées pour la génération des "sous-clusters"
        }
    }

    public static void main(String[] args) {
        Generator gen = new Generator(2, 50, 10);
        gen.generateTo("tests/test.dat");
    }
}
