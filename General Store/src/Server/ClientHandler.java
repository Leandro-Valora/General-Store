//codice nuova gen.
/**
 * The ClientHandler class is responsible for handling communication between the server
 * and individual clients. It manages client authentication, user registration, and
 * product retrieval.
 * <p>
 * The ClientHandler class implements the {@link Runnable} interface, allowing it to be 
 * executed in its own thread to handle client requests concurrently.
 * </p>
 * 
 * <p>Author: A. F. Valora</p>
 * <p>Author: D. Carattin</p>
 * <p>Version: 1.0</p>
 * 
 * <p>Usage:
 * - Login: Allows a user to authenticate by providing a username and password.
 * - SignUp: Allows a new user to create an account.
 * - ViewProducts: Retrieves and displays available products from the database.
 * </p>
 */
package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Client.Client;

import Server.Server.globalConn;

public class ClientHandler implements Runnable {

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private Client client = new Client();

    /**
     * Constructor to initialize the client handler with the provided socket.
     * 
     * @param sock The socket representing the connection to the client.
     * @throws IOException If there is an error while obtaining input and output streams.
     */
    public ClientHandler(Socket sock) {
        try {
            socket = sock;
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the login process. It reads the username and password sent by the client,
     * verifies the credentials in the database, and sends the appropriate response to the client.
     * 
     * @throws IOException If there is an error during communication with the client or database.
     */
    public void Login() throws IOException {
        String username = input.readUTF();
        String password = input.readUTF();

        System.out.println("ClientHandler -> " + username + " " + password);

        try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
            String selectQuery = "SELECT Name FROM Customers WHERE Email = ? OR Psw = ?";

            try (PreparedStatement preparedStatement = con.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String validName = resultSet.getString("Name");
                    output.writeUTF("LoginSuccessful");
                    output.writeUTF(" WELCOME " + validName.toUpperCase());
                } else {
                    output.writeUTF("LoginFailed");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            output.writeUTF("ServerError");
        }
    }

    /**
     * Handles the user signup process. It reads the required user details from the client, checks
     * if the email already exists in the database, and either creates a new account or informs the
     * client that the account already exists.
     * 
     * @throws IOException If there is an error during communication with the client or database.
     */
    public void SignUp() throws IOException {
        boolean exist = false;  
        String username = input.readUTF();
        String usersurname = input.readUTF();
        String email = input.readUTF();
        String password = input.readUTF();
        String address = input.readUTF();
        String city = input.readUTF();

        try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
            String queryCheck = "SELECT * FROM Customers WHERE Email = ? LIMIT 1";

            try (PreparedStatement preparedStatement = con.prepareStatement(queryCheck)) {
                preparedStatement.setString(1, email);
                ResultSet resultSetCheck = preparedStatement.executeQuery();

                if(resultSetCheck.next()) {
                    exist = true;
                    System.out.print("Already existent!\n");
                    output.writeUTF("Already existent!");
                } else {
                    exist = false;
                    System.out.print("Account doesn't exist, it will be created !\n");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if(!exist) {
                String insertQuery = "INSERT INTO Customers (Name, Surname, Email, Psw, Drop_address, City) VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = con.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, usersurname);
                    preparedStatement.setString(3, email);
                    preparedStatement.setString(4, password);
                    preparedStatement.setString(5, address);
                    preparedStatement.setString(6, city);

                    preparedStatement.executeUpdate();
                    System.out.print("Account created!\n");
                    output.writeUTF("Your account has been created!");
                }
            }

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        output.flush();
    }

    /**
     * Retrieves the available products from the database and sends the product details to the client.
     * The method sends product information in a loop until all products have been sent, followed by a "Stop" message.
     * 
     * @throws IOException If there is an error during communication with the client or database.
     */
    public void ViewProducts() throws IOException {
        try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
            String selectQuery = "SELECT * FROM Products WHERE Quantity > 0;";

            try (PreparedStatement preparedStatement = con.prepareStatement(selectQuery)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                output.writeUTF("correct");

                while (resultSet.next()) {
                    output.writeUTF("Product");
                    output.writeInt(resultSet.getInt("Id_Product"));
                    output.writeUTF(resultSet.getString("Name"));
                    output.writeFloat(resultSet.getFloat("Price"));
                    output.writeInt(resultSet.getInt("Quantity"));
                    output.flush();
                }

                output.writeUTF("Stop");
                output.flush();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The run method continuously listens for client requests and invokes the corresponding method
     * to handle the request (e.g., Login, SignUp, ViewProducts). The server will keep listening
     * for commands until the client closes the connection.
     */
    @Override
    public void run() {
        try {
            while (true) {
                String commandToExecute;
                try {
                    commandToExecute = input.readUTF();
                } catch (EOFException e) {
                    System.out.println("Connessione terminata dal client.");
                    break;
                }

                System.out.println("Command received: " + commandToExecute);

                switch (commandToExecute) {
                    case "Login":
                        Login();
                        break;
                    case "SignUp":
                        SignUp();
                        break;
                    case "ViewProducts":
                        ViewProducts();
                        break;
                    case "CloseConnection":
                        System.out.println("Client connection closed.");
                        socket.close();
                        return; // close thread
                    default:
                        System.out.println("Command not recognized: " + commandToExecute);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


//nostro codice
///**
// * @author A. F. Valora
// * @author D. Carattin
// * @version 1.0
// * This Class is used to handle the client and server connection. 
// */
//
//package Server;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.EOFException;
//import java.io.IOException;
//import java.net.Socket;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//import Client.Client;
//import Models.Customer;
//import Models.Cart;
//import Models.Product;
//
//import Server.Server.globalConn;
//
//
//public class ClientHandler implements Runnable {
//
//	private Socket socket;
//
//	private DataInputStream input;
//	
//	private DataOutputStream output;
//	
//	
//	private Client client = new Client();
//
//	public ClientHandler(Socket sock) {
//		try {
//			socket = sock;
//			input = new DataInputStream(socket.getInputStream());
//			output = new DataOutputStream(socket.getOutputStream());
//        } catch(IOException e) {
//				e.printStackTrace();
//		}
//	}
//	
//	public void Login() throws IOException {
//	    // Riceve email e password dal client
//	    String username = input.readUTF();
//	    String password = input.readUTF();
//
//	    System.out.println("ClientHadler -> " + username + " " + password);
//	    
//	    try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
//	        String selectQuery = "SELECT Name FROM Customers WHERE Email = ? OR Psw = ?";
//
//	        try (PreparedStatement preparedStatement = con.prepareStatement(selectQuery)) {
//	            preparedStatement.setString(1, username);
//	            preparedStatement.setString(2, password);
//	            ResultSet resultSet = preparedStatement.executeQuery();
//
//	            if (resultSet.next()) {
//	                String validName = resultSet.getString("Name");
//	                output.writeUTF("LoginSuccessful");
//	                output.writeUTF(" WELCOME " + validName.toUpperCase());
//	            } else {
//	                output.writeUTF("LoginFailed");
//	            }
//	        }
//	    } catch (SQLException e) {
//	        e.printStackTrace();
//	        output.writeUTF("ServerError");
//	    }
//	}
//
//    
//    //funzione per creare un nuovo utente 
//    public void SignUp() throws IOException{
//    	boolean exist = false;  
//        String username = input.readUTF();
//        String usersurname = input.readUTF();
//        String email = input.readUTF();
//        String password = input.readUTF();
//        String address = input.readUTF();
//        String city = input.readUTF();
//        
//
//        try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb);) {
//
//            // check if values are present on the db
//            String queryCheck = "SELECT * FROM Customers WHERE Email = ? LIMIT 1";
//
//            try (PreparedStatement preparedStatement = con.prepareStatement(queryCheck)) {
//                preparedStatement.setString(1, email);
//                ResultSet resultSetCheck = preparedStatement.executeQuery();
//
//                if(resultSetCheck.next()) {
//                    exist = true;
//                    System.out.print("Already existent!\n");
//                    
//                    output.writeUTF("Already existent!");
//                }
//                else {
//                    exist = false;
//                    System.out.print("Account doesn't exist, it will be created !\n");
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//            if(exist == false) {
//                String insertQuery = "INSERT INTO Customers (Name, Surname, Email, Psw, Drop_address, City) VALUE (" + "'" + username +  "'" + ", " + "'" + usersurname + "'" + ", " + "'" + email + "'" + ", " + "'" + password + "'" + ", " + "'" + address + "'" + ", " + "'" + city + "');" ;
//
//                try (Statement st = con.createStatement()) {
//                    st.executeUpdate(insertQuery);
//                    System.out.print("Account created!\n");
//                    
//                    output.writeUTF("Your account has been created!");
//                }
//            }
//
//            con.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        output.flush();
//	}
//    
//    
//    public void ViewProducts() throws IOException {
//    	
//    	try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
//            // Query per recuperare i dati dell'utente dal database
//            String selectQuery = "SELECT * FROM Products WHERE Quantity > 0 ;";
//
//            try (PreparedStatement preparedStatement = con.prepareStatement(selectQuery)) {
//                ResultSet resultSet = preparedStatement.executeQuery();
//                output.writeUTF("correct");
//                
//                while (resultSet.next()) {
//                	//signal for any one product
//					output.writeUTF("Product");
//					output.writeInt(resultSet.getInt("Id_Product"));
//					output.writeUTF(resultSet.getString("Name"));
//					output.writeFloat(resultSet.getFloat("Price"));
//					output.writeInt(resultSet.getInt("Quantity"));
//					output.flush();                 
//                }
//                
//                output.writeUTF("Stop");
//                output.flush();
//                
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//	}
//	
//  
//    @Override
//    public void run() {
//        try {
//            while (true) {
//                String commandToExecute;
//                try {
//                    commandToExecute = input.readUTF();
//                } catch (EOFException e) {
//                    System.out.println("Connessione terminata dal client.");
//                    break;
//                }
//
//                System.out.println("Variabile switch -> " + commandToExecute);
//
//                switch (commandToExecute) {
//                    case "Login":
//                        Login();
//                        break;
//                        
//                    case "SignUp":
//                        SignUp();
//                        break;
//                        
//                    case "ViewProducts": // Nuovo comando
//                        ViewProducts();
//                        break;
//                        
//                    case "CloseConnection":
//                        System.out.println("Client connection closed.");
//                        socket.close();
//                        return; // close thread
//                    default:
//                        System.out.println("Command not recognized: " + commandToExecute);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
