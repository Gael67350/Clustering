package clustering;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Point {
    private int dimension;
    private ArrayList<Double> coords = new ArrayList<>();

    public Point(int dimension) {
        if (dimension <= 0) {
            throw new IllegalArgumentException("Invalid dimension");
        }

        this.dimension = dimension;
    }

    public boolean addCoordinate(Double coord) {
        if (coords.size() >= dimension) return false;
        coords.add(coord);
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        DecimalFormat maxDigitsFormatter = new DecimalFormat("#.######");

        sb.append("[");
        for (int i = 0; i < coords.size(); i++) {
            sb.append(maxDigitsFormatter.format(coords.get(i).doubleValue()));

            if (i < coords.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");

        return sb.toString();
    }
}
