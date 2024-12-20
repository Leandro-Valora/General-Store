//codice new gen.
/**
 * The Server class is responsible for initializing the server socket and accepting client connections.
 * It continuously listens for incoming client connections and creates a new {@link ClientHandler}
 * to handle each client in a separate thread.
 * 
 * <p>Author: A. F. Valora</p>
 * <p>Author: D. Carattin</p>
 * <p>Version: 1.0</p>
 * 
 * <p>Usage: 
 * - The Server class initializes a server socket on a predefined port (6789).
 * - When a client connects, the server spawns a new thread to handle that client's requests
 *   using the {@link ClientHandler} class.
 * </p>
 */
package Server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket server = null;
    
    /**
     * The port number the server will listen on.
     */
    public static final int PORT = 6789;

    /**
     * Constructor for the Server class. It initializes the server socket on the specified port
     * and continuously accepts incoming client connections, passing each connection to a new
     * {@link ClientHandler} to handle communication.
     * 
     * @throws Exception If an error occurs while initializing the server socket or handling connections.
     */
    public Server() {
        try {
            server = new ServerSocket(PORT); // Initialize server socket to listen on the given port
            while (true) {
                iniConnections(); // Wait for and initialize new connections
            }
        } catch (Exception io) {
            System.out.println("Error initializing server.");
            io.printStackTrace();
        }
    }

    /**
     * The globalConn class contains the database connection parameters required to connect to the database.
     * These include the JDBC URL, user name, and password. This class is static and is accessible globally
     * within the Server class.
     */
    public static class globalConn {
        public static String jdbcURL = "jdbc:mysql://mysql-svalora.alwaysdata.net:3306/svalora_database_general_store";
        public static String usernameDb = "svalora_ge_store";
        public static String passwordDb = "ge_store";
    }

    /**
     * Initializes a new connection with a client. The server accepts a client connection
     * and creates a new {@link ClientHandler} to handle the communication in a separate thread.
     * 
     * @throws Exception If an error occurs while accepting a client connection or starting the client handler thread.
     */
    public void iniConnections() throws Exception {
        Socket socket = server.accept(); // Accept an incoming client connection
        System.out.println("Initializing connection...");
        
        // Create a new ClientHandler instance to handle the client connection
        final ClientHandler client = new ClientHandler(socket);

        // Create a new thread to handle the client
        final Thread thread = new Thread(client);
        thread.start(); // Start the client handler thread
    }

    /**
     * The main method is the entry point of the server application.
     * It creates a new instance of the Server class, which starts the server
     * and begins accepting client connections.
     * 
     * @param arg Command-line arguments (not used in this implementation).
     * @throws Exception If an error occurs while starting the server.
     */
    public static void main(String[] arg) throws Exception {
        new Server(); // Start the server
    }
}



//nostro codice
//package Server;
//
///**
// * @author A. F. Valora
// * @author D. Carattin
// * @version 1.0
// * This Class is used to server
// */
//
//import java.net.ServerSocket;
//import java.net.Socket;
//
//
//public class Server {
//	private ServerSocket server = null;
//	public static final int PORT = 6789;
//
//	public Server() {
//		try {
//			server = new ServerSocket(PORT);
//			while (true)
//				iniConnections();
//		} catch (Exception io) {
//			System.out.println("public server");
//			io.printStackTrace();
//		}
//	}
//
//    public static class globalConn {
//        public static String jdbcURL = "jdbc:mysql://mysql-svalora.alwaysdata.net:3306/svalora_database_general_store";
//        public static String usernameDb = "svalora_ge_store";
//        public static String passwordDb = "ge_store";
//
//    }
//
//
//	public void iniConnections() throws Exception {
//		Socket socket = server.accept();
//		System.out.println("Ini. connection...");
//        final ClientHandler client = new ClientHandler(socket);
//
//        // Give the handler its own thread
//        final Thread thread = new Thread(client);
//        thread.start();
//	}
//	
//
//	public static void main(String[] arg) throws Exception {
//		new Server();
//
//	}
//}