import java.util.*;

public class Player {
	
	private static final MiniMax miniMax =new MiniMax();
	
	/**
	 * cd D:/Users/Sylvain/git/hw2/Tic-Tac-Toe3D/src
	 * mkfifo pipe1 pipe2
	 * javac *.java
	 * java Main init verbose > pipe1 < pipe2
	 * java Main verbose < pipe1 > pipe2
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

        
       /* 	long t = deadline.timeUntil();
        	for (int i = 0; i < 100; i++) {
				gameState.findPossibleMoves( new Vector<GameState>());
			}
        	System.err.println("find moves : "+(t-deadline.timeUntil())); 
        	long t2 = deadline.timeUntil();
        	for (int i = 0; i < 1000; i++) {
        		miniMax.gamma(player, gameState);
			}
        	System.err.println("heuristique : "+(t2-deadline.timeUntil()));*/ 
        	
        	



    //    if (count>-1 && player==Constants.CELL_O){
        if (count>-1){
        	
        	Map<Integer, Double> mapPriority = miniMax.prioritizeStates(nextStates,true,player);
            //  MiniMax.printMap(mapPriority);
        	int bestIdx=-1;
        	int bestMiniMax=Integer.MIN_VALUE;
        	int bestGamma=Integer.MIN_VALUE;
        	long totalTime=(long) (deadline.timeUntil()*1000);
        	for (Map.Entry<Integer,Double> priority : mapPriority.entrySet()) {
        		GameState state = nextStates.get(priority.getKey());
        		//System.err.println("start deadline: "+deadline.timeUntil()+" nb "+(nextStates.size()-i)+" res "+((deadline.timeUntil()- 400000000)/(nextStates.size()-i)));
        		long time=(long) (Deadline.getCpuTime() + (totalTime- 1)*priority.getValue());

				int res=miniMax.minimax(state,player,100,new Deadline(time));
				System.err.println("move : "+state.getMove().toString()+" minimax :"+res); 
				if (res>bestMiniMax){
					bestIdx=priority.getKey();
					bestMiniMax=res;
					bestGamma=miniMax.gamma(player, state);
					if (res>=MiniMax.SCOREWIN)
                    	break;
				}else if (res==bestMiniMax){
					int gamma = miniMax.gamma(player, state);
					if (gamma>bestGamma){
						bestIdx=priority.getKey();
						bestGamma=gamma;
					}
				}
            }
        /*	for (int i = 0; i < nextStates.size(); i++) {
        		GameState state = nextStates.get(i);
        		//System.err.println("start deadline: "+deadline.timeUntil()+" nb "+(nextStates.size()-i)+" res "+((deadline.timeUntil()- 400000000)/(nextStates.size()-i)));
				int res=miniMax.minimax(state,player,100,new Deadline( Deadline.getCpuTime() + (deadline.timeUntil()- 100000000)/(nextStates.size()-i)));
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
				
			}*/
	        if (bestIdx!=-1){
	        	System.err.println("min depth : "+MiniMax.minDepth);
	        	System.err.println("move : "+nextStates.elementAt(bestIdx).getMove().toString()+" chosen"); 
	        	return nextStates.elementAt(bestIdx);
	        }
        }
        
        
        Random random = new Random();
        return nextStates.elementAt(random.nextInt(nextStates.size()));
    }    
}
