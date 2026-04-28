package co.edu.uniquindio.proptech.DoublyLinkedList;

import java.util.Iterator;

public class DoublyLinkedList<T extends Comparable<T>> implements Iterable<T> {
    private Node<T> first;
    private Node<T> last;
    private int size;

    public DoublyLinkedList() {
        first = null;
        last = null;
        size = 0;
    }

    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);
        if(isEmpty()) {
            first = last = newNode;
        } else {
            newNode.setNext(first);
            first.setPrev(newNode);
            first = newNode;
        }
        size++;
    }

    public void addLast(T data){
        Node<T> newNode = new Node<>(data);
        if(isEmpty()){
            first = last = newNode;
        } else {
            last.setNext(newNode);
            newNode.setPrev(last);
            last = newNode;
        }
        size++;
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
        Node<T> current = first;
        for(int i = 0; i < index; i++){
            current = current.getNext();
        }
        Node<T> previous = current.getPrev();
        Node<T> newNode = new Node<>(data);

        previous.setNext(newNode);
        newNode.setPrev(previous);

        newNode.setNext(current);
        current.setPrev(newNode);

        size++;
    }

    public T getData (int index){
        if(!isValidIndex(index)){
            throw new RuntimeException("Index out of range.");
        }
        Node<T> current = first;
        for(int i = 0; i < index; i++){
            current = current.getNext();
        }
        return current.getData();
    }

    public void setData (int index, T data){
        if(!isValidIndex(index)){
            throw new RuntimeException("Index out of range.");
        }
        Node<T> current = first;
        for(int i = 0; i < index; i++){
            current = current.getNext();
        }
        current.setData(data);
    }

   public int getIndex(T data){
        Node<T> current = first;
        int index = 0;
        while(current != null){
            if(current.getData().equals(data)){
                return index;
            }
            current = current.getNext();
            index++;
        }
        return -1;
   }

   public boolean isValidIndex(int index){
    return index >= 0 && index < size;
   }

   public boolean isEmpty() {
        return size == 0;
    }

    public void removeFirst(){
        if(isEmpty()){
            throw new RuntimeException("List is empty.");
        } 
        if(size == 1){
            first = last = null;
        } else {
            first = first.getNext();
            first.setPrev(null);
        }
        size--;
    }

    public void removeLast(){
        if(isEmpty()){
            throw new RuntimeException("List is empty");
        }
        if(size == 1){
            first = last = null;
        } else {
            last = last.getPrev();
            last.setNext(null);
        }
        size--;
    }

    public void removeData(T data){
        int index = getIndex(data);
        if(index != -1){
            removeAt(index);
        }
    }

    public void removeAt(int index){
        if(!isValidIndex(index)){
            throw new RuntimeException("Index out of range.");
        }
        if(index == 0){
            removeFirst();
            return;
        }
        if(index == size -1){
            removeLast();
            return;
        }
        Node<T> current = first;
        for(int i = 0; i < index; i++){
            current = current.getNext();
        }
        Node<T> prev = current.getPrev();
        Node<T> next = current.getNext();

        prev.setNext(next);
        next.setPrev(prev);

        size--;

    }

    public void sortList(){
        if(size <= 1) return;

        boolean swapped;

        do{
            swapped = false;
            Node<T> current = first;
            while(current.getNext() != null){
                if(current.getData().compareTo(current.getNext().getData()) > 0){
                    T temp = current.getData();
                    current.setData(current.getNext().getData());
                    current.getNext().setData(temp);

                    swapped = true;
                }
                current = current.getNext();
            }
        } while(swapped);
    }

    public void reverseList(){
        Node<T> current = first;
        Node<T> temp = null;

        while(current != null){
            temp = current.getPrev();
            current.setPrev(current.getNext());
            current.setNext(temp);

            current = current.getPrev();
        }
        temp = first;
        first = last;
        last = temp;
    }

    public void printList(){
        Node<T> current = first;
        if (current == null){
            System.out.println("List is empty.");
        } else {
            while(current != null){
                System.out.println(current + "<-> ");
                current = current.getNext();
            }
        }
    }

    public void removeList(){
        first = null;
        last = null;
        size = 0;
    }

    public int getSize() {
        return size;
    }
    public Node<T> getFirst(){
        return first;
    }
    public Node<T> getLast(){
        return last;
    }

    @Override
    public Iterator<T> iterator() {
        return new DoublyLinkedListIterator<>(this);
    }

}
