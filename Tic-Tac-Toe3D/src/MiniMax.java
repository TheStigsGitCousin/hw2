import java.util.ArrayList;
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
	
	public static final int SCOREWIN = 1000;
	
	public int minDepth =100;
	
	 public int minimax(final GameState state, int player, int depth, Deadline deadline) {  //called from Player
		 minDepth=101;
		 return minimax(state, player, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, deadline, "");
	 }

    public int minimax(final GameState state, int player, int depth, int alpha, int beta, Deadline deadline, String debug) {  //called in the Loop
        //state: the current state we are analyzing
        //player: the current player (A = 1, B = 2)
        //returns a heuristic value that approximates a utility function of the state

        //gamma is a function that returns 1, 0 or -1 depending on whether the current state is a win, tie or loss for the player
    	
    	minDepth=Math.min(minDepth, depth);
    	

    	if (deadline.timeUntil()<800_000 || depth==0){
    		int g=gamma(player, state);
    		if (g<SCOREWIN && g>-SCOREWIN){
    			g-=(int)(-depth%2+2)/(0.005*(100-depth)+0.04)+50*(-depth%2+1);
    		}
    		return g;
    	}
    	
                Vector<GameState> nextStates = new Vector<GameState>();
        state.findPossibleMoves(nextStates);

       
        if (nextStates.size()==0) {
        	int g=gamma(player, state);
    		return g;
        } else {
        	
        	debug+=" "+state.getMove();
        /*	if (depth<30){
        		System.err.println(debug);
        	}*/
        	
        	int currentPlayer = nextStates.get(0).getNextPlayer();
            int bestPossible = -1;
            int v = -1;
            long totalTime=(long) (deadline.timeUntil()*1.0);
        	Deadline totalDeadline = new Deadline(totalTime);
        	double sumPriority=0;
            //can search deeper
            if (player == currentPlayer) {
            	Map<Integer, Double> mapPriority = prioritizeStates(nextStates,true,player);
            	if (mapPriority.size()==1 && nextStates.size()!=1){
            		return  mapPriority.entrySet().iterator().next().getValue().intValue();
            	}
                bestPossible = Integer.MIN_VALUE;
                
                List<Integer> reverseOrderedKeys = new ArrayList<Integer>(mapPriority.keySet());
            	Collections.reverse(reverseOrderedKeys);
            	for (int key : reverseOrderedKeys) {
            		Double priority = mapPriority.get(key);
            		GameState child = nextStates.get(key);

            		long time=(long) (Deadline.getCpuTime() + totalDeadline.timeUntil()*priority/(1-sumPriority));
            		sumPriority+=priority;

                    v = minimax(child,player,depth-1,alpha,beta,new Deadline(time),debug);
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
            	if (mapPriority.size()==1 && nextStates.size()!=1){
            		return  mapPriority.entrySet().iterator().next().getValue().intValue();
            	}
            
                bestPossible = Integer.MAX_VALUE;
                
                List<Integer> reverseOrderedKeys = new ArrayList<Integer>(mapPriority.keySet());
            	Collections.reverse(reverseOrderedKeys);
            	for (int key : reverseOrderedKeys) {
            		Double priority = mapPriority.get(key);
            		GameState child = nextStates.get(key);

            		long time=(long) (Deadline.getCpuTime() + totalDeadline.timeUntil()*priority/(1-sumPriority));
            		sumPriority+=priority;
            		
                    v = minimax(child,player,depth-1,alpha,beta,new Deadline(time),debug);
                    bestPossible = Math.min(bestPossible, v);

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
            return SCOREWIN;
        }
        if ((player == Constants.CELL_X && state.isOWin()) || (player == Constants.CELL_O && state.isXWin())){
            return -SCOREWIN;
        }
        if (state.isEOG())
            return 0;
        int[][] align=getNbAlign(state);

        return pointFromAlign(align,player,state.getNextPlayer());  
    }

    
	private int pointFromAlign(int[][] align,int player,int nextPlayer) {
		int otherPlayer= player ^ (Constants.CELL_X | Constants.CELL_O);
		int res=0;
		if (player!=nextPlayer){  // You are the next player
			if (align[player-1][1]!=0){  // You have at least once 3 aligned
				return align[player-1][1]*SCOREWIN; //you win
			}
			if (align[otherPlayer-1][1]>1){  // the other player have more than 1 time 3 aligned
				return (int) (-SCOREWIN*0.8); //you loose
			}
			res+=-50*align[otherPlayer-1][1]; 
		}else{       								//You are not the next player
			if (align[otherPlayer-1][1]!=0){		//the other player have at least once 3 aligned
				return -align[otherPlayer-1][1]*SCOREWIN;  // you loose
			}
			if (align[otherPlayer-1][1]>1){   		//you have more than 1 time 3 aligned
				return (int) (SCOREWIN*0.8);		//you win
			}
			res+=50*align[player-1][1];
		}
		res+=5*align[player-1][0]-5*align[otherPlayer-1][0];
		
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
					return nbAlign;
				}
			}
		}
		if (nb>1){
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
		int max=Integer.MIN_VALUE;
		int idMax=-1;
		int idMin=-1;
		for (int i = 0; i < nextStates.size(); i++) {
			int g=gamma(player, nextStates.get(i));
			unsortMap.put(i, g);
			if (g<min){
				min=g;
				idMin=i;
			}
			if (g>max){
				max=g;
				idMax=i;
			}
			sum+=g;
		}

		if (maxFirst && max>= SCOREWIN || min>= SCOREWIN){
			Map<Integer, Double> map = new HashMap<Integer, Double>();
			map.put(idMax, (double) SCOREWIN);
			return map;
		}
		if (!maxFirst && min<= -SCOREWIN || max<=-SCOREWIN){
			Map<Integer, Double> map = new HashMap<Integer, Double>();
			map.put(idMax, (double) -SCOREWIN);
			return map;
		}
		
		
		Map<Integer, Double> sortedMap = sortByValue(unsortMap,maxFirst);
		
		
		if (maxFirst){
			sum-=min*nextStates.size();
			if (sum==0){
				sum=nextStates.size();
				min=-1;
			}
		//	System.err.println("new sum "+sum);
			for (int i = 0; i < nextStates.size(); i++) {
				sortedMap.put(i, 0.25/nextStates.size()+0.75*(unsortMap.get(i)-min)/(double)sum);
			}
		}else{
			sum-=max*nextStates.size();
			sum=-sum;
			if (sum==0){
				sum=nextStates.size();
				max=-1;
			}
		//	System.err.println("new sum "+sum);
			for (int i = 0; i < nextStates.size(); i++) {
				sortedMap.put(i, 0.25/nextStates.size()+0.75*(-(unsortMap.get(i)-max))/(double)sum);
			}
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
