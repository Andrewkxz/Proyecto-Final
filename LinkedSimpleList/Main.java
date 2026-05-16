package co.edu.uniquindio.proptech.LinkedSimpleList;

public class Main {
    public static void main(String[] args) {
        LinkedSimpleList<Integer> list = new LinkedSimpleList<>();
        System.out.println("**********Add elements**********");
        list.addFirst(3);
        list.addFirst(1);
        list.addLast(5);
        list.addLast(7);
        list.addIndex(2, 1);

        list.printList();

        System.out.println("**********Get data**********");
        System.out.println(list.getData(2));

        System.out.println("**********Set data**********");
        list.setData(2, 99);
        list.printList();

        System.out.println("**********Get index**********");
        System.out.println(list.getIndex(99));

        System.out.println("**********Remove first**********");
        list.removeFirst();
        list.printList();

        System.out.println("**********Remove last**********");
        list.removeLast();
        list.printList();

        System.out.println("**********Remove at**********");
        list.removeAt(1);
        list.printList();

        System.out.println("**********Sort list**********");
        list.addLast(4);
        list.addLast(8);
        list.addLast(2);
        list.printList();

        list.sortList();
        System.out.println("sorted list:");
        list.printList();

        System.out.println("***********reverse list**********");
        list.reverseList();
        list.printList();

        System.out.println("**********Iterator**********");
        for(Integer num : list){
            System.out.println(num + " ");
        }

        System.out.println("**********size**********");
        System.out.println("size: " + list.getSize());

        System.out.println("**********remove list**********");
        list.removeList();
        list.printList();



    }
    
}
