package mainpackage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//Contains Data from file that are needed throughout the Pathfinding process
//and a method to open the file and get the information we need
public class FileData {
	String[] roads;
	String[] nodeA;
	String[] nodeB;
	int[] cost;
	String source;
	String destination;
	String[][] prediction_traffic;
	String[][] actual_traffic;
	int avgcost;
	
	Speculation pr;
	
	public FileData(String[] roads,String[] nodeA,String[] nodeB,int[]cost, String[][] prediction_traffic,String[][] actual_traffic,String source, String destination){
		this.roads=roads;
		this.nodeA=nodeA;
		this.nodeB=nodeB;
		this.cost=cost;
		this.prediction_traffic=prediction_traffic;
		this.actual_traffic=actual_traffic;
		this.source=source;
		this.destination=destination;
		avgcost=0;
	}
	public FileData(){
		
	}

	public String[] getRoads() {
		return roads;
	}

	public int calculateAvgCost(){
		if(avgcost==0){
			int s=0,l=cost.length;
			for(int i=0;i<l;i++){
				s=s+cost[i];
			}
			avgcost=s/l;
		}
		return avgcost;
	}
	public String[] getNodeA() {
		return nodeA;
	}

	public String[] getNodeB() {
		return nodeB;
	}

	public int[] getCost() {
		return cost;
	}

	public String getSource() {
		return source;
	}

	public String getDestination() {
		return destination;
	}

	public String[][] getPrediction_traffic() {
		return prediction_traffic;
	}

	public String[][] getActual_traffic() {
		return actual_traffic;
	}
	
	
	public FileData test_Read(String filename) {
	       
        try {
            // helpful variables
            int i=0;
            int j=0;
            int num_of_days=80;
            String source_text="";
            String destination_text="";
            String Road_Text="";
            String Prediction_Text="";
            String Actual_Traffic_Text="";
            String[] prediction_per_day = new String[num_of_days];
            String[] actual_traffic_per_day = new String[num_of_days];
           
           
           
            // read the whole text
           
            String input_string = new String(Files.readAllBytes(Paths.get(filename)));
            input_string=input_string.replaceAll("[\r\n]+", " ; ");
           
           
            // get source and destination
           
            String regexString0 = Pattern.quote("<Source>") + "(.*?)" + Pattern.quote("</Source>");
            Pattern pattern0 = Pattern.compile(regexString0);
            Matcher matcher0 = pattern0.matcher(input_string);
            while (matcher0.find()) {
                source_text = matcher0.group(1);
            }
           
            String regexString1 = Pattern.quote("<Destination>") + "(.*?)" + Pattern.quote("</Destination>");
            Pattern pattern1 = Pattern.compile(regexString1);
            Matcher matcher1 = pattern1.matcher(input_string);
            while (matcher1.find()) {
                destination_text = matcher1.group(1);
            }
           
            System.out.println("Source is: "+source_text);  // SOURCE
            System.out.println("Destination is: "+destination_text);    // DESTINATION
           
           
           
            // get roads and vertexes
           
            String regexString2 = Pattern.quote("<Roads>") + "(.*?)" + Pattern.quote("</Roads>");
            Pattern pattern2 = Pattern.compile(regexString2);
            Matcher matcher2 = pattern2.matcher(input_string);
            while (matcher2.find()) {
                Road_Text = matcher2.group(1);
            }
           
            // System.out.println(Road_Text);
            //System.out.println(Road_Text);
            int count=(Road_Text.length() -Road_Text.replace(";","").length()-1)/4;
            
            System.out.println("Number of Streets:"+count);
            String [] Road=new String[count];
            String [] NodeA=new String[count];
            String [] NodeB=new String[count];            
            int [] cost=new int [count];
            
           
            Scanner road_s = new Scanner(Road_Text);
            road_s.useDelimiter("\\s*;\\s*");
           
            while(road_s.hasNext()) {
            	
            	Road[i]=road_s.next();
            	NodeA[i]=road_s.next();
            	NodeB[i]=road_s.next();
            	cost[i]=road_s.nextInt();
            	//System.out.println("Street:"+Road[i]+",NodeA:"+NodeA[i]+",NodeB:"+NodeB[i]+",cost:"+cost[i]);
                i++;
            }
            
            road_s.close();
           
   
           
            // get predictions...
           
            String regexString3 = Pattern.quote("<Predictions>") + "(.*?)" + Pattern.quote("</Predictions>");
            Pattern pattern3 = Pattern.compile(regexString3);
            Matcher matcher3 = pattern3.matcher(input_string);
            while (matcher3.find()) {
                Prediction_Text = matcher3.group(1);
            }
           
            //System.out.println(Prediction_Text);
           
           
            // ...for all days
           
            String regexString4 = Pattern.quote("<Day>") + "(.*?)" + Pattern.quote("</Day>");
            Pattern pattern4 = Pattern.compile(regexString4);
            Matcher matcher4 = pattern4.matcher(Prediction_Text);
            
            while (matcher4.find()) {
                prediction_per_day[j] = matcher4.group(1);
                //System.out.println(j+" day: "+prediction_per_day[j]);
                j++;
            }
            j=0;
            i=0;
            int k=0;
            String pred;
            String pred_cost;
            String prediction_cost[][]=new String[num_of_days][count];
            while(j<num_of_days) {
                Scanner prediction_s = new Scanner(prediction_per_day[j]);
                prediction_s.useDelimiter("\\s*;\\s*");
                //System.out.println("Start");
                while(prediction_s.hasNext()) {
                    //System.out.println(i+": "+prediction_s.next());     // PREDICTIONS OF DAY J, 0<J<NUM_OF_DAYS
                	
                	if(i%2==0){
                		pred=prediction_s.next();
                		//System.out.println(pred);
                		for(k=0;k<count;k++){
                			if(Road[k].equals(pred)){
                				break;
                			}
                		}
                		if(k==count){
                			System.out.println("Could not find Street in Predictions!");
                			prediction_s.close();                			
                			return null;
                		}
                	}
                	else{
                		pred_cost=prediction_s.next();
                		
                		//System.out.println(pred_cost);
                		prediction_cost[j][k]=pred_cost;
                	}
                	
                    i++;
                }
                prediction_s.close();
                j++;
            }
            j=0;
            i=0;

           
            String actual_cost[][]=new String[num_of_days][count];
            // get actual traffic...
           
            String regexString5 = Pattern.quote("<ActualTrafficPerDay>") + "(.*?)" + Pattern.quote("</ActualTrafficPerDay>");
            Pattern pattern5 = Pattern.compile(regexString5);
            Matcher matcher5 = pattern5.matcher(input_string);
            while (matcher5.find()) {
                Actual_Traffic_Text = matcher5.group(1);
            }
           
            //System.out.println(Actual_Traffic_Text);
           
           
            // ...for all days
           
            String regexString6 = Pattern.quote("<Day>") + "(.*?)" + Pattern.quote("</Day>");
            Pattern pattern6 = Pattern.compile(regexString6);
            Matcher matcher6 = pattern6.matcher(Actual_Traffic_Text);
            while (matcher6.find()) {
                actual_traffic_per_day[j] = matcher6.group(1);
                //System.out.println(j+" day: "+actual_traffic_per_day[j]);
                j++;
            }
            j=0;
            i=0;
           
            while(j<num_of_days) {
                Scanner actual_traffic_s = new Scanner(actual_traffic_per_day[j]);
                actual_traffic_s.useDelimiter("\\s*;\\s*");
           
                while(actual_traffic_s.hasNext()) {
                    //System.out.println(i+": "+actual_traffic_s.next());     // ACTUAL TRAFFIC OF DAY J, 0<J<NUM_OF_DAYS
                	if(i%2==0){
                		pred=actual_traffic_s.next();
                		for(k=0;k<count;k++){
                			if(Road[k].equals(pred)){
                				break;
                			}
                		}
                		if(k==count){
                			System.out.println("Could not find Street in Actual traffic!");
                			actual_traffic_s.close();
                			return null;
                		}
                	}
                	else{
                		pred_cost=actual_traffic_s.next();
                		actual_cost[j][k]=pred_cost;
                	}
                    i++;
                }
                actual_traffic_s.close();
                j++;
            }
            j=0;
            i=0;
             return new FileData(Road,NodeA,NodeB,cost,prediction_cost,actual_cost,source_text,destination_text);
           
        } catch (IOException e) {
            System.out.println("File error!");
            e.printStackTrace();
        }
		return null;
       
    }
	
	public void NewPredictions(double p1,double p2,double p3){
		pr=new Speculation(p1,p2,p3);
	}
	
	public Speculation getSpeculation(){
		return pr;
	}
	
	public void Estimate(int day){
		pr.evaluateP(this, day);
	}
	
	public String getPrediction(String pred){
		return pr.getPrediction(pred);
	}
}
