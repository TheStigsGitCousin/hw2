import java.util.*;

public class Player {
	
	
	/**
	 * cd D:/Users/Sylvain/git/hw2/Tic-Tac-Toe/src
	 * mkfifo pipe1 pipe2
	 * javac *.java
	 * java Main init verbose > pipe1 < pipe2
	 * java Main verbose < pipe1 > pipe2
	 */
	
	
	private static final MiniMax miniMax =new MiniMax();
	
    /**
     * Performs a move
     *
     * @param gameState
     *            the current state of the board
     * @param deadline
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState gameState, final Deadline deadline) {
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(gameState, new Move());
        }

        /**
         * Here you should write your algorithms to get the best next move, i.e.
         * the best next state. This skeleton returns a random move instead.
         */
        int count=0;
        for (int i = 0; i < gameState.CELL_COUNT; i++) {
			if (gameState.at(GameState.cellToRow(i), GameState.cellToCol(i))!=0){
				count++;
			}
		}
        int player=nextStates.get(0).getNextPlayer();
        System.err.println("turn "+count +" player : "+Constants.SIMPLE_TEXT[player]);

      //  if (count>-1 && player==Constants.CELL_O){
        if (count>-1){
        	int bestIdx=-1;
        	int bestMiniMax=Integer.MIN_VALUE;
        	int bestGamma=Integer.MIN_VALUE;
        	for (int i = 0; i < nextStates.size(); i++) {
        		GameState state = nextStates.get(i);
				int res=miniMax.minimax(state,player,15,new Deadline(deadline.timeUntil()/(nextStates.size()-i)));
				System.err.println("move : "+state.getMove().toString()+" minimax :"+res); 
				if (res>bestMiniMax){
					bestIdx=i;
					bestMiniMax=res;
					bestGamma=miniMax.gamma(player, state);
				}else if (res==bestMiniMax){
					int gamma = miniMax.gamma(player, state);
					if (gamma>bestGamma){
						bestIdx=i;
						bestGamma=gamma;
					}
				}
				
			}
	        if (bestIdx!=-1){
	        	System.err.println("move : "+nextStates.elementAt(bestIdx).getMove().toString()+" chosen"); 
	        	return nextStates.elementAt(bestIdx);
	        }
        }
        
        Random random = new Random();
        return nextStates.elementAt(random.nextInt(nextStates.size()));
    }    
}