package Models;

/**
 * @author A. F. Valora
 * @author D. Carattin
 * @version 1.0
 * This Class is used to contain information about the products
 */
public class Product {

	private int idProduct;
	private String name;
	private double price;
	private int quantity;
	
	/**
	 * Get id number of the product
	 * @return product id 
	*/
	public int getIdProduct() {
		return idProduct;
	}
	
	/**
	 * Get the product name
	 * @return product name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the product price
	 * @return product price
	 */
	public double getPrice() {
		return price;
	}
	
	/**
	 * Get the product quantity
	 * @return product quantity
	 */
	public int getQuantity() {
		return quantity;
	}
	
	/**
	 * Set the quantity of the product
	 * @param quantity
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * Constructor class products
	 * @param idProduct
	 * @param name
	 * @param price
	 * @param quantity
	 */
	public Product(int idProduct, String name, double price, int quantity) {
		
		this.idProduct = idProduct;
		this.name = name;
		this.price = price;
		this.quantity = quantity;
	}
	
}