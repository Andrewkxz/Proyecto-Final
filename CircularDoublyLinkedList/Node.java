package co.edu.uniquindio.proptech.CircularDoublyLinkedList;

public class Node<T extends Comparable<T>> implements Comparable<Node<T>>{
    private T data;
    Node<T> next;
    Node<T> prev;

    public Node (T data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data){
        this.data = data;
    }

    public Node<T> getNext() {
        return next;
    }

    public Node<T> getPrev(){
        return prev;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public void setPrev(Node<T> prev){
        this.prev = prev;
    }

    @Override
    public String toString() {
        return data + "-->";
    }

    @Override
    public int compareTo(Node<T> o) {
        return this.getData().compareTo(o.getData());
    }    

}
