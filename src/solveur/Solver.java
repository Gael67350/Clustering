package solveur;

import clustering.Point;

import java.util.*;

public class Solver {
    private static final String AUTHOR = "SCION Gael & PICHARD Thomas";
    private Parser parser;
    private Object Integer;

    Solver(String src){
        parser = new Parser("test.dat");
        chooseRandomClusters();
    }

    private void chooseRandomClusters(){
        ArrayList<Point> points = parser.getPoints();
        ArrayList<Point> pointsCopy = parser.getPoints();
        ArrayList<Boolean> yn = generateBooleanTab(parser.getNbPoints()-1);
        ArrayList<ArrayList<Boolean>> zn = generateBooleanTab2D(parser.getNbPoints()-1);

        HashSet<Integer> indCluster = new HashSet<>();
        while(indCluster.size()!=parser.getNbCluster()){
            int random = getRandomNumberInRange(0, yn.size()-1);
            indCluster.add(random);
        }
        ArrayList<Integer> indCluster2 = new ArrayList<>(indCluster);
        for(int i : indCluster2){
            yn.set(i, true);
            pointsCopy.remove(points.get(i));
        }

        for(int cluster=0; cluster<indCluster2.size(); cluster++) {
                    int nbPointByCluster = 0;
                    if(cluster!=indCluster.size()-1){
                        nbPointByCluster = Math.floorDiv( parser.getNbPoints() - parser.getNbCluster(), parser.getNbCluster());
                    }
                    else{
                        nbPointByCluster = pointsCopy.size();
                    }
                    TreeMap<Double, Point> distInRelationToCluster = new TreeMap<>();
                    for (Point po : pointsCopy) {
                        distInRelationToCluster.put(points.get(indCluster2.get(cluster)).distBetweenTwoPoints(po), po);
                    }
                    Collection<Point> val = distInRelationToCluster.values();
                    System.out.println(indCluster2.get(cluster)+" -> "+val);
                    System.out.println("---------------------------------------------");
                    Object[] array = val.toArray();
                    for(int ch=0; ch<nbPointByCluster; ch++){
                        Point p = (Point) array[ch];
                        zn.get(points.indexOf(p)).set(indCluster2.get(cluster), true);
                pointsCopy.remove(p);
            }
        }

        for(ArrayList<Boolean> z : zn){
            System.out.println(z.contains(true));
        }
    }

    private ArrayList<ArrayList<Boolean>> generateBooleanTab2D(int length){
        ArrayList<ArrayList<Boolean>> tab = new ArrayList<>();
        for(int i=0; i<length; ++i){
            tab.add(generateBooleanTab(length));
        }
        return tab;
    }

    private ArrayList<Boolean> generateBooleanTab(int length){
        ArrayList<Boolean> tab = new ArrayList<>();
        for(int i=0; i<length; ++i){
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
        Solver s = new Solver("test.dat");
    }
}
