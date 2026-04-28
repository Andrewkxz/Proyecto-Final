package co.edu.uniquindio.proptech.Queues;


/**
 *
 * @param <T>
 */
public class Queue<T> {

	public Node<T> firstNode, lastNode;
	public int size;
	

	
	/**
	 * Add element to the end of the Queue
	 * @param data element to save in the Queue
	 */
	public void enqueue(T data) {
		
		Node<T> node = new Node<>(data);
		
		if(isEmpty()) {
			firstNode = lastNode = node;
		}else {
			lastNode.setNext(node);
			lastNode = node;
		}
		
		size++;
	}
	
	/**
	 * Return and removes the element at the front of the Queue
	 * @return first element of the Queue
	 */
	public T dequeue() {
		
		if(isEmpty()) {
			throw new RuntimeException("Queue is Empty");
		}
		
		T data = firstNode.getData();
		firstNode = firstNode.getNext();
		
		if(firstNode == null) {
			lastNode = null;
		}
		
		size--;
		return data;
	}
	
	/**
	 * Verifies if the Queue is empty
	 * @return true if it is empty, false otherwise
	 */
	public boolean isEmpty() {
		return firstNode == null;
	}
	
	
	
	/**
	 * clean completely the Queue
	 */
	public void cleanQueue() {
		firstNode = lastNode = null;
		size = 0;
	}

	/**
	 * @return first element of the Queue
	 */
	public Node<T> getFirst() {
		return firstNode;
	}

	/**
	 * @return last element of the Queue
	 */
	public Node<T> getLast() {
		return lastNode;
	}

	/**
	 * @return size of the Queue
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Verifies if the Queue is identical to another Queue
	 * @param queue Queue to compare with current Queue
	 * @return True if both Queues are identical, false otherwise
	 */
	public boolean areIdentical(Queue<T> queue) {
		
		Queue<T> clon1 = clone();
		Queue<T> clon2 = queue.clone();
		
		if(clon1.getSize() == clon2.getSize()) {
			
			while( !clon1.isEmpty() ) {				
				if( !clon1.dequeue().equals( clon2.dequeue() ) ) {
					return false;
				}				
			}
			
		}else {
			return false;
		}
		
		return  true;
	}
	
	/**
	 * Prints a Queue in console
	 */
	public void print() {
		Node<T> aux = firstNode;
		while(aux != null) {
			System.out.print(aux.getData()+"\t");
			aux = aux.getNext();
		}
		System.out.println();
	}
	
	@Override
	protected Queue<T> clone() {
		
		Queue<T> newQueue = new Queue<>();
		Node<T> aux = firstNode;
		
		while(aux!=null) {
			newQueue.enqueue( aux.getData() );
			aux = aux.getNext();
		}
		
		return newQueue;		
	}
	
	
}