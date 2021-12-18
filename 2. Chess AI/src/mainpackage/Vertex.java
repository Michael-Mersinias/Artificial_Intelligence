package mainpackage;

import java.util.ArrayList;

public class Vertex {
	private String[][] board = null;
	private int myColor = 0;
	private ArrayList<String> availableMoves = null;
	private String action;
	private double scoreWhite;
	private double scoreBlack;
	private int whitepawns;
	private int blackpawns;
	private int whiterooks;
	private int blackrooks;
	private int whiteking;
	private int blackking;
	private int depth;
	private boolean gameEnded;
	
	public Vertex(String[][] board,int myColor,ArrayList<String> avMoves,double scoreWhite,double scoreBlack,int depth,int whitepawns,int blackpawns,int whiterooks,int blackrooks,boolean gameEnded,String action){
		this.board=new String[7][5];
		evBoard(board);
		this.myColor=myColor;
		availableMoves=avMoves;
		this.scoreWhite=scoreWhite;
		this.scoreBlack=scoreBlack;
		this.depth=depth;
		this.gameEnded=gameEnded;
		this.whitepawns=whitepawns;
		this.blackpawns=blackpawns;
		this.action=new String(action);
		this.whiterooks=whiterooks;
		this.blackrooks=blackrooks;
		this.whiteking=1;
		this.blackking=1;
		
	}
	

	public void whitePresent(){
		scoreWhite=scoreWhite+0.7;//Average Points: 1*0.7 +0*0.3=0.7
	}
	public void blackPresent(){
		scoreBlack=scoreBlack+0.7;//Average Points: 1*0.7 +0*0.3=0.7
	}
	
	public void whitePawnEnd(){
		scoreWhite=scoreWhite+1;
		whitepawns=whitepawns-1;
		
		if(whitepawns==1 && blackpawns==1){
			gameEnded=true;
		}
	}
	
	public void blackPawnEnd(){
		scoreBlack=scoreBlack+1;
		blackpawns=blackpawns-1;
		
		if(whitepawns==1 && blackpawns==1){
			gameEnded=true;
		}
	}
	
	public void WhiteCaught(String pawn){
		whitepawns=whitepawns-1;
		if(whitepawns==1 && blackpawns==1){
			gameEnded=true;
		}
		if(pawn.equals("P")){
			scoreBlack=scoreBlack+1;
		}
		else if(pawn.equals("R")){
			scoreBlack=scoreBlack+3;
			whiterooks=whiterooks-1;
		}
		else{
			scoreBlack=scoreBlack+7;
			whiteking=0;
			gameEnded=true;
		}
	}
	public void BlackCaught(String pawn){
		blackpawns=blackpawns-1;
		if(whitepawns==1 && blackpawns==1){
			gameEnded=true;
		}
		if(pawn.equals("P")){
			scoreWhite=scoreWhite+1;
		}
		else if(pawn.equals("R")){
			scoreWhite=scoreWhite+3;
			blackrooks=blackrooks-1;
		}
		else{
			scoreWhite=scoreWhite+7;
			blackking=0;
			gameEnded=true;
		}
	}
	public void evScoreBlack(double score){
		this.scoreBlack=score;
	}
	public void evScoreWhite(double score){
		this.scoreWhite=score;
	}
	public void evBoard(String[][] board){
		
		for(int i=0;i<7;i++){
			for(int j=0;j<5;j++){
				this.board[i][j]=new String(board[i][j]);
			}
		}
	}


	public String[][] getBoard() {
		return board;
	}
	
	
	public ArrayList<String> getAvailableMoves() {
		return availableMoves;
	}


	public String getAction() {
		return new String(action);
	}


	public int getMyColor() {
		
		return myColor;
	}


	public boolean isGameEnded() {
		return gameEnded;
	}


	public double getScoreWhite() {
		return scoreWhite;
	}


	public double getScoreBlack() {
		return scoreBlack;
	}


	public int getWhitepawns() {
		return whitepawns;
	}


	public int getBlackpawns() {
		return blackpawns;
	}


	public int getDepth() {
		return depth;
	}


	public void evWhitepawns(int whitepawns) {
		this.whitepawns = whitepawns;
	}


	public void evBlackpawns(int blackpawns) {
		this.blackpawns = blackpawns;
	}
	
	
	public void setColor(int mycolor){
		this.myColor=mycolor;
	}
	
	public void setAction(String action){
		this.action=action;
	}
	
	public void evWhiteRooks(int whiterooks){
		this.whiterooks=whiterooks;
	}
	
	public void evBlackRooks(int blackrooks){
		this.blackrooks=blackrooks;
	}
	
	public int getBlackRooks(){
		return blackrooks;
	}
	public int getWhiteRooks(){
		return whiterooks;
	}
	
	public int getWhiteKing(){
		return whiteking;
	}
	
	public int getBlackKing(){
		return blackking;
	}
	
}
