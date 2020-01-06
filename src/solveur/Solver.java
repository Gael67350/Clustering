package solveur;

import clustering.Point;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.Random;

public class Solver {
    private static final String AUTHOR = "SCION Gael & PICHARD Thomas";
    private Parser parser;

    Solver(String src){
        parser = new Parser("test.dat");
        heuristiqueConstructivePMedian();
    }

    private void heuristiqueConstructivePMedian(){
        ArrayList<Point> points = new ArrayList<Point>();
        ArrayList<Boolean> yn = generateBooleanTab(parser.getNbPoints());

        int clusterChoose = 0;
        for(int i=0; i<yn.size(); ++i){
            int random = getRandomNumberInRange(0, 1);
            if(random==1 && clusterChoose<parser.getNbCluster()){
                yn.set(i, true);
                clusterChoose++;
            }
        }

        System.out.println(yn);
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
