/**
 * @author A. F. Valora
 * @author D. Carattin
 * @version 1.0
 * This Class is used to handle the client and server connection. 
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
import java.sql.Statement;

import Client.Client;
import Models.Customer;
import Models.Cart;
import Models.Product;

import Server.Server.globalConn;


public class ClientHandler implements Runnable {

	private Socket socket;

	private DataInputStream input;
	
	private DataOutputStream output;
	
	
	private Client client = new Client();

	public ClientHandler(Socket sock) {
		try {
			socket = sock;
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
        } catch(IOException e) {
				e.printStackTrace();
		}
	}
	
	public void Login() throws IOException {
	    // Riceve email e password dal client
	    String username = input.readUTF();
	    String password = input.readUTF();

	    System.out.println("ClientHadler -> " + username + " " + password);
	    
	    try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
	        String selectQuery = "SELECT Name FROM Customers WHERE Email = ? OR Psw = ?";

	        try (PreparedStatement preparedStatement = con.prepareStatement(selectQuery)) {
	            preparedStatement.setString(1, username);
	            preparedStatement.setString(2, password);
	            ResultSet resultSet = preparedStatement.executeQuery();

	            if (resultSet.next()) {
	                String validName = resultSet.getString("Name");
	                output.writeUTF("LoginSuccessful");
	                output.writeUTF(" Welcome " + validName);
	            } else {
	                output.writeUTF("LoginFailed");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        output.writeUTF("ServerError");
	    }
	}

    
    //funzione per creare un nuovo utente 
    public void SignUp() throws IOException{
    	boolean exist = false;  
        String username = input.readUTF();
        String usersurname = input.readUTF();
        String email = input.readUTF();
        String password = input.readUTF();
        String address = input.readUTF();
        String city = input.readUTF();
        

        try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb);) {

            // check if values are present on the db
            String queryCheck = "SELECT * FROM Customers WHERE Email = ? LIMIT 1";

            try (PreparedStatement preparedStatement = con.prepareStatement(queryCheck)) {
                preparedStatement.setString(1, email);
                ResultSet resultSetCheck = preparedStatement.executeQuery();

                if(resultSetCheck.next()) {
                    exist = true;
                    System.out.print("Already existent!\n");
                    
                    output.writeUTF("Already existent!");
                }
                else {
                    exist = false;
                    System.out.print("Account doesn't exist, it will be created !\n");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(exist == false) {
                String insertQuery = "INSERT INTO Customers (Name, Surname, Email, Psw, Drop_address, City) VALUE (" + "'" + username +  "'" + ", " + "'" + usersurname + "'" + ", " + "'" + email + "'" + ", " + "'" + password + "'" + ", " + "'" + address + "'" + ", " + "'" + city + "');" ;

                try (Statement st = con.createStatement()) {
                    st.executeUpdate(insertQuery);
                    System.out.print("Account created!\n");
                    
                    output.writeUTF("Add account");
                }
            }

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        output.flush();
	}
    
    
    public void ViewProducts() throws IOException {
    	
    	try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
            // Query per recuperare i dati dell'utente dal database
            String selectQuery = "SELECT * FROM Products WHERE Quantity > 0 ;";

            try (PreparedStatement preparedStatement = con.prepareStatement(selectQuery)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                output.writeUTF("correct");
                
                while (resultSet.next()) {
                	//signal for any one product
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

                System.out.println("Variabile switch -> " + commandToExecute);

                switch (commandToExecute) {
                    case "Login":
                        Login();
                        break;
                        
                    case "SignUp":
                        SignUp();
                        break;
                        
                    case "ViewProducts": // Nuovo comando
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