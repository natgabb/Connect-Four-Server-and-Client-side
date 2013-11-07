package cs543.project1.connectfour;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * This is the initial Frame that will contain all other GUI components
 * 
 * @author Natacha Gabbamonte
 * @author Gabriel Gheorghian
 * @author Mark Scerbo
 * 
 */
public class C4GuiApp extends JFrame {

	private static final long serialVersionUID = -4278158477985191910L;

	private C4Menu menu;
	private C4ContentCardLayout content;

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				C4GuiApp gui = new C4GuiApp();
				gui.createAndShowGUI();
			}
		});
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private void createAndShowGUI() {
		// Create and set up the window.

		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Could not load Look & Feel: "
					+ e.getMessage(), "Look & Feel Error",
					JOptionPane.INFORMATION_MESSAGE);
		}
		menu = new C4Menu();
		try {
			content = new C4ContentCardLayout(menu);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					"An error has occured: " + e.getMessage()
							+ "\nThe game client will close now.");
			System.exit(0);
		}
		setJMenuBar(menu.createMenuBar(content));
		// Display the window.

		setTitle("Connect 4");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);

		GridBagConstraints constraints = new GridBagConstraints();

		// Prepare and add the button panel
		constraints.gridx = 0; // Column
		constraints.gridy = 0; // Row
		constraints.weightx = 0.2;
		constraints.weighty = 0.2;

		// Prepare and add the display panel
		constraints.gridx = 0; // Column
		constraints.gridy = 1; // Row
		constraints.weightx = 0.8;
		constraints.weighty = 0.8;
		constraints.fill = GridBagConstraints.BOTH;
		add(content.getConnectFourContent(), constraints);

		this.setIconImage(getCoinImg("images/icon.gif"));
		setSize(500, 500);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		pack();
	}

	/**
	 * Gets image of the Connected Four to be displayed on welcome page
	 * 
	 * @param url
	 *            :location of the image to be used.
	 * @return image (to be displayed as the game welcome page.)
	 */
	private Image getCoinImg(String url) {
		URL iconURL = C4GuiApp.class.getResource(url);
		if (iconURL != null)
			return new ImageIcon(iconURL).getImage();
		else {
			System.out.println("URL is null");
			return null;
		}
	}
}
