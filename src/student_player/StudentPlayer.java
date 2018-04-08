package student_player;

import java.util.List;
import java.util.Random;

import boardgame.Board;
import boardgame.Move;
import tablut.TablutBoardState;
import tablut.TablutMove;
import tablut.TablutPlayer;

/**
 * A player file submitted by a student
 * 
 * @author isaacsultan
 *
 */
public class StudentPlayer extends TablutPlayer {
	private static final int SEARCH_DEPTH = 2; // two-ply game tree
	private Random rand = new Random(1848);

	public StudentPlayer() {
		super("2606080295");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * This is the primary method that you need to implement. The ``boardState``
	 * object contains the current state of the game, which your agent must use to
	 * make decisions.
	 * 
	 * @see tablut.TablutPlayer#chooseMove(tablut.TablutBoardState)
	 */
	@Override
	public Move chooseMove(TablutBoardState boardState) {

		Move myMove = alphaBetaSearch(boardState);
		return myMove;
	}

	/**
	 * Minimax algorithm with alpha-beta pruning Depth-first exploration of game
	 * tree - simulates each move and evaluates the outcome when the leaves are
	 * reached Assumes opponent plays to minimise utility.
	 * 
	 * @param boardState
	 * @return the optimal move for StudentPlayer
	 */
	Move alphaBetaSearch(TablutBoardState boardState) {
		List<TablutMove> options = boardState.getAllLegalMoves();

		TablutMove bestMove = options.get(rand.nextInt(options.size())); // picks a random legal move
		int maxUtility = Integer.MIN_VALUE;
		int newUtility;

		for (TablutMove move : options) {

			TablutBoardState cloneBS = (TablutBoardState) boardState.clone();
			cloneBS.processMove(move);

			newUtility = min(cloneBS, StudentPlayer.SEARCH_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE);

			if (newUtility > maxUtility) {
				bestMove = move;
				maxUtility = newUtility;
			}
		}
		return bestMove;
	}

	/**
	 * Heuristic function - calculates number of pieces taken from opponent
	 * 
	 * @param boardState
	 * @return number of pieces
	 */
	private int evaluation(TablutBoardState boardState) {
		if (player_id == TablutBoardState.SWEDE) {
			return (16 - boardState.getNumberPlayerPieces(TablutBoardState.MUSCOVITE));
		} else {
			return (9 - boardState.getNumberPlayerPieces(TablutBoardState.SWEDE));
		}
	}

	/**
	 * Max algorithm - corecursion with @see
	 * {@link student_player.StudentPlayer#min(TablutBoardState, int, int, int)}
	 * 
	 * @param boardState
	 * @param currentDepth
	 * @param alpha
	 * @param beta
	 * @return greatest utility achieved
	 */
	private int max(TablutBoardState boardState, int currentDepth, int alpha, int beta) {

		int isFinished = terminalTest(boardState, currentDepth);
		if (isFinished != -1) {
			return isFinished;
		}

		List<TablutMove> options = boardState.getAllLegalMoves();

		int optimalUtility = Integer.MIN_VALUE;
		for (Move move : options) {
			TablutBoardState cloneBS = (TablutBoardState) boardState.clone();
			cloneBS.processMove((TablutMove) move);
			optimalUtility = Math.max(optimalUtility, min(cloneBS, currentDepth - 1, alpha, beta));
			alpha = Math.max(alpha, optimalUtility);
			if (beta <= alpha) { // prunes subtree
				return optimalUtility;
			}
		}
		return optimalUtility;
	}

	/**
	 * Min algorithm - corecursion with @see
	 * {@link student_player.StudentPlayer#max(TablutBoardState, int, int, int)}
	 * 
	 * @param boardState
	 * @param currentDepth
	 * @param alpha
	 * @param beta
	 * @return smallest utility achieved
	 */
	private int min(TablutBoardState boardState, int currentDepth, int alpha, int beta) {
		int isFinished = terminalTest(boardState, currentDepth);
		if (isFinished != -1) {
			return isFinished;
		}
		List<TablutMove> options = boardState.getAllLegalMoves();

		int optimalUtility = Integer.MAX_VALUE;
		for (Move move : options) {
			TablutBoardState cloneBS = (TablutBoardState) boardState.clone();
			cloneBS.processMove((TablutMove) move);
			optimalUtility = Math.min(optimalUtility, max(cloneBS, currentDepth - 1, alpha, beta));
			beta = Math.min(beta, optimalUtility);
			if (beta <= alpha) { // prunes subtree
				return optimalUtility;
			}
		}
		return optimalUtility;
	}

	/**
	 * Under certain conditions min/max terminate early: 1) if the (simulated) game
	 * has been won -> returns max_utility if StudentPlayer wins 2) if a root has
	 * been reached -> calls heuristic function @see
	 * student_player.StudentPlayer#evaluation(TablutBoardState)
	 * 
	 * @param boardState
	 * @param currentDepth
	 * @return appropriate utility for condition
	 */
	private int terminalTest(TablutBoardState boardState, int currentDepth) {
		if (boardState.getWinner() != Board.NOBODY) {
			if (boardState.getWinner() == player_id) {
				return Integer.MAX_VALUE;
			} else {
				return Integer.MIN_VALUE;
			}
		}
		if (currentDepth == 0) {
			return evaluation(boardState);
		}
		return -1;
	}
}
