package cs543.project1.connectfour;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * The object to keep track of player and AI movements as well as mouse events
 * 
 * @author Natacha Gabbamonte
 * @author Gabriel Gheorghian
 * @author Mark Scerbo
 * 
 */
public class C4BoardPanel extends JPanel {

	public enum GameType {
		SINGLEPLAYER, MULTIPLAYER
	}

	private static final long serialVersionUID = -4107255535013778600L;
	private int[] fullColumns = new int[7];

	private ImageIcon DEFAULT;
	private ImageIcon PLAYER_ONE;
	private ImageIcon PLAYER_TWO;
	private ImageIcon currentPlayer = null;

	private final Color DEFAULT_PANEL_BACK = new JPanel().getBackground();
	private final Color HOVER_PANEL_BACK = new Color(0xFAFAD2);
	private final Color CLICK_PANEL_BACK = HOVER_PANEL_BACK.darker().darker();

	private C4Model model;
	private C4ServerConnection connection;
	private JPanel[] columns = new JPanel[7];
	private GridBagLayout layout = new GridBagLayout();
	private JLabel[][] allLabels = new JLabel[6][7];
	@SuppressWarnings("unused")
	private String ipNumber = null;

	private boolean waitingForMove = false;

	private GameType gameType = null;
	/*
	 * The MouseAdapter that listens for clicks on the columns.
	 */
	private MouseAdapter mouseAdapter = new MouseAdapter() {
		/**
		 * This will change the color of the column as the client Hovers over
		 * the column
		 */
		public void mouseEntered(MouseEvent e) {
			JPanel panel = (JPanel) e.getSource();
			for (int i = 0; i < columns.length; i++)
				if (columns[i] == panel)
					columns[i].setBackground(HOVER_PANEL_BACK);
				else
					columns[i].setBackground(DEFAULT_PANEL_BACK);

		}

		/**
		 * This is to set all of the columns' backgrounds back to default.
		 */
		public void mouseExited(MouseEvent e) {
			for (int i = 0; i < columns.length; i++)
				columns[i].setBackground(DEFAULT_PANEL_BACK);
		}

		/**
		 * This will do all the processing of the move once a player clicks on a
		 * column (attemps to add a piece to that column).
		 */
		public void mousePressed(MouseEvent e) {
			if (waitingForMove) {
				waitingForMove = false;
				JPanel panel = (JPanel) e.getSource();
				int i = 0;
				for (i = 0; i < columns.length; i++)
					if (columns[i] == panel) {
						columns[i].setBackground(CLICK_PANEL_BACK);
						break;
					}
				if (fullColumns[i] > 0)
					try {
						model.setMessage(" ");
						connection.sendPacket(new byte[] {
								C4Messages.NORMAL_MOVE, (byte) i });
						checkForNextAction();
					} catch (IOException e1) {
						model.setMessage("An error occured with the connection with the server: "
								+ e1.getMessage());
					}
				else {
					waitingForMove = true;
					model.setMessage("You cannot add a coin to that column, it is full.");
				}
			}

		}

		/**
		 * Reverts the background of the column back to original color.
		 */
		public void mouseReleased(MouseEvent e) {
			JPanel panel = (JPanel) e.getSource();
			panel.setBackground(HOVER_PANEL_BACK);
		}
	};

	/*
	 * The KeyAdapter for the board so that the user can press 1-7 so "click" on
	 * the columns.
	 */
	private KeyAdapter keyAdapter = new KeyAdapter() {

		/**
		 * Will generate a MouseEvent if a number from 1-7 was pressed.
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			char key = e.getKeyChar();
			int col = -1;
			switch (key) {
			case '1':
				col = 0;
				break;
			case '2':
				col = 1;
				break;
			case '3':
				col = 2;
				break;
			case '4':
				col = 3;
				break;
			case '5':
				col = 4;
				break;
			case '6':
				col = 5;
				break;
			case '7':
				col = 6;
				break;
			}
			if (col != -1) {
				columns[col].dispatchEvent(new MouseEvent(columns[col],
						MouseEvent.MOUSE_PRESSED, 0, 0, 0, 0, 1, false,
						MouseEvent.BUTTON1));
				columns[col].dispatchEvent(new MouseEvent(columns[col],
						MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, 1, false,
						MouseEvent.BUTTON1));
			}
		}
	};

	/**
	 * Constructor: Creates the C4BoardPanel Object
	 * 
	 * @param model
	 *            :handle to the model
	 * @param ipNumber
	 *            :ip of the server connecting to
	 * @throws UnknownHostException
	 *             If the host that was entered is invalid.
	 * @throws IOException
	 *             If an error occurs with the connection.
	 */
	public C4BoardPanel(C4Model model, String ipNumber)
			throws UnknownHostException, IOException {
		super();
		gameType = GameType.SINGLEPLAYER;
		this.addKeyListener(keyAdapter);
		DEFAULT = new ImageIcon(
				C4GuiApp.class.getResource("images/default.png"));
		PLAYER_ONE = new ImageIcon(
				C4GuiApp.class.getResource("images/green_coin.png"));
		PLAYER_TWO = new ImageIcon(
				C4GuiApp.class.getResource("images/red_coin.png"));
		this.model = model;
		this.setLayout(layout);
		for (int i = 0; i < fullColumns.length; i++)
			fullColumns[i] = 6;
		populatePanel();
		this.ipNumber = ipNumber;
		connection = new C4ServerConnection(ipNumber);
		model.resetGameIsOver();
		requestFocusForPanel();
		checkForNextAction();
	}

	/**
	 * Requests the focus to be on this panel.
	 */
	public void requestFocusForPanel() {
		this.requestFocusInWindow();
	}

	/**
	 * This lets server know the client forfeits.
	 * 
	 * @throws IOException
	 *             If an error occurs with the connection.
	 */
	public void forfeit(GameType gameType) throws IOException {
		if (this.gameType != gameType) {
			model.setNextIsNewGame(true);
			if (gameType == GameType.SINGLEPLAYER)
				model.setGameOverMessage(2);
			else
				model.setGameOverMessage(1);
		}

		this.gameType = gameType;
		connection.sendPacket(new byte[] { C4Messages.FORFEIT,
				C4Messages.GARBAGE });
		checkForNextAction();
	}

	/**
	 * This populates the panel with all the inner grid bags (one per column)
	 * and all the labels contained by said gridbags.
	 */
	private void populatePanel() {
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new JPanel();
			columns[i].setLayout(new GridBagLayout());
			columns[i].setPreferredSize(new Dimension(50, 300));
			for (int x = 0; x < 6; x++) {
				JLabel label = new JLabel();
				label.setBorder(border);
				label.setHorizontalAlignment(JLabel.CENTER);
				// label.setOpaque(true);
				label.setIcon(DEFAULT);
				label.setMinimumSize(new Dimension(50, 50));
				allLabels[x][i] = label;
				columns[i].add(label, makeConstraints(0, x, 1, 1));
			}

			// Adding the board
			this.add(columns[i], makeConstraints(i, 0, 1, 6));
			columns[i].addMouseListener(mouseAdapter);
		}
	}

	/**
	 * Sets constraints based on the values sent in.
	 * 
	 * @param gridx
	 *            x coordinates
	 * @param gridy
	 *            y coordinates
	 * @param gridwidth
	 *            number of columns to go across
	 * @param gridheight
	 *            number of rows to go across
	 * @return constraints (the constraints to be added to a component
	 */
	private GridBagConstraints makeConstraints(int gridx, int gridy,
			int gridwidth, int gridheight) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridheight = gridheight;
		constraints.gridwidth = gridwidth;
		constraints.gridx = gridx;
		constraints.gridy = gridy;

		// Default for all the components.
		constraints.insets = new Insets(1, 1, 1, 1);
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		return constraints;
	}

	/**
	 * this displays message and process based on who's move it is and what may
	 * be going on at the moment
	 * 
	 * @throws IOException
	 *             If an error occurs with the connection.
	 */
	private void checkForNextAction() throws IOException {
		byte[] messages = connection.receivePacket();

		switch (messages[0]) {
		case C4Messages.WHAT_TYPE:
			switch (gameType) {
			case MULTIPLAYER:
				connection.sendPacket(new byte[] { C4Messages.GAME_TYPE,
						C4Messages.MULTIPLAYER });
				break;
			case SINGLEPLAYER:
				connection.sendPacket(new byte[] { C4Messages.GAME_TYPE,
						C4Messages.SINGLEPLAYER });
				break;
			}
			checkForNextAction();
			break;
		case C4Messages.PLAYER_ONE_PLAY:
			currentPlayer = PLAYER_ONE;
			model.setMessage("Player One's turn.");
			waitingForMove = true;
			break;
		case C4Messages.PLAYER_TWO_PLAY:
			currentPlayer = PLAYER_TWO;
			if (gameType == GameType.SINGLEPLAYER) {
				updateBoard(messages[1], currentPlayer);
				model.setMessage(" ");
				checkForNextAction();
			} else {
				model.setMessage("Player Two's turn.");
				waitingForMove = true;
			}
			break;
		case C4Messages.ERROR_WITH_PLAY:
			model.setMessage("You cannot add a coin to that column, it is full.");
			checkForNextAction();
			break;
		case C4Messages.PLAY_IS_GOOD:
			updateBoard(messages[1], currentPlayer);
			model.setMessage(" ");
			checkForNextAction();
			break;
		case C4Messages.GAME_OVER:
			String gameOverMessage = "Game Over! ";
			switch (messages[1]) {
			case C4Messages.PLAYER_ONE_WON:
				gameOverMessage += "PLAYER ONE WON!";
				model.addWin();
				model.setWinner(1);
				break;
			case C4Messages.PLAYER_TWO_WON:
				gameOverMessage += "PLAYER TWO WON!";
				model.addLoss();
				model.setWinner(2);
				break;
			case C4Messages.DRAW:
				gameOverMessage += "IT'S A DRAW!";
				model.addDraw();
				model.setWinner(0);
				break;
			}
			model.setMessage(gameOverMessage);
			model.gameIsOver();
			break;
		}
	}

	/**
	 * This updates the board and places the image based on the player playing.
	 * 
	 * @param column
	 *            :column the move was done in
	 * @param player
	 *            :player making move
	 */
	private void updateBoard(byte column, ImageIcon player) {
		allLabels[fullColumns[column] - 1][column].setIcon(player);
		fullColumns[column]--;
	}

	/**
	 * This is used to reset the board to original settings
	 */
	private void resetValues() throws IOException {
		for (int x = 0; x < allLabels.length; x++)
			for (int y = 0; y < allLabels[0].length; y++)
				allLabels[x][y].setIcon(DEFAULT);
		for (int i = 0; i < fullColumns.length; i++)
			fullColumns[i] = 6;
	}

	/**
	 * This will inform the server that the player wishes to play again.
	 * 
	 * @throws IOException
	 *             If an error occurs with the connection.
	 */
	public void playerWantsToPlayAgain() throws IOException {
		connection.sendPacket(new byte[] { C4Messages.PLAY_AGAIN,
				C4Messages.GARBAGE });
		model.resetGameIsOver();

		checkForNextAction();
		resetValues();
		model.setMessage(" ");
		requestFocusForPanel();
	}

	/**
	 * This lets server know player is done playing C4.
	 * 
	 * @throws IOException
	 *             If an error occurs with the connection.
	 */
	public void playerDoesntWantToPlayAgain() throws IOException {
		connection.sendPacket(new byte[] { C4Messages.NOT_PLAY_AGAIN,
				C4Messages.GARBAGE });
		connection.closeConnection();
	}

}