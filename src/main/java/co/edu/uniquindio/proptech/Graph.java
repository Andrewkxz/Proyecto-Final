package co.edu.uniquindio.proptech;

import co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList;
import co.edu.uniquindio.proptech.Queues.Queue;
import co.edu.uniquindio.proptech.Stack.Stack;

public class Graph<T extends Comparable<T>>{
    private LinkedSimpleList<Vertice<T>> vertices;

    public Graph(){
        vertices = new LinkedSimpleList<>();
    }

    public void addVertex(T data){
        if(searchVertex(data) == null){
            vertices.addLast(new Vertice<>(data));
        }
    }

    public Vertice<T> searchVertex(T data){
        for(Vertice<T> vertex : vertices){
            if(vertex.getDato().equals(data)){
                return vertex;
            }
        }
        return null;
    }

    public void connect(T origin, T destination){
        Vertice<T> v1 = searchVertex(origin);
        Vertice<T> v2 = searchVertex(destination);

        if(v1 != null && v2 != null){
            if(v1.getAdyacentes().getIndex(v2) == -1){
                v1.agregarConexion(v2);
            }
            if(v2.getAdyacentes().getIndex(v1) == -1){
                v2.agregarConexion(v1);
            }
        }
    }

    public void printGraph(){
        for(Vertice<T> vertex : vertices){
            System.out.println(vertex.getDato() + " -> ");
            for(Vertice<T> adjacent : vertex.getAdyacentes()){
                System.out.println(adjacent.getDato() + " ");
            }
            System.out.println();
        }
    }

    public void BFS(T startData){
        Vertice<T> start = searchVertex(startData);
        if(start == null){
            return;
        }
        LinkedSimpleList<Vertice<T>> visited = new LinkedSimpleList<>();
        Queue<Vertice<T>> queue = new Queue<>();

        queue.enqueue(start);
        visited.addLast(start);

        while(!queue.isEmpty()){
            Vertice<T> current = queue.dequeue();
            System.out.println(current.getDato() + " ");
            for(Vertice<T> neighbor : current.getAdyacentes()){
                if(visited.getIndex(neighbor) == -1){
                    visited.addLast(neighbor);
                    queue.enqueue(neighbor);
                }
            }
        }
        System.out.println();
    }

    public void DFS(T startData){
        Vertice<T> start = searchVertex(startData);
        if(start == null){
            return;
        }
        LinkedSimpleList<Vertice<T>> visited = new LinkedSimpleList<>();
        Stack<Vertice<T>> stack = new Stack<>();

        stack.push(start);
        while(!stack.isEmpty()){
            Vertice<T> current = stack.pop();
            if(visited.getIndex(current) == -1){
                visited.addLast(current);
                System.out.println(current.getDato() + " ");
                for(Vertice<T> neighbor : current.getAdyacentes()){
                    if(visited.getIndex(neighbor) == -1){
                        stack.push(neighbor);
                    }
                }
            }
        }
        System.out.println();
    }

    public void removeConnection(T origin, T destination){
        Vertice<T> v1 = searchVertex(origin);
        Vertice<T> v2 = searchVertex(destination);

        if(v1 != null && v2 != null){
            v1.getAdyacentes().removeData(v2);
            v2.getAdyacentes().removeData(v1);
        }
    }

    public void removeVertex(T data){
        Vertice<T> vertexToRemove = searchVertex(data);
        if(vertexToRemove == null){
            return;
        }
        for(Vertice<T> vertex : vertices){
            vertex.getAdyacentes().removeData(vertexToRemove);
        }
        vertices.removeData(vertexToRemove);
    }

    public int size(){
        return vertices.getSize();
    }

    public boolean isEmpty(){
        return vertices.getSize() == 0;
    }
}
