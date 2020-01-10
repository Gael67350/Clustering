package clustering;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class Point {
    protected int dimension;
    protected ArrayList<Double> coords = new ArrayList<>();

    public Point(int dimension) {
        if (dimension <= 1) {
            throw new IllegalArgumentException("Invalid dimension (the dimension must be at least 2)");
        }

        this.dimension = dimension;

        for (int i = 0; i < dimension; i++) {
            coords.add(0.0);
        }
    }

    public Point(Point point) {
        this.dimension = point.dimension;
        this.coords = point.coords;
    }

    public double distBetweenTwoPoints(Point p2){
        ArrayList<Double> coordP1 = getCoordinates();
        ArrayList<Double> coordP2 = p2.getCoordinates();
        double dist = 0;

        if (coordP1.size() != coordP2.size()) {
            throw new IllegalArgumentException("The number of coordinates of point 1 is not the same as the number of coordinates of point 2.");
        }

        for(int i=0; i<coordP1.size(); ++i){
            dist += Math.pow(coordP2.get(i) - coordP1.get(i), 2);
        }

        return Math.sqrt(dist);
    }

    public void addCoordinates(ArrayList<Double> coords) {
        if (coords.size() <= 0 || coords.size() > dimension) {
            throw new IllegalArgumentException("The number of coordinates provided does not correspond to the size of the point");
        }

        this.coords = coords;
    }

    public void updateCoordinateAt(int position, Double value) {
        if (position < 0 || position >= dimension) {
            throw new IllegalArgumentException("Invalid position");
        }

        coords.set(position, value);
    }

    public ArrayList<Double> getCoordinates() {
        return new ArrayList<>(coords);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return dimension == point.dimension &&
                Objects.equals(coords, point.coords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dimension, coords);
    }
}
