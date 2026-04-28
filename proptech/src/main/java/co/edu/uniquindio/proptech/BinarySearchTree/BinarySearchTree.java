package co.edu.uniquindio.proptech.BinarySearchTree;

import co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList;
import co.edu.uniquindio.proptech.Queues.Queue;

public class BinarySearchTree<T extends Comparable<T>> {
    public Node<T> root;
    
    public BinarySearchTree() {
        this.root = null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void insert(T data) {
        root = insertRec(root, data);
    }

    private Node<T> insertRec (Node<T> root, T data) {
        if(root == null) {
            return new Node<>(data);
        }
        if(data.compareTo(root.getData()) < 0) {
            root.setLeft(insertRec(root.getLeft(), data));
        } else if(data.compareTo(root.getData()) > 0) {
            root.setRight(insertRec(root.getRight(), data));
        }
        return root;
    }

    public LinkedSimpleList<T> getInOrder() {
        LinkedSimpleList<T> result = new LinkedSimpleList<>();
        getInOrderRec(root, result);
        return result;
    }

    private void getInOrderRec(Node<T> node, LinkedSimpleList<T> result){
        if(node != null){
            getInOrderRec(node.getLeft(), result);
            result.addLast(node.getData());
            getInOrderRec(node.getRight(), result);
        }
    }

    public LinkedSimpleList<T> getPreOrder() {
        LinkedSimpleList<T> result = new LinkedSimpleList<>();
        getPreOrderRec(root, result);
        return result;
    }

    private void getPreOrderRec(Node<T> node, LinkedSimpleList<T> result){
        if(node != null){
            result.addLast(node.getData());
            getPreOrderRec(node.getLeft(), result);
            getPreOrderRec(node.getRight(), result);
        }
    }

    public LinkedSimpleList<T> getPostOrder() {
        LinkedSimpleList<T> result = new LinkedSimpleList<>();
        getPostOrderRec(root, result);
        return result;
    }

    private void getPostOrderRec(Node<T> node, LinkedSimpleList<T> result){
        if(node != null){
            getPostOrderRec(node.getLeft(), result);
            getPostOrderRec(node.getRight(), result);
            result.addLast(node.getData());
        }
    }

    public boolean exists(T data) {
        return existsRec(root, data);
    }

    private boolean existsRec(Node<T> node, T data) {
        if (node == null) {
            return false;
        }
        int compareResult = data.compareTo(node.getData());
        if (compareResult == 0) {
            return true;
        }
        if (compareResult < 0) {
            return existsRec(node.getLeft(), data);
        }
        return existsRec(node.getRight(), data);    
    }

    public LinkedSimpleList<T> searchInRange(T minValue, T maxValue){
        LinkedSimpleList<T> result = new LinkedSimpleList<>();
        searchInRangeRec(root, minValue, maxValue, result);
        return result;
    }

    private void searchInRangeRec(Node<T> node, T minValue, T maxValue, LinkedSimpleList<T> result){
        if(node == null){
            return;
        }
        if(node.getData().compareTo(minValue) > 0){
            searchInRangeRec(node.getLeft(), minValue, maxValue, result);
        }
        if(node.getData().compareTo(minValue) >= 0 && node.getData().compareTo(maxValue) <= 0){
            result.addLast(node.getData());
        }
        if(node.getData().compareTo(maxValue) < 0){
            searchInRangeRec(node.getRight(), minValue, maxValue, result);
        }
    }

    public int getSize() {
        return getSizeRec(root);
    }

    private int getSizeRec(Node<T> node) {
        if (node == null) {
            return 0;
        }
        return 1 + getSizeRec(node.getLeft()) + getSizeRec(node.getRight());
    }

    public int getHeight() {
        return getHeightRec(root);
    }

    private int getHeightRec(Node<T> node) {
        if (node == null) {
            return 0;
        }
        int leftHeight = getHeightRec(node.getLeft());
        int rightHeight = getHeightRec(node.getRight());
        return Math.max(leftHeight, rightHeight) + 1;
    }

    public int getLevel(T data) {
        return getLevelRec(root, data, 1);
    }

    private int getLevelRec(Node<T> node, T data, int level) {
        if (node == null) {
            return -1;
        }
        int compareResult = data.compareTo(node.getData());
        if (compareResult == 0) {
            return level;
        }
        if(compareResult < 0) {
            return getLevelRec(node.getLeft(), data, level + 1);
        }
        return getLevelRec(node.getRight(), data, level + 1);
    }

    public int countLeaves() {
        return countLeavesRec(root);
    }

    private int countLeavesRec(Node<T> node) {
        if (node == null) {
            return 0;
        }
        if (node.getLeft() == null && node.getRight() == null) {
            return 1;
        }
        return countLeavesRec(node.getLeft()) + countLeavesRec(node.getRight());
    }

    public T findMin() {
        if (root == null) {
            return null;
        }
        return findMin(root).getData();
    }

    private Node<T> findMin(Node<T> node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }

    public LinkedSimpleList<T> printAmplitude() {
        LinkedSimpleList<T> result = new LinkedSimpleList<>();
        if (root == null) {
            return result;
        }

        Queue<Node<T>> queue = new Queue<>();
        queue.enqueue(root);
        while (!queue.isEmpty()) {
            Node<T> current = queue.dequeue();
            result.addLast(current.getData());
            if (current.getLeft() != null) {
                queue.enqueue(current.getLeft());
            }
            if (current.getRight() != null) {
                queue.enqueue(current.getRight());
            }
        }
        return result;
    }

    public void delete(T data) {
        root = deleteRec(root, data);
    }

    private Node<T> deleteRec(Node<T> root, T data){
        if(root == null){
            return null;
        }
        int compareResult = data.compareTo(root.getData());
        if(compareResult < 0){
            root.setLeft(deleteRec(root.getLeft(), data));
        } else if(compareResult > 0){
            root.setRight(deleteRec(root.getRight(), data));
        } else {
            if(root.getLeft() == null && root.getRight() == null){
                return null;
            }
            if(root.getLeft() == null){
                return root.getRight();
            } else if(root.getRight() == null){
                return root.getLeft();
            }
            Node<T> minNode = findMin(root.getRight());
            root.setData(minNode.getData());
            root.setRight(deleteRec(root.getRight(), minNode.getData()));
        }
        return root;
    }

    public Node<T> getMajorNode() {
        if (root == null) {
            return null;
        }
        Node<T> currentNode = root;
        while (currentNode.getRight() != null) {
            currentNode = currentNode.getRight();
        }
        return currentNode;
    }

    public Node<T> getMinorNode() {
        if (root == null) {
            System.out.println("The tree is empty.");
            return null;
        }
        Node<T> currentNode = root;
        while (currentNode.getLeft() != null) {
            currentNode = currentNode.getLeft();
        }
        return currentNode;
    }

    public void clearTree(){
        root = null;
        System.out.println("The tree has been cleared.");
    }
}
