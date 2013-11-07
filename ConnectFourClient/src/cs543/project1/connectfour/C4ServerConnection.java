package cs543.project1.connectfour;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Creates a connection with a server. Receives and sends messages to the
 * server. None of the exceptions are handled at this level.
 * 
 * @author Natacha Gabbamonte
 * @author Gabriel Gheorghian
 * @author Mark Scerbo
 * 
 */
public class C4ServerConnection {

	private static final int PORT = 50000;
	private static final int NUM_BYTES = 2;

	private Socket socket = null;
	private InputStream in = null;
	private OutputStream out = null;

	/**
	 * Constructor for the C4ServerConnection. Creates a new socket and connects
	 * to a server.
	 * 
	 * @throws UnknownHostException
	 *             If the server is not found.
	 * @throws IOException
	 *             If an error occurs while creating a socket or getting the
	 *             input and output stream of the socket.
	 */
	public C4ServerConnection(String server) throws UnknownHostException,
			IOException {
		SocketAddress sockaddr = new InetSocketAddress(server, PORT);
		socket = new Socket();

		socket.setSoTimeout(10000);
		socket.connect(sockaddr);
		in = socket.getInputStream();
		out = socket.getOutputStream();
	}

	/**
	 * Sends a message to the server.
	 * 
	 * @param messages
	 *            The messages to be sent.
	 * @throws IOException
	 *             If an error occurs while writing on the output stream.
	 */
	public void sendPacket(byte[] messages) throws IOException {
		out.write(messages);
	}

	/**
	 * Receives a messages from the server.
	 * 
	 * @return The messages that were received.
	 * @throws IOException
	 *             If an error occurs while reading on the input stream.
	 */
	public byte[] receivePacket() throws IOException {
		byte[] receivedMessages = null;
		receivedMessages = new byte[NUM_BYTES];
		int receivedBytes = 0;
		try {
			while (receivedBytes < NUM_BYTES)
				receivedBytes = in.read(receivedMessages);
		} catch (SocketTimeoutException ste) {
			if (socket != null)
				socket.close();
			throw ste;
		}
		return receivedMessages;
	}

	/**
	 * Closes the socket.
	 * 
	 * @throws IOException
	 *             If an error occurs.
	 */
	public void closeConnection() throws IOException {
		socket.close();
	}
}
