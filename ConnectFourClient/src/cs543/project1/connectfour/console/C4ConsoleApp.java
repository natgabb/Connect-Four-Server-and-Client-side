package cs543.project1.connectfour.console;

import java.io.IOException;
import java.util.Scanner;

import cs543.project1.connectfour.C4ServerConnection;
import cs543.project1.connectfour.C4Messages;

/**
 * This is a console application of the Connect 4 game.
 * 
 * @author Natacha Gabbamonte
 * @author Gabriel Gheorghian
 * @author Mark Scerbo
 * 
 */
public class C4ConsoleApp {

	/**
	 * Starts the app.
	 * 
	 * @param args
	 */
	private static String starLine = "********************************************************************************";
	private static Scanner keyboard = null;
	private static C4ServerConnection serverConnection = null;
	private static String[][] board;
	private static int[] rows;
	private static boolean gameIsOver = false;
	private static final String PLAYER = "1";
	private static final String OPPONENT = "2";

	/**
	 * Main method. Continuously checks for packets from the server, until the
	 * user quits.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("\n" + starLine);
		print("CONNECT FOUR");
		System.out.println("\n" + starLine + "\n");
		keyboard = new Scanner(System.in);
		String server = acceptServerIP();
		if (server.toUpperCase().charAt(0) == 'Q')
			quit();
		try {
			serverConnection = new C4ServerConnection(server);
			print("Connected to server (" + server + ") successfully.");
			print("Press enter to start!");
			keyboard.nextLine();
			print("Enter Q to quit this game at any time. Enter F to forfeit the current game.");
			do {
				gameIsOver = false;
				board = new String[6][7];
				for (int x = 0; x < board.length; x++)
					for (int y = 0; y < board[0].length; y++)
						board[x][y] = "0";
				rows = new int[] { 5, 5, 5, 5, 5, 5, 5 };
				displayBoard();
				do {
					processPacket(serverConnection.receivePacket());
				} while (!gameIsOver);
			} while (playAgain());

			System.out.println(starLine);
			print("Thanks for playing!");
			System.out.println("\n" + starLine);
		} catch (IOException e) {
			print("An error has occured: " + e.getMessage());
		}

	}

	/*
	 * Checks to see if the user wants to play again.
	 */
	private static boolean playAgain() throws IOException {
		String response = "";
		boolean invalid = true;
		boolean willPlayAgain = false;
		do {
			print("Would you like to play again? (Y/N)");
			response = keyboard.nextLine().trim();
			if (response.length() > 0) {
				char firstLetter = response.toUpperCase().charAt(0);
				if (firstLetter == 'Q')
					quit();
				if (firstLetter == 'Y') {
					willPlayAgain = true;
					invalid = false;
				} else if (firstLetter == 'N')
					invalid = false;
			}
		} while (invalid);

		if (willPlayAgain)
			serverConnection.sendPacket(new byte[] { C4Messages.PLAY_AGAIN,
					C4Messages.GARBAGE });
		else
			serverConnection.sendPacket(new byte[] { C4Messages.NOT_PLAY_AGAIN,
					C4Messages.GARBAGE });
		return (willPlayAgain);
	}

	/*
	 * Displays the game board.
	 */
	private static void displayBoard() {
		String line = addSpacesInFrontOfString("-----------");
		System.out.println(line);
		String aLine = "";
		for (int x = 0; x < board.length; x++) {
			aLine += "| ";
			for (int y = 0; y < board[0].length; y++) {
				aLine += board[x][y];
			}
			aLine += " |";
			print(aLine);
			aLine = "";
		}
		System.out.println(line);
	}

	/*
	 * Processes the messages received from the server.
	 */
	private static void processPacket(byte[] messages) throws IOException {
		switch (messages[0]) {
		case C4Messages.WHAT_TYPE:
			serverConnection.sendPacket(new byte[] { C4Messages.GAME_TYPE,
					C4Messages.SINGLEPLAYER });
			break;
		case C4Messages.PLAYER_ONE_PLAY:
			askForMove();
			break;
		case C4Messages.PLAYER_TWO_PLAY:
			print("Your opponent's move:");
			updateBoard(messages[1], OPPONENT);
			break;
		case C4Messages.ERROR_WITH_PLAY:
			print("You cannot add a coin to that column, it is full.");
			break;
		case C4Messages.PLAY_IS_GOOD:
			print("Your move was successful:");
			updateBoard(messages[1], PLAYER);
			break;
		case C4Messages.GAME_OVER:
			gameIsOver = true;
			String gameOverMessage = starLine + "\n"
					+ addSpacesInFrontOfString("GAME OVER") + "\n";
			switch (messages[1]) {
			case C4Messages.PLAYER_ONE_WON:
				gameOverMessage += addSpacesInFrontOfString("YOU WON!");
				break;
			case C4Messages.PLAYER_TWO_WON:
				gameOverMessage += addSpacesInFrontOfString("YOUR OPPONENT WON!");
				break;
			case C4Messages.DRAW:
				gameOverMessage += addSpacesInFrontOfString("IT'S A DRAW!");
				break;
			}
			gameOverMessage += "\n\n" + starLine;
			System.out.println(gameOverMessage);
			break;
		}
	}

	/*
	 * Asked the user for a move.
	 */
	private static void askForMove() throws IOException {
		String response = "";
		int move = -1;
		boolean invalid = true;

		while (invalid) {
			try {
				do {
					print("Which column would you like to add a coin? (1-7)");
					response = keyboard.nextLine().trim();
				} while (response.equals(""));
				char firstChar = response.toUpperCase().charAt(0);
				if (firstChar == 'Q')
					quit();
				else if (firstChar == 'F') {
					serverConnection.sendPacket(new byte[] {
							C4Messages.FORFEIT, C4Messages.GARBAGE });
					invalid = false;
				}

				else {
					move = Integer.parseInt(response);
					if (move > 0 && move < 8) {
						invalid = false;
						move = move - 1;
						serverConnection.sendPacket(new byte[] {
								C4Messages.NORMAL_MOVE, (byte) move });
					} else {
						print("You must enter a column number between 1 and 7 inclusive!");
						keyboard.nextLine();
					}
				}
			} catch (NumberFormatException e) {
				invalid = true;
			}
			if (invalid) {
				print("You must enter a column number between 1 and 7 inclusive!");
				keyboard.nextLine();
			}
		}
	}

	/*
	 * Updates one place on the board.
	 * 
	 * @param move The move that is played.
	 * 
	 * @param player Who's move it is.
	 */
	private static void updateBoard(int move, String player) {
		board[rows[move]][move] = player;
		rows[move]--;
		displayBoard();
	}

	/*
	 * Fixes a string to have a correct number of spaces in front to make the
	 * string appear centered.
	 * 
	 * @param s The string to be fixed.
	 */
	private static String addSpacesInFrontOfString(String s) {
		int characters = 40 - (s.length() / 2);
		String spaces = "";
		for (int i = 0; i < characters; i++)
			spaces += " ";
		return spaces + s;

	}

	/*
	 * Prints a message.
	 * 
	 * @param message The message to be printed.
	 */
	private static void print(String message) {
		System.out.println(addSpacesInFrontOfString(message));
	}

	/*
	 * Accepts from the user an IP address.
	 * 
	 * @return The server's IP address.
	 */
	private static String acceptServerIP() {
		String response = "";
		do {
			print("Please enter the IP of the Connect Four server you want to play on:");
			response = keyboard.nextLine().trim();
		} while (response.equals(""));
		return response;
	}

	/*
	 * Displays a message and quits the app.
	 */
	private static void quit() {
		print("Quitting application.");
		System.exit(0);
	}
}
