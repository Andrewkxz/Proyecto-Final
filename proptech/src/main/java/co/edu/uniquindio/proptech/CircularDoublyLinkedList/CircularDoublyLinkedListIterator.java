package co.edu.uniquindio.proptech.CircularDoublyLinkedList;

import java.util.Iterator;

public class CircularDoublyLinkedListIterator<T extends Comparable <T>> implements Iterator<T> {
    private Node<T> actual;

    public CircularDoublyLinkedListIterator(CircularDoublyLinkedList<T> lista){
        this.actual = lista.getFirst();
    }

    @Override
    public boolean hasNext() {
        return actual != null;
    }

    @Override
    public T next() {
        T data = actual.getData();
        actual = actual.getNext();
        return data;
    }
    
}
