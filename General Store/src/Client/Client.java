package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import Models.Product;

import java.util.ArrayList;
import java.util.List;

public class Client {

	private static List<Product> productList = new ArrayList<>();
	
    public static void main(String args[]) {
        
        final String SERVER_IP = "127.0.0.1";
        final int SERVER_PORT = 6789;

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             Scanner scanner = new Scanner(System.in)) {

        	//code for color menu
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

                        System.out.print("Insert email: ");
                        String email = scanner.nextLine();
                        System.out.print("Insert password: ");
                        String password = scanner.nextLine();

                        output.writeUTF(email);
                        output.writeUTF(password);
                        output.flush();

                        String serverResponse = input.readUTF();
                        if (serverResponse.equals("LoginSuccessful")) {
                            String welcomeMessage = input.readUTF();
                            System.out.println(ANSI_BLUE + " -----------------------------------" + ANSI_RESET);
                        	System.out.println(ANSI_GREEN + " |        "+welcomeMessage+"              |" + ANSI_RESET);
                        	System.out.println(ANSI_BLUE + " -----------------------------------" + ANSI_RESET);

                            // Second menu
                            while (true) {
                            	System.out.println(ANSI_YELLOW + "Choose an option:" + ANSI_RESET);
                            	System.out.println("  " + ANSI_GREEN + "1.  View Products" + ANSI_RESET);
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
                                                
                                                //Local list of products
                                                Product product = new Product(id, name, price, quantity);
                                                productList.add(product);
                                                
                                                //Updated max length of the name of products
                                                if(name.length() > maxLengthName) {
                                                	maxLengthName = name.length();
                                                }
                                            } else {
                                                System.out.println("Unexpected data received: " + productIndicator);
                                            }
                                        }
                                        
                                        // Print table with dynamic form
                                        String nameColumnFormat = "%-" + (maxLengthName + 2) + "s"; // Add 2 space for padding
                                        String headerFormat = " " + nameColumnFormat + "  " + " | %8s | %8s\n";
                                        String rowFormat =" \u2022 " + nameColumnFormat + " | %8.2f | %8d\n";

                                        System.out.printf(headerFormat, "NAME", "PRICE", "  QUANTITY");
                                        System.out.println(" "+ "-".repeat(maxLengthName + 28)); // Dynamic divider

                                        for (Product product : productList) {
                                            System.out.printf(rowFormat, product.getName(), product.getPrice(), product.getQuantity());
                                        }
                                        System.out.println("  __                     __");
                                        System.out.println(" |__  End of product list  __|\n");
                                     
                                    } else {
                                        System.out.println("Error retrieving product list!\n");
                                    }
                                    break;
                                    
                                    case "2":
                                        System.out.println("--- Buy Products ---");
                                        // Implement purchase logic here
                                        break;

                                    case "3":
                                        System.out.println("--- Suggest New Product ---");
                                        // Implement product suggestion logic here
                                        break;

                                    case "4":
                                        System.out.println("--- Exit to General Store ---");
                                        output.writeUTF("CloseConnection");
                                        output.flush();
                                        System.out.println("Connection closed");
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

                        System.out.println("Insert user details: ");
                        System.out.print("Name: ");
                        output.writeUTF(scanner.nextLine());
                        System.out.print("Surname: ");
                        output.writeUTF(scanner.nextLine());
                        System.out.print("Email: ");
                        output.writeUTF(scanner.nextLine());
                        System.out.print("Password: ");
                        output.writeUTF(scanner.nextLine());
                        System.out.print("Address: ");
                        output.writeUTF(scanner.nextLine());
                        System.out.print("City: ");
                        output.writeUTF(scanner.nextLine());
                        output.flush();

                        serverResponse = input.readUTF();
                        System.out.println("Server's answer: " + serverResponse);
                        break;

                    case "3":
                        output.writeUTF("CloseConnection");
                        output.flush();
                        System.out.println("Connection closed");
                        return;

                    default:
                        System.out.println("Option not valid!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}