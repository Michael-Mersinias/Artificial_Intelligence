package mainpackage;

//Mersinias Michail A.M.:2013030057, Troullinos Dimitrios A.M.:2013030032

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Pathfinder {



	
	public int[] findNodeIdxs(String node,String[] nodeA,String[] nodeB){
		int[] idx=new int[nodeA.length];
		int k,j=0;
		for(k=0;k<nodeA.length;k++){
			if(node.equals(nodeA[k]) || node.equals(nodeB[k])){
				
				idx[j]=k;
				j++;
			}
		}
		
		int[] fidx=new int[j];
		
		System.arraycopy( idx, 0, fidx, 0, j );
		return fidx;
	}
	
	
	public void addChildren(FileData data,Node<Vertex> t_node,int depth,int day){
		
		Vertex child,nd= t_node.getData();
		String c_name,n_name=nd.getName();
		
		int i,c_i;
		double parent_cost=t_node.getData().getG();
		String[] nodeA=data.getNodeA();
		String[] nodeB=data.getNodeB();
		int[] children=findNodeIdxs(n_name,nodeA,nodeB);
		
		String[] roads=data.getRoads();
		int[] cost=data.getCost();
		String[] predictions=data.getPrediction_traffic()[day];
		double pred;
		String prediction;
		for(i=0;i<children.length;i++){
			c_i=children[i];
			prediction=data.getPrediction(predictions[c_i]);
			
			c_name=nodeA[c_i].equals(n_name) ? nodeB[c_i]  : nodeA[c_i];
			if(prediction.equals("low")){
				pred=0.9;
			}else if(prediction.equals("heavy")){
				pred=1.25;
			}else{
				pred=1;
			}
			child=new Vertex(c_name,((cost[c_i]*pred+parent_cost)),0,roads[c_i]);
			t_node.addChild(child);
				
		}

	}
	
	public Node<Vertex> createTree(FileData data){
		
		String source=data.getSource();
		
		Vertex src=new Vertex(source,0,0,null);
		Node<Vertex> tree=new Node<Vertex>(src);
		
		
		return tree;
	}
	
	public void printTree(Node<Vertex> nd,int depth){
		
		if(nd.isLeaf()){
			return;
		}
		ArrayList<Node<Vertex>> cld=nd.getChildren();
		for(int i=0;i<cld.size();i++){
			System.out.println("Child of:"+nd.getData().getName()+", Node Name:"+cld.get(i).getData().getName()+", Street Name:"+cld.get(i).getData().getStreet()+", Depth:"+depth);
		}
		for(int i=0;i<cld.size();i++){
			printTree(cld.get(i),depth+1);
		}
	}
	
	//Search Algorithms below
	
	public Node<Vertex> IDAstar(Heuristic ht,FileData data,Node<Vertex> t_node,int depth,ArrayList<String> Expanded,ArrayList<Node<Vertex>> Explored,double limit,int day,BufferedWriter writer) throws IOException{
		
		int min_I = -1;
		
		//System.out.println("IDAstar");
		
		addChildren(data, t_node, depth+1,day);
        Expanded.add(t_node.getData().getName());
        int i;
        ArrayList<Node<Vertex>> Front_Queue=t_node.getChildren();
        Explored.addAll(Front_Queue);
        
        Node<Vertex> tmp;
        ArrayList<Node<Vertex>> ft=new ArrayList<Node<Vertex>>();
		
        for(i=0;i<Explored.size();i++){
        	tmp=Explored.get(i);
        	if(!Expanded.contains(tmp.getData().getName())){
        		ft.add(tmp);
        	}
        }
        if(ft.isEmpty()){
        	System.out.println("Problem, no child found");
        	return null;
        }
        
        
        double tmp_cost;
        double min_cost=0;
        
        
        for(i=0;i<ft.size();i++){
        	tmp_cost=ft.get(i).getData().getG()+ht.DijkstraHeur(data, ft.get(i).getData().getName(), data.getDestination(), day);//ht.Pessimistic(data,ft.get(i),depth,day);
        	if(i==0){
        		min_I=0;
        		min_cost=tmp_cost;
        	}
        	if((tmp_cost)<min_cost){
        		
        		min_cost=tmp_cost;
        		min_I=i;
        	}
        }
        if(min_cost>limit){
        	Expanded.clear();
        	Explored.clear();
        	Explored.add(t_node.getHead());
        	//System.out.println(min_cost);
        	return IDAstar(ht,data,t_node.getHead(),0,Expanded,Explored,min_cost,day,writer);
        }
        
        t_node=ft.get(min_I);
        if(t_node.getData().getName().equals(data.getDestination())){
        	System.out.println("Success!");
        	System.out.println("Number of nodes expanded:"+Expanded.size());
        	writer.write("Number of nodes expanded:"+Expanded.size()+"\n");
        	System.out.println("Number of nodes in fringe:"+ft.size());
        	writer.write("Number of nodes in fringe:"+ft.size()+"\n");
        	return t_node;
        }
        
        
        
		return IDAstar(ht,data,t_node,depth+1,Expanded,Explored,limit,day,writer);
	}
	
	

    public Node<Vertex> Uniform_Cost_Search(FileData data,Node<Vertex> t_node,int depth,ArrayList<String> Expanded,ArrayList<Node<Vertex>> Explored,int day, BufferedWriter writer) throws IOException {
    	int min_I = -1;
    	
    	
    	
    	
    	    	
        addChildren(data, t_node, depth+1,day);
        Expanded.add(t_node.getData().getName());
        
        int i;
        ArrayList<Node<Vertex>> Front_Queue=t_node.getChildren();
        Explored.addAll(Front_Queue);
        
        Node<Vertex> tmp;
        ArrayList<Node<Vertex>> ft=new ArrayList<Node<Vertex>>();
        for(i=0;i<Explored.size();i++){
        	tmp=Explored.get(i);
        	if(!Expanded.contains(tmp.getData().getName())){
        		ft.add(tmp);
        	}
        }
        if(ft.isEmpty()){
        	System.out.println("Problem, no child found");
        	return null;
        }

        double tmp_cost;
        double min_cost=0;
        for(i=0;i<ft.size();i++){
        	tmp_cost=ft.get(i).getData().getG();
        	if(i==0){
        		min_cost=tmp_cost;
        		min_I=0;
        	}
        	if((tmp_cost)<min_cost){
        		
        		min_cost=tmp_cost;
        		min_I=i;
        	}
        }
        
        if(min_I==-1) {
        	System.out.println("Either error, or no children found! ");
        }
        
        
        
        t_node=ft.get(min_I);
        if(t_node.getData().getName().equals(data.getDestination())){
        	System.out.println("Success!");
        	System.out.println("Number of nodes expanded:"+Expanded.size());
        	writer.write("Number of nodes expanded:"+Expanded.size()+"\n");
        	System.out.println("Number of nodes in fringe:"+ft.size());
        	writer.write("Number of nodes in fringe:"+ft.size()+"\n");
        	
        	return t_node;
        }
        
        
        
    	return Uniform_Cost_Search(data,t_node,depth+1,Expanded,Explored,day,writer);
    
    }
	
    public double LRTAstarf(Heuristic ht, FileData data, String c_node,int c_node_idx,double[] cost,boolean[] c_table,String[] nodes,double[] g,double[] h,int day,BufferedWriter writer,double a_cost) throws IOException{
    	int[] idx=findNodeIdxs(c_node,data.getNodeA(),data.getNodeB());
    	String[] streets=data.getRoads();
    	String[] nodeA=data.getNodeA();
    	String[] nodeB=data.getNodeB();
    	
    	int j;
    	double min_cost=Double.MAX_VALUE;
    	String[] act;
    	double f;
    	int min_cost_I=-1;
    	int min_cost_j=-1;
    	//add g costs
    	for(int i=0;i<idx.length;i++){
    		if(nodeA[idx[i]].equals(c_node)){
    			
    			for(j=0;j<nodes.length;j++){
    				if(nodeB[idx[i]].equals(data.getDestination())){
    					if(c_table[idx[i]]==false){
    						act=data.getActual_traffic()[day];
    				    	if(act[idx[i]].equals("heavy")){
    				    		cost[idx[i]]=cost[idx[i]]*1.25;
    				    			    	}
    				    	else if(act[idx[i]].equals("low")){
    				    		cost[idx[i]]=cost[idx[i]]*0.9;
    				    	}
    				    	c_table[idx[i]]=true;
    			    	}
    					writer.write(streets[idx[i]]+" with cost:"+cost[idx[i]]+"\n");
    		    		writer.write("Total cost for LRTA* is:"+(a_cost+cost[idx[i]])+"\n");
    		    		System.out.println("Success!");
    		    		System.out.println(streets[idx[i]]);
    		    		System.out.println(nodes[j]);
    		    		System.out.println("Total Cost:"+(a_cost+cost[idx[i]])+"\n");
    		    		return a_cost+cost[idx[i]];
    				}
    				if(nodeB[idx[i]].equals(nodes[j])){
    					//System.out.println(nodes[j]);
    					//System.out.println("B");
    					g[j]=a_cost+cost[idx[i]];
    					
    					if(h[j]==0){
    						h[j]=ht.LRTAHeur(data, new Node<Vertex>(new Vertex(nodes[j],g[j],0,streets[idx[i]])), day);
    					}
    					f=g[j]+h[j];
    					if(f<min_cost){
    						min_cost=f;
    						min_cost_I=j;
    						min_cost_j=idx[i];
    					}
    					break;
    				}
    			}
    		}
    		
    		if(nodeB[idx[i]].equals(c_node)){
    			
    			for(j=0;j<nodes.length;j++){
    				if(nodeA[idx[i]].equals(data.getDestination())){
    					if(c_table[idx[i]]==false){
    						act=data.getActual_traffic()[day];
    				    	if(act[idx[i]].equals("heavy")){
    				    		cost[idx[i]]=cost[idx[i]]*1.25;
    				    			    	}
    				    	else if(act[idx[i]].equals("low")){
    				    		cost[idx[i]]=cost[idx[i]]*0.9;
    				    	}
    				    	c_table[idx[i]]=true;
    			    	}
    					writer.write(streets[idx[i]]+" with cost:"+cost[idx[i]]+"\n");
    		    		writer.write("Total cost for LRTA* is:"+(a_cost+cost[idx[i]])+"\n");
    		    		System.out.println("Success!");
    		    		System.out.println(streets[idx[i]]);
    		    		System.out.println(nodes[j]);
    		    		System.out.println("Total Cost:"+(a_cost+cost[idx[i]]));
    		    		return a_cost+cost[idx[i]];
    				}
    				if(nodeA[idx[i]].equals(nodes[j])){
    					//System.out.println(nodes[j]);
    					//System.out.println("A "+idx[i]);
    					g[j]=a_cost+cost[idx[i]];
    					//System.out.println(g[j]);
    					if(h[j]==0){
    						h[j]=ht.LRTAHeur(data, new Node<Vertex>(new Vertex(nodes[j],g[j],0,streets[idx[i]])), day);
    					}
    					f=g[j]+h[j];
    					if(f<min_cost){
    						min_cost=f;
    						min_cost_I=j;
    						min_cost_j=idx[i];
    					}
    					break;
    				}
    			}
    		}
    	}
    	
    	if(min_cost_I==-1){
    		System.out.println("Error! No neighbour nodes found");
    		return 0;
    	}
    	System.out.println(min_cost);
    	h[c_node_idx]=min_cost;
    	
    	act=data.getActual_traffic()[day];
    	if(c_table[min_cost_j]==false){
	    	if(act[min_cost_j].equals("heavy")){
	    		cost[min_cost_j]=cost[min_cost_j]*1.25;
	    			    	}
	    	else if(act[min_cost_j].equals("low")){
	    		cost[min_cost_j]=cost[min_cost_j]*0.9;
	    	}
	    	c_table[min_cost_j]=true;
    	}
    	
    	if(nodes[min_cost_I].equals(data.getDestination())){
    		writer.write(streets[min_cost_j]+" with cost:"+cost[min_cost_j]+"\n");
    		writer.write("Total cost for LRTA* is:"+(a_cost+cost[min_cost_j])+"\n");
    		System.out.println("Success!");
    		System.out.println(streets[min_cost_j]);
    		System.out.println(nodes[min_cost_I]);
    		System.out.println("Total Cost:"+(a_cost+cost[min_cost_j]));
    		return a_cost+cost[min_cost_j];
    	}
    	writer.write(streets[min_cost_j]+" with cost:"+cost[min_cost_j]+", ");
    	System.out.println(streets[min_cost_j]);
		System.out.println(nodes[min_cost_I]);
    	return LRTAstarf(ht,data,nodes[min_cost_I], min_cost_I,cost,c_table,nodes, g, h, day,writer,a_cost+cost[min_cost_j]);
    }

	
	
	public double CalculateActualCost(FileData data,Node<Vertex> n,int day){
    	double acost=0.0;
    	int i;
    	String[] act_cost=data.getActual_traffic()[day];
    	int[] cost=data.getCost();
    	String[] roads=data.getRoads();
    	String road;
    	while(n!=null && n.getData().getStreet()!=null){
    		
    		road=n.getData().getStreet();
    		for(i=0;i<roads.length;i++){
    			
    			if(road.equals(roads[i])){
    				if(act_cost[i].equals("low")){
    					acost=acost+(cost[i]*0.9);
    				}
    				else if(act_cost[i].equals("heavy")){
    					acost=acost+(cost[i]*1.25);
    				}
    				else{
    					acost=acost+cost[i];
    				}
    				break;
    			}
    			
    		}
    		n=n.getParent();
    	}
    	
    	return acost;
    	
    }
	
	
	
	public double CalculateStreetCost(FileData data,int idx,int day){
		
		int[] cost=data.getCost();
		String[] predictions=data.getPrediction_traffic()[day];
		String pred;
		
			
		pred=data.getPrediction(predictions[idx]);
		if(pred.equals("heavy")){
			return cost[idx]*1.25;
		}
		else if(pred.equals("low")){
			return cost[idx]*0.9;
		}
		else if(pred.equals("normal")){
			return cost[idx];
		}
		
		return 0;
	}
	
	
	
	
	public double runLRTA(Heuristic ht,FileData data,int day,BufferedWriter writer) throws IOException{
		
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
    	double[] g=new double[nodes.length];
    	double[] h=new double[nodes.length];
    	for(i=0;i<g.length;i++){
    		g[i]=0;
    		h[i]=0;
    	}
    	int[] cA=data.getCost();
    	double[] cB=new double[cA.length];
    	boolean[] c_table=new boolean[cA.length];
    	
    	
    	for(i=0;i<cB.length;i++){
    		cB[i]=this.CalculateStreetCost(data, i, day);
    	}
    	int c_node_idx=0;
    	String source=data.getSource();
    	for(i=0;i<nodes.length;i++){
    		if(nodes[i].equals(source)){
    			c_node_idx=i;
    			break;
    		}
    	}
    	writer.write("Path followed(with actual cost):");
    	double res=LRTAstarf(ht,data,source,c_node_idx,cB,c_table,nodes,g,h,day,writer,0);
    	return res;
    	
    	
	}
	
	public static void main(String[] args) {
		Pathfinder pf=new Pathfinder();
		FileData data=new FileData();
		String filename="sampleGraph1.txt";
		int choice,hchoice;
		String heuristic="optimistic";
		try{
			Scanner reader = new Scanner(System.in);  // Reading from System.in
			System.out.print("Enter a file name: ");
			filename = reader.next();
			System.out.println("Insert number 1 to run Offline Algorithms(UCS+IDA*) or number 2: to run Online Algorithm(LRTA*)");
			System.out.print("Input(1/2):");
			choice=reader.nextInt();
			
			while(choice!=1 && choice!=2){
				System.out.println("Wrong Input!");
				System.out.print("Input(1/2):");
				choice=reader.nextInt();
			}
			
			System.out.println(choice);
			data=data.test_Read(filename);
			
			System.out.println("Insert number 1 to run with Dijkstra Heuristic, 2 to run with Optimistic Heuristic or 3 to run with Pessimistic Heuristic");
			System.out.print("Input(1/2/3):");
			hchoice=reader.nextInt();
			reader.close();
			switch(hchoice){
				case 1:	heuristic="dijkstra";
						break;
				case 2:	heuristic="optimistic";
						break;
				case 3: heuristic="pessimistic";
						break;
				default:break;
			}
		}catch(Exception e){
			System.out.println("Error. File Could not be found!");
			System.out.println(e.getMessage());
			return;
		}
		
		if(data==null){
			System.out.println("Something went wrong!");
			return;
		}
	
		
		Node<Vertex> pf_tree;
		ArrayList<String> Expanded=new ArrayList<String>();
		ArrayList<Node<Vertex>> Explored=new ArrayList<Node<Vertex>>();
		
		Heuristic ht=new Heuristic(heuristic);
		Node<Vertex> res;
		String streets,street_cost;
		String[] dstreets=data.getRoads();
		

		long start_time_day;
		
		long stop_time_day;
		double elapsed_time_day;
		double cost_total_ucs=0;
		double cost_total_ida=0;
		double pred_total_ucs=0;
		double pred_total_ida=0;
		double pred_cost;
		double temp_cost;
		
		try{
						
			
			if(choice==1){
				double total_time_ucs=0;
				double total_time_idastar=0;
				BufferedWriter writer = new BufferedWriter( new FileWriter("OfflineResults"+filename));
				
				writer.write("Results for filename:"+filename+"\n");
				DateFormat df=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date date=new Date();
				
				writer.write("File created: "+df.format(date)+"\n");
				writer.write("\nSource is:"+data.getSource()+"\n");
				writer.write("Destination is:"+data.getDestination()+"\n");
				writer.write("Number of Streets:"+data.getRoads().length+"\n");
				
				int day,i;
				data.NewPredictions(0.6, 0.2, 0.2);
				for(day=0;day<80;day++){
					data.Estimate(day);
					System.out.println("\nDay:"+(day+1));
					writer.write("\n\nDay "+(day+1)+":\n");
					System.out.println("UCS");
					writer.write("Uniform Cost Search\n");
					pf_tree=pf.createTree(data);
					Explored.add(pf_tree);
					/*
					mem.gc();
					memoryB = mem.totalMemory() - mem.freeMemory();
					*/
					start_time_day = System.currentTimeMillis();
					
					res=pf.Uniform_Cost_Search(data, pf_tree, 0, Expanded, Explored,day,writer);
					stop_time_day = System.currentTimeMillis();
					
					/*
					mem.gc();
					memoryA = mem.totalMemory() - mem.freeMemory();
					System.out.println("Memory Used:"+((memoryB-memoryA)/1000)+" KBytes");
					writer.write("Memory Used:"+((memoryB-memoryA)/1000)+" KBytes\n");
					*/
					
					elapsed_time_day = (stop_time_day-start_time_day)*0.001;
					total_time_ucs=total_time_ucs+elapsed_time_day;
	
					
					System.out.println("Execution time of UCS for day "+day+" is: "+elapsed_time_day+" seconds");
					// save to file
					writer.write("Execution time of UCS for day "+day+" is: "+elapsed_time_day+" seconds\n");
					pred_cost = res.getData().getG();
					System.out.println("Prediciton Cost of UCS of day "+day+" is: "+pred_cost);
					writer.write("Prediciton Cost of UCS of day "+day+" is: "+pred_cost+"\n");
					pred_total_ucs=pred_total_ucs+pred_cost;
					
					temp_cost = pf.CalculateActualCost(data, res,day);
					System.out.println("Actual Cost of UCS of day "+day+" is: "+temp_cost);
					writer.write("Actual Cost of UCS of day "+day+" is: "+temp_cost+"\n");
					
					
					cost_total_ucs=cost_total_ucs+temp_cost;
					streets="";
					while(res!=null && res.isRoot()==false){//To save
						//System.out.println(res.getData().getName());
						//System.out.println("Street:"+res.getData().getStreet());
						street_cost="";
						for(i=0;i<dstreets.length;i++){
							if(dstreets[i].equals(res.getData().getStreet())){
								street_cost=String.valueOf(pf.CalculateStreetCost(data, i, day));
							}
						}
						if(streets.equals("")){
							streets=res.getData().getStreet()+" with estimated cost:"+street_cost;
						}
						else{
							streets=res.getData().getStreet()+" with estimated cost:"+street_cost+", "+streets;
						}
						// save to file
						res=res.getParent();
					}
					System.out.println("Recommended Route:"+streets);
					writer.write("Recommended Route: "+streets+"\n");
					System.out.println("Total prediction cost (UCS) until this day is: "+pred_total_ucs);
					writer.write("Total prediction cost (UCS) until this day is: "+pred_total_ucs+"\n");
					System.out.println("Total actual cost (UCS) until this day is: "+cost_total_ucs);
					writer.write("Total actual cost (UCS) until this day is: "+cost_total_ucs+"\n");
					
					writer.write("\n");
					System.out.println("IDA*");
					writer.write("Iterative Deepening A*\n");
					pf_tree=pf.createTree(data);
					Explored.clear();
					Expanded.clear();
					
					Explored.add(pf_tree);
					/*
					System.gc();
					memoryB = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					*/
					start_time_day = System.currentTimeMillis();
					
					res=pf.IDAstar(ht,data, pf_tree, 0, Expanded, Explored, ht.DijkstraHeur(data,data.getSource(),data.getDestination(),day),day,writer);
					stop_time_day = System.currentTimeMillis();
					elapsed_time_day = (stop_time_day-start_time_day)*0.001;
					
					total_time_idastar=total_time_idastar+elapsed_time_day;
					
					/*
					System.gc();
					memoryA = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					System.out.println("Memory Used:"+((memoryB-memoryA)/1000)+" KBytes");
					writer.write("Memory Used:"+((memoryB-memoryA)/1000)+" KBytes\n");
					*/
					
				
					System.out.println("Execution time of IDA* for day "+(day+1)+" is: "+elapsed_time_day+" seconds");
					writer.write("Execution time of IDA* for day "+(day+1)+" is: "+elapsed_time_day+" seconds\n");
					
					
					
					//Node<Vertex> res=pf.LRTAstar(data, pf_tree, 0, Expanded, Explored);
					
					pred_cost = res.getData().getG();
					System.out.println("Prediciton Cost of IDA* for day "+(day+1)+" is: "+pred_cost);
					writer.write("Prediciton Cost of IDA* for day "+(day+1)+" is: "+pred_cost+"\n");
					
					pred_total_ida=pred_total_ida+pred_cost;
					
					temp_cost = pf.CalculateActualCost(data, res,day);
					System.out.println("Actual Cost of IDA* for day "+(day+1)+" is: "+temp_cost);
					writer.write("Actual Cost of IDA* for day "+(day+1)+" is: "+temp_cost+"\n");
					
					cost_total_ida=cost_total_ida+temp_cost;
					streets="";
					while(res!=null && res.isRoot()==false){//To save later
						//System.out.println(res.getData().getName());
						street_cost="";
						for(i=0;i<dstreets.length;i++){
							if(dstreets[i].equals(res.getData().getStreet())){
								street_cost=String.valueOf(pf.CalculateStreetCost(data, i, day));
							}
						}
						if(streets.equals("")){
							streets=res.getData().getStreet()+" with estimated cost:"+street_cost;
						}
						else{
							streets=res.getData().getStreet()+" with estimated cost:"+street_cost+", "+streets;
						}
						
						res=res.getParent();
					}
					
					System.out.println("Recommended Route: "+streets);
					writer.write("Recommended Route: "+streets+"\n");
					
					System.out.println("Total prediction cost (IDA*) until this day is: "+pred_total_ida);
					writer.write("Total prediction cost (IDA*) until this day is: "+pred_total_ida+"\n");
					System.out.println("Total actual cost (IDA*) until this day is: "+cost_total_ida);
					writer.write("Total actual cost (IDA*) until this day is: "+cost_total_ida+"\n");
					
					Explored.clear();
					Expanded.clear();
				}
				
				System.out.println("\nAverage Actual Cost for 80 days:\n");
				writer.write("\n\nAverage Actual Cost for 80 days:\n\n");
				System.out.println("UCS: The average cost per day, for 1 month (80 days) is: "+(cost_total_ucs/80));
				writer.write("UCS: The average cost per day, for 1 month (80 days) is: "+(cost_total_ucs/80)+"\n");
				System.out.println("IDA*: The average cost per day, for 1 month (80 days) is: "+(cost_total_ida/80));
				writer.write("IDA*: The average cost per day, for 1 month (80 days) is: "+(cost_total_ida/80)+"\n");
				writer.write("\nAverage Time needed:\n\n");
				System.out.println("UCS: The Average time needed per day, for 1 month(80 days) is: "+(total_time_ucs/80)+" seconds");
				writer.write("UCS: The Average time needed per day, for 1 month(80 days) is: "+(total_time_ucs/80)+" seconds\n");
				System.out.println("IDA*: The Average time needed per day, for 1 month(80 days) is: "+(total_time_idastar/80)+" seconds");
				writer.write("IDA*: The Average time needed per day, for 1 month(80 days) is: "+(total_time_idastar/80)+" seconds\n");
				
				writer.close();
				System.out.println("Results are contained in file OfflineResults"+filename);

			}
			else{
				double total_cost=0;
				double total_time=0;
				BufferedWriter writer = new BufferedWriter( new FileWriter("OnlineResults"+filename));
				
				writer.write("Results for filename:"+filename+"\n");
				DateFormat df=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date date=new Date();
				
				writer.write("File created: "+df.format(date)+"\n");
				writer.write("\nSource is:"+data.getSource()+"\n");
				writer.write("Destination is:"+data.getDestination()+"\n");
				writer.write("Number of Streets:"+data.getRoads().length+"\n");
				
				
				int day;
				data.NewPredictions(0.6, 0.2, 0.2);
				for(day=0;day<80;day++){
					data.Estimate(day);
					System.out.println("\nDay:"+(day+1));
					writer.write("\n\nDay "+(day+1)+":\n");
					System.out.println("LRTA*");
					writer.write("LRTA* - Learning Real-Time A*\n");
					pf_tree=pf.createTree(data);
					Explored.add(pf_tree);
					writer.write("Path followed(with actual cost):");
					
					start_time_day = System.currentTimeMillis();
					/*
					System.gc();
					memoryB = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					*/
					
					total_cost=total_cost+pf.runLRTA(ht,data,0,writer);//pf.LRTAstar(ht,data, pf_tree, 0, Expanded, Explored, day,0,writer);
					stop_time_day = System.currentTimeMillis();
					
					elapsed_time_day = (stop_time_day-start_time_day)*0.001;
					total_time=total_time+elapsed_time_day;
					/*
					System.gc();
					memoryA = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					System.out.println("Memory Used:"+((memoryB-memoryA)/1000)+" KBytes");
					writer.write("Memory Used:"+((memoryB-memoryA)/1000)+" KBytes\n");
					*/
					
					System.out.println("Execution time of LRTA* for day "+(day+1)+" is: "+elapsed_time_day+" seconds");
					
					writer.write("Execution time of LRTA* for day "+(day+1)+" is: "+elapsed_time_day+" seconds\n");
					
					Explored.clear();
					Expanded.clear();
					
				}
				
				System.out.println("The average cost per day, for 1 month (80 days) is: "+(total_cost/80));
				writer.write("\n\nThe Average cost per day, for 1 month (80 days) is: "+(total_cost/80)+"\n");
				System.out.println("The Average time needed per day, for 1 month(80 days) is: "+(total_time/80)+" seconds");
				writer.write("The Average time needed per day, for 1 month(80 days) is: "+(total_time/80)+" seconds\n");
				
				writer.close();
				System.out.println("Results are contained in file OnlineResults"+filename);
			}
			
		}
		catch(IOException e){
			System.out.println("Error on file");
			e.printStackTrace();
		}
		catch(Exception e){
			System.out.println("Error");
			e.printStackTrace();
		}
		
		
		
		
	}
	
	
}
