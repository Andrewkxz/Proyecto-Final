package co.edu.uniquindio.proptech.Queues;


/**
 * Node class applying Generics
 * 
 * 
 * 
 * **/


public class Node<T> {

	private Node<T> next;
	private T data;
	
	
	/**
	 * Constructor of class Node
	 * @param data Element to save in the Node
	 */
	public Node(T data) {
		this.data = data;
	}
	
	
	/**
	 * Constructor of class Node
	 * @param data Element to save in the Node
	 * @param next link to the next Node
	 */
	public Node(T data, Node<T> next) {
		super();
		this.data = data;
		this.next = next;
	}
	

	//Methods get and set of class Node
	
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
