package cs543.project1.connectfour;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * An app that will start the ConnectFour server-side.
 * 
 * @author Natacha Gabbamonte
 * @author Gabriel Gheorghian
 * @author Mark Scerbo
 * 
 */
public class C4ServerSession {

	private static final int NUM_BYTES = 2;
	private C4Game game;
	private GameStatus gameStatus;
	private boolean gameOver;
	private boolean playAgain;
	private static final int PLAYER_ONE = 1;
	private static final int PLAYER_TWO = 2;
	private Socket socket;
	private GameType gameType = null;
	private int currentPlayer = 0;

	/**
	 * Constructor for the game's session. Receives a socket.
	 * 
	 * @param socket
	 *            The socket of the client.
	 */
	public C4ServerSession(Socket socket) {
		System.out.println("Session created.");
		this.socket = socket;
		System.out.println("Creating new game.");

		// Need to ask the client what type of game it is.
		try {
			sendPacket(C4Messages.WHAT_TYPE, C4Messages.GARBAGE);
			processInputPacket();

			game = new C4Game();
			playAgain = true;
			while (playAgain) {
				gameOver = false;
				do {
					respondToGameStatus();
					game.displayBoard();
				} while (!gameOver);

				// Check to see if the Player wants to play again.
				// processInputPacket();
				if (playAgain) {
					System.out
							.println("PLAYER wants to play again, creating new game.");
					game.newGame();
				}
			}
		} catch (IOException e1) {
			System.out
					.println("Client closed connection prematurely.\nClosing session");
		}
		try {
			System.out.println("Closing client socket...");
			socket.close();
			System.out.println("Client socket closed. Session is over.");
		} catch (IOException e) {
			System.out.println("Error occured closing client socket.");
			e.printStackTrace();
		}
	}

	/**
	 * Receives a packet from the client.
	 * 
	 * @return The received message.
	 */
	public byte[] receivePacket() throws IOException {
		byte[] receivedMessages = null;
		try {
			InputStream in = socket.getInputStream();
			receivedMessages = new byte[NUM_BYTES];
			int receivedBytes = 0;
			while (receivedBytes < NUM_BYTES)
				receivedBytes = in.read(receivedMessages);
			System.out.println("Messaged Received:\n[0] " + receivedMessages[0]
					+ "\n[1] " + receivedMessages[1]);

		} catch (IOException e) {
			System.out.println("Error occured receiving packet.");
			throw e;
		}

		return receivedMessages;
	}

	/**
	 * Sends the message.
	 * 
	 * @param message
	 *            The message to send.
	 */
	public void sendPacket(byte firstMessage, byte secondMessage)
			throws IOException {
		try {
			OutputStream out = socket.getOutputStream();
			byte[] byteBuffer = { firstMessage, secondMessage };
			out.write(byteBuffer);
			System.out.println("Messaged Sent:\n[0] " + byteBuffer[0]
					+ "\n[1] " + byteBuffer[1]);
		} catch (IOException e) {
			System.out.println("Error occured sending packet.");
			throw e;
		}
	}

	/**
	 * Processes the input message from the client
	 */
	private void processInputPacket() throws IOException {
		byte[] messages = receivePacket();
		switch (messages[0]) {
		case C4Messages.FORFEIT:
			gameOver = true;
			sendPacket(C4Messages.GAME_OVER, C4Messages.PLAYER_TWO_WON);
			processInputPacket();
			break;
		case C4Messages.PLAY_AGAIN:
			sendPacket(C4Messages.WHAT_TYPE, C4Messages.GARBAGE);
			processInputPacket();
			playAgain = true;
			break;
		case C4Messages.NOT_PLAY_AGAIN:
			playAgain = false;
			break;
		case C4Messages.NORMAL_MOVE:
			makeAPlay(messages[1]);
			break;
		case C4Messages.GAME_TYPE:
			switch (messages[1]) {
			case C4Messages.SINGLEPLAYER:
				System.out.println("This game will be SINGLEPLAYER.");
				gameType = GameType.SINGLEPLAYER;
				break;
			case C4Messages.MULTIPLAYER:
				System.out.println("This game will be MULTIPLAYER.");
				gameType = GameType.MULTIPLAYER;
				break;
			}
		}
	}

	/**
	 * Sends in a column (depending on what the user input)
	 * @param move
	 * @throws IOException
	 */
	private void makeAPlay(int move) throws IOException {
		if (game.play(move, currentPlayer)) {
			// Move was successful.
			System.out.println("PLAYER " + currentPlayer
					+ "'s move was successful: " + move);
			sendPacket(C4Messages.PLAY_IS_GOOD, (byte) move);
		} else {
			// Move was not successful, request another move from the
			// player.
			System.out.println("PLAYER " + currentPlayer
					+ "'s move was NOT  successful: " + move);
			sendPacket(C4Messages.ERROR_WITH_PLAY, (byte) move);
		}
	}

	/**
	 * Responds to the current game status.
	 */
	private void respondToGameStatus() throws IOException {
		gameStatus = game.getGameStatus();
		int move;
		System.out.println("Current Game Status: " + gameStatus);
		switch (gameStatus) {
		case PLAY_PLAYER_ONE:
			currentPlayer = PLAYER_ONE;
			// Request move from the player.
			sendPacket(C4Messages.PLAYER_ONE_PLAY, C4Messages.GARBAGE);
			processInputPacket();
			break;
		case PLAY_PLAYER_TWO:
			currentPlayer = PLAYER_TWO;
			if (gameType == GameType.SINGLEPLAYER) {
				move = game.playForAI();
				// Send the move played by the AI to the player.
				System.out.println("PLAYER_TWO move: " + move);
				sendPacket(C4Messages.PLAYER_TWO_PLAY, (byte) move);
			} else { // The client will be sending PLAYER_TWO's move
				sendPacket(C4Messages.PLAYER_TWO_PLAY, C4Messages.GARBAGE);
				processInputPacket();
			}
			break;
		case OVER_PLAYER_ONE:
			gameOver = true;
			// Send Player_One_Won message.
			sendPacket(C4Messages.GAME_OVER, C4Messages.PLAYER_ONE_WON);
			processInputPacket();
			break;
		case OVER_PLAYER_TWO:
			gameOver = true;
			// Send Player_Two_Won message.
			sendPacket(C4Messages.GAME_OVER, C4Messages.PLAYER_TWO_WON);
			processInputPacket();
			break;
		case OVER_DRAW:
			gameOver = true;
			// Send Game_Draw message.
			sendPacket(C4Messages.GAME_OVER, C4Messages.DRAW);
			processInputPacket();
			break;
		case WAIT:
			// Do nothing.
			break;
		}
	}

	/**
	 * Enum to check the game type and the number of human players
	 * @author 0737019
	 *
	 */
	public enum GameType {
		SINGLEPLAYER, MULTIPLAYER
	}
}
