package solver;

import clustering.Point;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
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
        System.out.println("************************************");
        System.out.println("* Donn�es "+src+" charg�es");
        System.out.println("* Dimension : "+dimension);
        System.out.println("* Nombre de points : "+nbPoints);
        System.out.println("* Nombre de cluster : "+nbCluster);
        System.out.println("************************************");
    }

    private void readFile(String src) throws FileNotFoundException {
        try {
            FileInputStream fis = new FileInputStream(src);
            Scanner sc = new Scanner(fis);

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
