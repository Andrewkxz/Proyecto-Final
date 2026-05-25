package co.edu.uniquindio.proptech.EstructuraDatos.Queues;


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
	public void offer(T data) {
		
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
	public T poll() {
		
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

	public T peek() {
		if(isEmpty()) {
			throw new RuntimeException("Queue is Empty");
		}
		return firstNode.getData();
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
	public void clear() {
		firstNode = lastNode = null;
		size = 0;
	}

	public void enqueue(T data) {
		offer(data);
	}

	public T dequeue() {
		return poll();
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
	public int size() {
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
		
		if(clon1.size() == clon2.size()) {
			
			while( !clon1.isEmpty() ) {				
				if( !clon1.poll().equals( clon2.poll() ) ) {
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
			newQueue.offer( aux.getData() );
			aux = aux.getNext();
		}
		
		return newQueue;		
	}
	
	
}