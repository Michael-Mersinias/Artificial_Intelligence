package mainpackage;


import java.util.ArrayList;
import java.util.Iterator;

public class World {
	private String[][] board = null;
	private int rows = 7;
	private int columns = 5;
	private int myColor = 0;
	private ArrayList<String> availableMoves = null;
	private int rookBlocks = 3;		// rook can move towards <rookBlocks> blocks in any vertical or horizontal direction
	private int nTurns = 0;
	private int nBranches = 0;
	private int noPrize = 9;
	private Node<Vertex> MinimaxTree = null;
	private int MaxDepth = 4;
	
	public World()
	{
		board = new String[rows][columns];
		
		/* represent the board
		
		BP|BR|BK|BR|BP
		BP|BP|BP|BP|BP
		--|--|--|--|--
		P |P |P |P |P 
		--|--|--|--|--
		WP|WP|WP|WP|WP
		WP|WR|WK|WR|WP
		*/
		
		// initialization of the board
		for(int i=0; i<rows; i++)
			for(int j=0; j<columns; j++)
				board[i][j] = " ";
		
		// setting the black player's chess parts
		
		// black pawns
		for(int j=0; j<columns; j++)
			board[1][j] = "BP";
		
		board[0][0] = "BP";
		board[0][columns-1] = "BP";
		
		// black rooks
		board[0][1] = "BR";
		board[0][columns-2] = "BR";
		
		// black king
		board[0][columns/2] = "BK";
		
		// setting the white player's chess parts
		
		// white pawns
		for(int j=0; j<columns; j++)
			board[rows-2][j] = "WP";
		
		board[rows-1][0] = "WP";
		board[rows-1][columns-1] = "WP";
		
		// white rooks
		board[rows-1][1] = "WR";
		board[rows-1][columns-2] = "WR";
		
		// white king
		board[rows-1][columns/2] = "WK";
		
		// setting the prizes
		for(int j=0; j<columns; j++)
			board[rows/2][j] = "P";
		
		availableMoves = new ArrayList<String>();
	}
	
	public void setMyColor(int myColor)
	{
		this.myColor = myColor;
	}
	
	public String selectAction()
	{
		
		//System.gc();
		availableMoves.clear();
		
				
		if(myColor == 0)		// I am the white player
			this.whiteMoves(null);
		else					// I am the black player
			this.blackMoves(null);
		
		// keeping track of the branch factor
		nTurns++;
		nBranches += availableMoves.size();
		/*
		if(MinimaxTree==null){
			Vertex head=new Vertex(board,myColor,availableMoves,0,0,1,10,10,false,null);
			MinimaxTree=new Node<Vertex>(head);
		}
		*/
		double alpha=-Double.MAX_VALUE;
		double beta=Double.MAX_VALUE;
		
		Move mymove=MiniMax(MaxDepth,myColor,MinimaxTree,alpha,beta);
		if(nTurns>2){
			
			String myres = mymove.getAction();
			if(availableMoves.contains(myres)){
				return myres;
			}
			else{
				System.out.println("Error!");
				return availableMoves.get(0);
			}
		}
		else{
			if(nTurns==1){
				if(myColor==0){
					return "5141";
				}
				else{
					return "1121";
				}
			}
			else{
				if(myColor==0){
					return "5343";
				}
				else{
					return "1323";
				}
			}
		}
		
	
		
	}
	
	public void generateGameTree(int myColor){
		availableMoves = new ArrayList<String>();
		setMyColor(myColor);
		if(myColor==1){		
			this.whiteMoves(null);
		}
		String[][] tboard=copyBoard(board);
		
		Vertex head=new Vertex(tboard,0,availableMoves,0,0,1,10,10,2,2,false,"0000");
		MinimaxTree=new Node<Vertex>(head);
		if(myColor==0){
			return;
		}
		else{
			//this.blackMoves(null);
			String t_action;
			Node<Vertex> c_node; 
			Vertex child;
			int x1,x2,y1,y2;
			for(int i=0;i<availableMoves.size();i++){
				t_action=availableMoves.get(i);
				child=new Vertex(tboard,1,new ArrayList<String>(),0,0,2,10,10,2,2,false,t_action);
				c_node=MinimaxTree.addChild(child);
				x1=Integer.parseInt(t_action.substring(0,1));
				y1=Integer.parseInt(t_action.substring(1,2));
				x2=Integer.parseInt(t_action.substring(2,3));
				y2=Integer.parseInt(t_action.substring(3,4));
				makeMove(c_node,x1,y1,x2,y2,9,9,0,0);
			}
			
		}
		
	}
	
	private void whiteMoves(Node<Vertex> tree)
	{
		
		String firstLetter = "";
		String secondLetter = "";
		String move = "";
		ArrayList<String> avMoves;
		String [][] brd;
		if(tree==null){
			avMoves=availableMoves;
			brd=board;
		}
		else{
			avMoves=tree.getData().getAvailableMoves();
			brd=tree.getData().getBoard();
		}
				
		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				firstLetter = Character.toString(brd[i][j].charAt(0));
				
				// if it there is not a white chess part in this position then keep on searching
				if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;
				
				// check the kind of the white chess part
				secondLetter = Character.toString(brd[i][j].charAt(1));
				
				if(secondLetter.equals("P"))	// it is a pawn
				{
					// check if it can move towards the last row
					if(i-1 == 0 && (Character.toString(brd[i-1][j].charAt(0)).equals(" ") 
							         || Character.toString(brd[i-1][j].charAt(0)).equals("P")))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
						       Integer.toString(i-1) + Integer.toString(j);
						
						avMoves.add(move);
						continue;
					}
					
					// check if it can move one vertical position ahead
					firstLetter = Character.toString(brd[i-1][j].charAt(0));
					
					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-1) + Integer.toString(j);
						
						avMoves.add(move);
					}
					
					// check if it can move crosswise to the left
					if(j!=0 && i!=0)
					{
						firstLetter = Character.toString(brd[i-1][j-1].charAt(0));
						
						if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
							continue;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-1) + Integer.toString(j-1);
						
						avMoves.add(move);
					}
					
					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=0)
					{
						firstLetter = Character.toString(brd[i-1][j+1].charAt(0));
						
						if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
							continue;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-1) + Integer.toString(j+1);
						
						avMoves.add(move);
					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(brd[i-(k+1)][j].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-(k+1)) + Integer.toString(j);
						
						avMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == rows)
							break;
						
						firstLetter = Character.toString(brd[i+(k+1)][j].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+(k+1)) + Integer.toString(j);
						
						avMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(brd[i][j-(k+1)].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j-(k+1));
						
						avMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == columns)
							break;
						
						firstLetter = Character.toString(brd[i][j+(k+1)].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j+(k+1));
						
						avMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(brd[i-1][j].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i-1) + Integer.toString(j);
								
							avMoves.add(move);	
						}
					}
					
					// check if it can move downwards
					if((i+1) < rows)
					{
						firstLetter = Character.toString(brd[i+1][j].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i+1) + Integer.toString(j);
								
							avMoves.add(move);	
						}
					}
					
					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(brd[i][j-1].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j-1);
								
							avMoves.add(move);	
						}
					}
					
					// check if it can move on the right
					if((j+1) < columns)
					{
						firstLetter = Character.toString(brd[i][j+1].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j+1);
								
							avMoves.add(move);	
						}
					}
				}			
			}	
		}
		
	}
	
	private void blackMoves(Node<Vertex> tree)
	{
		String firstLetter = "";
		String secondLetter = "";
		String move = "";
		ArrayList<String> avMoves;
		String [][] brd;
		if(tree==null){
			avMoves=availableMoves;
			brd=board;
		}
		else{
			avMoves=tree.getData().getAvailableMoves();
			brd=tree.getData().getBoard();
		}
				
		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				firstLetter = Character.toString(brd[i][j].charAt(0));
				
				// if it there is not a black chess part in this position then keep on searching
				if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;
				
				// check the kind of the white chess part
				secondLetter = Character.toString(brd[i][j].charAt(1));
				
				if(secondLetter.equals("P"))	// it is a pawn
				{
					// check if it is at the last row
					if(i+1 == rows-1 && (Character.toString(brd[i+1][j].charAt(0)).equals(" ")
										  || Character.toString(brd[i+1][j].charAt(0)).equals("P")))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
						       Integer.toString(i+1) + Integer.toString(j);
						
						avMoves.add(move);
						continue;
					}
					
					// check if it can move one vertical position ahead
					firstLetter = Character.toString(brd[i+1][j].charAt(0));
					
					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+1) + Integer.toString(j);
						
						avMoves.add(move);
					}
					
					// check if it can move crosswise to the left
					if(j!=0 && i!=rows-1)
					{
						firstLetter = Character.toString(brd[i+1][j-1].charAt(0));
						
						if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
							continue;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+1) + Integer.toString(j-1);
						
						avMoves.add(move);
					}
					
					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=rows-1)
					{
						firstLetter = Character.toString(brd[i+1][j+1].charAt(0));
						
						if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
							continue;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+1) + Integer.toString(j+1);
						
						avMoves.add(move);
					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(brd[i-(k+1)][j].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-(k+1)) + Integer.toString(j);
						
						avMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == rows)
							break;
						
						firstLetter = Character.toString(brd[i+(k+1)][j].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+(k+1)) + Integer.toString(j);
						
						avMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(brd[i][j-(k+1)].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j-(k+1));
						
						avMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == columns)
							break;
						
						firstLetter = Character.toString(brd[i][j+(k+1)].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j+(k+1));
						
						avMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(brd[i-1][j].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i-1) + Integer.toString(j);
								
							avMoves.add(move);	
						}
					}
					
					// check if it can move downwards
					if((i+1) < rows)
					{
						firstLetter = Character.toString(brd[i+1][j].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i+1) + Integer.toString(j);
								
							avMoves.add(move);	
						}
					}
					
					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(brd[i][j-1].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j-1);
								
							avMoves.add(move);	
						}
					}
					
					// check if it can move on the right
					if((j+1) < columns)
					{
						firstLetter = Character.toString(brd[i][j+1].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j+1);
								
							avMoves.add(move);	
						}
					}
				}			
			}	
		}
	}
	

	
	public double getAvgBFactor()
	{
		return nBranches / (double) nTurns;
	}
	
	public void makeMove(Node<Vertex> tree,int x1, int y1, int x2, int y2, int prizeX, int prizeY,int scoreWhite,int scoreBlack)
	{	
		
		String [][] brd;
		if(tree==null){
			brd=board;
			
		}
		else{
			
			//System.out.println("B4:Creating Tree, Player:"+tree.getData().getMyColor()+", depth:"+tree.getData().getDepth()+", action:"+tree.getData().getAction()+", whiteScore:"+tree.getData().getScoreWhite()+", blackScore:"+tree.getData().getScoreBlack());
			//printBoard(tree.getData().getBoard());
			brd=tree.getData().getBoard();
			if(brd[x2][y2].equals("P")){
				if(brd[x1][y1].substring(0, 1).equals("W")){
					tree.getData().whitePresent();
				}
				else{
					tree.getData().blackPresent();
				}
			}
			else if(brd[x2][y2].substring(0,1).equals("W")){
				tree.getData().WhiteCaught(brd[x2][y2].substring(1,2));
			}
			else if(brd[x2][y2].substring(0,1).equals("B")){
				tree.getData().BlackCaught(brd[x2][y2].substring(1,2));
			}
		}
		
		String chesspart = Character.toString(brd[x1][y1].charAt(1));
		
		boolean pawnLastRow = false;
		
		// check if it is a move that has made a move to the last line
		if(chesspart.equals("P"))
			if( (x1==rows-2 && x2==rows-1) || (x1==1 && x2==0) )
			{
				brd[x2][y2] = " ";	// in a case an opponent's chess part has just been captured
				brd[x1][y1] = " ";
				pawnLastRow = true;
				if(tree!=null){
					if(x1==rows-2){
						tree.getData().blackPawnEnd();
					}
					else{
						tree.getData().whitePawnEnd();
					}
				}
			}
		
		// otherwise
		if(!pawnLastRow)
		{
			brd[x2][y2] = brd[x1][y1];
			brd[x1][y1] = " ";
		}
		String action=new String(""+x1+y1+x2+y2);
		// check if a prize has been added in the game
		if(prizeX != noPrize){
			brd[prizeX][prizeY] = "P";
			//System.out.println("Prize added:"+prizeX+" "+prizeY+" on action:"+action);
			}
		
		//System.out.println("action:"+action);
		
		Node<Vertex> t_i;
		
		if(tree==null){//Find Action in Tree Children and evaluate board & score
			boolean check=false;
			ArrayList<Node<Vertex>> ch=MinimaxTree.getChildren();
			int i;
			for(i=0;i<ch.size();i++){
				t_i=ch.get(i);
				if(t_i.getData().getAction().equals(action)){
					check=true;
					t_i.getData().evBoard(brd);
					t_i.getData().evScoreBlack(scoreBlack);
					t_i.getData().evScoreWhite(scoreWhite);
							
					MinimaxTree=t_i;
					ch.clear();	
					MinimaxTree.setParent(null);
					
					break;
				}
				
			}
			if(check==false){
				//System.out.println("Error!!");
				//System.out.println(action);
				//printBoard(brd);
				//printBoard(MinimaxTree.getData().getBoard());
				
				Vertex mt=MinimaxTree.getData();
				int nextmovecolor;
				if(mt.getMyColor()==0){
					nextmovecolor=1;
				}
				else{
					nextmovecolor=0;
				}
				Vertex v=new Vertex(mt.getBoard(),nextmovecolor,new ArrayList<String>(),mt.getScoreWhite(),mt.getScoreBlack(),(mt.getDepth()+1),mt.getWhitepawns(),mt.getBlackpawns(),mt.getWhiteRooks(),mt.getBlackRooks(),false,action);
				t_i=MinimaxTree.addChild(v);
				makeMove(t_i,x1,y1,x2,y2,9,9,0,0);
				MinimaxTree=t_i;
				ch.clear();
				MinimaxTree.setParent(null);
			}
			
		}
		if(tree==null){
			//System.out.println("Actual Move, Player:"+brd[x2][y2].substring(0,1));
			//printBoard(brd);
		}
		else{
			
			//System.out.println("Creating Tree, Player:"+tree.getData().getMyColor()+", depth:"+tree.getData().getDepth()+", action:"+tree.getData().getAction()+", whiteScore:"+tree.getData().getScoreWhite()+", blackScore:"+tree.getData().getScoreBlack());
			//printBoard(tree.getData().getBoard());
		}
	
	}
	
	private void generateGameLeafs(Node<Vertex> tree,int depth){
		if(tree.getData().isGameEnded() || depth==0){
			
			return;
		}
		boolean hasAlreadyChildren=!(tree.isLeaf());
		int thismovecolor=tree.getData().getMyColor();
		ArrayList<String> availMoves=tree.getData().getAvailableMoves();
		availMoves.clear();
		
		if(thismovecolor == 0)		// I am the white player
			whiteMoves(tree);
		else										// I am the black player
			blackMoves(tree);
		
			
		
		
		int nextmovecolor;
		if(thismovecolor==1){
			nextmovecolor=0;
		}
		else{
			nextmovecolor=1;
		}
		
		String [][]nboard=copyBoard(tree.getData().getBoard());
		
		double scorewhite=tree.getData().getScoreWhite();
		double scoreblack=tree.getData().getScoreBlack();
		int c_depth=tree.getData().getDepth()+1;
		int whitepawns=tree.getData().getWhitepawns();
		int blackpawns=tree.getData().getBlackpawns();
		int whiterooks=tree.getData().getWhiteRooks();
		int blackrooks=tree.getData().getBlackRooks();
		int i,f_i=0;
		int x1,y1,x2,y2;
		String t_action;
		Node<Vertex> c_node;
		Vertex child;
		ArrayList<Node<Vertex>> children=tree.getChildren();
		boolean isGood=true;
		
		for(i=0;i<availMoves.size();i++){
			t_action=availMoves.get(i);
			
			if(hasAlreadyChildren==false || isGood==false){				
				child=new Vertex(nboard,nextmovecolor,new ArrayList<String>(),scorewhite,scoreblack,c_depth,whitepawns,blackpawns,whiterooks,blackrooks,false,t_action);
				c_node=tree.addChild(child);
			}
			else{
				c_node=children.get(i);
				if(t_action.equals(c_node.getData().getAction())){				
					c_node.getData().evBoard(nboard);
					c_node.getData().evScoreBlack(scoreblack);
					c_node.getData().evScoreWhite(scorewhite);
				}
				else{
					//System.out.println("Is happening!"+" Action:"+c_node.getData().getAction());
					//printBoard(nboard);
					isGood=false;
					f_i=i;
					while(f_i<children.size()){
						c_node=children.get(f_i);
						c_node.getChildren().clear();
						c_node=null;
						children.remove(f_i);						
					}
					
					child=new Vertex(nboard,nextmovecolor,new ArrayList<String>(),scorewhite,scoreblack,c_depth,whitepawns,blackpawns,whiterooks,blackrooks,false,t_action);
					c_node=tree.addChild(child);
					
					
					
				}
				
			}
			x1=Integer.parseInt(t_action.substring(0,1));
			y1=Integer.parseInt(t_action.substring(1,2));
			x2=Integer.parseInt(t_action.substring(2,3));
			y2=Integer.parseInt(t_action.substring(3,4));
			makeMove(c_node,x1,y1,x2,y2,9,9,0,0);
			//generateGameLeafs(c_node,depth+1);
		}
		
		while(availMoves.size()<children.size()){
			
			c_node=children.get(availMoves.size());
			c_node.getChildren().clear();
			c_node=null;
			children.remove(availMoves.size());
		}
		
		if(availMoves.size()!=children.size()){
			System.out.println("Shit!");
			for(i=0;i<availMoves.size();i++){
				System.out.println(availMoves.get(i));
			}
			System.out.println("Ch");
			for(i=0;i<children.size();i++){
				System.out.println(children.get(i).getData().getAction());
			}
		}
		
		
	}
	
	
	/*
	private void printBoard(String[][] brd){
		for(int i=0;i<rows;i++){
			for(int j=0;j<columns;j++){
				System.out.print(brd[i][j]+"|");
			}
			System.out.print("\n\n");
		}
	}
	*/
	private String[][] copyBoard(String[][] brd){
		String[][] nb=new String[rows][columns];
		for(int i=0;i<rows;i++){
			for(int j=0;j<columns;j++){
				nb[i][j]=new String(brd[i][j]);
			}
		}
		return nb;
	}
	
	
	private Move MiniMax(int depth,int player,Node<Vertex> node,double alpha,double beta){
		
		Move mymove=new Move();
		double v;
		if(depth==0 || node.getData().isGameEnded()){
			if(node.getData().isGameEnded()){
				v=EvaluationM(node);
			}
			else{
				Node<Vertex> cn=copyNode(node);
				v=QuiescenceSearch(cn,5);
				cn=null;
			}
			mymove.setValue(v);
			mymove.setAction(node.getData().getAction());
			return mymove;
			
		}
		
		
		boolean isMax;
		int nextplayer;
		if(player==0){
			isMax=true;
			nextplayer=1;
		}
		else{
			isMax=false;
			nextplayer=0;
		}
		
		generateGameLeafs(node,depth);
		
		Iterator<Node<Vertex>> state=node.getChildren().iterator();
		//ArrayList<Node<Vertex>> ch=node.getChildren();
		Node<Vertex> st;
		double value;
		String vaction="";
		
		
		if(isMax){
			value=-Double.MAX_VALUE;
			//System.out.println(value);
			while(state.hasNext()){
				st=state.next();
				//st=ch.get(i);
				
				mymove=MiniMax(depth-1,nextplayer,st,alpha,beta);
				
				/*
				if(mymove.getValue()>=value){
					
					vaction=st.getData().getAction();
					
					value=mymove.getValue();
				}*/
				//i++;
				if(mymove.getValue()>alpha){
					alpha=mymove.getValue();
					vaction=st.getData().getAction();					
					value=mymove.getValue();
				//	System.out.println("Success alpha");
					
				}
				
				if(beta<=alpha){
					//value=beta;
					break;
				}
			}
			//System.out.println("White -> depth:"+(MaxDepth-depth)+", Action:"+mymove.getAction());
			if(value==-Double.MAX_VALUE){
			//	System.out.println("ERROR!");
			}
			
		}
		else{
			value=Double.MAX_VALUE;
			//System.out.println(value);
			while(state.hasNext()){
				st=state.next();
				//st=ch.get(i);
				
				mymove=MiniMax(depth-1,nextplayer,st,alpha,beta);
				/*
				if(mymove.getValue()<=value){
					
					vaction=st.getData().getAction();					
					
					value=mymove.getValue();
				}
				*/
				if(mymove.getValue()<beta){
					beta=mymove.getValue();
					vaction=st.getData().getAction();					
					value=mymove.getValue();
					//System.out.println("Success beta");
				}
				
				if(beta<=alpha){
					//value=alpha;
					break;
				}
				//i++;
			}
			//System.out.println("Black -> depth:"+(MaxDepth-depth)+", Action:"+mymove.getAction());
			if(value==Double.MAX_VALUE){
				//System.out.println("ERROR!");
			}
		}
		
		
		
		//System.out.println("Player:"+player+", Depth:"+(MaxDepth-depth)+", Action T:"+mymove.getAction());
		mymove.setAction(vaction);
		mymove.setValue(value);
		if(depth==MaxDepth){
			//System.out.println("Alpha:"+alpha+", beta:"+beta);
		}
		return mymove;
	}
	
	private class Move{
		private double value;
		private String maction;
		
		public Move(){
			value=0;
		}
		
		public void setValue(double value) {
	        this.value = value;
	    }
		
		public void setAction(String action){
			maction=action;
		}
		
		public double getValue(){
			return value;
		}
		public String getAction(){
			return maction;
		}
		
		
		
		
	}
	/*
	private double evaluateState(Node<Vertex> node){
		
		
		Vertex state=node.getData();
		int whitepawns=state.getWhitepawns();
		int blackpawns=state.getBlackpawns();
		double whitescore=state.getScoreWhite();
		double blackscore=state.getScoreBlack();
		
		double v=(whitepawns+whitescore)-(blackpawns+blackscore);
		
		return v;
	}
	*/
	private  double QuiescenceSearch(Node<Vertex> n,int depth){
		Node<Vertex> cn;
		double letitgo=EvaluationM(n),tmp;
		
		if(depth==0 || n.getData().isGameEnded()){
			return letitgo;
		}
		Vertex v=n.getData();
		ArrayList<String> moves=v.getAvailableMoves();
		moves.clear();
		int color=v.getMyColor();
		
		
		
		String action;
		String[][] brd=copyBoard(v.getBoard());
		int i,x1,y1,x2,y2;
		if(color==0){
			this.whiteMoves(n);
			
		}
		else{
			this.blackMoves(n);
			
		}
		for(i=0;i<moves.size();i++){
			action=moves.get(i);
			x1=Integer.parseInt(action.substring(0,1));
			y1=Integer.parseInt(action.substring(1,2));
			x2=Integer.parseInt(action.substring(2,3));
			y2=Integer.parseInt(action.substring(3,4));
			if(brd[x2][y2].substring(0,1).equals("W")){
				//v.evBoard(brd);
				//v.evBlackpawns(blackpawns);
				//v.evWhitepawns(whitepawns);
				//v.evScoreBlack(scoreblack);
				//v.evScoreWhite(scorewhite);	
				cn=copyNode(n);
				cn.getData().setColor(0);
				this.makeMove(cn,x1,y1,x2,y2,0,0,0,0);
				//v.setColor(0);
				tmp = QuiescenceSearch(cn,depth-1);
				cn=null;
				if(tmp<letitgo){
					letitgo=tmp;
				}				
			}
			if(brd[x2][y2].substring(0,1).equals("B")){
				
				cn=copyNode(n);
				cn.getData().setColor(1);
				this.makeMove(cn,x1,y1,x2,y2,0,0,0,0);
				//v.setColor(1);
				tmp = QuiescenceSearch(cn,depth-1);
				cn=null;
				if(tmp>letitgo){
					letitgo=tmp;
				}
				
			}
			
		}
		return letitgo;
		
	}
	
	
	private Node<Vertex> copyNode(Node<Vertex> n){
		
		Vertex v=n.getData();
		Vertex copyofv=new Vertex(v.getBoard(),v.getMyColor(),new ArrayList<String>(),v.getScoreWhite(),v.getScoreBlack(),v.getDepth(),v.getWhitepawns(),v.getBlackpawns(),v.getWhiteRooks(),v.getBlackRooks(),v.isGameEnded(),v.getAction());
		
		Node<Vertex> copyofn=new Node<Vertex>(copyofv);
		
		return copyofn;
	}
	/*
	private double gameType(Vertex v){
			
			int numOfPawns=(v.getBlackpawns()-v.getBlackRooks()-1)+(v.getWhitepawns()-v.getWhiteRooks()-1);
			
			if(numOfPawns>15){
				return 1;	//early game
			}
			else if(numOfPawns>10){
				return 1.1;		//middle game
			}
			else{
				return 1.3; 		//late game
			}
		}
	*/
	
	private double EvaluationM(Node<Vertex> n){
		Vertex v=n.getData();
		int WK=v.getWhiteKing();
		int BK=v.getBlackKing();
		int WR=v.getWhiteRooks();
		int BR=v.getBlackRooks();
		int WP=v.getWhitepawns()-WR-WK;
		int BP=v.getBlackpawns()-BR-BK;
		double scorewhite=v.getScoreWhite();
		double scoreblack=v.getScoreBlack();
		
		
		double center_control=center_control(v.getBoard());
		
		double ev=3*(WR-BR)+(WP-BP)+0.1*center_control+(scorewhite-scoreblack);
		
		if(v.isGameEnded())
			return 10*(WK-BK)+(scorewhite-scoreblack);
		else 
			return ev;
		
			
	}
	
	private double center_control(String[][] brd){
		
		int i,j,cc=0;
		for(i=1;i<rows/2;i++){
			for(j=0;j<columns;j++){
				if(brd[rows-1-i][j].equals("WP")){
					cc=cc+i;
				}
				else if(brd[i][j].equals("BP")){
					cc=cc-i;
				}
				
			}
		}
		
		return cc;
	}
	
	
}
