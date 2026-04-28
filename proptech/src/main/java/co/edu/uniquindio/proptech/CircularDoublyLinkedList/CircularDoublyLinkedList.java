package co.edu.uniquindio.proptech.CircularDoublyLinkedList;

import java.util.Iterator;

public class CircularDoublyLinkedList<T extends Comparable<T>> implements Iterable<T> {
    private Node<T> first;
    private int size;

    public CircularDoublyLinkedList() {
        first = null;
        size = 0;
    }

    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);
        if(isEmpty()) {
            first = newNode;
            first.setNext(first);
            first.setPrev(first);
        } else {
            Node<T> last = first.getPrev();
            newNode.setNext(first);
            newNode.setPrev(last);

            last.setNext(newNode);
            first.setPrev(newNode);

            first = newNode;
        }
        size++;
    }

    public void addLast(T data){
        if(isEmpty()){
            addFirst(data);
            return;
        }
        Node<T> newNode = new Node<>(data);
        Node<T> last = first.getPrev();

        newNode.setNext(first);
        newNode.setPrev(last);

        last.setNext(newNode);
        first.setPrev(newNode);

        size ++;
    }

    public void addIndex (T data, int index){
        if(!isValidIndex(index)){
            throw new RuntimeException("Index out of range");
        }
        if(index == 0){
            addFirst(data);
            return;
        }
        if(index == size){
            addLast(data);
            return;
        }
        Node<T> actual = first;
        for (int i = 0; i < index; i++){
            actual = actual.getNext();
        }
        Node<T> prev = actual.getPrev();
        Node<T> newNode = new Node<>(data);

        newNode.setNext(actual);
        newNode.setPrev(prev);

        prev.setNext(newNode);
        actual.setPrev(newNode);

        size ++;
    }

    public T getData (int index){
        if(!isValidIndex(index)){
            throw new RuntimeException("Index out of range.");
        }
        Node<T> actual = first;
        for(int i = 0; i < index; i++){
            actual = actual.getNext();
        }
        return actual.getData();
    }

    public void setData (int index, T data){
        if(!isValidIndex(index)){
            throw new RuntimeException("Index out of range.");
        }
        Node<T> actual = first;
        for(int i = 0; i < index; i++){
            actual = actual.getNext();
        }
        actual.setData(data);
    }

   public int getIndex(T data){
        if(isEmpty()){
            return -1;
        }
        Node<T> actual = first;
        int index = 0;

        do{
            if(actual.getData().equals(data)){
                return index;
            }
            actual = actual.getNext();
            index++;
        } while(actual != first);
        return -1;
   }

   public boolean isValidIndex(int index){
    return index >= 0 && index < size;
   }

   private boolean isEmpty() {
        return first == null;
    }

    public void removeFirst(){
        if(isEmpty()){
            throw new RuntimeException("List is empty.");
        } 
        if(first.getNext() == first){
            first = null;
        } else {
            Node<T> last = first.getPrev();
            first = first.getNext();

            first.setPrev(last);
            last.setNext(first);
        }
        size --;
    }

    public void removeLast(){
        if(isEmpty()){
            throw new RuntimeException("List is empty");
        }
        if (first.getNext() == first){
            first = null;
        } else {
            Node<T>  last = first.getPrev();
            Node<T> newLast = last.getPrev();

            newLast.setNext(first);
            first.setPrev(newLast);
        }
        size--;
    }

    public void removeData(T data){
        if(isEmpty()){
            throw new RuntimeException("List is empty.");
        }
        Node<T> actual = first;
        do {
            if(actual.getData().equals(data)){
                if(actual == first){
                    removeFirst();
                    return;
                }
                Node<T> prev = actual.getPrev();
                Node<T> next = actual.getNext();

                prev.setNext(next);
                next.setPrev(prev);

                size--;
                return;
            }
            actual = actual.getNext();
        } while(actual != first);
    }

    public void removeAt(int index){
        if(!isValidIndex(index)){
            throw new RuntimeException("Index out of range.");
        } else {
            if(index == 0){
                removeFirst();
                return;
            }

            Node<T> actual = first;
            for(int i = 0; i < index; i++){
                actual = actual.getNext();
            }

            Node<T> prev = actual.getPrev();
            Node<T> next = actual.getNext();

            
            prev.setNext(next);
            next.setPrev(prev);
            size--;
        }
    }

    public void sortList(){
        if(first == null || first.getNext() == first) return;
        boolean swapped;
        do{
            swapped = false;
            Node<T> actual = first;
            do{
                Node<T> next = actual.getNext();
                if(next != first && actual.getData().compareTo(next.getData()) > 0){
                    T temp = actual.getData();
                    actual.setData(next.getData());
                    next.setData(temp);
                    swapped = true;
                }
                actual = actual.getNext();
            } while(actual.getNext() != first);
        } while(swapped);
    }

    public void reverseList(){
        if(first == null || first.getNext() == first){
            return;
        }
        reverseList(first);
        first = first.getNext();
    }

    private void reverseList(Node<T> actual){
        Node<T> next = actual.getNext();
        
        actual.setNext(actual.getPrev());
        actual.setPrev(next);

        if(next != first){
            reverseList(next);
        }
    }

    public void printList(){
        if(isEmpty()){
            throw new RuntimeException("List is empty.");
        }
        Node<T> aux = first;
        do{
            System.out.println(aux.getData());
            aux = aux.getNext();
        } while(aux != first);
    }

    public void removeList(){
        first = null;
        size = 0;
    }

    public int getSize() {
        return size;
    }

    public Node<T> getFirst(){
        return first;
    }

    @Override
    public Iterator<T> iterator() {
        return new CircularDoublyLinkedListIterator<>(this);
    }

}
