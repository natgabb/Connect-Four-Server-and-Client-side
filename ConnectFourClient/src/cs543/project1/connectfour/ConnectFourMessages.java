package cs543.project1.connectfour;

/**
 * Defines variables that will be used by the server and the client to
 * communicate.
 * 
 * @author Natacha Gabbamonte
 * @author Gabriel Gheorghian
 * @author Mark Scerbo
 * 
 */
public class ConnectFourMessages {

	// Server to Client
	public static final byte PLAYER_ONE_PLAY = 10; // Second byte is the
													// player's move being sent
													// back.
	public static final byte PLAYER_TWO_PLAY = 20; // Second byte is player 2's
													// move.
	public static final byte ERROR_WITH_PLAY = 30; // Second byte is the
													// player's move being sent
													// back.
	public static final byte PLAY_IS_GOOD = 40; // Second byte is garbage.

	public static final byte GAME_OVER = 50; // Second byte is one of the 3
												// below.
	public static final byte DRAW = 51; // Possible second byte of GAME_OVER
	public static final byte PLAYER_ONE_WON = 52; // Possible second byte of
													// GAME_OVER
	public static final byte PLAYER_TWO_WON = 53; // Possible second byte of
													// GAME_OVER

	// Client to Server
	public static final byte NORMAL_MOVE = 90; // Second byte is the client's
												// move.
	public static final byte FORFEIT = 91; // Second byte is garbage.
	public static final byte PLAY_AGAIN = 92; // Second byte is garbage.
	public static final byte NOT_PLAY_AGAIN = 93; // Second byte is garbage.

	// Used for both
	public static final byte GARBAGE = 99; // placeholder
}
