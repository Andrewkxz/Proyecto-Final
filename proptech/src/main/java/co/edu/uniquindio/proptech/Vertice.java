package co.edu.uniquindio.proptech;

import co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList;

public class Vertice<T extends Comparable<T>> implements Comparable<Vertice<T>> {
    private T dato;
    private LinkedSimpleList<Vertice<T>> adyacentes;

    public Vertice(T dato){
        this.dato = dato;
        this.adyacentes = new LinkedSimpleList<>();
    }

    public void agregarConexion(Vertice<T> vecino){
        this.adyacentes.addLast(vecino);
    }

    public T getDato() {
        return dato;
    }
    public LinkedSimpleList<Vertice<T>> getAdyacentes() {
        return adyacentes;
    }
    @Override
    public int compareTo(Vertice<T> otroVertice) {
        return this.dato.compareTo(otroVertice.dato);
    }
}
