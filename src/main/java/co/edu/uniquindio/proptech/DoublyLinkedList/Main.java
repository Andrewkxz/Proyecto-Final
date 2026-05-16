package co.edu.uniquindio.proptech.DoublyLinkedList;

public class Main {
    public static void main(String[] args) {

        DoublyLinkedList<Integer> list = new DoublyLinkedList<>();

        System.out.println("=== ADD FIRST & LAST ===");
        list.addFirst(3);
        list.addFirst(1);
        list.addLast(5);
        list.addLast(7);
        list.printList();

        System.out.println("\n=== ADD INDEX ===");
        list.addIndex(99, 2);
        list.printList();

        System.out.println("\n=== GET DATA ===");
        System.out.println("Index 2: " + list.getData(2));

        System.out.println("\n=== SET DATA ===");
        list.setData(2, 50);
        list.printList();

        System.out.println("\n=== GET INDEX ===");
        System.out.println("Index of 50: " + list.getIndex(50));

        System.out.println("\n=== REMOVE FIRST ===");
        list.removeFirst();
        list.printList();

        System.out.println("\n=== REMOVE LAST ===");
        list.removeLast();
        list.printList();

        System.out.println("\n=== REMOVE DATA ===");
        list.addLast(50);
        list.printList();
        System.out.println("*************************");
        list.removeData(50);
        list.printList();

        System.out.println("\n=== REMOVE AT ===");
        list.removeAt(1);
        list.printList();

        System.out.println("\n=== SORT ===");
        list.addLast(8);
        list.addLast(2);
        list.addLast(6);
        list.printList();

        list.sortList();
        System.out.println("Sorted:");
        list.printList();

        System.out.println("\n=== REVERSE ===");
        list.reverseList();
        list.printList();

        System.out.println("\n=== ITERATOR ===");
        for(Integer num : list){
            System.out.print(num + " ");
        }

        System.out.println("\n\n=== SIZE ===");
        System.out.println(list.getSize());

        System.out.println("\n=== REMOVE LIST ===");
        list.removeList();
        list.printList();
    }
}