import java.util.Vector;

/**
 * Created by david on 9/21/2016.
 */
public class MiniMax {

    public int minimax(final GameState state, int player) {
        //state: the current state we are analyzing
        //player: the current player (A = 1, B = 2)
        //returns a heuristic value that approximates autility function of the state

        //gamma is a function that returns 1, 0 or -1 depending on whether the current state is a win, tie or loss for the player
                Vector<GameState> nextStates = new Vector<GameState>();
        state.findPossibleMoves(nextStates);

        if (nextStates.size()==0) {
            //terminal state
            return gamma(player, state);
        } else {
            int bestPossible = -1;
            int v = -1;
            //can search deeper
            if (player == 1) {
                bestPossible = Integer.MIN_VALUE;
                for (GameState child : nextStates) {
                    v = minimax(child, 2);
                    bestPossible = Math.max(bestPossible, v);
                }
                return bestPossible;
            } else {
                //player = B
                bestPossible = Integer.MAX_VALUE;
                for (GameState child : nextStates) {
                    v = minimax(child, 1);
                    bestPossible = Math.min(bestPossible, v);
                }
                return bestPossible;
            }
        }
    }

    private GameState[] mu(int player, GameState state) {
        if (state.isEOG()) {
            return null;
        } else {
            return null;
        }
    }

    private int gamma(int player, GameState state) {
        if (state.isOWin() && player == 1)
            return 1;
        else if (state.isXWin() && player == 2) {
            return -1;
        } else {
            return 0;
        }
    }
}
