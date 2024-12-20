package Models;

import java.util.jar.Attributes.Name;

/**
 * @author A. F. Valora
 * @author D. Carattin
 * @version 1.0
 * This Class is used to contain information about the customer
 */
public class Customer {

	private int idCustomer;
	private static String nameCustomer;
	private String email;
	private String password;
	
	/**
	 * Constructor class customer 
	 * @param idCustomer
	 * @param name
	 * @param email
	 * @param password
	 */
	public Customer(int idCustomer, String name, String email, String password) {
		this.idCustomer = idCustomer;
		nameCustomer = name;
		this.email = email;
		this.password = password;
		}
	
	/**
	 * Constructor used for login page 
	 * @param email
	 * @param password
	 */
	public Customer(String email, String password) {
		this.email = email;
		this.password = password;
		}
	
	
	/**
	 * Get Number id of the customer
	 * @return customers' id
	 */
	public int getIdCustomer() {
		return idCustomer;
	}

	/**
	 * Get the customer name
	 * @return customers' name
	 */
	public String getName() {
		return nameCustomer;
	}
	
	/**
	 * Get the email of the customer used for access 
	 * @return customers' email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Get the password of the customer used for access
	 * @return customers' password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set the name of the customer
	 * @param name customer
	 */
	public static void setName(String name) {
		nameCustomer = name;
	}

	/**
	 * Set the email of the customer
	 * @param email customer
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Set the password of the customer
	 * @param password customer
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
