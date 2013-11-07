package cs543.project1.connectfour;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import cs543.project1.connectfour.C4BoardPanel.GameType;

/**
 * 
 * This is the Class that sets up and displays the menu bar on the top of the
 * window
 * 
 * @author Natacha Gabbamonte
 * @author Gabriel Gheorghian
 * @author Mark Scerbo
 * 
 */
public class C4Menu {

	C4ContentCardLayout cardLayout;
	JMenu newGame;
	JMenu file;
	JMenuItem singleplayer;
	JMenuItem multiplayer;

	/**
	 * instantiates the object
	 */
	public C4Menu() {
		newGame = new JMenu("New Game");
		newGame.setEnabled(false);
	}

	private MouseAdapter mouseAdapter = new MouseAdapter() {
		/**
		 * to handle the mouse release event
		 */
		public void mouseReleased(MouseEvent e) {
			try {
				GameType gameType = null;
				if (e.getSource() == singleplayer)
					gameType = GameType.SINGLEPLAYER;
				else
					gameType = GameType.MULTIPLAYER;
				cardLayout.changePanel("Game", gameType);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "An error has occured: "
						+ e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	};

	/**
	 * sets up the options on the menu bar and the options with in the first
	 * layer of options
	 * 
	 * @param cardLayout
	 *            :handle to the layout
	 * @return JMenuBar the menu to be added to the display
	 */
	public JMenuBar createMenuBar(final C4ContentCardLayout cardLayout) {
		this.cardLayout = cardLayout;
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;

		// Create the menu bar.
		menuBar = new JMenuBar();

		// Build the first menu.
		menu = new JMenu("File");
		menuBar.add(menu);

		// Adding sub-menu to New Game
		singleplayer = new JMenuItem("Singleplayer");
		multiplayer = new JMenuItem("Multiplayer");
		newGame.add(singleplayer);
		newGame.add(multiplayer);

		// Adding New Game to the menu.
		menu.add(newGame);

		menu.addSeparator();

		menuItem = new JMenuItem("Exit");
		menuItem.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				System.exit(0);
			}
		});
		menu.add(menuItem);

		// Build the second menu.
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);

		JMenuItem howToPlayItem = new JMenuItem("How To Play");
		howToPlayItem.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				JOptionPane
						.showMessageDialog(
								null,
								"The goal of this game is to connect four of your colored disks, horiziontally, vertically or\n"
										+ "diagonally while stopping your opponent from doing the same.",
								"How To Play", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		helpMenu.add(howToPlayItem);

		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				JOptionPane
						.showMessageDialog(
								null,
								"This game is inspired from the classic Connect Four game.\n"
										+ "It was created by Natacha Gabbamonte, Gabriel Gheorghian and Mark Scerbo",
								"About Connect Four",
								JOptionPane.INFORMATION_MESSAGE);
			}
		});

		helpMenu.add(aboutItem);

		return menuBar;
	}

	/**
	 * just disables the NewGame option under file
	 */
	public void disableNewGame() {
		newGame.setEnabled(false);
		singleplayer.removeMouseListener(mouseAdapter);
		multiplayer.removeMouseListener(mouseAdapter);
	}

	/**
	 * just enables the NewGame option under file
	 */
	public void enableNewGame() {
		newGame.setEnabled(true);
		singleplayer.addMouseListener(mouseAdapter);
		multiplayer.addMouseListener(mouseAdapter);
	}

	/**
	 * takes care if the player clicks new game when it is enabled (starts new
	 * game).
	 */
	public void doClickNewGame() {
		newGame.doClick();
	}
}
