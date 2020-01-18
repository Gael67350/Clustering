# Clustering
Projet de PLNE S7 - Résolution d'un problème d'optimisation de clusters

### Objectifs

Le but de ce projet est d’appliquer les techniques exactes et heuristiques abordées durant le cours de Programmation Linéaire en Nombres Entiers sur un problème de clustering.

Ce projet exploratoire a pour but de réaliser un générateur de jeux de données permettant d’analyser les différences de résultats empiriques entre méthodes exactes et heuristiques en faisant varier un certain nombre de paramètres tels que la dimension, le nombre de clusters naturellement formés ou encore le nombre de points répartis dans les différents clusters naturels.


### Description des fonctionnalités

#### Générateur de données

Le générateur de données permet de générer des jeux de données au format OPL pour un problème de clustering.

Avant d'exécuter la commande, veillez à vous rendre dans le dossier /build du projet.

```
java -jar Generator.jar <dimension> <nbPoint> <nbCluster> <filepath>

dimension:  La dimension dans laquelle les points doivent être générés
nbPoint:    Le nombre total de points à générer incluant les centres de cluster
nbCLuster:  Le nombre de clusters désirés
filepath:   Le chemin où le fichier de sortie au format OPL sera généré (par exemple, "../tests/test.dat")
```

Pour chaque jeu de données généré, la liste des coordonnées des centres de cluster choisis pour la génération est également fournie sous forme de commentaire en fin de fichier.

**Note : Les clusters sont générés dans un environnement borné dans l'intervalle [0 ; 1000] pour toutes les dimensions.**

##### Exemple :

Pour générer un fichier avec 10 points en 2D et 3 clusters nommé "dev.dat" dans le dossier "tests" du projet :

```
java -jar Generator.jar 2 10 3 ../tests/dev.dat
```

##### Autres informations :

L'exécutable .JAR a été compilé et exécuté avec Java 1.8.0_152.

#### Affichage graphique des jeux de données

Le script d'affichage permet de générer un fichier image au format .png pour visualiser l’implantation des clusters dans un jeu de données en dimension 2D.

Avant d'exécuter la commande, veillez à vous rendre dans le dossier /plotter du projet.

```
python plotter.py <filepath> <map_filepath>

filepath:       Le chemin vers le fichier de données au format OPL à afficher
map_filepath:   Le nom de l'image à générer dans le dossier "tests" du projet
```

**Note : Seuls les jeux de données avec des points en dimension 2 sont pris en charge.**

##### Exemple :

Pour ouvrir le fichier OPL "test.dat" situé dans le dossier "tests" du projet et générer une image "test.png" à ce même endroit :

```
python plotter.py ../tests/test.dat test
```

##### Autres informations :

Le script a été exécuté avec Python 2.7.14 et nécessite le module pyplot de la librairie matplotlib.  

#### Exécution des méta-heuristiques (solveur)

Le solveur utilisant différentes méta-heuristiques et affichant la solution soit après le dépassement du temps limite ou par arrêt du Hill-Climbing.
 
Avant d'exécuter la commande, veillez à vous rendre dans le dossier /build du projet.

````shell script
java -jar Solver.jar <filepath> <waitTimeMax> <displayChart> <disruptMode1> <disruptMode2> <onlyMode>

filepath:      Le chemin vers le fichier de données au format OPL
waitTimeMax:   Temps maximal en milliseconde avant l'arrêt du solveur si aucune solution n'est trouvée avant
displayChart:  Boolean indiquant si vous souhaitez afficher le graphique durant la recherche de solution *Défaut=0*
disruptMode1 : Mode de perturbation intensification (cf liste des modes disponibles) *Défaut=0*
disruptMode2 : Mode de perturbation diversification (cf liste des modes disponibles) *Défaut=0*
onlyMode :     Boolean indiquant si vous souhaitez n'utiliser que la méthode <disruptMode1> durant tout le temps
               de recherche ou réaliser de l'intensification/diversification *Défaut=0*
               [Si <onlyMode> est à false alors toute les 50 itérations le solveur appellera la méthode <disruptMode2> pour
               modifier les centres de cluster]
````

##### Modes de perturbations disponibles
````text
[0] Aléatoire complet
[1] Modification d'un seul centre de cluster
[2] Modification de la moitié des centres de cluster
[3] Modification de tout les clusters avec le points le plus proche de chaque centre de cluster
````


##### Exemple :
Pour ouvrir le fichier OPL "clustering.dat" situé dans le même dossier que Solveur.jar, avec un temps maximal de 10 secondes, 
en affichant le graphique, avec le mode 2 de perturbation intensification et le mode 0 de perturbation diversification et en demandant de faire de l'intensification/diversification :

````shell script
java -jar Solver.jar clustering.dat 10000 1 2 0 0
````

##### Autres informations :

L'exécutable .JAR a été compilé et exécuté avec Java 1.8.0_152.

### Licence

Ce projet est sous licence GNU General Public License v3.0. Pour plus d'informations, consultez le fichier LICENSE.

This project is licensed under GNU General Public License v3.0. For further informations take a look at the LICENSE file.
