package Server;

/**
 * @author A. F. Valora
 * @author D. Carattin
 * @version 1.0
 * This Class is used to server
 */

import java.net.ServerSocket;
import java.net.Socket;


public class Server {
	private ServerSocket server = null;
	public static final int PORT = 6789;

	public Server() {
		try {
			server = new ServerSocket(PORT);
			while (true)
				iniConnections();
		} catch (Exception io) {
			System.out.println("public server");
			io.printStackTrace();
		}
	}

    public static class globalConn {
        public static String jdbcURL = "jdbc:mysql://mysql-svalora.alwaysdata.net:3306/svalora_database_general_store";
        public static String usernameDb = "svalora_ge_store";
        public static String passwordDb = "ge_store";

    }


	public void iniConnections() throws Exception {
		Socket socket = server.accept();
		System.out.println("Ini. connection...");
        final ClientHandler client = new ClientHandler(socket);

        // Give the handler its own thread
        final Thread thread = new Thread(client);
        thread.start();
	}
	

	public static void main(String[] arg) throws Exception {
		new Server();

	}
}