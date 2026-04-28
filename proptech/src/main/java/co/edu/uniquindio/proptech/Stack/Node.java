package co.edu.uniquindio.proptech.Stack;


/**
 * node Class applying Generics
 * 
 * 
 * 
 * **/


public class Node<T> {

	private Node<T> next;
	private T data;
	
	
	/**
	 * Constructor of node class
	 * @param data Element that is stored in the Node
	 */
	public Node(T data) {
		this.data = data;
	}
	
	
	/**
	 * Constructor of node class
	 * @param data Element that is stored in the Node
	 * @param next Link at next node
	 */
	public Node(T data, Node<T> next) {
		super();
		this.data = data;
		this.next = next;
	}
	

	// get and set methods of node class
	
	public Node<T> getNext() {
		return next;
	}


	public void setNext(Node<T> next) {
		this.next = next;
	}


	public T getData() {
		return data;
	}


	public void setData(T data) {
		this.data = data;
	}
}
