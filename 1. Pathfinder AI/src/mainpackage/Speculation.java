package mainpackage;
//Contains probabilities for every of the 3 possible predictions
//a method to evaluate this information each day, and a method that returns the evaluated prediction
public class Speculation {
	private double[] p_heavy;
	//p[0]=Pcorrect,p[1]=Perror(normal),p[2]=Perror(low)
	private int hcount;
	private String heavyPred;
	
	private double[] p_normal;
	//p[0]=Pcorrect,p[1]=Perror(heavy),p[2]=Perror(low)
	private int ncount;
	private String normalPred;
	
	private double[] p_low;
	//p[0]=Pcorrect,p[1]=Perror(heavy),p[2]=Perror(normal)
	private int lcount;
	private String lowPred;
	
	
	public Speculation(double p1,double p2,double p3){
		p_heavy=new double[3];
		p_normal=new double[3];
		p_low=new double[3];
		
		p_heavy[0]=p1;
		p_normal[0]=p1;
		p_low[0]=p1;

		p_heavy[1]=p2;
		p_normal[1]=p2;
		p_low[1]=p2;

		p_heavy[2]=p2;
		p_normal[2]=p2;
		p_low[2]=p2;
		
		hcount=0;
		ncount=0;
		lcount=0;
		
		heavyPred="heavy";
		normalPred="normal";
		lowPred="low";
	}
	
	public void evaluateP(FileData data,int day){
		String[] predictions;
		String[] actual;
		
		predictions=data.getPrediction_traffic()[day];
		if(day==200){
		System.out.println(predictions[day]);}
		actual=data.getActual_traffic()[day];
		
		int l=predictions.length;
		int i;
		double [] ph=new double[3];
		double [] pn=new double[3];
		double [] pl=new double[3];
		
		for(i=0;i<3;i++){
			ph[i]=hcount*p_heavy[i];
			pn[i]=ncount*p_normal[i];
			pl[i]=lcount*p_low[i];
		}
		
		String spec;
		
		for(i=0;i<l;i++){
			spec=predictions[i];
			if(spec.equals("heavy")){
				if(actual[i].equals("heavy")){
					ph[0]=ph[0]+1;
				}
				else if(actual[i].equals("normal")){
					ph[1]=ph[1]+1;
				}
				else{
					ph[2]=ph[2]+1;
				}
				hcount=hcount+1;
			}
			else if(spec.equals("normal")){
				if(actual[i].equals("normal")){
					pn[0]=pn[0]+1;
				}
				else if(actual[i].equals("heavy")){
					pn[1]=pn[1]+1;
				}
				else{
					pn[2]=pn[2]+1;
				}
				ncount=ncount+1;
			}
			else{
				if(actual[i].equals("low")){
					pl[0]=pl[0]+1;
				}
				else if(actual[i].equals("heavy")){
					pl[1]=pl[1]+1;
				}
				else{
					pl[2]=pl[2]+1;
				}
				lcount=lcount+1;
			}
		}
		
		double maxh=0;
		double maxn=0;
		double maxl=0;
		
		
		for(i=0;i<3;i++){
			p_heavy[i]=ph[i]/hcount;
			
			if(p_heavy[i]>maxh){
				maxh=p_heavy[i];
				switch(i){
					case 0: heavyPred="heavy";
							break;
					case 1: heavyPred="normal";
							break;
					case 2: heavyPred="low";
							break;
				}
			}
			p_normal[i]=pn[i]/ncount;
			if(p_normal[i]>maxn){
				maxn=p_normal[i];
				switch(i){
					case 0: normalPred="normal";
							break;
					case 1: normalPred="heavy";
							break;
					case 2: normalPred="low";
							break;
				}
			}
			
			p_low[i]=pl[i]/lcount;
			
			if(p_low[i]>maxl){
				maxl=p_low[i];
				switch(i){
					case 0: lowPred="low";
							break;
					case 1: lowPred="heavy";
							break;
					case 2: lowPred="normal";
							break;
				}
			}
		}
		/*
		System.out.println("HeavyPredictions: p1="+p_heavy[0]+", p2="+p_heavy[1]+", p3="+p_heavy[2]);
		System.out.println("NormalPredictions: p1="+p_normal[0]+", p2="+p_normal[1]+", p3="+p_normal[2]);
		System.out.println("LowPredictions: p1="+p_low[0]+", p2="+p_low[1]+", p3="+p_low[2]);
		*/
	}
	
	
	public String getPrediction(String prediction){
		
		if(prediction.equals("heavy")){
			return heavyPred;
		}
		else if(prediction.equals("normal")){
			return normalPred;
		}
		else if(prediction.equals("low")){
			return lowPred;
		}
		
		return null;
	}
}
