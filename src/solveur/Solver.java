package solveur;

import clustering.Point;
import java.util.*;

public class Solver {
    private static final String AUTHOR = "SCION Gael & PICHARD Thomas";
    private Parser parser;
    private ArrayList<Boolean> yn;
    private ArrayList<ArrayList<Boolean>> zn;

    public Solver(String src){
        parser = new Parser(src);
        constructiveSolution();
        toStringZn();
        disruptionSolution();
        toStringZn();
    }

    /**
     * Solution constructive
     */
    private void constructiveSolution() {
        yn = generateBooleanTab(parser.getNbPoints());

        HashSet<Integer> indCluster = new HashSet<>();
        while (indCluster.size() != parser.getNbCluster()) {
            int random = getRandomNumberInRange(0, yn.size()-1);
            indCluster.add(random);
        }
        ArrayList<Integer> indCluster2 = new ArrayList<>(indCluster);
        for (int i : indCluster2) {
            yn.set(i, true);
        }
        setPointInCluster();
    }

    private void setPointInCluster(){
        ArrayList<Point> points = parser.getPoints();
        ArrayList<Integer> indCluster2 = new ArrayList<>();
        zn = generateBooleanTab2D(parser.getNbPoints());

        for(int i=0; i<yn.size(); ++i){
            if(yn.get(i)){
                indCluster2.add(i);
            }
        }
        for(Point p : points){
            TreeMap<Double, Point> distInRelationToCluster = new TreeMap<>();
            for(int i : indCluster2){
                distInRelationToCluster.put(points.get(i).distBetweenTwoPoints(p), points.get(i));
            }
            Collection<Point> val = distInRelationToCluster.values();
            Object[] array = val.toArray();
            Point poi = (Point) array[0];
            zn.get(points.indexOf(p)).set(points.indexOf(poi), true);
        }
    }

    private void disruptionSolution(){
        Collections.shuffle(yn, new Random());
        setPointInCluster();
    }


    private void toStringZn(){
        for(int point=0; point<zn.size(); ++point){
            if(point+1<10){
                System.out.print((point+1)+"  [ ");
            }
            else{
                System.out.print((point+1)+" [ ");
            }
            for(int pointC=0; pointC<zn.get(0).size(); ++pointC){
                if(zn.get(point).get(pointC)){
                    System.out.print("1 ");
                }
                else{
                    System.out.print("_ ");
                }
            }
            System.out.println("]");
        }
        System.out.println("------------------------------------------------------------------------------------------");
    }

    private ArrayList<ArrayList<Boolean>> generateBooleanTab2D(int length) {
        ArrayList<ArrayList<Boolean>> tab = new ArrayList<>();
        for (int i = 0; i < length; ++i) {
            tab.add(generateBooleanTab(length));
        }
        return tab;
    }

    private ArrayList<Boolean> generateBooleanTab(int length) {
        ArrayList<Boolean> tab = new ArrayList<>();
        for (int i = 0; i < length; ++i) {
            tab.add(false);
        }
        return tab;
    }

    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static void main(String [] args){
        Solver s = new Solver("tests/test.dat");
    }
}
