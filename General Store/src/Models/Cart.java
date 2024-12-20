package Models;

/**
 * @author A. F. Valora
 * @author D. Carattin
 * @version 1.0
 * This Class is used to contain information about the products bought about the customer
 */
public class Cart {

	private int idCartCust;
	private int idCartProd;
	private int numProduct;
	
	/**
	 * Get the id number of the cart
	 * @return cart id
	 */
	public int getIdCartCust() {
		return idCartCust;
	}


	/**
	 * Get the id number of the product
	 * @return product id
	 */
	public int getIdCartProd() {
		return idCartProd;
	}

	/**
	 * Get the number of the product sold out
	 * @return quantity of products
	 */
	public int getNumProduct() {
		return numProduct;
	}

	/**
	 * Set the id of the cart containing the product
	 * @param idCartProd
	 */
	public void setIdCartProd(int idCartProd) {
		this.idCartProd = idCartProd;
	}

	/**
	 * Set the number of the products in the cart
	 * @param numProduct
	 */
	public void setNumProduct(int numProduct) {
		this.numProduct = numProduct;
	}

	/**
	 * Constructor class carts
	 * @param idCartCust
	 * @param idCartProd
	 * @param numProduct
	*/
	public Cart(int idCartCust, int idCartProd, int numProduct) {
		super();
		this.idCartCust = idCartCust;
		this.idCartProd = idCartProd;
		this.numProduct = numProduct;
	}
	
}
