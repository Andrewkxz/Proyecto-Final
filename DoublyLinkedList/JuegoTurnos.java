package co.edu.uniquindio.proptech.DoublyLinkedList;

public class JuegoTurnos {

    private DoublyLinkedList<String> jugadores;
    private Node<String> actual;

    public JuegoTurnos() {
        jugadores = new DoublyLinkedList<>();
        actual = null;
    }

    public static void main(String[] args) {
        JuegoTurnos juego = new JuegoTurnos();

        // Agregar jugadores
        juego.agregarJugador("Juan");
        juego.agregarJugador("Ana");
        juego.agregarJugador("Luis");
        juego.agregarJugador("Sofía");

        System.out.println(" Jugadores:");
        juego.mostrarJugadores();

        // Turno actual
        System.out.println("\n Turno actual: " + juego.jugadorActual());

        // Avanzar turnos
        juego.siguienteTurno();
        System.out.println(" Siguiente: " + juego.jugadorActual());

        juego.siguienteTurno();
        System.out.println(" Siguiente: " + juego.jugadorActual());

        // Retroceder
        juego.turnoAnterior();
        System.out.println(" Anterior: " + juego.jugadorActual());

        // Ver vecinos
        System.out.println("\nJuega antes: " + juego.jugadorAnterior());
        System.out.println("Juega después: " + juego.jugadorSiguiente());

        // Eliminar jugador
        System.out.println("\nEliminando a Luis...");
        juego.eliminarJugador("Luis");

        System.out.println("\nJugadores actuales:");
        juego.mostrarJugadores();

        // Probar que sigue siendo circular
        juego.siguienteTurno();
        juego.siguienteTurno();
        juego.siguienteTurno();

        System.out.println("\n Turno después de varios avances: " + juego.jugadorActual());
    }

    // Agregar jugador
    public void agregarJugador(String nombre) {
        jugadores.addFirst(nombre);

        if (actual == null) {
            actual = jugadores.getFirst();
        }
    }

    // Siguiente turno
    public void siguienteTurno() {
        if (actual != null) {
            actual = actual.getNext();
        }
    }

    // Turno anterior
    public void turnoAnterior() {
        if (actual != null) {
            actual = actual.getPrev();
        }
    }

    // Jugador actual
    public String jugadorActual() {
        if (actual == null) return "No hay jugadores";
        return actual.getData();
    }

    // Jugador siguiente
    public String jugadorSiguiente() {
        if (actual == null) return "No hay jugadores";
        return actual.getNext().getData();
    }

    // Jugador anterior
    public String jugadorAnterior() {
        if (actual == null) return "No hay jugadores";
        return actual.getPrev().getData();
    }

    // Eliminar jugador
    public void eliminarJugador(String nombre) {
        if (jugadores.isEmpty()) return;

        Node<String> aux = jugadores.getFirst();

        do {
            if (aux.getData().equals(nombre)) {

                // Si eliminamos el actual, avanzamos
                if (aux == actual) {
                    actual = actual.getNext();
                }

                jugadores.removeData(nombre);
                return;
            }

            aux = aux.getNext();

        } while (aux != jugadores.getFirst());
    }

    // Mostrar jugadores
    public void mostrarJugadores() {
        if (jugadores.isEmpty()) {
            System.out.println("No hay jugadores");
            return;
        }

        Node<String> aux = jugadores.getFirst();

        do {
            System.out.println(aux.getData());
            aux = aux.getNext();
        } while (aux != jugadores.getFirst());
    }
}