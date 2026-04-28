package co.edu.uniquindio.proptech.Queues;

public class RunColas {

	public static void main(String[] args) {
		
		
		Queue<Integer> cola= new Queue<>();
		
		cola.enqueue(1);
		cola.enqueue(2);
		cola.enqueue(3);
		
		cola.dequeue();
		
		cola.print();
		

	}

}
