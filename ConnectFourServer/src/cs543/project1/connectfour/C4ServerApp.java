package cs543.project1.connectfour;

/**
 * An app that will start the ConnectFour server-side.
 * 
 * @author Natacha Gabbamonte
 * @author Gabriel Gheorghian
 * @author Mark Scerbo
 * 
 */
public class C4ServerApp {

	/**
	 * Starts the server for the ConnectFour game(s).
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		C4Server server = new C4Server();
		System.out.println("Starting server...");
		server.start();
	}
}
