package cs543.project1.connectfour;

import java.util.Observable;

/**
 * This is where we keep track of the messages to be displayed and the we keep
 * track of the wins, loses, draws.
 * 
 * @author Natacha Gabbamonte
 * @author Gabriel Gheorghian
 * @author Mark Scerbo
 * 
 */
public class C4Model extends Observable {

	private String message = "";
	private boolean gameIsOver = false;
	private boolean nextIsNewGame = false;
	private int losses = 0;
	private int wins = 0;
	private int draws = 0;

	private int winner = -1;
	private int gameOverMessage = 0;

	/**
	 * Sets the display message.
	 * 
	 * @param message
	 *            the message to be stored and displayed later.
	 */
	public void setMessage(String message) {
		this.message = message;
		setChanged();
		notifyObservers();
	}

	/**
	 * Returns the display message.
	 * 
	 * @return string of a message sent in earlier
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Notify Observers that game is over.
	 */
	public void gameIsOver() {
		gameIsOver = true;
		setChanged();
		notifyObservers();
	}

	/**
	 * Resets the value of game over, making it no longer over.
	 */
	public void resetGameIsOver() {
		gameIsOver = false;
		setChanged();
		notifyObservers();
	}

	/**
	 * Returns whether or not the game is over.
	 * 
	 * @return boolean which indicates true if game is over and false if it is
	 *         not
	 */
	public boolean getGameIsOver() {
		return gameIsOver;
	}

	/**
	 * returns losses
	 * 
	 * @return int the nubmer of losses so far
	 */
	public int getLosses() {
		return losses;
	}

	/**
	 * Increments number of losses so far.
	 */
	public void addLoss() {
		losses++;
	}

	/**
	 * Returns the number wins .
	 * 
	 * @return int the number of wins so far
	 */
	public int getWins() {
		return wins;
	}

	/**
	 * Increments the number of wins so far =.
	 */
	public void addWin() {
		wins++;
	}

	/**
	 * Returns number of draws so far.
	 * 
	 * @return int the current number of draws
	 */
	public int getDraws() {
		return draws;
	}

	/**
	 * Increments the number of draws so far.
	 */
	public void addDraw() {
		draws++;
	}

	/**
	 * Returns the winner.
	 * 
	 * @return int the winner of the game
	 */
	public int getWinner() {
		return winner;
	}

	/**
	 * Sets the winner.
	 * 
	 * @param winner
	 *            the player who won the game.
	 */
	public void setWinner(int winner) {
		this.winner = winner;
	}

	/**
	 * Returns whether the next game will be a new game type.
	 * 
	 * @return True or false depending.
	 */
	public boolean isNextIsNewGame() {
		return nextIsNewGame;
	}

	/**
	 * Sets whether the next game will be a new game type.
	 * 
	 * @param nextIsNewGame
	 */
	public void setNextIsNewGame(boolean nextIsNewGame) {
		this.nextIsNewGame = nextIsNewGame;
	}

	/**
	 * Resets the stats of the session
	 */
	public void resetStats() {
		wins = 0;
		losses = 0;
		draws = 0;
	}

	/**
	 * Returns a game over message
	 * @return
	 */
	public int getGameOverMessage() {
		return gameOverMessage;
	}

	/**
	 * Sets the game over message
	 * @param gameOverMessage
	 */
	public void setGameOverMessage(int gameOverMessage) {
		this.gameOverMessage = gameOverMessage;
	}
}
