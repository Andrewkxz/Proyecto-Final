package co.edu.uniquindio.proptech.Stack;

public class RunPilas {

	public static void main(String[] args) {
	
		
		
		Stack<Libro> pila = new Stack<Libro>();

        Libro l1 = new Libro("Titulo 1", "Autor 1");
        Libro l2 = new Libro("Titulo 2", "Autor 2");
        Libro l3 = new Libro("Titulo 3", "Autor 3");

        pila.push(l1); // adiciona un libro a la pila
        pila.push(l2);
        pila.push(l3);

//        System.out.println(pila.peek().getTitulo()); // el ultimo elemento adicionado

        while (!pila.isEmpty()) { // mostrar pila completa
            System.out.println(pila.pop().getTitulo()); // extrae un elemento de la pila
        }
		
		

	}

}
