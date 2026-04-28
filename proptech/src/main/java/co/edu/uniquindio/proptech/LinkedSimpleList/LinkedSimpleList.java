package co.edu.uniquindio.proptech.LinkedSimpleList;

import java.util.Iterator;

public class LinkedSimpleList<T extends Comparable<T>> implements Iterable<T> {
    private Node<T> first;
    private int size;

    public LinkedSimpleList() {
        this.first = null;
        this.size = 0;
    }

    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);
        if(isEmpty()) {
            first = newNode;
        } else {
            newNode.setNext(first);
            first = newNode;
        }
        size++;
    }

    public void addLast(T data){
        Node<T> newNode = new Node<>(data);
        if(isEmpty()){
            first = newNode;
        } else {
            Node<T> aux = first;
            while(aux.getNext() != null){
                aux = aux.getNext();
            }
            aux.setNext(newNode);
        }
        size ++;
    }

    public void addIndex (T data, int index){
        Node<T> newNode = new Node<>(data);
        if(index == 0){
           addFirst(data);
           return;
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
        int actualIndex = 0;
        while(actual != null){
            if(actualIndex == index){
                T data = actual.getData();
                return data;
            }
            actual = actual.getNext();
            actualIndex++;
        }
        return null;
    }

    public void setData (int index, T data){
        if(!isValidIndex(index)){
            throw new RuntimeException("Index out of range.");
        }
        Node<T> actual = first;
        int actualIndex = 0;
        while(actual != null){
            if(actualIndex == index){
                actual.setData(data);
                return;
            }
            actual = actual.getNext();
            actualIndex++;
        }
    }

   public int getIndex(T data){
        Node<T> actual = first;
        int searchIndex = 0;
        while (actual != null){
            if(actual.getData().equals(data)){
                return searchIndex;
            }
            searchIndex++;
            actual = actual.getNext();
        }
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
        } else {
            Node<T> next = first.getNext();
            first.setNext(null);
            first = next;
            size --;
        }
    }

    public void removeLast(){
        if(isEmpty()){
            throw new RuntimeException("List is empty");
        }
        if (first.getNext() == null){
            first = null;
        } else {
            Node<T>  aux = first;
            while(aux.getNext().getNext() != null){
                aux = aux.getNext();
            }
            aux.setNext(null);
        }
        size--;
    }

    public void removeData(T data){
        if(isEmpty()){
            throw new RuntimeException("List is empty.");
        }
        if(first.getData() == data){
            removeFirst();
        }
        Node<T> aux = first;
        while(aux.getNext() != null){
            if(aux.getNext().getData().equals(data)){
                aux.setNext(aux.getNext().getNext());
                size--;
            }
            aux = aux.getNext();
        }
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
            while(actual.getNext() != null){
                if(actual.getData().compareTo(actual.getNext().getData()) > 0){
                    T temp = actual.getData();
                    actual.setData(actual.getNext().getData());
                    actual.getNext().setData(temp);
                    swapped = true;
                }
                actual = actual.getNext();
            }
        } while(swapped);
    }

    public void reverseList(){
        if(first == null || first.getNext() == null){
            return;
        }
        first = reverseList(first, null);
    }

    private Node<T> reverseList(Node<T> actual, Node<T> prev){
        if(actual == null){
            return prev;
        }
        Node<T> next = actual.getNext();
        actual.setNext(prev);
        return reverseList(next, actual);
    }

    public void printList(){
        if (isEmpty()){
            System.out.println("List is empty.");
        } else {
            Node<T> aux = first;
            while(aux != null){
                System.out.println(aux.getData());
                aux = aux.getNext();
            }
        }
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
        return new LinkedSimpleListIterator<>(this);
    }

}
