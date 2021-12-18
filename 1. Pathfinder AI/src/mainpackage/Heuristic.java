package mainpackage;

import java.util.ArrayList;
//This class contains our heuristic functions
public class Heuristic {
	private String typeofHeuristic;
	private Dijkstra dj;
	
	
	public Heuristic(String typeofHeuristic){
		this.typeofHeuristic=typeofHeuristic;
		dj=null;
	}
	
	public double runHeur(FileData data,Node<Vertex> n,int depth,int day){
		if(typeofHeuristic.equals("optimistic")){
			return this.Optimistic(data, n, depth, day);
		}
		else if(typeofHeuristic.equals("pessimistic")){
			return this.Pessimistic(data, n, depth, day);
		}
		else{
			return this.DijkstraHeur(data, n.getData().getName(), data.getDestination(), day);
		}
	}
	
	public double Optimistic(FileData data,Node<Vertex> n,int depth,int day){
		ArrayList<Node<Vertex>> ft;
		Pathfinder pf=new Pathfinder();
		
		if(n.isLeaf()){
			Node<Vertex> k=new Node<Vertex>(new Vertex(n.getData().getName(),n.getData().getG(),n.getData().getH(),n.getData().getStreet()));
			pf.addChildren(data,k,depth,day);
			ft=k.getChildren();       
	        
		}
		else{
			ft=n.getChildren();
		}
		double min_cost=0;
        int i;
        for(i=0;i<ft.size();i++){
        	if(i==0){
        		min_cost=(ft.get(0).getData().getG());
        		
        	}
        	if((ft.get(i).getData().getG())<min_cost){
        		
        		min_cost=ft.get(i).getData().getG();
        		
        	}
        }
        return min_cost;
		
	}
	
	
	public double Pessimistic(FileData data,Node<Vertex> n,int depth,int day){
		int avgcost=data.calculateAvgCost();
		int rem=data.getCost().length-depth;
		
		return avgcost*rem;
	}
	

	public double LRTAHeur(FileData data, Node<Vertex> n,int day){
		double h=n.getData().getH();
		if((h)!=0){
			return h;
		}
		h=this.runHeur(data,n,0,day);
		
		
		return h;
	}
	
	
	public double DijkstraHeur(FileData data,String source,String destination,int day){
		
		dj=new Dijkstra();
		dj.createGraph(data,day);
		return dj.getDistance(source, destination);
		
	}


}
