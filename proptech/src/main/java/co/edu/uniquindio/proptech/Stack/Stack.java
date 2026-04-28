package co.edu.uniquindio.proptech.Stack;


/**
 *
 * @param <T>
 */
public class Stack<T> {

	private Node<T> top;
	private int size;
	
	/**
	 * Verifies if the stack is empty
	 * @return true if is empty
	 */
	public boolean isEmpty() {
		return top == null;
	}
	
	/**
	 * Pushes an item onto the top of this stack
	 * @param data element to save onto the stack
	 */
	public void push(T data) {
		
		Node<T> Node = new Node<>(data);
		Node.setNext(top);
		top = Node;		
		
		size++;
	}
	
	/**
	 * Returns and removes the element at the top of this stack
	 * @return Element of the top
	 */
	public T pop() {
		
		if(isEmpty()) {
			throw new RuntimeException("Stack is empty");
		}
		
		T data = top.getData();
		top = top.getNext();
		size--;
		
		return data;		
	}
	
	/**
	 * Removes all of the elements from this stack
	 */
	public void clearStack() {
		top = null;
		size = 0;
	}
	
	/**
	 * Look at the element at the top of this stack without removing it from the stack
	 * @return Element of the top
	 */
	public T peek() {
		return top.getData();
	}
	
	/**
	 * @return peek
	 */
	public Node<T> getPeek() {
		return top;
	}

	/**
	 * @return size of this stack.
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Add a new stack
	 * @param stack new stack
	 */
	public void add(Stack<T> stack) {
		
		Stack<T> clon = stack.clone();
		Stack<T> aux = new Stack<>();
		
		while(!clon.isEmpty()) {
			aux.push(clon.pop());
		}
		
		while(!aux.isEmpty()) {
			push(aux.pop());
		}
		
	}
	
	/**
	 * Prints a stack in console
	 */
	public void print() {
		Node<T> aux = top;
		while(aux != null) {
			System.out.print(aux.getData()+"\t");
			aux = aux.getNext();
		}
		System.out.println();
	}
	
	
	
	@Override
	protected Stack<T> clone(){

		Stack<T> finalStack = new Stack<>();		
		Node<T> topNode = null;
		
		for (Node<T> aux = top; aux != null; aux = aux.getNext()) {
			
			Node<T> newNode = new Node<>( aux.getData() );
			
			if(finalStack.isEmpty()) {
				finalStack.top = newNode;
				topNode = newNode;
			}else {
				finalStack.top.setNext(newNode);
				finalStack.top = newNode;
			}			
			finalStack.size++;
			
		}
		
		finalStack.top = topNode;
		
		return finalStack;
	}
	
}