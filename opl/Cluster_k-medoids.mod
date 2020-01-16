/*********************************************
 * OPL 12.10.0.0 Model
 * Author: Thomas
 * Creation Date: 5 janv. 2020 at 16:00:27
 *********************************************/
int dimension = ...;
int nbPoints = ...;
int nbCluster = ...;

range pts = 1..nbPoints;
range dim = 1..dimension;

float points[pts][dim] = ...;

dvar boolean zn[pts][pts];
dvar boolean yn[pts];

float dist[p1 in pts][p2 in pts] = sqrt(sum(d in dim) pow(points[p1][d] - points[p2][d], 2));  

minimize
  sum(p1 in pts, p2 in pts) pow(dist[p1][p2], 2) * zn[p1][p2];
  

subject to{
	forall(p1 in pts)
	  ctAffect:
	  	sum(p2 in pts)
	  	  zn[p1][p2] >= 1;
	  ctRepre:
	  	sum(p2 in pts)
	  	  yn[p2] == nbCluster;
	 forall(p1 in pts, p2 in pts)
	   ct:
	   	zn[p1][p2] <= yn[p2];
}

execute PRINT_SOLUTION {
// affichage ecran des bornes inférieures et supérieures

writeln("BORNE INFERIEURE : ",cplex.getBestObjValue())
writeln("BORNE SUPERIEURE : ",cplex.getObjValue())
}