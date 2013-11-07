package cs543.project1.connectfour;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The server for ConnectFour. It listens for a client, and creates a session
 * when it finds one.
 * 
 * @author Natacha Gabbamonte
 * @author Gabriel Gheorghian
 * @author Mark Scerbo
 * 
 */
public class C4Server {
	private final int PORT = 50000;

	/**
	 * Starts the server, which listens on port 50,000.
	 */
	@SuppressWarnings({ "resource" })
	// Can't close ServerSocket because of infinite loop.
	public void start() {
		try {
			System.out.println("MULTI THREADED SERVER.");
			System.out.println("Creating server socket.");
			ServerSocket servSocket = new ServerSocket(PORT);
			System.out.println("IP address: "
					+ InetAddress.getLocalHost().getHostAddress());
			System.out.println("Listening on port number "
					+ servSocket.getLocalPort());
			while (true) {
				System.out.println("Waiting for client...");
				Socket clientSocket = servSocket.accept();
				System.out
						.println("Client socket accepted, creating session...");
				new Thread(new SessionThread(clientSocket)).start();
			}
		} catch (BindException be) {
			System.out
					.println("Error occured while creating ServerSocket, there is already something listening at port "
							+ PORT);
		} catch (IOException e) {
			System.out
					.println("Error occured while creating ServerSocket, or accepting client socket.");
			e.printStackTrace();
		}
	}

	/*
	 * Creates a new Server session.
	 */
	private class SessionThread implements Runnable {
		private Socket clientSocket = null;

		public SessionThread(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		public void run() {
			new C4ServerSession(clientSocket);
		}
	}
}
