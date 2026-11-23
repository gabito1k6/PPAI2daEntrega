package com.utn.bolsines.entidades;

import com.utn.bolsines.entidades.estados.EstadoDocumentacion;
import com.utn.bolsines.entidades.estados.EstadoRemito;
import com.utn.bolsines.entidades.estados.EstadoRemitoRecibidoYAceptado;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * ENTIDAD: Remito (<<entity>>)
 * Diagrama de Clases y Diagrama de Secuencia Grupo 8 - Rediseño State Pattern
 * ============================================================================
 * Representa el remito de entrega que acompaña la documentación en el bolsín.
 * 
 * ATRIBUTOS SEGÚN DIAGRAMA DE CLASES:
 * - fecha: date
 * - numero: int
 * 
 * OPERACIONES SEGÚN DIAGRAMA DE CLASES:
 * - + getNumero(): int
 * - + obtenerInfoDocumentacion(): List<String>
 * - + recibirYAceptar(fechaHora: datetime, responsable: Empleado): void
 * 
 * SECUENCIA EN CU 28:
 * Bolsin llama a recibirYAceptar(fechaHora, responsable).
 * Remito actualiza su estado y en un bucle llama a detalle.actualizarEstadoDoc(fechaHora, responsable).
 */
public class Remito {

    private int id;
    private long numeroRemito; // 'numero: int' en Diagrama de Clases
    private LocalDateTime fechaHoraAlta; // 'fecha: date' en Diagrama de Clases
    private Empleado empleadoEmisor;
    private ComisionMedica cmDestino;
    private List<DetalleRemito> detalles = new ArrayList<>();
    private EstadoRemito estado;

    public Remito() {}

    public Remito(int id, long numeroRemito, LocalDateTime fechaHoraAlta, Empleado empleadoEmisor, ComisionMedica cmDestino) {
        this(id, numeroRemito, fechaHoraAlta, empleadoEmisor, cmDestino, null);
    }

    public Remito(int id, long numeroRemito, LocalDateTime fechaHoraAlta, Empleado empleadoEmisor, ComisionMedica cmDestino, EstadoRemito estado) {
        this.id = id;
        this.numeroRemito = numeroRemito;
        this.fechaHoraAlta = fechaHoraAlta;
        this.empleadoEmisor = empleadoEmisor;
        this.cmDestino = cmDestino;
        this.estado = estado;
    }

    public void agregarDetalle(DetalleRemito detalle) {
        this.detalles.add(detalle);
    }

    // =========================================================================
    // MÉTODOS DEL DIAGRAMA DE CLASES Y SECUENCIAS (GRUPO 8)
    // =========================================================================

    /**
     * Operación del Diagrama de Clases: getNumero(): int
     */
    public long getNumero() {
        return numeroRemito;
    }

    /**
     * Operación del Diagrama de Clases y Secuencias: obtenerInfoDocumentacion()
     */
    public List<String> obtenerInfoDocumentacion() {
        List<String> docsInfo = new ArrayList<>();
        for (DetalleRemito det : detalles) {
            docsInfo.add(det.obtenerDatosDetalle());
        }
        return docsInfo;
    }

    public List<String> mostrarDocumentacion() {
        return obtenerInfoDocumentacion();
    }

    /**
     * Operación del Diagrama de Clases y Secuencias (Patrón State Grupo 8):
     * recibirYAceptar(fechaHora: datetime, responsable: Empleado): void
     * Actualiza el estado del Remito y propaga la recepción a cada DetalleRemito.
     */
    public void recibirYAceptar(LocalDateTime fechaHora, Empleado responsable) {
        this.estado = new EstadoRemitoRecibidoYAceptado();
        for (DetalleRemito det : detalles) {
            det.actualizarEstadoDoc(fechaHora, responsable);
        }
    }

    /**
     * Sobrecarga para compatibilidad previa:
     */
    public void recibirYAceptar(EstadoRemito nuevoEstadoRemito, EstadoDocumentacion nuevoEstadoDoc, LocalDateTime fechaHora, Empleado responsable) {
        if (nuevoEstadoRemito != null) {
            this.estado = nuevoEstadoRemito;
        } else {
            this.estado = new EstadoRemitoRecibidoYAceptado();
        }
        for (DetalleRemito det : detalles) {
            det.actualizarEstadoDoc(fechaHora, responsable);
        }
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getNumeroRemito() { return numeroRemito; }
    public void setNumeroRemito(long numeroRemito) { this.numeroRemito = numeroRemito; }

    public LocalDateTime getFechaHoraAlta() { return fechaHoraAlta; }
    public void setFechaHoraAlta(LocalDateTime fechaHoraAlta) { this.fechaHoraAlta = fechaHoraAlta; }

    public Empleado getEmpleadoEmisor() { return empleadoEmisor; }
    public void setEmpleadoEmisor(Empleado empleadoEmisor) { this.empleadoEmisor = empleadoEmisor; }

    public ComisionMedica getCmDestino() { return cmDestino; }
    public void setCmDestino(ComisionMedica cmDestino) { this.cmDestino = cmDestino; }

    public List<DetalleRemito> getDetalles() { return detalles; }

    public EstadoRemito getEstado() { return estado; }
    public void setEstado(EstadoRemito estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Remito #" + numeroRemito + " [Estado: " + (estado != null ? estado.getNombre() : "N/A") + ", Cant. Detalles: " + detalles.size() + "]";
    }
}
