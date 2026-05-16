package co.edu.uniquindio.proptech.DoublyLinkedList;

import java.util.Iterator;

public class DoublyLinkedListIterator<T extends Comparable <T>> implements Iterator<T> {
    private Node<T> current;

    public DoublyLinkedListIterator(DoublyLinkedList<T> list){
        current = list.getFirst();
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public T next() {
        T data = current.getData();
        current = current.getNext();
        return data;
    }
    
}
