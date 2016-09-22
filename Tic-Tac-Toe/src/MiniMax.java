import java.util.Vector;

/**
 * Created by david on 9/21/2016.
 */
public class MiniMax {
	
	private static int toLook=1;
	private static int looked=0;
	
	public void resetCount(){
		toLook=1;
		looked=0;
	}

    public int minimax(final GameState state, int player) {
        //state: the current state we are analyzing
        //player: the current player (A = 1, B = 2)
        //returns a heuristic value that approximates a utility function of the state

        //gamma is a function that returns 1, 0 or -1 depending on whether the current state is a win, tie or loss for the player
    	
    	toLook--;
    	looked++;
    	
                Vector<GameState> nextStates = new Vector<GameState>();
        state.findPossibleMoves(nextStates);
        

     //   System.err.println("minimax player "+Constants.SIMPLE_TEXT[player]+" nb next "+nextStates.size());
  //      System.err.println("looked "+looked+"    to look "+toLook); 
        if (nextStates.size()==0) {
            //terminal state
        	//System.err.println(state.toString(state.getNextPlayer()));
        	//System.err.println("gamma "+gamma(player, state));
            return gamma(player, state);
        } else {
        	toLook+=nextStates.size();
        	int currentPlayer = nextStates.get(0).getNextPlayer();
            int bestPossible = -1;
            int v = -1;
            //can search deeper
            if (player == currentPlayer) {
                bestPossible = Integer.MIN_VALUE;
                for (GameState child : nextStates) {
                    v = minimax(child,player);
                    bestPossible = Math.max(bestPossible, v);
                }
                return bestPossible;
            } else {
                //player = B
                bestPossible = Integer.MAX_VALUE;
                for (GameState child : nextStates) {
                    v = minimax(child,player);
                    bestPossible = Math.min(bestPossible, v);
                }
                return bestPossible;
            }
        }
    }

    private int gamma(int player, GameState state) {
        if ((player == Constants.CELL_X && state.isXWin()) || (player == Constants.CELL_O && state.isOWin()))
            return 1;
        else if ((player == Constants.CELL_X && state.isOWin()) || (player == Constants.CELL_O && state.isXWin())) {
            return -1;
        } else {
            return 0;
        }
    }
}
