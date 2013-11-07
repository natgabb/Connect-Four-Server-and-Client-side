package cs543.project1.connectfour;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This is the Panel that is displayed when client activated program. Just asks
 * for ip / server to connect to.
 * 
 * @author Natacha Gabbamonte
 * @author Gabriel Gheorghian
 * @author Mark Scerbo
 * 
 */
public class C4InputPanel extends JPanel {

	private static final long serialVersionUID = 5324702784391148158L;
	C4ContentCardLayout parentLayout;
	JTextField input;
	JLabel errorLabel;
	String labelMessage = "Enter the IP of the Connect Four server:";
	JButton submitButton;

	/**
	 * Constructor: takes in 1 parameter which is a handle to card layout.
	 * 
	 * @param parent
	 *            the card layout
	 */
	public C4InputPanel(C4ContentCardLayout parent) {
		this.setLayout(new GridBagLayout());
		parentLayout = parent;

		errorLabel = new JLabel(" ", JLabel.CENTER);
		errorLabel.setFont(new Font("Arial", Font.PLAIN, 15));
		errorLabel.setForeground(Color.RED);
		JLabel label = new JLabel(labelMessage);
		input = new JTextField();
		input.setPreferredSize(new Dimension(100, 25));
		input.requestFocus();
		submitButton = new JButton("Enter");
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!input.getText().equals(""))
					try {
						setTheCursor(Cursor
								.getPredefinedCursor(Cursor.WAIT_CURSOR));
						parentLayout.startGame(input.getText());
					} catch (SocketTimeoutException ste) {
						errorLabel
								.setText("The server is busy. Please try again later.");
					} catch (IOException e1) {
						errorLabel.setText("You have entered an invalid IP!");
					} finally {
						setTheCursor(Cursor.getDefaultCursor());
					}
				else
					errorLabel.setText("You did not enter anything!");
			}

		});
		input.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				char key = e.getKeyChar();
				if (key == KeyEvent.VK_ENTER)
					submitButton.doClick();
			}
		});

		ImageIcon titleImg = new ImageIcon(
				C4GuiApp.class.getResource("images/titleImage.png"));
		JLabel title = new JLabel(titleImg);

		// Adding components
		add(title, makeConstraints(0, 0, 3, 1, 0.1, 0.1));
		add(errorLabel, makeConstraints(0, 1, 3, 1, 0.1, 0.1));
		add(label, makeConstraints(0, 2, 2, 1, 0.1, 0.1));
		add(input, makeConstraints(2, 2, 1, 1, 0.1, 0.1));
		add(submitButton, makeConstraints(0, 3, 3, 1, 0.1, 0.1));
	}

	private void setTheCursor(Cursor c) {
		this.setCursor(c);
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
	 *            weigh of the component in the x direction
	 * @param weighty
	 *            weigh of the component in the y direction
	 * @return constraints (the constraints to be added to a component
	 */
	private GridBagConstraints makeConstraints(int gridx, int gridy,
			int gridwidth, int gridheight, double weightx, double weighty) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridheight = gridheight;
		constraints.gridwidth = gridwidth;
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.weightx = weightx;
		constraints.weighty = weighty;

		// Default for all the components.
		constraints.insets = new Insets(0, 0, 0, 0);
		// constraints.fill = GridBagConstraints.BOTH;
		return constraints;
	}
}
