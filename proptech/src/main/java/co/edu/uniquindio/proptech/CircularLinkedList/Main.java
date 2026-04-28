package co.edu.uniquindio.proptech.CircularLinkedList;

public class Main {
    public static void main(String[] args) {

        CircularLinkedList<Integer> list = new CircularLinkedList<>();

        System.out.println("=== ADD ===");
        list.addFirst(3);
        list.addFirst(1);
        list.addLast(5);
        list.addLast(7);
        list.printList();

        System.out.println("\n=== ADD INDEX ===");
        list.addIndex(99, 2);
        list.printList();

        System.out.println("\n=== GET ===");
        System.out.println(list.getData(2));

        System.out.println("\n=== SET ===");
        list.setData(2, 50);
        list.printList();

        System.out.println("\n=== INDEX ===");
        System.out.println(list.getIndex(50));

        System.out.println("\n=== REMOVE FIRST ===");
        list.removeFirst();
        list.printList();

        System.out.println("\n=== REMOVE LAST ===");
        list.removeLast();
        list.printList();

        System.out.println("\n=== REMOVE DATA ===");
        list.removeData(50);
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

        System.out.println("\n=== SIZE ===");
        System.out.println(list.getSize());

        System.out.println("\n=== CLEAR ===");
        list.removeList();
        list.printList();
    }
}