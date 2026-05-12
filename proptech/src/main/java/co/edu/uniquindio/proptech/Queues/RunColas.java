package co.edu.uniquindio.proptech.Queues;

public class RunColas {

	public static void main(String[] args) {
		
		
		Queue<Integer> cola= new Queue<>();
		
		cola.offer(1);
		cola.offer(2);
		cola.offer(3);
		
		cola.poll();
		
		cola.print();
		

	}

}
