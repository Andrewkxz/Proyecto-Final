package co.edu.uniquindio.proptech.HashTable;

public class HashTable<K, V> {
    private Node<K, V>[] table;
    private int capacity;
    private int size;

    @SuppressWarnings("unchecked")
    public HashTable(){
        this.capacity = 16;
        this.table = new Node[capacity];
        this.size = 0;
    }

    @SuppressWarnings("unchecked")
    public HashTable(int capacity){
        this.capacity = capacity;
        this.table = new Node[capacity];
        this.size = 0;
    }

    /**
     * Generates index from key
     */
    private int hash(K key){
        return Math.abs(key.hashCode()) % capacity;
    }

    /**
     * Inserts or updates a key-value pair
     */
    public void put(K key, V value){
        int index = hash(key);
        Node<K, V> current = table[index];

        while(current != null){
            if(current.getKey().equals(key)){
                current.setValue(value);
                return;
            }
            current = current.getNext();
        }
        Node<K, V> newNode = new Node<>(key, value);
        newNode.setNext(table[index]);
        table[index] = newNode;
        size++;
    }

    /**
     * Returns value associated with key
     */
    public V get(K key){
        int index = hash(key);
        Node<K, V> current = table[index];

        while(current != null){
            if(current.getKey().equals(key)){
                return current.getValue();
            }
            current = current.getNext();
        }
        return null;
    }

    /**
     * Removes key-value pair
     */
    public V remove(K key){
        int index = hash(key);
        Node<K, V> current = table[index];
        Node<K, V> previous = null;

        while(current != null){
            if(current.getKey().equals(key)){
                if(previous == null){
                    table[index] = current.getNext();
                } else {
                    previous.setNext(current.getNext());
                }
                size--;
                return current.getValue();
            }
            previous = current;
            current = current.getNext();
        }
        return null;
    }

    /**
     * Checks if key exists
     */
    public boolean containsKey(K key){
        return get(key) != null;
    }

    /**
     * Checks if table is empty
     */
    public boolean isEmpty(){
        return size == 0;
    }

    /**
     * Returns number of elements
     */
    public int size(){
        return size;
    }

    /**
     * Clears the table
     */
    @SuppressWarnings("unchecked")
    public void clear(){
        table = new Node[capacity];
        size = 0;
    }

    /**
     * Prints all elements
     */
    public void printTable(){
        for(int i = 0; i < capacity; i++){
            Node<K, V> current = table[i];
            System.out.println("Index " + i + ": ");

            while(current != null){
                System.out.println("[" + current.getKey() + " = " + current.getValue() + "] -> ");
                current = current.getNext();
            }
            System.out.println("null");
        }
    }
}
