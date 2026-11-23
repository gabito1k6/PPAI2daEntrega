package com.utn.bolsines.entidades;

import com.utn.bolsines.entidades.estados.EstadoDocumentacion;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * ENTIDAD: CambioEstadoDocumentacion (<<entity>>)
 * Diagrama de Clases Grupo 8 - Rediseño State Pattern
 * ============================================================================
 * Historial de cambio de estado para Documentación.
 * 
 * ATRIBUTOS SEGÚN DIAGRAMA DE CLASES:
 * - fechaHoraFin: datetime
 * - fechaHoraInicio: datetime
 * - responsable: Empleado (rol: responsableCE)
 * - estado: EstadoDocumentacion
 * 
 * OPERACIONES SEGÚN DIAGRAMA DE CLASES:
 * - + sosActual(): boolean
 * - + setFechaHoraFin(fechaHoraFin: datetime): void
 * - + new(fechaHoraInicio: datetime, responsable: Empleado, estado: EstadoDocumentacion)
 */
public class CambioEstadoDocumentacion {

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private EstadoDocumentacion estado;
    private Empleado responsable; // Rol: responsableCE (1 a 1)

    public CambioEstadoDocumentacion() {}

    /**
     * Constructor del Diagrama de Clases (Grupo 8):
     * new(fechaHoraInicio: datetime, responsable: Empleado, estado: EstadoDocumentacion)
     */
    public CambioEstadoDocumentacion(LocalDateTime fechaHoraInicio, Empleado responsable, EstadoDocumentacion estado) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.responsable = responsable;
        this.estado = estado;
        this.fechaHoraFin = null;
    }

    /**
     * Constructor de sobrecarga para compatibilidad:
     * new(estado, fechaHoraInicio, responsable)
     */
    public CambioEstadoDocumentacion(EstadoDocumentacion estado, LocalDateTime fechaHoraInicio, Empleado responsable) {
        this(fechaHoraInicio, responsable, estado);
    }

    /**
     * Operación del Diagrama de Clases y Secuencias:
     * sosActual(): boolean -> Retorna true si es el estado vigente (fechaHoraFin == null).
     */
    public boolean sosActual() {
        return this.fechaHoraFin == null;
    }

    /**
     * Operación del Diagrama de Clases y Secuencias:
     * setFechaHoraFin(fechaHoraFin: datetime): void
     */
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    // Getters y Setters
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) { this.fechaHoraInicio = fechaHoraInicio; }

    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }

    public EstadoDocumentacion getEstado() { return estado; }
    public void setEstado(EstadoDocumentacion estado) { this.estado = estado; }

    public Empleado getResponsable() { return responsable; }
    public void setResponsable(Empleado responsable) { this.responsable = responsable; }

    @Override
    public String toString() {
        return "CambioEstadoDocumentacion{" +
                "estado=" + (estado != null ? estado.getNombre() : "null") +
                ", inicio=" + fechaHoraInicio +
                ", fin=" + fechaHoraFin +
                ", responsable=" + (responsable != null ? responsable.toString() : "N/A") +
                '}';
    }
}
