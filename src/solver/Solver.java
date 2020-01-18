package solver;

import clustering.Point;
import graph.Courbes;
import javafx.util.Pair;

import javax.swing.*;
import java.util.*;

public class Solver {
    private Parser parser;
    private ArrayList<Point> alreadyVisited = new ArrayList<>();

    public Solver(String src, int waitTimeMax, boolean displayChart, int disruptMode1, int disruptMode2, boolean onlyMode) {
        parser = new Parser(src);
        ArrayList<Boolean> yn = constructiveSolution();
        ArrayList<ArrayList<Boolean>> zn = setPointInCluster(yn);
        // Mode de perturbation :
        // [0] aléatoire complet *Defaut*
        // [1] modification d'un seul centre de cluster
        // [2] modification de la moitie des centres de cluster
        // [3] modification de tout les clusters avec le points le plus proche de chaque centre de cluster
        recalculationOfTheSolution(waitTimeMax, displayChart, disruptMode1, disruptMode2, onlyMode, yn, zn);
    }

    /**
     * Solution constructive qui permet de generer le yn indiquant
     * quels points sont choisis pour etre centre de cluster
     */
    private ArrayList<Boolean> constructiveSolution() {
        ArrayList<Boolean> yn = generateBooleanTab(parser.getNbPoints());

        HashSet<Integer> indCluster = new HashSet<>();
        while (indCluster.size() != parser.getNbCluster()) {
            int random = getRandomNumberInRange(0, yn.size() - 1);
            indCluster.add(random);
        }
        ArrayList<Integer> indCluster2 = new ArrayList<>(indCluster);
        for (int i : indCluster2) {
            yn.set(i, true);
        }

        return yn;
    }

    /**
     * Methode qui prend en entree les centres de cluster et remplit zn pour indiquer
     * A quel cluster appartient chaque point
     *
     * @param yn les centres de cluster
     * @return la matrice zn
     */
    private ArrayList<ArrayList<Boolean>> setPointInCluster(ArrayList<Boolean> yn) {
        ArrayList<Point> points = parser.getPoints();
        ArrayList<Integer> indCluster2 = new ArrayList<>();
        ArrayList<ArrayList<Boolean>> zn = generateBooleanTab2D(parser.getNbPoints());

        for (int i = 0; i < yn.size(); ++i) {
            if (yn.get(i)) {
                indCluster2.add(i);
            }
        }
        for (Point p : points) {
            TreeMap<Double, Point> distInRelationToCluster = new TreeMap<>();
            for (int i : indCluster2) {
                distInRelationToCluster.put(points.get(i).distBetweenTwoPoints(p), points.get(i));
            }
            Collection<Point> val = distInRelationToCluster.values();
            Object[] array = val.toArray();
            Point poi = (Point) array[0];
            zn.get(points.indexOf(p)).set(points.indexOf(poi), true);
        }
        return zn;
    }

    /**
     * Methode qui calcule la somme des distances entre les points d'un cluster par rapport au centre de ce dernier
     * et renvoie la somme de ces distances pour tout les clusters
     *
     * @param yn les centres de cluster
     * @param zn les associations des points dans chaque cluster
     * @return la somme des distances
     */
    private double sumDistInCluster(ArrayList<Boolean> yn, ArrayList<ArrayList<Boolean>> zn) {
        ArrayList<Point> points = parser.getPoints();
        ArrayList<ArrayList<Double>> distInCluster = new ArrayList<>();
        int ind = 0;

        double res = 0;

        for (int i = 0; i < yn.size(); ++i) {
            if (yn.get(i)) {
                distInCluster.add(new ArrayList<>());
                for (int p = 0; p < zn.size(); ++p) {
                    if (zn.get(p).get(i)) {
                        distInCluster.get(ind).add(points.get(i).distBetweenTwoPoints(points.get(p)));
                    }
                }
                ind++;
            }
        }

        ArrayList<Double> sumDistInCluster = new ArrayList<>();
        for (ArrayList<Double> a : distInCluster) {
            double sum = 0;
            for (Double d : a) {
                sum += d;
            }
            sumDistInCluster.add(sum);
        }

        for (Double d : sumDistInCluster) {
            res += d;
        }

        return res;
    }

    /**
     * Methode qui modifie le choix des clusters d'une certaines mani�res
     *
     * @param mode type de perturbation
     * @param yn   les centres de cluster
     * @param zn   les associations des points dans chaque cluster
     *             return yn modifie
     */
    private ArrayList<Boolean> disruptionSolution(int mode, ArrayList<Boolean> yn, ArrayList<ArrayList<Boolean>> zn) {
        switch (mode) {
            case 1:
                yn = changeYn(1, yn, zn);
                break;
            case 2:
                yn = changeYn(Math.floorDiv(parser.getNbCluster(), 2), yn, zn);
                break;
            case 3:
                yn = changeYnNearestNeighbor(yn, zn);
                break;
            default:
                Collections.shuffle(yn, new Random());
                break;
        }
        return yn;
    }


    private ArrayList<Boolean> changeYn(int clusterToChange, ArrayList<Boolean> yn, ArrayList<ArrayList<Boolean>> zn) {
        ArrayList<Integer> indCluster = new ArrayList<>();
        if (clusterToChange <= parser.getNbCluster()) {
            clusterToChange = parser.getNbCluster();
        }
        for (int i = 0; i < clusterToChange; ++i) {
            if (yn.get(i)) {
                indCluster.add(i);
            }
        }

        yn = generateBooleanTab(parser.getNbPoints());
        HashSet<Integer> indCluster2 = new HashSet<>(indCluster);
        while (indCluster2.size() != parser.getNbCluster()) {
            int random = getRandomNumberInRange(0, yn.size() - 1);
            indCluster2.add(random);
        }
        ArrayList<Integer> indCluster3 = new ArrayList<>(indCluster2);
        for (int i : indCluster3) {
            yn.set(i, true);
        }

        return yn;
    }

    private ArrayList<Boolean> changeYnNearestNeighbor(ArrayList<Boolean> yn, ArrayList<ArrayList<Boolean>> zn) {
        ArrayList<Boolean> yn2 = generateBooleanTab(yn.size());
        ArrayList<Pair<Point, Double>> dists = new ArrayList<>();
        ArrayList<Point> points = parser.getPoints();
        for (int i = 0; i < yn.size(); ++i) {
            if (yn.get(i)) {
                alreadyVisited.add(points.get(i));
            }
        }
        if(alreadyVisited.size()>=points.size() - parser.getNbCluster()){
            alreadyVisited = new ArrayList<>();
        }
        for (int i = 0; i < yn.size(); ++i) {
            if (yn.get(i)) {
                for (Point p : parser.getPoints()) {
                    if (p != points.get(i)) {
                        dists.add(new Pair<>(p, p.distBetweenTwoPoints(points.get(i))));
                    }
                }
                dists.sort((o1, o2) -> {
                    if (o1.getValue().equals(o2.getValue())) {
                        return 0;
                    } else if (o1.getValue() > o2.getValue()) {
                        return 1;
                    } else {
                        return -1;
                    }
                });
                for(Pair<Point, Double> p : dists){
                    if(!alreadyVisited.contains(p.getKey())){
                        yn2.set(points.indexOf(p.getKey()), true);
                        break;
                    }
                }
                dists = new ArrayList<>();
            }
        }

        int check = 0;
        for (Boolean b : yn2) {
            if (b) {
                check++;
            }
        }

        ArrayList<Integer> rest = new ArrayList<>();
        for(int i=0; i<points.size(); ++i){
            if(!alreadyVisited.contains(points.get(i))){
                rest.add(i);
            }
        }

        while (check != parser.getNbCluster()) {
            int random = getRandomNumberInRange(0, rest.size() - 1);
            if(!yn2.get(rest.get(random))){
                yn2.set(rest.get(random), true);
                rest.remove(random);
                check++;
            }
            else{
                rest.remove(random);
            }
        }

        return yn2;
    }

    /**
     * Methode pour progresser dans la courbe en modifiant les cluster
     *
     * @param waitTime temps maximal avant interruption si aucune solution n'est trouvee en millisecondes
     */
    private void recalculationOfTheSolution(int waitTime, boolean displayChart, int disruptMode1, int disruptMode2, boolean onlyMode, ArrayList<Boolean> yn, ArrayList<ArrayList<Boolean>> zn) {
        System.out.println("*****************************************************************************");
        System.out.println("* Demarrage du calcul d'une solution avec un temps maximal de : " + (waitTime / 1000) + " secondes *");
        System.out.println("*****************************************************************************");
        long wait = waitTime;

        ArrayList<Boolean> ynTest = (ArrayList<Boolean>) yn.clone();
        ArrayList<ArrayList<Boolean>> znTest = copyTab2D(zn);

        long startTime = System.currentTimeMillis();
        long actualTime = System.currentTimeMillis();
        double previousSolution = sumDistInCluster(yn, zn);
        double actualSolution = 0;

        double bestSolution = previousSolution;

        List<Double> res = new ArrayList<>();
        if (displayChart) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Courbes.createAndShowGui();
                }
            });
        }

        int iter = 0;
        while ((actualTime - startTime) < wait && previousSolution != actualSolution) {
            if (onlyMode) {
                ynTest = disruptionSolution(disruptMode1, ynTest, znTest);
            } else {
                if (iter <= 1000) {
                    ynTest = disruptionSolution(0, ynTest, znTest);
                } else if (iter % 30 == 0) {
                    ynTest = disruptionSolution(disruptMode2, ynTest, znTest);
                } else {
                    ynTest = disruptionSolution(disruptMode1, ynTest, znTest);
                }
            }

            znTest = setPointInCluster(ynTest);
            previousSolution = actualSolution;
            actualSolution = sumDistInCluster(ynTest, znTest);
            actualTime = System.currentTimeMillis();
            if (actualSolution < bestSolution) {
                bestSolution = actualSolution;
                yn = (ArrayList<Boolean>) ynTest.clone();
                zn = copyTab2D(znTest);
                res.add(actualSolution);
            }
            if (iter % 10000 == 0 || iter <= 1) {
                res.add(actualSolution);
                if (displayChart) {
                    Courbes.mainPanel.setScores(res);
                }
            }
            iter++;
        }

        res.add(actualSolution);
        res.add(bestSolution);
        Courbes.mainPanel.setScores(res);

        int check = 0;
        for (Boolean b : yn) {
            if (b) {
                check++;
            }
        }
        if(check != parser.getNbCluster()){
            System.err.println("Erreur dans la solution trouvée");
            System.exit(0);
        }

        if ((actualTime - startTime) >= wait) {
            System.out.println("Arret du Systeme le temps est depasse, meilleur solution trouvee : " + bestSolution + " en un total de " + iter + " iterations");
        }

        if (previousSolution == actualSolution) {
            System.out.println("Une solution a ete trouvee : " + bestSolution + " en un total de " + iter + " iterations" + " en " + (actualTime - startTime) + " millisecondes");
        }

        displaySolution(yn, zn);
    }

    /**
     * Methode pour afficher proprement les parametres yn et zn
     *
     * @param yn les centres de cluster
     * @param zn les associations des points dans chaque cluster
     */
    private void displaySolution(ArrayList<Boolean> yn, ArrayList<ArrayList<Boolean>> zn) {
        displaySolution(yn);
        System.out.println("Associations des points dans chaque cluster :");

        for (int point = 0; point < zn.size(); ++point) {
            if (point + 1 < 10) {
                System.out.print((point + 1) + "  [ ");
            } else {
                System.out.print((point + 1) + " [ ");
            }
            for (int pointC = 0; pointC < zn.get(0).size(); ++pointC) {
                if (zn.get(point).get(pointC)) {
                    System.out.print("1 ");
                } else {
                    System.out.print("_ ");
                }
            }
            System.out.println("]");
        }
        System.out.println("------------------------------------------------------------------------------------------");
    }

    private void displaySolution(ArrayList<Boolean> yn) {
        System.out.print("\nCluster : [ ");
        for (int i = 0; i < yn.size(); ++i) {
            if (yn.get(i)) {
//                System.out.print("1 ");
                System.out.print(parser.getPoints().get(i) + " ");
            } else {
                System.out.print("_ ");
            }
        }
        System.out.println("]\n");
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

    private ArrayList<ArrayList<Boolean>> copyTab2D(ArrayList<ArrayList<Boolean>> tab) {
        ArrayList<ArrayList<Boolean>> copy = new ArrayList<>();

        for (ArrayList<Boolean> booleans : tab) {
            copy.add((ArrayList<Boolean>) booleans.clone());
        }

        return copy;
    }

    public static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static void main(String[] args) {
        String src = "";
        int waitTimeMax;
        boolean displayChart;
        int disruptMode1;
        int disruptMode2;
        boolean onlyMode;
        try {
            if (args.length > 0) {
                src = args[0];
            } else {
                System.out.println("Veuillez indiquer au moins le nom d'un fichier .dat");
                System.exit(0);
            }
            if (args.length > 1) {
                waitTimeMax = Integer.parseInt(args[1]);
            } else {
                waitTimeMax = 10000;
            }
            if (args.length > 2 && args[2].compareTo("1") == 0) {
                displayChart = true;
            } else {
                displayChart = false;
            }
            if (args.length > 3) {
                disruptMode1 = Integer.parseInt(args[3]);
            } else {
                disruptMode1 = 0;
            }
            if (args.length > 4) {
                disruptMode2 = Integer.parseInt(args[4]);
            } else {
                disruptMode2 = 0;
            }
            if (args.length > 5 && args[5].compareTo("1") == 0) {
                onlyMode = true;
            } else {
                onlyMode = false;
            }
            System.out.println("************************************");
            System.out.println("* src : "+src + "\n* waitTimeMax : " + waitTimeMax + "\n* displayChart : " + displayChart + "\n* disruptMode1 : " + disruptMode1 + "\n* disruptMode2 : " + disruptMode2 + "\n* onlyMode : " + onlyMode);
            Solver s = new Solver(src, waitTimeMax, displayChart, disruptMode1, disruptMode2, onlyMode);
        } catch (NumberFormatException exception) {
            System.out.println("Erreur dans les parametres d'entree");
        }
    }
}
