/*********************************************
 * OPL 12.10.0.0 Model
 * Author: Thomas
 * Creation Date: 5 janv. 2020 at 16:00:02
 *********************************************/
int dimension = ...;
int nbPoints = ...;
int nbCluster = ...;

range pts = 1..nbPoints;
range dim = 1..dimension;

float points[pts][dim] = ...;

dvar boolean zn[pts][pts];
dvar float+ r;

float dist[p1 in pts][p2 in pts] = sqrt(sum(d in dim) pow(points[p1][d] - points[p2][d], 2));  

minimize r;
	

subject to{
	c1: 
	  forall(p1 in pts)
	    sum(p2 in pts)  dist[p1][p2] * zn[p1][p2] <= r;
	c2:
	  sum(p2 in pts)
	    zn[p2][p2] == nbCluster;
	c3:
	  forall(p1 in pts) 
	  sum( p2 in pts)  zn[p1][p2] == 1;
	    
	forall(p1 in pts, p2 in pts)
	  c4:
	    zn[p1][p2] <= zn[p2][p2];
}

execute PRINT_SOLUTION {
// affichage ecran des bornes inférieures et supérieures

writeln("BORNE INFERIEURE : ",cplex.getBestObjValue())
writeln("BORNE SUPERIEURE : ",cplex.getObjValue())
}