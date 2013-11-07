package cs543.project1.connectfour;

import cs543.project1.connectfour.exceptions.PlayException;

/**
 * This defines the ConnectFour game.
 * 
 * @author Natacha Gabbamonte
 * @author Gabriel Gheorghian
 * @author Mark Scerbo
 * 
 */
public class C4Game {

	private GameStatus gameStatus;
	private static final int PLAYER_ONE = 1;
	private static final int PLAYER_TWO = 2;

	private int currentPlayer;

	private int[][] board;
	private int[] fullColumns;

	/**
	 * Constructor for ConnectFour.
	 */
	public C4Game() {
		gameStatus = GameStatus.WAIT;
		newGame();
	}

	/**
	 * Tries to play a certain move for a player.
	 * 
	 * @param move
	 *            The move to be made (column 0 to 6)
	 * @param player
	 *            The player's id.
	 * @return Whether or not the move was played.
	 */
	public boolean play(int move, int player) {
		if (move < 0 || move > 6)
			return false;
		if (currentPlayer != player)
			throw new PlayException("Trying to play with wrong player: "
					+ player);
		boolean valid = validateMove(move);
		if (valid) {
			int row = makeMove(move);
			checkIfGameIsOver(move, row, currentPlayer);
		}
		return valid;
	}

	/**
	 * Determines the best move to be played and plays for the AI.
	 * 
	 * @return The move made by the AI.
	 */
	public int playForAI() {
		int move = findWinningMoveOrBlockingMove(PLAYER_TWO);
		if (move == -1) {
			move = findWinningMoveOrBlockingMove(PLAYER_ONE);
			if (move == -1) {

				// This will try to find the best possible move for the AI.
				int[] allPossMoves = new int[7];
				int numOfPossMoves = 0;
				for (int i = 0; i < fullColumns.length; i++)
					if (fullColumns[i] > 0)
						allPossMoves[numOfPossMoves++] = i;
				if (numOfPossMoves == 0)
					// This should never happen since the game status would have
					// been set to OVER_DRAW.
					throw new PlayException(
							"Trying to play for AI when the board is full.");
				if (numOfPossMoves == 1) {
					move = allPossMoves[0];
					play(move, PLAYER_TWO);
					return move;
				}
				int[] pointsOfMoves = new int[numOfPossMoves];

				// Checking for each possible move.
				for (int i = 0; i < numOfPossMoves; i++) {
					// If it's not > 1 then there's nothing to check above it.
					if (fullColumns[allPossMoves[i]] > 1) {
						// This checks if the position above would make it
						// possible for the AI to win (but also to be blocked).
						if (checkPosWin(fullColumns[allPossMoves[i]] + 1,
								allPossMoves[i] + 3, PLAYER_ONE)) {
							pointsOfMoves[i] = -1;
						}
						// This checks if the position above will let the
						// opponent win. (This is highly undesirable)
						else if (checkPosWin(fullColumns[allPossMoves[i]] + 1,
								allPossMoves[i] + 3, PLAYER_TWO)) {
							pointsOfMoves[i] = 0;
						} else
							// This is probably the most desirable outcome.
							pointsOfMoves[i] = 2;
					} else {
						// We want the AI to play on less full rows first, so
						// this one will be have a lesser point.
						pointsOfMoves[i] = 1;

					}
				}

				// Now we have points for all the moves, we get the ones with
				// the highest points, and select one at random.
				int[] bestMoves = new int[numOfPossMoves];
				int countOfBestMoves = 0;

				for (int i = 0; i < numOfPossMoves; i++) {
					if (pointsOfMoves[i] == 2) {
						bestMoves[countOfBestMoves++] = allPossMoves[i];
					}
				}

				// This means there are no moves with a point of 2.
				if (countOfBestMoves == 0) {
					for (int i = 0; i < numOfPossMoves; i++) {
						if (pointsOfMoves[i] == 1) {
							bestMoves[countOfBestMoves++] = allPossMoves[i];
						}
					}

				}

				// This means there are no moves with a point of 1.
				if (countOfBestMoves == 0) {
					for (int i = 0; i < numOfPossMoves; i++) {
						if (pointsOfMoves[i] == 0) {
							bestMoves[countOfBestMoves++] = allPossMoves[i];
						}
					}
				}

				// This means that there is no way to stop from giving the
				// opponent a way to win.
				if (countOfBestMoves == 0) {
					for (int i = 1; i < numOfPossMoves; i++) {
						bestMoves[countOfBestMoves++] = allPossMoves[i];
					}
				}

				// At this point we should have an array if the best possible
				// move. Now we should select one at random and play it.

				// This will return a number between 0 and countOfBestMoves - 1,
				// inclusive.

				int randomNum = (int) (Math.random() * countOfBestMoves);
				move = bestMoves[randomNum];

			} else {
				System.out.println("The AI found a blocking move.");
			}
		} else {
			System.out.println("The AI found a winning move.");
		}
		play(move, PLAYER_TWO);
		return move;
	}

	/*
	 * Checks to see if the any move on the board can make a win for a certain
	 * id.
	 * 
	 * @param id The id of the player to check for.
	 */
	private int findWinningMoveOrBlockingMove(int id) {
		int move = -1;
		for (int currentColumn = 0; currentColumn < 7; currentColumn++) {
			if (fullColumns[currentColumn] > 0) {
				int currentRow = fullColumns[currentColumn] + 2;
				if (checkPosWin(currentRow, currentColumn + 3, id)) {
					move = currentColumn;
					break;
				}
			}
		}
		return move;
	}

	/**
	 * Resets the values for a new game.
	 */
	public void newGame() {
		board = new int[12][13];
		fullColumns = new int[7];
		for (int i = 0; i < fullColumns.length; i++)
			fullColumns[i] = 6;

		currentPlayer = PLAYER_ONE;

		gameStatus = GameStatus.PLAY_PLAYER_ONE;
	}

	/*
	 * Makes a move on the board.
	 * 
	 * @param move the column of the move
	 * 
	 * @return The row of the move.
	 */
	private int makeMove(int move) {
		// fullColumns values go from 1-6, therefore not zero based.
		int row = fullColumns[move] + 2;
		board[row][move + 3] = currentPlayer;
		fullColumns[move]--;
		return row;
	}

	/*
	 * Checks to see if the move is at a column that is not full.
	 * 
	 * @param move The column.
	 * 
	 * @return If the move is valid
	 */
	private boolean validateMove(int move) {
		return (fullColumns[move] > 0 && fullColumns[move] < 7);
	}

	/*
	 * Checks if the game is over.
	 * 
	 * @param col The col of the move
	 * 
	 * @param row The row of the move
	 */
	private void checkIfGameIsOver(int col, int row, int player) {
		col += 3;
		boolean isDraw = true;
		for (int c : fullColumns)
			if (c != 0) {
				isDraw = false;
				break;
			}
		if (isDraw) {
			gameStatus = GameStatus.OVER_DRAW;
		}
		// Check if someone won.
		if (checkPosWin(row, col, currentPlayer)) {
			if (currentPlayer == PLAYER_ONE) {
				gameStatus = GameStatus.OVER_PLAYER_ONE;
			} else {
				gameStatus = GameStatus.OVER_PLAYER_TWO;
			}
		} else {
			if (!isDraw) {
				if (currentPlayer == PLAYER_ONE) {
					System.out.println("Changing currentPlayer to PLAYER_TWO");
					gameStatus = GameStatus.PLAY_PLAYER_TWO;
					currentPlayer = PLAYER_TWO;
				} else {
					gameStatus = GameStatus.PLAY_PLAYER_ONE;
					System.out.println("Changing currentPlayer to PLAYER_ONE");
					currentPlayer = PLAYER_ONE;
				}
			}
		}
	}

	/**
	 * Checks to see if the move sent in is a winning move
	 * 
	 * @param row
	 * @param col
	 * @param player
	 * @return
	 */
	public boolean checkPosWin(int row, int col, int player) {
		int numOfCoins = 1;

		// Checking VERTICAL
		for (int x = 1; x < 4; x++)
			if (board[row + x][col] == player)
				numOfCoins++;
			else
				break;

		if (numOfCoins >= 4) {
			return true;
		}

		// Checking HORIZONTAL
		numOfCoins = 1;
		for (int x = 1; x < 4; x++)
			if (board[row][col - x] == player)
				numOfCoins++;
			else
				break;

		if (numOfCoins >= 4) {
			return true;
		}

		for (int x = 1; x < 4; x++)
			if (board[row][col + x] == player)
				numOfCoins++;
			else
				break;
		if (numOfCoins >= 4) {
			return true;
		}

		// Checking DIAGONAL: top left to bottom right
		numOfCoins = 1;
		for (int x = 1; x < 4; x++)
			if (board[row - x][col + x] == player)
				numOfCoins++;
			else
				break;

		if (numOfCoins >= 4) {
			return true;
		}

		for (int x = 1; x < 4; x++)
			if (board[row + x][col - x] == player)
				numOfCoins++;
			else
				break;

		if (numOfCoins >= 4) {
			return true;
		}

		// Checking DIAGONAL: top right to bottom left
		numOfCoins = 1;
		for (int x = 1; x < 4; x++)
			if (board[row - x][col - x] == player)
				numOfCoins++;
			else
				break;

		if (numOfCoins >= 4) {
			return true;
		}

		for (int x = 1; x < 4; x++)
			if (board[row + x][col + x] == player)
				numOfCoins++;
			else
				break;

		if (numOfCoins >= 4) {
			return true;
		}

		return false;
	}

	/**
	 * Displays the board.
	 */
	public void displayBoard() {
		String line = "-----------------";
		System.out.println(line);
		for (int x = 0; x < board.length; x++) {
			System.out.print("| ");
			for (int y = 0; y < board[0].length; y++) {
				System.out.print(board[x][y]);
			}
			System.out.print(" |\n");
		}
		System.out.println(line);
	}

	/**
	 * Returns the game's status.
	 * 
	 * @return The game status
	 */
	public GameStatus getGameStatus() {
		return gameStatus;
	}
}

/**
 * Defines an enum type for the game's status.
 * 
 * @author Natacha Gabbamonte
 * @author Gabriel Gheorghian
 * @author Mark Scerbo
 * 
 */
enum GameStatus {
	WAIT, PLAY_PLAYER_ONE, PLAY_PLAYER_TWO, OVER_PLAYER_ONE, OVER_PLAYER_TWO, OVER_DRAW
}