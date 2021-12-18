package mainpackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;


//Code used to save time from:http://stackoverflow.com/a/17480244
//We added a function EstimateCost to adjust this Class to our data,
//and a function addAdjacencies that was needed for functionality.

//This class computes a graph and calculates the minimum distance between 2 vertexes using Dijkstra's algorithm.
//We use this as one of our heuristic functions
public class Dijkstra {
	class Vertex implements Comparable<Vertex>
	{
	    public final String name;
	    public Edge[] adjacencies=null;
	    public double minDistance = Double.POSITIVE_INFINITY;
	    public Vertex previous;
	    public Vertex(String argName) { name = argName; }
	    public String toString() { return name; }
	    public int compareTo(Vertex other)
	    {
	        return Double.compare(minDistance, other.minDistance);
	    }
	    public void addAdjacencies(Edge e){
	    	if(adjacencies==null){
	    		adjacencies=new Edge[1];
	    		adjacencies[0]=e;
	    		return;
	    	}
	    	Edge[] a2=new Edge[adjacencies.length+1];
	    	for(int i=0;i<adjacencies.length;i++){
	    		a2[i]=adjacencies[i];
	    	}
	    	a2[adjacencies.length]=e;
	    	
	    	adjacencies=a2;
	    }

	}
	class Edge
	{
	    public final Vertex target;
	    public final double weight;
	    public Edge(Vertex argTarget, double argWeight)
	    { target = argTarget; weight = argWeight; }
	}


	public Vertex[] v;
	public static void computePaths(Vertex source)
    {
        source.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
        vertexQueue.add(source);

	    while (!vertexQueue.isEmpty()) {
	       Vertex u = vertexQueue.poll();
	
	            // Visit each edge exiting u
	       for (Edge e : u.adjacencies)
	            {
	                Vertex v = e.target;
	                double weight = e.weight;
	                double distanceThroughU = u.minDistance + weight;
	        if (distanceThroughU < v.minDistance) {
	            vertexQueue.remove(v);
	
	            v.minDistance = distanceThroughU ;
	            v.previous = u;
	            vertexQueue.add(v);
	        }
	            }
	        }
    }

    public static List<Vertex> getShortestPathTo(Vertex target)
    {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);

        Collections.reverse(path);
        return path;
    }

    public double[] EstimateCost(FileData data,int day){
    	
    	String[] pred=data.getPrediction_traffic()[day];
    	String p;
    	int[] c=data.getCost();
    	double[] cost=new double[c.length];
    	for(int i=0;i<pred.length;i++){
    		p=data.getPrediction(pred[i]);
    		if(p.equals("heavy")){
    			cost[i]=c[i]*1.25;
    		}
    		else if(p.equals("low")){
    			cost[i]=c[i]*0.9;
    		}
    		else{
    			cost[i]=c[i];
    		}
    	}
    	return cost;
    }
    
    public void createGraph(FileData data,int day)
    
    {	
    	String[] streets=data.getRoads();
    	String[] nodesA=data.getNodeA();
    	String[] nodesB=data.getNodeB();
    	String[] nodes=new String[nodesA.length+nodesB.length];
    	int i,j=0;
    	for(i=0;i<nodes.length;i++){
    		if(i<nodesA.length){
    			nodes[i]=nodesA[i];
    		}
    		else{
    			nodes[i]=nodesB[j];
    			j=j+1;
    		}
    	}
    	Set<String> f_nodes=new HashSet<String>(Arrays.asList(nodes));
       
    	
    	nodes=f_nodes.toArray(new String[f_nodes.size()]);
    	v=new Vertex[nodes.length];
        for(i=0;i<nodes.length;i++){
        	v[i]=new Vertex(nodes[i]);
        }

        Vertex nA=null,nB=null;

        double[] cost=this.EstimateCost(data,day);
        // set the edges and weight
        for(i=0;i<streets.length;i++){
        	for(j=0;j<nodes.length;j++){
        		if(v[j].name.equals(nodesA[i])){
        			
        			nA=v[j];
        		}
        		if(v[j].name.equals(nodesB[i])){
        			nB=v[j];
        		}
        	}
        	
        	nA.addAdjacencies(new Edge(nB,cost[i]));
        	
        	nB.addAdjacencies(new Edge(nA,cost[i]));
        	
        }
        
        

      
        
    }
    
    public double getDistance(String source,String destination){
    	Vertex nA=null,nB=null;
    	
    	for(int i=0;i<v.length;i++){
    		if(v[i].name.equals(source)){
    			nA=v[i];
    		}
    		if(v[i].name.equals(destination)){
    			nB=v[i];
    		}
    	}
    	computePaths(nA);
    	
    	return nB.minDistance;
    }
}
