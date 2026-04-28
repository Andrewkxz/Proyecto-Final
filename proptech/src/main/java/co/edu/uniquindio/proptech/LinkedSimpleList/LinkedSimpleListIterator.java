package co.edu.uniquindio.proptech.LinkedSimpleList;

import java.util.Iterator;

public class LinkedSimpleListIterator<T extends Comparable <T>> implements Iterator<T> {
    private Node<T> actual;

    public LinkedSimpleListIterator(LinkedSimpleList<T> lista){
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
