import java.util.*;

public class Player {
	
	private static final MiniMax miniMax =new MiniMax();
	
	/**
	 * cd D:/Users/Sylvain/git/hw2/Tic-Tac-Toe3D/src
	 * mkfifo pipe1 pipe2
	 * javac *.java
	 * java Main init verbose > pipe1 < pipe2
	 * java Main verbose < pipe1 > pipe2
	 * java Main init verbose < pipe | java Main > pipe
	 */
	
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
			if (gameState.at(GameState.cellToRow(i), GameState.cellToCol(i),GameState.cellToLay(i))!=0){
				count++;
			}
		}
        int player=nextStates.get(0).getNextPlayer();
        System.err.println("turn "+count +" player : "+Constants.SIMPLE_TEXT[player]);

        

        	


        	
        	Map<Integer, Double> mapPriority = miniMax.prioritizeStates(nextStates,true,player);
        	
            //  MiniMax.printMap(mapPriority);
        	int bestIdx=-1;
        	int bestMiniMax=Integer.MIN_VALUE;
        	int bestGamma=Integer.MIN_VALUE;
        	long totalTime=(long) (deadline.timeUntil()*5);
        	Deadline totalDeadline = new Deadline(totalTime);
        	double sumPriority=0;
        	
        	List<Integer> reverseOrderedKeys = new ArrayList<Integer>(mapPriority.keySet());
        	Collections.reverse(reverseOrderedKeys);
        	for (int key : reverseOrderedKeys) {
        		Double priority = mapPriority.get(key);
        		GameState state = nextStates.get(key);

        		long time=(long) (Deadline.getCpuTime() + totalDeadline.timeUntil()*priority/(1-sumPriority));
        		sumPriority+=priority;
				int res=miniMax.minimax(state,player,100,new Deadline(time));
				System.err.println("move : "+state.getMove().toString()+" minimax :"+res+" depth : "+miniMax.minDepth); 
				if (res>bestMiniMax){
					bestIdx=key;
					bestMiniMax=res;
					bestGamma=miniMax.gamma(player, state);
					if (res>=MiniMax.SCOREWIN){
                    	break;
					}
				}else if (res==bestMiniMax){
					int gamma = miniMax.gamma(player, state);
					if (gamma>bestGamma){
						bestIdx=key;
						bestGamma=gamma;
					}
				}
        	}
        	
        	
        	
      
	        if (bestIdx!=-1){
	        	System.err.println("min depth : "+miniMax.minDepth);
	        	System.err.println("move : "+nextStates.elementAt(bestIdx).getMove().toString()+" chosen"); 
	        	return nextStates.elementAt(bestIdx);
	        }
        
        
        
        Random random = new Random();
        return nextStates.elementAt(random.nextInt(nextStates.size()));
    }    
}
