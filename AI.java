
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

class Board{
  	int xpos;
  	int ypos;
  	int count;
  	public Board(int x, int y, int c){
  		xpos=x;
  		ypos=y;
  		count=c;
  	}
}

class utility_return{
 int xpos;
 int ypos;
 int utility;
 public utility_return(int x,int y,int utility)
	 {
		 xpos=x;
		 ypos=y;
		 this.utility=utility;
	 }
}

public class Agent {
	static int expanded=0;
	static int inf=99999999;
	static int neg_inf=-99999999;
	static int visited[][];
	static int visit[][];
	static int bsize;
	static int fruits;
	static float inputtime;
	//program to print the board
	public void print_board(char board[][]){
	  	for(int i=0;i<bsize;i++){
	  		for(int j=0;j<bsize;j++){
	  			System.out.print(board[i][j]+"");
	  		}
	  	 System.out.println();
	  	}
	  	System.out.println("######################33");
	}
	public void print_visited(int board[][]){
	  	for(int i=0;i<bsize;i++){
	  		for(int j=0;j<bsize;j++){
	  			System.out.print(board[i][j]+"");
	  		}
	  	 System.out.println();
	  	}
	  	System.out.println("######################33");
	}

	//
	public ArrayList<Board> Generate_child(char parent[][]){
		ArrayList<Board> children= new ArrayList<Board>();
		visit=new int[parent.length][parent.length];
		for(int i=0;i<parent.length;i++){
			for(int j=parent.length-1;j>=0;j--){
				if(visit[j][i]!=1 && parent[j][i]!='*'){
					char val=parent[j][i];
					int g=Assign_Underscores(parent, j, i,val,0);
					children.add(new Board(j,i,g));
				}
			}
		}
	return children;
	}


	//DFS Method to calcualte the count of each cluster
	public void calculate_count(char state[][],int i,int j,char val)
	{
			if(issafe(state, i, j, val) && visited[i][j]!=1)
			{
				state[i][j]='*';
				visited[i][j]=1;
				calculate_count(state, i, j+1, val);
				calculate_count(state, i, j-1, val);
				calculate_count(state, i-1, j, val);
				calculate_count(state, i+1, j, val);
			}
	}



	public int Assign_Underscores(char state[][],int i,int j,char val,int count)
	{
		//print_visited(visited);
			if(issafe(state, i, j, val) && visit[i][j]!=1)
			{
				visit[i][j]=1;
				state[i][j]='-';
				count=count+1;
				count=Assign_Underscores(state, i, j+1, val, count);
				count=Assign_Underscores(state, i, j-1, val, count);
				count=Assign_Underscores(state, i-1, j, val, count);
				count=Assign_Underscores(state, i+1, j, val, count);
			}
		return count;
	}

	//To perform copy operation
	public char[][] Copy_Board(char board[][]){
		char temp[][]= new char[board.length][board.length];
		for(int i=0;i<board.length;i++){
			for(int j=0;j<board.length;j++){
				temp[i][j]=board[i][j];
			}
		}
	 return temp;
	}


	//Is safe function to comare each evaluation
    public boolean issafe(char state[][], int i, int j, char val){
	        if ((i >= 0 && i < state.length) && (j >= 0 && j<state.length) &&(state[i][j]== val))
	            return true;
	        else
	            return false;
        }

    //Gravity to drop the bold with * at the Top
    public char[][] gravity(char state[][]){
	    	int count=0;
	    	for(int row=0;row<state.length;row++){
	    		int k=state.length-1;
	    		count=0;
	    		for (int col=state.length-1;col>=0;col--){
	    			if(state[col][row]!='*'){
		    				state[k][row]=state[col][row];
		    				k=k-1;
		    			}
	    			else if(state[col][row]=='*'){
		    				count=count+1;
		    			}
	    		}
	    		for(int l=0;l<count;l++){
		    		 state[l][row]='*';
		    		}
	    	}
			return state;
	    }


    //Function to check if the board is filled with stars:
     public boolean check_stars(char board[][]){
	    	 for(int i=0;i<board.length;i++){
	    		 for(int j=0;j<board.length;j++){
	    			 if(board[i][j]!='*'){
	    				 return false;
	    			 }
	    		 }
	    	 }
			return true;
	     }

     public utility_return Min_Max(Board parent,char board[][],int depth,int alpha,int beta,boolean maximizing,int max_score,int min_score)
     {
    	 //System.out.println("MinMAx");
    	 expanded++;
    	 if(depth==0 || check_stars(board))
               {
    		 utility_return u=new utility_return(parent.xpos,parent.ypos,max_score-min_score);
            	   return u;
               }
            visited=new int[bsize][bsize];
            //print_visited(visited);
            char temp_board[][]=Copy_Board(board);
            ArrayList<Board> children=Generate_child(temp_board);
			Collections.sort(children, new Comparator<Board>() {
					@Override
					public int compare (Board b, Board c)
					{
						return Double.compare(c.count, b.count);
					}
				});

            if(maximizing)
            {
            	utility_return ur=new utility_return(neg_inf,neg_inf,neg_inf);
            	for(int i=0;i<children.size();i++)
            	{
            		visited=new int[bsize][bsize];
            		Board b= children.get(i);
            		char t_b[][]=Copy_Board(board);

            		{
            			calculate_count(t_b, b.xpos, b.ypos, t_b[b.xpos][b.ypos]);
            		}
            	    max_score=max_score+(b.count*b.count);
            	    gravity(t_b);
            	    utility_return rs=Min_Max(b, t_b, depth-1, alpha, beta, false, max_score, min_score);
            	    max_score=max_score-(b.count*b.count);
            	    if(rs.utility > ur.utility)
	        		 {
	        			 ur.utility=rs.utility;
	        			 ur.xpos=b.xpos;
	        			 ur.ypos=b.ypos;
	        		 }
		       		 alpha=Math.max(alpha, ur.utility);
		       		 if(beta<=alpha)
		       		  {
		       			 break;
		       		  }
            	}
            	return ur;
            }
            else
            {
            	utility_return ur=new utility_return(neg_inf,neg_inf,inf);
            	for(int i=0;i<children.size();i++)
            	{
            		visited=new int[bsize][bsize];
            		Board b= children.get(i);
            		char t_b[][]=Copy_Board(board);
            	    calculate_count(t_b, b.xpos, b.ypos, t_b[b.xpos][b.ypos]);

            	   // print_board(t_b);
            	    min_score=min_score+(b.count*b.count);
            	    gravity(t_b);
            	   // print_board(t_b);
            	    utility_return rs=Min_Max(b, t_b, depth-1, alpha, beta, true, max_score, min_score);
            	    min_score=min_score-(b.count*b.count);
            	    if(rs.utility < ur.utility)
	        		 {
	        			 ur.utility=rs.utility;
	        			 ur.xpos=b.xpos;
	        			 ur.ypos=b.ypos;
	        		 }
		       		 beta=Math.min(beta, ur.utility);
		       		 if(beta<=alpha)
		       		  {
		       			 break;
		       		  }
            	}
            	return ur;

            }
     }

	static void output_Generator(utility_return res,char board[][]) {
		BufferedWriter br = null;
		try {

			br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt")));
				br.append((char)(res.ypos+65)+""+(res.xpos+1));
				Agent h=new Agent();

				visited=new int[bsize][bsize];
				h.calculate_count(board, res.xpos, res.ypos, board[res.xpos][res.ypos]);
				h.gravity(board);
				br.newLine();
				for (int i = 0; i < board.length; i++) {
					StringBuilder sb = new StringBuilder();
					for (int j = 0; j < board.length; j++) {
						sb.append(board[i][j] + "");
					}
					br.append(sb.toString());
					br.newLine();
				}
				br.close();
			}

            catch (IOException ex) {

		}
	}




 public static void main(String args[]) throws NumberFormatException, IOException
		{
	 long starttime = System.currentTimeMillis();
			String algo = null;
			Agent h= new Agent();
			BufferedReader br = null;
			FileReader fr = null;
			StringBuffer sb = new StringBuffer();
			File myFile = new File("input.txt");
			fr = new FileReader(myFile.getCanonicalPath());
			br = new BufferedReader(fr);
			bsize = Integer.parseInt(br.readLine());
			fruits = Integer.parseInt(br.readLine());
			inputtime=Float.parseFloat(br.readLine());
			char board[][]= new char[bsize][bsize];
			for (int i = 0; i < bsize; i++)
				{
				        sb.append(br.readLine());
						char[] ch = sb.toString().toCharArray();
						int abc = 0;
						for (int j = 0; j < bsize; j++) {
							board[i][j] = ch[j];
							if (board[j][i] == 2) {
								abc++;
							}
						}
					sb.setLength(0);
				}
		  Board parent=new Board(0, 0, 0);


		  //long time=TimeUnit.SECONDS.toMillis(inputtime);
		  //System.out.println(time);
		  utility_return temp=h.Min_Max(parent,board,1, neg_inf, inf, true,0,0);
		  int exp=expanded;
		  int depth=4;
		  if(bsize<=5)
		  {
			  depth=5;
			  if(inputtime<=5)
				  {
					  depth=1;
				  }
			  else if(inputtime<=10&& inputtime>=5)
				  {
					  depth=2;
				  }
			  else if(inputtime>=10 &&inputtime<=20)
				  {
					  depth=3;
				  }
			  else if(inputtime>=20&& inputtime<=100)
			  {
				     depth=4;
			  }

		  }

		  else if(bsize>=5 && bsize<=10)
		  {
			  depth=4;
			  if(inputtime<=10)
				  {
					  depth=1;
				  }
			  else if(inputtime>10 &&inputtime<20 && exp>(bsize*bsize)/2 )
			  {
				  depth=2;
			  }
			  else if(inputtime>10 &&inputtime<20 && exp<(bsize*bsize)/2 )
			  {
				  depth=3;
			  }
			  else if(inputtime>=20 && inputtime<=30 && exp>(bsize*bsize)/2 )
			  {
				  depth=3;
			  }
		 }

		  else if(bsize>=10 && bsize<=15)
		  {
			  depth=3;
			  if(inputtime>=100)
			  {
				  depth=4;
			  }
			  else if(inputtime<=10)
			  {
				  depth=1;
			  }
			  else if(inputtime>=10 &&inputtime<=20 && exp>=(bsize*bsize)/2 )
			  {
				  depth=2;
			  }
		  }


		  else if(bsize>=15 && bsize<=20)
		  {
			  depth=3;
			  if(inputtime>=100)
			  {
				  depth=4;
			  }
			  else if(inputtime<=10)
			  {
				  depth=1;
			  }
			  else if(inputtime>=10 &&inputtime<=20 && exp>=(bsize*bsize)/2 )
			  {
				  depth=2;
			  }
		  }

		  else if(bsize>=20 && bsize<=26)
		  {
			  depth=3;
			  //System.out.println("Entered");
			  if(inputtime>=100)
			     {
			    	 depth=4;
			     }
			  else if(inputtime<10)
			  {
				  depth=1;
			  }
			  else if((inputtime>10 && inputtime<20) &&exp<(bsize*bsize)/3)
			  {
				  depth=2;
			  }
			  else if((inputtime>10 && inputtime<=15) &&exp>(bsize*bsize)/3)
			  {
				  depth=1;
			  }
			  else if((inputtime>=20 && inputtime<=30) && exp>(bsize*bsize)/3)
              {
             	 depth=2;
              }
		  }
		  //System.out.println("Depth"+depth);
		  expanded=0;
          utility_return res=h.Min_Max(parent,board,depth, neg_inf, inf, true,0,0);
          // System.out.println(res.utility);
           //System.out.println("X "+res.xpos+"::Y "+res.ypos);
           h.output_Generator(res,board);
           //System.out.println(expanded);
           long endTime = System.currentTimeMillis();
          // System.out.println("Took "+(endTime - starttime)/1000 + " s");
	  }
}
