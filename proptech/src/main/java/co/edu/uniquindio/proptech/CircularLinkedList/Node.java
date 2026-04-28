package co.edu.uniquindio.proptech.CircularLinkedList;

public class Node<T extends Comparable<T>> implements Comparable<Node<T>>{
    private T data;
    Node<T> next;

    public Node (T data) {
        this.data = data;
        this.next = null;
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

    public void setNext(Node<T> next) {
        this.next = next;
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
