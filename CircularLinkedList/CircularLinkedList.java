package co.edu.uniquindio.proptech.CircularLinkedList;

import java.util.Iterator;

public class CircularLinkedList<T extends Comparable<T>> implements Iterable<T> {
    private Node<T> first;
    private int size;

    public CircularLinkedList() {
        this.first = null;
        this.size = 0;
    }

    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);
        if(isEmpty()) {
            first = newNode;
            first.setNext(first);
        } else {
            Node<T> aux = first;
            while(aux.getNext() != first){
                aux = aux.getNext();
            }
            newNode.setNext(first);
            aux.setNext(newNode);
            first = newNode;
        }
        size++;
    }

    public void addLast(T data){
        Node<T> newNode = new Node<>(data);
        if(isEmpty()){
            first = newNode;
            first.setNext(first);
        } else {
            Node<T> aux = first;
            while(aux.getNext() != first){
                aux = aux.getNext();
            }
            aux.setNext(newNode);
            newNode.setNext(first);
        }
        size ++;
    }

    public void addIndex (T data, int index){
        Node<T> newNode = new Node<>(data);
        if(index == 0){
            newNode.setNext(first);
            first = newNode;
        } else {
            Node<T> actual = first;
            for (int i = 0; i < index -1; i++){
                actual = actual.getNext();
            }
            newNode.setNext(actual.getNext());
            actual.setNext(newNode);
        }
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
        for(int i = 0; i < index;i++){
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

   public boolean isEmpty() {
        return first == null;
    }

    public void removeFirst(){
        if(isEmpty()){
            throw new RuntimeException("List is empty.");
        }
        if(first.getNext() == first){
            first = null;
        } else {
            Node<T> aux = first;
            while(aux.getNext() != first){
                aux = aux.getNext();
            }
            aux.setNext(first.getNext());
            first = first.getNext();
        }
        size--;
    }

    public void removeLast(){
        if(isEmpty()){
            throw new RuntimeException("List is empty");
        }
        if (first.getNext() == first){
            first = null;
        } else {
            Node<T>  aux = first;
            while(aux.getNext().getNext() != first){
                aux = aux.getNext();
            }
            aux.setNext(first);
        }
        size--;
    }

    public void removeData(T data){
        if(isEmpty()){
            throw new RuntimeException("List is empty.");
        }
        if(first.getData().equals(data)){
            removeFirst();
            return;
        }
        Node<T> aux = first;
        do{
            if(aux.getNext().getData().equals(data)){
                aux.setNext(aux.getNext().getNext());
                size--;
                return;
            }
            aux = aux.getNext();
        } while(aux != first);
    }

    public void removeAt(int index){
        if(isEmpty()){
            throw new RuntimeException("List is empty.");
        } else {
            if(index == 0){
                removeFirst();
                return;
            }
            if(index == size-1){
                removeLast();
                return;
            }

            Node<T> aux = first;
            for(int i = 0; i < index -1; i++){
                aux = aux.getNext();
            }

            Node<T> nodeToDelete = aux.getNext();
            aux.setNext(nodeToDelete.getNext());
            nodeToDelete.setNext(null);
            size--;

        }

    }

    public void sortList(){
        if(first == null || first.getNext() == null) return;
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
        Node<T> last = first;
        while(last.getNext() != first){
            last = last.getNext();
        }
        reverseList(first, last);
        first.setNext(last);
        first = last;
    }

    private void reverseList(Node<T> actual, Node<T> prev){
        if(actual == prev){
            return;
        }
        Node<T> next = actual.getNext();
        reverseList(next, prev);

        next.setNext(actual);
    }

    public void printList(){
        if(isEmpty()){
            throw new RuntimeException("List is empty.");
        }
        Node<T> aux = first;
        do {
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
        return new CircularLinkedListIterator<>(this);
    }

}
