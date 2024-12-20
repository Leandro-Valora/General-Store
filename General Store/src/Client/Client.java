package Client;

/**
 * @author A. F. Valora
 * @author D. Carattin
 * @version 1.0
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import Models.Product;

import java.util.ArrayList;
import java.util.List;

//codice nuova gen

/**
 * Client class that provides the client-side functionality for a general store application.
 * This application allows users to connect to a server, login, sign up, and interact with products.
 */
public class Client {

    /**
     * List of products available on the server.
     */
    private static List<Product> productList = new ArrayList<>();

    /**
     * The main method acts as the entry point for the client application.
     * 
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String args[]) {

        // Server IP and port configuration
        final String SERVER_IP = "127.0.0.1";
        final int SERVER_PORT = 6789;

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             Scanner scanner = new Scanner(System.in)) {

            // Console colors for better user interface
            final String ANSI_RESET = "\u001B[0m";
            final String ANSI_BLUE = "\u001B[34m";
            final String ANSI_GREEN = "\u001B[32m";
            final String ANSI_YELLOW = "\u001B[33m";
            final String ANSI_RED =  "\u001B[31m";

            System.out.println(" |--- Connected to the server! ---|\n");
            System.out.println(ANSI_BLUE + "=====================================" + ANSI_RESET);
            System.out.println(ANSI_GREEN + "         GENERAL STORE              " + ANSI_RESET);
            System.out.println(ANSI_BLUE + "=====================================" + ANSI_RESET);

            while (true) {

                System.out.println(ANSI_YELLOW + "Choose an option:" + ANSI_RESET);
                System.out.println("  " + ANSI_GREEN + "1. Login" + ANSI_RESET);
                System.out.println("  " + ANSI_GREEN + "2. SignUp" + ANSI_RESET);
                System.out.println("  " + ANSI_GREEN + "3. Exit" + ANSI_RESET);
                System.out.println(ANSI_BLUE + "=====================================" + ANSI_RESET);
                System.out.print(ANSI_YELLOW + "Your choice: " + ANSI_RESET);
                String choice = scanner.nextLine();
                System.out.println("\n");

                switch (choice) {
                    case "1":
                        output.writeUTF("Login");
                        output.flush();

                        System.out.println("  __           __");
                        System.out.println(" |__   " + ANSI_GREEN + "Login" + ANSI_RESET + "   __|");

                        System.out.print("\n Insert email: ");
                        String email = scanner.nextLine();
                        System.out.print(" Insert password: ");
                        String password = scanner.nextLine();

                        output.writeUTF(email);
                        output.writeUTF(password);
                        output.flush();

                        String serverResponse = input.readUTF();
                        if (serverResponse.equals("LoginSuccessful")) {
                            String welcomeMessage = input.readUTF();

                            System.out.println(ANSI_BLUE + "\n╔════════════════════════════╗" + ANSI_RESET);
                            System.out.println(ANSI_GREEN + "    " + welcomeMessage + ANSI_RESET);
                            System.out.println(ANSI_BLUE + "╚════════════════════════════╝ \n" + ANSI_RESET);

                            // Second menu for logged-in users
                            while (true) {
                                System.out.println(ANSI_YELLOW + "Choose an option:" + ANSI_RESET);
                                System.out.println("  " + ANSI_GREEN + "1. View Products" + ANSI_RESET);
                                System.out.println("  " + ANSI_GREEN + "2. Buy Products" + ANSI_RESET);
                                System.out.println("  " + ANSI_GREEN + "3. Suggest New Product" + ANSI_RESET);
                                System.out.println("  " + ANSI_GREEN + "4. Exit" + ANSI_RESET);
                                System.out.println(ANSI_BLUE + "=====================================" + ANSI_RESET);
                                System.out.print(ANSI_YELLOW + "Your choice: " + ANSI_RESET);
                                String choiceSecondMenu = scanner.nextLine();
                                System.out.println("\n");

                                switch (choiceSecondMenu) {
                                    case "1":
                                        output.writeUTF("ViewProducts");
                                        output.flush();
                                        
                                        System.out.println("  __              __");
                                        System.out.println(" |__   " + ANSI_BLUE + "Products" + ANSI_RESET + "   __|\n");

                                        String response = input.readUTF();
                                        if (response.equals("correct")) {

                                            int maxLengthName = "Name".length();

                                            while (true) {
                                                String productIndicator = input.readUTF();
                                                if (productIndicator.equals("Stop")) {
                                                    break;
                                                }

                                                if (productIndicator.equals("Product")) {
                                                    int id = input.readInt();
                                                    String name = input.readUTF();
                                                    float price = input.readFloat();
                                                    int quantity = input.readInt();

                                                    // Local list of products
                                                    Product product = new Product(id, name, price, quantity);
                                                    productList.add(product);

                                                    // Update max length of the name of products
                                                    if(name.length() > maxLengthName) {
                                                        maxLengthName = name.length();
                                                    }
                                                } else {
                                                    System.out.println("Unexpected data received: " + productIndicator);
                                                }
                                            }

                                            // Print table with dynamic formatting
                                            String nameColumnFormat = "%-" + (maxLengthName + 2) + "s"; // Add 2 spaces for padding
                                            String headerFormat = " " + nameColumnFormat + "  " + " | %8s | %8s\n";
                                            String rowFormat =" \u2022 " + nameColumnFormat + " | %8.2f | %8d\n";

                                            System.out.printf(headerFormat, "NAME", "PRICE", "  QUANTITY");
                                            System.out.println(" "+ "-".repeat(maxLengthName + 28)); // Dynamic divider

                                            for (Product product : productList) {
                                                System.out.printf(rowFormat, product.getName(), product.getPrice(), product.getQuantity());
                                            }
                                            System.out.println("  __                       __");
                                            System.out.println(" |__  End of product list  __|\n");

                                        } else {
                                            System.out.println("Error retrieving product list!\n");
                                        }
                                        break;

                                    case "2":
                                    	System.out.println("  __                  __");
                                        System.out.println(" |__   " + ANSI_BLUE + "Buy Products" + ANSI_RESET + "   __|\n");
                                        // Implement purchase logic here
                                        break;

                                    case "3":
                                    	System.out.println("  __                         __");
                                        System.out.println(" |__   " + ANSI_BLUE + "Suggest New Product" + ANSI_RESET + "   __|\n");
                                        
                                        // Implement product suggestion logic here
                                        break;

                                    case "4":
                                        System.out.println("  -----------------------------");
                                        System.out.println("  |   Exit to General Store   |");
                                        System.out.println("  -----------------------------");
                                        output.writeUTF("CloseConnection");
                                        output.flush();
                                        System.out.println("\n\n  |--- Connection closed ---|");
                                        return;

                                    default:
                                        System.out.println("Invalid option! Please try again.\n");
                                }
                            }

                        } else if (serverResponse.equals("LoginFailed")) {
                            System.out.println(ANSI_BLUE + "----------------------------------------------" + ANSI_RESET);
                            System.out.println(ANSI_RED + "  Error wrong email or password! Try again." + ANSI_RESET);
                            System.out.println(ANSI_BLUE + "----------------------------------------------" + ANSI_RESET);

                        } else if (serverResponse.equals("ServerError")) {
                            System.out.println(ANSI_BLUE + "---------------------------------------------" + ANSI_RESET);
                            System.out.println(ANSI_RED + "        Server error! Try again later.      " + ANSI_RESET);
                            System.out.println(ANSI_BLUE + "---------------------------------------------" + ANSI_RESET);
                        }

                        break;

                    case "2":
                        output.writeUTF("SignUp");
                        output.flush();

                        System.out.println("  __             __");
                        System.out.println(" |__   " + ANSI_GREEN + "Sign up" + ANSI_RESET + "   __|");

                        // Handle user sign-up process
                        do {
                            System.out.print("\n Name: ");
                            String nameSignup = scanner.nextLine();
                            while (nameSignup.trim().isEmpty() || !nameSignup.matches("[a-zA-Z]+")) {
                                System.out.println(ANSI_BLUE+"------------------------------------------------------------------" + ANSI_RESET);
                                System.out.println(ANSI_RED+ "  Invalid input! Name must be non-empty and only contain letters. "+ANSI_RESET);
                                System.out.println(ANSI_BLUE+"------------------------------------------------------------------ \n" + ANSI_RESET);
                                System.out.print(" Name: ");
                                nameSignup = scanner.nextLine();
                            }
                            output.writeUTF(nameSignup.substring(0,1).toUpperCase() + nameSignup.substring(1).toLowerCase());

                            // Repeat similar input validation for other fields
                            // Implement field validation and sending logic

                            output.flush();
                        } while (false);

                        serverResponse = input.readUTF();
                        System.out.println("\n |--- " + serverResponse + " ---|\n");
                        break;

                    case "3":
                        System.out.println("  ---------------");
                        System.out.println("  |   Goodbye!  |");
                        System.out.println("  ---------------");
                        output.writeUTF("CloseConnection");
                        output.flush();
                        System.out.println("\n\n |--- Connection closed ---|");
                        return;

                    default:
                        System.out.println(ANSI_RED + "Option not valid!" + ANSI_RESET + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



//nostro codice
//public class Client {
//
//	private static List<Product> productList = new ArrayList<>();
//	
//    public static void main(String args[]) {
//        
//        final String SERVER_IP = "127.0.0.1";
//        final int SERVER_PORT = 6789;
//
//        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
//             DataInputStream input = new DataInputStream(socket.getInputStream());
//             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
//             Scanner scanner = new Scanner(System.in)) {
//
//        	//code for color menu
//        	final String ANSI_RESET = "\u001B[0m";
//        	final String ANSI_BLUE = "\u001B[34m";
//        	final String ANSI_GREEN = "\u001B[32m";
//        	final String ANSI_YELLOW = "\u001B[33m";
//        	final String ANSI_RED =  "\u001B[31m";
//
//        	System.out.println(" |--- Connected to the server! ---|\n");
//        	System.out.println(ANSI_BLUE + "=====================================" + ANSI_RESET);
//        	System.out.println(ANSI_GREEN + "         GENERAL STORE              " + ANSI_RESET);
//        	System.out.println(ANSI_BLUE + "=====================================" + ANSI_RESET);
//        	
//            while (true) {
//            	
//            	System.out.println(ANSI_YELLOW + "Choose an option:" + ANSI_RESET);
//            	System.out.println("  " + ANSI_GREEN + "1. Login" + ANSI_RESET);
//            	System.out.println("  " + ANSI_GREEN + "2. SignUp" + ANSI_RESET);
//            	System.out.println("  " + ANSI_GREEN + "3. Exit" + ANSI_RESET);
//            	System.out.println(ANSI_BLUE + "=====================================" + ANSI_RESET);
//            	System.out.print(ANSI_YELLOW + "Your choice: " + ANSI_RESET);
//                String choice = scanner.nextLine();
//                System.out.println("\n");
//                
//                switch (choice) {
//                    case "1":
//                        output.writeUTF("Login");
//                        output.flush();
//                        
//                        System.out.println("  __           __");
//                    	System.out.println(" |__   " + ANSI_GREEN + "Login" + ANSI_RESET + "   __|");
//
//                        System.out.print("\n Insert email: ");
//                        String email = scanner.nextLine();
//                        System.out.print(" Insert password: ");
//                        String password = scanner.nextLine();
//
//                        output.writeUTF(email);
//                        output.writeUTF(password);
//                        output.flush();
//
//                        String serverResponse = input.readUTF();
//                        if (serverResponse.equals("LoginSuccessful")) {
//                            String welcomeMessage = input.readUTF();
//                            
//                            System.out.println(ANSI_BLUE + "\n╔════════════════════════════╗" + ANSI_RESET);
//                            System.out.println(ANSI_GREEN + "    " + welcomeMessage + ANSI_RESET);
//                            System.out.println(ANSI_BLUE + "╚════════════════════════════╝ \n" + ANSI_RESET);
//
//                            // Second menu
//                            while (true) {
//                            	System.out.println(ANSI_YELLOW + "Choose an option:" + ANSI_RESET);
//                            	System.out.println("  " + ANSI_GREEN + "1. View Products" + ANSI_RESET);
//                            	System.out.println("  " + ANSI_GREEN + "2. Buy Products" + ANSI_RESET);
//                            	System.out.println("  " + ANSI_GREEN + "3. Suggest New Product" + ANSI_RESET);
//                            	System.out.println("  " + ANSI_GREEN + "4. Exit" + ANSI_RESET);
//                            	System.out.println(ANSI_BLUE + "=====================================" + ANSI_RESET);
//                            	System.out.print(ANSI_YELLOW + "Your choice: " + ANSI_RESET);
//                                String choiceSecondMenu = scanner.nextLine();
//                                System.out.println("\n");
//                                
//                                switch (choiceSecondMenu) {
//                                
//                                	case "1":
//                                    output.writeUTF("ViewProducts");
//                                    output.flush();
//
//                                    String response = input.readUTF();
//                                    if (response.equals("correct")) {
//                                    	
//                                        int maxLengthName = "Name".length();
//
//                                        while (true) {
//                                            String productIndicator = input.readUTF();
//                                            if (productIndicator.equals("Stop")) {
//                                                break;
//                                            }
//
//                                            if (productIndicator.equals("Product")) {
//                                                int id = input.readInt();
//                                                String name = input.readUTF();
//                                                float price = input.readFloat();
//                                                int quantity = input.readInt();
//                                                
//                                                //Local list of products
//                                                Product product = new Product(id, name, price, quantity);
//                                                productList.add(product);
//                                                
//                                                //Updated max length of the name of products
//                                                if(name.length() > maxLengthName) {
//                                                	maxLengthName = name.length();
//                                                }
//                                            } else {
//                                                System.out.println("Unexpected data received: " + productIndicator);
//                                            }
//                                        }
//                                        
//                                        // Print table with dynamic form
//                                        String nameColumnFormat = "%-" + (maxLengthName + 2) + "s"; // Add 2 space for padding
//                                        String headerFormat = " " + nameColumnFormat + "  " + " | %8s | %8s\n";
//                                        String rowFormat =" \u2022 " + nameColumnFormat + " | %8.2f | %8d\n";
//
//                                        System.out.printf(headerFormat, "NAME", "PRICE", "  QUANTITY");
//                                        System.out.println(" "+ "-".repeat(maxLengthName + 28)); // Dynamic divider
//
//                                        for (Product product : productList) {
//                                            System.out.printf(rowFormat, product.getName(), product.getPrice(), product.getQuantity());
//                                        }
//                                        System.out.println("  __                       __");
//                                        System.out.println(" |__  End of product list  __|\n");
//                                     
//                                    } else {
//                                        System.out.println("Error retrieving product list!\n");
//                                    }
//                                    break;
//                                    
//                                    case "2":
//                                        System.out.println("--- Buy Products ---");
//                                        // Implement purchase logic here
//                                        break;
//
//                                    case "3":
//                                        System.out.println("--- Suggest New Product ---");
//                                        // Implement product suggestion logic here
//                                        break;
//
//                                    case "4":
//                                    	System.out.println("  -----------------------------");
//                                    	System.out.println("  |   Exit to General Store   |");
//                                    	System.out.println("  -----------------------------");
//                                        output.writeUTF("CloseConnection");
//                                        output.flush();
//                                        System.out.println("\n\n  |--- Connection closed ---|");
//                                        return;
//                                        
//                                    default:
//                                        System.out.println("Invalid option! Please try again.\n");
//                                }
//                            }
//
//                        } else if (serverResponse.equals("LoginFailed")) {
//                            System.out.println(ANSI_BLUE + "----------------------------------------------" + ANSI_RESET);
//                        	System.out.println(ANSI_RED + "  Error wrong email or password! Try again." + ANSI_RESET);
//                        	System.out.println(ANSI_BLUE + "----------------------------------------------" + ANSI_RESET);
//                        	                        	
//                        } else if (serverResponse.equals("ServerError")) {
//                            System.out.println(ANSI_BLUE + "---------------------------------------------" + ANSI_RESET);
//                        	System.out.println(ANSI_RED + "        Server error! Try again later.      " + ANSI_RESET);
//                        	System.out.println(ANSI_BLUE + "---------------------------------------------" + ANSI_RESET);
//                        }
//
//                        break;
//
//                    case "2":
//                    	output.writeUTF("SignUp");
//                    	output.flush();
//
//                    	System.out.println("  __             __");
//                    	System.out.println(" |__   " + ANSI_GREEN + "Sign up" + ANSI_RESET + "   __|");
//
//                    	do {
//                    	    System.out.print("\n Name: ");
//                    	    String nameSignup = scanner.nextLine();
//                    	    while (nameSignup.trim().isEmpty() || !nameSignup.matches("[a-zA-Z]+")) {
//                    	    	//error message
//                    	    	System.out.println(ANSI_BLUE+"------------------------------------------------------------------" + ANSI_RESET);
//                    	        System.out.println(ANSI_RED+ "  Invalid input! Name must be non-empty and only contain letters. "+ANSI_RESET);
//                    	        System.out.println(ANSI_BLUE+"------------------------------------------------------------------ \n" + ANSI_RESET);
//                    	        System.out.print(" Name: ");
//                    	        nameSignup = scanner.nextLine();
//                    	    }
//                    	    output.writeUTF(nameSignup.substring(0,1).toUpperCase() + nameSignup.substring(1).toLowerCase());
//                    	    
//                    	    System.out.print(" Surname: ");
//                    	    String surnameSignup = scanner.nextLine();
//                    	    while (surnameSignup.trim().isEmpty() || !surnameSignup.matches("[a-zA-Z]+")) {
//                    	    	System.out.println(ANSI_BLUE+"---------------------------------------------------------------------" + ANSI_RESET);
//                    	        System.out.println(ANSI_RED+"  Invalid input! Surname must be non-empty and only contain letters." + ANSI_RESET);
//                    	        System.out.println(ANSI_BLUE+"--------------------------------------------------------------------- \n" + ANSI_RESET);
//                    	        System.out.print(" Surname: ");
//                    	        surnameSignup = scanner.nextLine();
//                    	    }
//                    	    output.writeUTF(surnameSignup.substring(0,1).toUpperCase() + surnameSignup.substring(1).toLowerCase());
//
//                    	    System.out.print(" Email: ");
//                    	    String emailSignup = scanner.nextLine();
//                    	    while (emailSignup.trim().isEmpty() || !emailSignup.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
//                    	    	System.out.println(ANSI_BLUE+"----------------------------------------------" + ANSI_RESET);
//                    	        System.out.println(ANSI_RED+"  Invalid input! Enter a valid email address. "+ANSI_RESET);
//                    	        System.out.println(ANSI_BLUE+"---------------------------------------------- \n" + ANSI_RESET);
//                    	        System.out.print(" Email: ");
//                    	        emailSignup = scanner.nextLine();
//                    	    }
//                    	    output.writeUTF(emailSignup);
//
//                    	    System.out.print(" Password: ");
//                    	    String passwordSignup = scanner.nextLine();
//                    	    while (passwordSignup.trim().isEmpty()) {
//                    	    	System.out.println(ANSI_BLUE+"---------------------------------------------" + ANSI_RESET);
//                    	        System.out.println(ANSI_RED+"  Invalid input! Password must be non-empty. "+ANSI_RESET);
//                    	        System.out.println(ANSI_BLUE+"--------------------------------------------- \n" + ANSI_RESET);
//                    	        System.out.print(" Password: ");
//                    	        passwordSignup = scanner.nextLine();
//                    	    }
//                    	    output.writeUTF(passwordSignup);
//
//                    	    System.out.print(" Address: ");
//                    	    String addressSignup = scanner.nextLine();
//                    	    while (addressSignup.trim().isEmpty()) {
//                    	    	System.out.println(ANSI_BLUE+"--------------------------------------------" + ANSI_RESET);
//                    	        System.out.println(ANSI_RED+"  Invalid input! Address must be non-empty. "+ANSI_RESET);
//                    	        System.out.println(ANSI_BLUE+"-------------------------------------------- \n" + ANSI_RESET);
//                    	        System.out.print(" Address: ");
//                    	        addressSignup = scanner.nextLine();
//                    	    }
//                    	    output.writeUTF(addressSignup.substring(0,1).toUpperCase() + addressSignup.substring(1).toLowerCase());
//
//                    	    System.out.print(" City: ");
//                    	    String citySignup = scanner.nextLine();
//                    	    while (citySignup.trim().isEmpty() || !citySignup.matches("[a-zA-Z ]+")) {
//                    	    	System.out.println(ANSI_BLUE+"------------------------------------------------------------------" + ANSI_RESET);
//                    	        System.out.println(ANSI_RED+"  Invalid input! City must be non-empty and only contain letters. "+ANSI_RESET);
//                    	        System.out.println(ANSI_BLUE+"------------------------------------------------------------------ \n" + ANSI_RESET);
//                    	        System.out.print(" City: ");
//                    	        citySignup = scanner.nextLine();
//                    	    }
//                    	    output.writeUTF(citySignup.substring(0,1).toUpperCase() + citySignup.substring(1).toLowerCase());
//
//                    	    output.flush();
//                    	    // You can define "condition" based on server validation
//                    	} while (false);
//
//                    	serverResponse = input.readUTF();
//                    	System.out.println("\n |--- " + serverResponse + " ---|\n");
//                    	break;
//
//
//                    case "3":
//                    	System.out.println("  ---------------");
//                    	System.out.println("  |   Goodbye!  |");
//                    	System.out.println("  ---------------");
//                        output.writeUTF("CloseConnection");
//                        output.flush();
//                        System.out.println("\n\n |--- Connection closed ---|");
//                        return;
//
//                    default:
//                        System.out.println(ANSI_RED + "Option not valid!" + ANSI_RESET + "\n");
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
