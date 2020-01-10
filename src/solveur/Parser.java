package solveur;

import clustering.Point;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
    private static final String AUTHOR = "SCION Gael & PICHARD Thomas";

    private int dimension;
    private int nbPoints;
    private int nbCluster;
    private ArrayList<Point> points = new ArrayList<>();

    Parser(String src) {
        try{
            readFile(src);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    private void readFile(String src) throws FileNotFoundException {
        try {
            URL url = Parser.class.getResource(src);
            File file = new File(url.getPath());
            Scanner sc = new Scanner(file);

            int nbLine = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                if(nbLine>6){
                    if(nbLine==7){
                        dimension = Integer.parseInt(line.split("=")[1].replaceAll("\\s", "").replaceAll(";", ""));
                    }
                    else if(nbLine==8){
                        nbPoints = Integer.parseInt(line.split("=")[1].replaceAll("\\s", "").replaceAll(";", ""));
                    }
                    else if(nbLine==9){
                        nbCluster = Integer.parseInt(line.split("=")[1].replaceAll("\\s", "").replaceAll(";", ""));
                    }
                    else if(nbLine==11){
                        String split = line.split("=")[1];
                        split = split.substring(2, split.length()-2);
                        split = split.replaceAll("\\[|\\]" , "");
                        int nbCoord = 0;
                        Point p = new Point(dimension);
                        for(String s : split.split(", ")){
                            if (nbCoord == dimension) {
                                points.add(p);
                                p = new Point(dimension);
                                nbCoord = 0;
                            }
                            p.updateCoordinateAt(nbCoord, Double.parseDouble(s.replace(',', '.')));
                            nbCoord++;
                        }
                        points.add(p);
                    }
                }
                nbLine++;
            }
        }
        catch (FileNotFoundException exception){
            System.err.println("File not found");
            throw new FileNotFoundException();
        }
    }

    public static String getAUTHOR() {
        return AUTHOR;
    }

    public int getDimension() {
        return dimension;
    }

    public int getNbPoints() {
        return nbPoints;
    }

    public int getNbCluster() {
        return nbCluster;
    }

    public ArrayList<Point> getPoints() {
        return (ArrayList<Point>) points.clone();
    }
}
