package mainpackage;
//This class contains information needed for every Vertex of our Tree/Graph
public class Vertex {
	private String Name;
	
	private double g;
	private double h;
	private String Street;
	
	public Vertex(String Name,double g,double h, String Street){
		this.Name=Name;
		this.g=g;
		
		this.h=h;
		this.Street=Street;
	}
	
	public String getName(){
		return Name;
	}
	
	public String getStreet(){
		return Street;
	}
	
	public double getG(){
		return g;
	}
	public void setG(double g){
		this.g=g;
	}

	public void setH(double h){
		this.h=h;
	}
	
	public double getH(){
		return h;
	}


	
	
}
