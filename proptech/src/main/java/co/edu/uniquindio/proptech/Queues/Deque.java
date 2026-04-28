package co.edu.uniquindio.proptech.Queues;

public class Deque<T> extends Queue<T>{
	
	public Deque() 
	{
		
	}
	
	// add element to the end of the Deque
	public void addLast(T data)
	{
		enqueue(data); // inherited method from Queue
	}
	
	// add element to the front of the Deque
	public void addFirst(T data)
	{
		Node<T> newNode;
		newNode = new Node<T>(data);
		if (isEmpty())
		{
			lastNode = newNode;
		}
		newNode.setNext(firstNode);
		firstNode = newNode;
	}
	
	
	// removes element from the front of the Deque
	public T removesFront() 
	{
		T value = dequeue();
		return value; // inherited method from Queue
	}
	
	// removes element from the end of the Deque
	// it's necessary to traverse the Deque until the node before the last one
	public T removeEnd() throws Exception
	{
		T aux;
		if (!isEmpty())
		{
			if (firstNode == lastNode) // deque has only one node
				aux = dequeue();
			else
			{
				Node<T> node = firstNode;
				while (node.getNext() != lastNode)
					node = node.getNext();
				
				// unlink the last node
				node.setNext(null);
				aux = lastNode.getData();
				lastNode = node;
			}
		}
		else
			throw new Exception("Cannot remove from an empty Deque");
		return aux;
	}
	

}
