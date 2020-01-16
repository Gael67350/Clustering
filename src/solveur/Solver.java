package solveur;

import clustering.Point;
import graph.Courbes;

import javax.swing.*;
import java.util.*;

public class Solver {
    private Parser parser;

    public Solver(String src, int waitTimeMax, boolean displayChart, int disruptMode, boolean onlyMode) {
        parser = new Parser(src);
        ArrayList<Boolean> yn = constructiveSolution();
        ArrayList<ArrayList<Boolean>> zn = setPointInCluster(yn);
        // Mode de perturbation :
        // [0] random complet *Défaut*
        // [1] modification d'un seul centre de cluster
        // [2] modification de la moitié des centres de cluster
        // [3] TODO modification de tout les clusters avec le points le plus proche de chaque centre de cluster
        recalculationOfTheSolution(waitTimeMax, displayChart, disruptMode, onlyMode, yn, zn);
    }

    /**
     * Solution constructive qui permet de générer le yn indiquant
     * quels points sont choisis pour être centre de cluster
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
     * Méthode qui prend en entrée les centres de cluster et remplit zn pour indiquer
     * à quel cluster appartient chaque point
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
     * Méthode qui modifie le choix des clusters d'une certaines manières
     *
     * @param mode type de perturbation
     * @param yn   les centres de cluster
     * @param zn   les associations des points dans chaque cluster
     *             return yn modifié
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
//                yn = changeYnNearestNeighbor(yn, zn);
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
        ArrayList<Double> dists = new ArrayList<>();
        ArrayList<Point> points = parser.getPoints();
        for (int i = 0; i < yn.size(); ++i) {
            if (yn.get(i)) {
                for (Point p : parser.getPoints()) {
                    if (p != points.get(i)) {
                        dists.add(p.distBetweenTwoPoints(points.get(i)));
                    } else {
                        dists.add(Double.MAX_VALUE);
                    }
                }
                yn2.set(dists.indexOf(Collections.min(dists)), true);
                dists = new ArrayList<>();
            }
        }

        int check = 0;
        for (Boolean b : yn2) {
            if (b) {
                check++;
            }
        }
        if (check != parser.getNbCluster()) {
            System.out.println("Erreur yn ne contient pas assez de cluster");
            System.exit(0);
        }

        return yn2;
    }

    /**
     * Méthode pour progresser dans la courbe en modifiant les cluster
     *
     * @param waitTime temps maximal avant interruption si aucune solution n'est trouvée en millisecondes
     */
    private void recalculationOfTheSolution(int waitTime, boolean displayChart, int disruptMode, boolean onlyMode, ArrayList<Boolean> yn, ArrayList<ArrayList<Boolean>> zn) {
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
                ynTest = disruptionSolution(disruptMode, ynTest, znTest);
            } else {
                if (iter <= 1000) {
                    ynTest = disruptionSolution(0, ynTest, znTest);
                } else if (iter % 50 == 0) {
                    ynTest = disruptionSolution(0, ynTest, znTest);
                } else {
                    ynTest = disruptionSolution(disruptMode, ynTest, znTest);
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

        if ((actualTime - startTime) >= wait) {
            System.out.println("Arrêt du Système le temps est dépassé, meilleur solution trouvée : " + bestSolution + " en un total de " + iter + " itérations");
        }

        if (previousSolution == actualSolution) {
            System.out.println("Une solution a été trouvée : " + bestSolution + " en un total de " + iter + " itérations");
        }

        displaySolution(yn, zn);
    }

    /**
     * Méthode pour afficher proprement les paramètres yn et zn
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
        int disruptMode;
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
            displayChart = args.length > 2 && args[2].compareTo("1") == 0;
            if (args.length > 3) {
                disruptMode = Integer.parseInt(args[3]);
            } else {
                disruptMode = 2;
            }
            onlyMode = args.length > 4 && args[4].compareTo("1") == 0;
            System.out.println(src + " " + waitTimeMax + " " + displayChart + " " + disruptMode + " " + onlyMode);
            Solver s = new Solver(src, waitTimeMax, displayChart, disruptMode, onlyMode);
        } catch (NumberFormatException exception) {
            System.out.println("Erreur dans les paramètres d'entrée");
        }
    }
}
