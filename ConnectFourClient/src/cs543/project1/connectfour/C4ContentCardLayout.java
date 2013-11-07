package cs543.project1.connectfour;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cs543.project1.connectfour.C4BoardPanel.GameType;

/**
 * This is the Card Layout which will contain Panels to which will be rotated as
 * the user play or interact with the menu.
 * 
 * @author Natacha Gabbamonte
 * @author Gabriel Gheorghian
 * @author Mark Scerbo
 * 
 */
public class C4ContentCardLayout implements Observer {

	private C4Menu menu;
	private JPanel content;
	private C4Model model;
	private C4ScorePanel score;
	private C4GamePanel gamePanel = null;
	private final static String INPUTPANEL = "Input";
	private final static String GAMEPANEL = "Game";
	private final static String SCOREPANEL = "Score";

	/**
	 * 
	 * Constructor creating the card layout and populating the panels to be used
	 * with in the GUI.
	 * 
	 * @param menu
	 *            handle to the menu
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public C4ContentCardLayout(C4Menu menu) throws UnknownHostException,
			IOException {
		this.menu = menu;
		CardLayout card = new CardLayout(25, 25);
		content = new JPanel(card);
		content.setPreferredSize(new Dimension(500, 500));

		model = new C4Model();
		model.addObserver(this);

		C4InputPanel inputPanel = new C4InputPanel(this);

		score = new C4ScorePanel();

		content.add(inputPanel, INPUTPANEL);
		content.add(score, SCOREPANEL);

	}

	/**
	 * This will activate the game play for the client to start playing.
	 * 
	 * @param ipNumber
	 *            (to be used to connect to the server)
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void startGame(String ipNumber) throws UnknownHostException,
			IOException {

		gamePanel = new C4GamePanel(ipNumber, model);
		content.add(gamePanel, GAMEPANEL);
		((CardLayout) content.getLayout()).show(content, GAMEPANEL);
		menu.enableNewGame();
		requestFocusForPanel();

	}

	public void requestFocusForPanel() {
		gamePanel.requestFocusForPanel();
	}

	/**
	 * Will rotate the panels in the CardLayout
	 * 
	 * @param panel
	 *            (the panel to be displayed)
	 * @throws IOException
	 */
	public void changePanel(String panel, GameType gameType) throws IOException {
		if (panel.equals(GAMEPANEL)) {
			gamePanel.getBoardPanel().forfeit(gameType);
		} else {
			((CardLayout) content.getLayout()).show(content, panel);
		}
	}

	/**
	 * Returns the content panel.
	 * 
	 * @return The content panel.
	 */
	public Container getConnectFourContent() {
		return content;
	}

	private Timer t = null;

	/**
	 * updates the display
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (model.getGameIsOver()) {
			t = new Timer();
			t.schedule(new TimerTask() {
				public void run() {
					score.setScore();
					try {
						changePanel(SCOREPANEL, null);
						score.requestFocusForPanel();
					} catch (IOException e) {
						model.setMessage("An error has occured: "
								+ e.getMessage());
					}
					t.cancel();
				}
			}, 1000);
		} else {
			if (gamePanel != null) {
				((CardLayout) content.getLayout()).show(content, GAMEPANEL);
			}
		}
	}

	/**
	 * Inner class to create the panel to be displayed in the card layout during
	 * Game play
	 * 
	 * @author Natacha Gabbamonte
	 * @author Gabriel Gheorghian
	 * @author Mark Scerbo
	 */
	private class C4GamePanel extends JPanel implements Observer {

		private static final long serialVersionUID = 5636939329643138282L;
		private C4BoardPanel board;
		private C4Model model;
		JLabel message;

		/**
		 * Constructor in creating the C4GamePanel
		 * 
		 * @param ipNumber
		 *            (the ip address for the server connection)
		 * @param model
		 *            :handle to the model
		 * @throws UnknownHostException
		 * @throws IOException
		 */
		public C4GamePanel(String ipNumber, C4Model model)
				throws UnknownHostException, IOException {
			setLayout(new GridBagLayout());
			this.model = model;
			model.addObserver((Observer) this);
			JLabel heading = new JLabel("Connect Four", JLabel.CENTER);
			heading.setFont(new Font("Arial", Font.PLAIN, 25));
			message = new JLabel("Error message will go here!", JLabel.CENTER);
			board = new C4BoardPanel(model, ipNumber);
			board.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			add(heading, makeConstraints(0, 0, 1, 1, .2, .2));
			add(message, makeConstraints(0, 1, 1, 1, .2, .2));
			add(board, makeConstraints(0, 2, 1, 1, 1, 1));
		}

		public void requestFocusForPanel() {
			board.requestFocusForPanel();
		}

		/**
		 * Returns the board.
		 * 
		 * @return current board
		 */
		public C4BoardPanel getBoardPanel() {
			return board;
		}

		/**
		 * Updates the message stating state of play
		 */
		@Override
		public void update(Observable o, Object arg) {
			message.setText(model.getMessage());
		}
	}

	/**
	 * This is the panel to display player's score. This may be displayed in
	 * several locations.
	 * 
	 * @author Natacha Gabbamonte
	 * @author Gabriel Gheorghian
	 * @author Mark Scerbo
	 * 
	 */
	private class C4ScorePanel extends JPanel {

		private static final long serialVersionUID = -3115017397824719761L;
		JLabel winL;
		JLabel lossL;
		JLabel drawL;
		JLabel winner;
		JButton yesButton;
		JButton noButton;
		JLabel question;
		String[] winners = new String[] { "IT'S A DRAW", "PLAYER ONE WON!",
				"PLAYER TWO WON!" };
		String[] questions = new String[] { "Would you like to play again?",
				"Would you like to start a new Multiplayer game?",
				"Would you like to start a new Singleplayer game?" };

		/**
		 * Constructor to the Score panel populates variables and proccess them.
		 */
		public C4ScorePanel() {
			setLayout(new GridBagLayout());

			winL = new JLabel("", JLabel.CENTER);
			lossL = new JLabel("", JLabel.CENTER);
			drawL = new JLabel("", JLabel.CENTER);

			winner = new JLabel("", JLabel.CENTER);
			winner.setFont(new Font("Times New Roman", Font.BOLD, 20));
			question = new JLabel(" ", JLabel.CENTER);
			yesButton = new JButton("Yes");
			yesButton.addActionListener(new ActionListener() {

				/**
				 * updates the content
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					menu.enableNewGame();
					try {
						gamePanel.getBoardPanel().playerWantsToPlayAgain();
					} catch (IOException e1) {
						model.setMessage("An error has occured: "
								+ e1.getMessage());
						((CardLayout) content.getLayout())
								.show(content, "GAME");
					}
				}

			});

			noButton = new JButton("No");
			noButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						gamePanel.getBoardPanel().playerDoesntWantToPlayAgain();
					} catch (IOException e1) {
						model.setMessage("An error has occured: "
								+ e1.getMessage());
						((CardLayout) content.getLayout())
								.show(content, "GAME");
					}
					System.exit(0);
				}

			});

			// Adds a keyboard listener to listen for Y or N.
			this.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					char key = e.getKeyChar();
					switch (key) {
					case 'y':
					case 'Y':
						yesButton.doClick();
						break;
					case 'n':
					case 'N':
						noButton.doClick();
						break;
					}
				}
			});

			JLabel gameOverLabel = new JLabel("Game Over", JLabel.CENTER);
			gameOverLabel.setFont(new Font("Times New Roman", Font.BOLD, 25));

			add(gameOverLabel, makeConstraints(0, 0, 2, 1, 0.2, 0.2));
			add(winner, makeConstraints(0, 1, 2, 1, 0.2, 0.2));
			add(winL, makeConstraints(0, 2, 2, 1, 0.2, 0.2));
			add(lossL, makeConstraints(0, 3, 2, 1, 0.2, 0.2));
			add(drawL, makeConstraints(0, 4, 2, 1, 0.2, 0.2));

			add(question, makeConstraints(0, 5, 2, 1, 0.2, 0.2));
			add(yesButton, makeConstraints(0, 6, 1, 1, 0.2, 0.2));
			add(noButton, makeConstraints(1, 6, 1, 1, 0.2, 0.2));
		}

		/**
		 * This arranges the text to be displayed on the score panel
		 */
		public void setScore() {
			menu.disableNewGame();
			winner.setText(winners[model.getWinner()]);
			question.setText(questions[model.getGameOverMessage()]);
			winL.setText("Player one's Wins:     " + model.getWins());
			lossL.setText("Player two's Wins:     " + model.getLosses());
			drawL.setText("Draws:                          " + model.getDraws());
			if (model.isNextIsNewGame()) {
				model.setNextIsNewGame(false);
				model.setGameOverMessage(0);
				model.resetStats();
			}
		}

		public void requestFocusForPanel() {
			this.requestFocusInWindow();
		}
	}

	/**
	 * sets constraints based on the values sent in
	 * 
	 * @param gridx
	 *            x coordinates
	 * @param gridy
	 *            y coordinates
	 * @param gridwidth
	 *            number of columns to go across
	 * @param gridheight
	 *            number of rows to go across
	 * @param weightx
	 *            weight of the component in the x direction
	 * @param weighty
	 *            weight of the component in the y direction
	 * @return constraints (the constraints to be added to a component
	 */
	public static GridBagConstraints makeConstraints(int gridx, int gridy,

	int gridwidth, int gridheight, double weightx, double weighty) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridheight = gridheight;
		constraints.gridwidth = gridwidth;
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.weightx = weightx;
		constraints.weighty = weighty;

		// Default for all the components.
		constraints.insets = new Insets(1, 1, 1, 1);
		constraints.fill = GridBagConstraints.BOTH;
		return constraints;
	}
}
