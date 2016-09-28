import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by david on 9/21/2016.
 */
public class MiniMax {
	
	public static final int SCOREWIN = 500;
	
	public static int minDepth =100;
	
	 public int minimax(final GameState state, int player, int depth, Deadline deadline) {
		 return minimax(state, player, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, deadline);
	 }

    public int minimax(final GameState state, int player, int depth, int alpha, int beta, Deadline deadline) {
        //state: the current state we are analyzing
        //player: the current player (A = 1, B = 2)
        //returns a heuristic value that approximates a utility function of the state

        //gamma is a function that returns 1, 0 or -1 depending on whether the current state is a win, tie or loss for the player
    	
    	minDepth=Math.min(minDepth, depth);
    	
    	//System.err.println("deadline1: "+deadline.timeUntil());
    //	System.err.println("depth : "+depth+" move"+state.getMove().toMessage());
    	if (deadline.timeUntil()<800_000 || depth==0){
    		
    		return gamma(player, state);
    	}
    	
                Vector<GameState> nextStates = new Vector<GameState>();
        state.findPossibleMoves(nextStates);

        

     //   System.err.println("minimax player "+Constants.SIMPLE_TEXT[player]+" nb next "+nextStates.size());
  //      System.err.println("looked "+looked+"    to look "+toLook); 
       // System.err.println("deadline2: "+deadline.timeUntil());
        if (nextStates.size()==0) {
            //terminal state
        	//System.err.println("terminal depth : "+depth);
        	//System.err.println(state.toString(state.getNextPlayer()));
        	//System.err.println("gamma "+gamma(player, state));
            return gamma(player, state);
        } else {
        	int currentPlayer = nextStates.get(0).getNextPlayer();
            int bestPossible = -1;
            int v = -1;
            long totalTime=(long) (deadline.timeUntil()*0.8);
            //can search deeper
            if (player == currentPlayer) {
            	Map<Integer, Double> mapPriority = prioritizeStates(nextStates,true,player);
                bestPossible = Integer.MIN_VALUE;
                for (Map.Entry<Integer,Double> priority : mapPriority.entrySet()) {
            		GameState child = nextStates.get(priority.getKey());
            		long time=(long) (Deadline.getCpuTime() +totalTime*priority.getValue());
                    v = minimax(child,player,depth-1,alpha,beta,new Deadline(time));
                    bestPossible = Math.max(bestPossible, v);
                    alpha=Math.max(alpha, v);
                    if (bestPossible>=SCOREWIN)
                    	break;
                    if (beta<=alpha)
                    	break; // beta pruning
                }
                return bestPossible;
            } else {
                //player = B
            	Map<Integer, Double> mapPriority = prioritizeStates(nextStates,false,player);
                bestPossible = Integer.MAX_VALUE;
                for (Map.Entry<Integer,Double> priority : mapPriority.entrySet()) {
            		GameState child = nextStates.get(priority.getKey());
                	long time=(long) (Deadline.getCpuTime() +totalTime*priority.getValue());
                    v = minimax(child,player,depth-1,alpha,beta,new Deadline(time));
                    bestPossible = Math.min(bestPossible, v);
              /*      if (bestPossible==-SCOREWIN_000){
                    	System.err.println("bestpos "+state.getMove());
                    }*/
                    beta=Math.min(beta, v);
                    if (bestPossible<=-SCOREWIN)
                    	break;
                    if (beta<=alpha)
                    	break; // alpha pruning
                }
                return bestPossible;
            }
        }
    }
    
    public int gamma(int player, GameState state) {
        if ((player == Constants.CELL_X && state.isXWin()) || (player == Constants.CELL_O && state.isOWin())){
    //    	System.err.println("1000000 "+state.getMove());
            return SCOREWIN;
        }
        if ((player == Constants.CELL_X && state.isOWin()) || (player == Constants.CELL_O && state.isXWin())){
    //    	System.err.println("-1000000 "+state.getMove());
            return -SCOREWIN;
        }
        if (state.isEOG())
            return 0;
        int[][] align=getNbAlign(state);
        //System.err.println("nbAlign :"+Arrays.deepToString(align));
        return pointFromAlign(align,player,state.getNextPlayer());  
    }

    
	private int pointFromAlign(int[][] align,int player,int nextPlayer) {
	/*	int otherPlayer= player ^ (Constants.CELL_X | Constants.CELL_O);
		int res=0;
		if (player!=nextPlayer){
			if (align[player-1][1]!=0){
				return SCOREWIN_000;
			}
			res+=-50*align[otherPlayer-1][1];
		}else{
			if (align[otherPlayer-1][1]!=0){
				return -SCOREWIN_000;
			}
			res+=50*align[player-1][1];
		}
		res=4*align[player-1][0]-align[otherPlayer-1][0];*/
		
		int otherPlayer= player ^ (Constants.CELL_X | Constants.CELL_O);
        int res=4*align[player-1][0]-align[otherPlayer-1][0];
        if (player!=nextPlayer){
            res+=1000*align[player-1][1]-50*align[otherPlayer-1][1];
        }else{
            res+=50*align[player-1][1]-1000*align[otherPlayer-1][1];
        }
		return res;
	}

	private int[][] getNbAlign(GameState state) {
		int[][] nbAlign=new int[2][2];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < GameState.BOARD_SIZE; j++) {
				for (int k = 0; k < GameState.BOARD_SIZE; k++) {
					//line 1D (48)
					nbAlign = checkLigne(state,nbAlign,j*indicator(i,2)+k*indicator(i,1),j*indicator(i,0)+k*indicator(i,2),j*indicator(i,1)+k*indicator(i,0),indicator(i,0), indicator(i,1),indicator(i,2));
				}
				//diagonal 2D (24)
				nbAlign = checkLigne(state,nbAlign,j*indicator(i,0),j*indicator(i,1),j*indicator(i,2),indicator(i,1)+indicator(i,2), indicator(i,0)+indicator(i,2), indicator(i,0)+indicator(i,1));
				nbAlign = checkLigne(state,nbAlign,j*indicator(i,0)+(GameState.BOARD_SIZE-1)*indicator(i,2),j*indicator(i,1)+(GameState.BOARD_SIZE-1)*indicator(i,0),j*indicator(i,2)+(GameState.BOARD_SIZE-1)*indicator(i,1),indicator(i,1)-indicator(i,2), indicator(i,2)-indicator(i,0), indicator(i,0)-indicator(i,1));
			}
		}
		
		//diagonal 3D (4)
		nbAlign = checkLigne(state,nbAlign,0,0,0,1,1,1);
		nbAlign = checkLigne(state,nbAlign,GameState.BOARD_SIZE-1,0,0,-1,1,1);
		nbAlign = checkLigne(state,nbAlign,0,GameState.BOARD_SIZE-1,0,1,-1,1);
		nbAlign = checkLigne(state,nbAlign,0,0,GameState.BOARD_SIZE-1,1,1,-1);
	
	//	System.err.println("nbAlign :"+Arrays.deepToString(nbAlign));
		return nbAlign;
	}
	
	private int[][] checkLigne(GameState state,int[][] nbAlign,int initRow,int initCol, int initLay,int incrRow, int incrCol, int incrLay){
		int nb=0;
		int p=0;
		int r=initRow;
		int c=initCol;
		int l=initLay;

		for (int k = 0; k < GameState.BOARD_SIZE; k++) {
			
			int s=state.at(r, c, l);
			r+=incrRow;
			c+=incrCol;
			l+=incrLay;

			if (nb==0 ){
				if(s!=Constants.CELL_EMPTY){
					p=s;
					nb++;
				}
			}else{
				if (s==p){
					nb++;
				}else if (s!=Constants.CELL_EMPTY){
					nb=0;
					return nbAlign;
				}
			}
		}
		if (nb>1){
		/*	if (state.getMove().equals(new Move(52, 2))){
				System.err.println("at "+initRow+","+incrRow+"/"+initCol+","+incrCol+"/"+initLay+","+incrLay+" -> "+nb );
			}*/
			nbAlign[p-1][nb-2]++;
		}
		return nbAlign;
	}
	
	private int indicator(int a,int b){
		return (a==b) ? 1 : 0;
	}
	
	
	public static <K, V> void printMap(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.err.println("Key : " + entry.getKey()
                    + " Value : " + entry.getValue());
        }
    }
	
	
	
	public Map<Integer,Double> prioritizeStates(Vector<GameState> nextStates,boolean maxFirst,int player){
		Map<Integer, Integer> unsortMap = new HashMap<Integer, Integer>();
		int sum=0;
		int min=Integer.MAX_VALUE;
		for (int i = 0; i < nextStates.size(); i++) {
			int g=gamma(player, nextStates.get(i));
			//System.err.println("move : " + nextStates.get(i).getMove() + " gamma : " + g);
			unsortMap.put(i, g);
			min=Math.min(min, g);
			sum+=g;
		}
		//printMap(unsortMap);
		
		Map<Integer, Double> sortedMap = sortByValue(unsortMap,maxFirst);
	//	printMap(sortedMap);
	//	System.err.println("sum "+sum+"  min "+min);
		//normalize priority between 0 and 1
		sum-=min*nextStates.size();
		if (sum==0){
			sum=nextStates.size();
			min=-1;
		}
	//	System.err.println("new sum "+sum);
		for (int i = 0; i < nextStates.size(); i++) {
			sortedMap.put(i, (unsortMap.get(i)-min)/(double)sum);
		}
		return sortedMap;
	}
	
	 private static Map<Integer, Double> sortByValue(Map<Integer, Integer> unsortMap,boolean maxFirst) {

	        // 1. Convert Map to List of Map
	        List<Map.Entry<Integer, Integer>> list = new LinkedList<Map.Entry<Integer, Integer>>(unsortMap.entrySet());

	        // 2. Sort list with Collections.sort(), provide a custom Comparator
	        //    Try switch the o1 o2 position for a different order
	        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
	            public int compare(Map.Entry<Integer, Integer> o1,
	                               Map.Entry<Integer, Integer> o2) {
	            	if (maxFirst)
	            		return (o2.getValue()).compareTo(o1.getValue());
	                return (o1.getValue()).compareTo(o2.getValue());
	            }
	        });

	        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
	        Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
	        for (Map.Entry<Integer, Integer> entry : list) {
	            sortedMap.put(entry.getKey(), entry.getValue()+0.0);
	        }

	        return sortedMap;
	    }
	
	
}
