import java.util.Arrays;
import java.util.Vector;

/**
 * Created by david on 9/21/2016.
 */
public class MiniMax {
	

    public int minimax(final GameState state, int player, int deep, Deadline deadline) {
        //state: the current state we are analyzing
        //player: the current player (A = 1, B = 2)
        //returns a heuristic value that approximates a utility function of the state

        //gamma is a function that returns 1, 0 or -1 depending on whether the current state is a win, tie or loss for the player
    	

    	
                Vector<GameState> nextStates = new Vector<GameState>();
        state.findPossibleMoves(nextStates);
        

     //   System.err.println("minimax player "+Constants.SIMPLE_TEXT[player]+" nb next "+nextStates.size());
  //      System.err.println("looked "+looked+"    to look "+toLook); 
        if (deadline.timeUntil()<deep*10000000 || deep==0 || nextStates.size()==0) {
            //terminal state

        	//System.err.println(state.toString(state.getNextPlayer()));
        	//System.err.println("gamma "+gamma(player, state));
            return gamma(player, state);
        } else {
        	int currentPlayer = nextStates.get(0).getNextPlayer();
            int bestPossible = -1;
            int v = -1;
            //can search deeper
            if (player == currentPlayer) {
                bestPossible = Integer.MIN_VALUE;
                for (GameState child : nextStates) {
                    v = minimax(child,player,deep-1,deadline);
                    bestPossible = Math.max(bestPossible, v);
                }
                return bestPossible;
            } else {
                //player = B
                bestPossible = Integer.MAX_VALUE;
                for (GameState child : nextStates) {
                    v = minimax(child,player,deep-1,deadline);
                    bestPossible = Math.min(bestPossible, v);
                }
                return bestPossible;
            }
        }
    }

    public int gamma(int player, GameState state) {
        if ((player == Constants.CELL_X && state.isXWin()) || (player == Constants.CELL_O && state.isOWin()))
            return 1_000_000;
        if ((player == Constants.CELL_X && state.isOWin()) || (player == Constants.CELL_O && state.isXWin())) 
            return -1_000_000;
        if (state.isEOG())
            return 0;
        int[][] align=getNbAlign(state);
        return pointFromAlign(align,player);
        
    }

	private int pointFromAlign(int[][] align,int player) {
		int otherPlayer= player ^ (Constants.CELL_X | Constants.CELL_O);
		return 10*(align[player-1][1]-align[otherPlayer-1][1])+4*align[player-1][0]-align[otherPlayer-1][0];
	}

	private int[][] getNbAlign(GameState state) {
		int[][] nbAlign=new int[2][2];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < GameState.BOARD_SIZE; j++) {
				nbAlign = checkLigne(state,nbAlign,j*(1-i),j*i,i, 1-i);
			}
		}
		nbAlign = checkLigne(state,nbAlign,0,0,1, 1);
		nbAlign = checkLigne(state,nbAlign,GameState.BOARD_SIZE-1,0,-1, 1);
		//System.err.println("nbAlign :"+Arrays.deepToString(nbAlign));
		return nbAlign;
	}
	
	private int[][] checkLigne(GameState state,int[][] nbAlign,int initRow,int initCol,int incrRow, int incrCol){
		int nb=0;
		int p=0;
		for (int k = 0; k < GameState.BOARD_SIZE; k++) {
			
			int c=state.at(initRow+k*incrRow, initCol+k*incrCol);
			//System.err.println("at "+(initRow+k*incrRow)+"/"+(initCol+k*incrCol)+" -> "+c );
			if (nb==0 ){
				if(c!=Constants.CELL_EMPTY){
					p=c;
					nb++;
				}
			}else{
				if (c==p){
					nb++;
				}else if (c!=Constants.CELL_EMPTY){
					nb=0;
					break;
				}
			}
		}
		if (nb>1){
			nbAlign[p-1][nb-2]++;
		}
		return nbAlign;
	}
}
