package co.edu.uniquindio.proptech;

import java.util.ArrayList;
import java.util.List;

import co.edu.uniquindio.proptech.DoublyLinkedList.DoublyLinkedList;
import co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList;
import co.edu.uniquindio.proptech.Queues.Queue;
import jakarta.persistence.*;

@Entity
@Table(name = "asesores")
public class Asesor implements Comparable<Asesor>{
    @Id
    private String id;
    private String nombre;
    private String contacto;
    private String especialidadZona;
    private int cargaTrabajoActiva;
    private int numeroCierres;

    // --- Estructuras propias (en memoria) ---
    @Transient
    private LinkedSimpleList<Inmueble> inmueblesAsignados;
    @Transient
    private Queue<Visita> visitasAgendadas;
    @Transient
    private DoublyLinkedList<Operacion> cierresRealizados;

    // --- Listas JPA para persistencia ---
    @ManyToMany
    private List<Inmueble> inmueblesAsignadosJpa = new ArrayList<>();
    @ElementCollection
    private List<String> cierresRealizadosJpa = new ArrayList<>();

    public Asesor() {
        inicializarColecciones();
    }

    private void inicializarColecciones() {
        this.inmueblesAsignados = new LinkedSimpleList<>();
        this.visitasAgendadas = new Queue<>();
        this.cierresRealizados = new DoublyLinkedList<>();
    }

    public Asesor(String id, String nombre, String contacto, String especialidadZona){
        this();
        this.id = id;
        this.nombre = nombre;
        this.contacto = contacto;
        this.especialidadZona = especialidadZona;
        this.cargaTrabajoActiva = 0;
        this.numeroCierres = 0;
    }

    @PostLoad
    public void postLoad() {
        inicializarColecciones();
        for (Inmueble i : inmueblesAsignadosJpa) inmueblesAsignados.addLast(i);
    }

    @PrePersist
    @PreUpdate
    public void prePersist() {
        inmueblesAsignadosJpa = new ArrayList<>();
        for (int i = 0; i < inmueblesAsignados.getSize(); i++) inmueblesAsignadosJpa.add(inmueblesAsignados.getData(i));
    }

    public void asignarInmuebles(Inmueble inmueble){ this.inmueblesAsignados.addLast(inmueble); }

    public void agendarVisita(Visita visita){
        this.visitasAgendadas.offer(visita);
        this.cargaTrabajoActiva++;
        if(this.cargaTrabajoActiva > 15){
            System.out.println("ALERTA: El asesor " + this.nombre + "tiene sobrecarga excesiva de atención.");
        }
    }

    public Visita atenderSiguienteVisita(){
        if(!visitasAgendadas.isEmpty()){
            this.cargaTrabajoActiva--;
            return visitasAgendadas.poll();
        }
        return null;
    }

    public void registrarCierre(Operacion operacion){
        this.cierresRealizados.addLast(operacion);
        this.numeroCierres++;
    }

    @Override
    public int compareTo(Asesor otroAsesor) {
        return Integer.compare(otroAsesor.numeroCierres, this.numeroCierres);
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getContacto() { return contacto; }
    public String getEspecialidadZona() { return especialidadZona; }
    public int getCargaTrabajoActiva() { return cargaTrabajoActiva; }
    public int getNumeroCierres() { return numeroCierres; }
    public LinkedSimpleList<Inmueble> getInmueblesAsignados() { return inmueblesAsignados; }
    public Queue<Visita> getVisitasAgendadas() { return visitasAgendadas; }
    public DoublyLinkedList<Operacion> getCierresRealizados() { return cierresRealizados; }
}
