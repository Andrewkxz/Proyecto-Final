package co.edu.uniquindio.proptech.Queues;

public class PriorityQueue<T extends Comparable<T>> extends Queue<T> {

    /**
     * Inserts element according to its priority (natural order)
     * Lowest value has the highest priority
     * @param data Element to be added
     */
    @Override
    public void offer(T data){
        Node<T> newNode = new Node<>(data);
        if(isEmpty()){
            firstNode = lastNode = newNode;
        }
        else if(data.compareTo(firstNode.getData()) < 0){
            newNode.setNext(firstNode);
            firstNode = newNode;
        } else {
            Node<T> current = firstNode;
            while(current.getNext() != null && data.compareTo(current.getNext().getData()) >= 0){
                current = current.getNext();
            }
            newNode.setNext(current.getNext());
            current.setNext(newNode);

            if(newNode.getNext() == null){
                lastNode = newNode;
            }
        }
        size++;
    }

    public T peek(){
        if(isEmpty()){
            return null;
        }
        return firstNode.getData();
    }
    
}
