package com.utn.bolsines.entidades;

import com.utn.bolsines.entidades.estados.EstadoDocumentacion;
import java.time.LocalDateTime;

/**
 * Historial de cambio de estado para Documentación (Corrección 1 y 7).
 */
public class CambioEstadoDocumentacion {
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private EstadoDocumentacion estado;
    private Empleado responsable;

    public CambioEstadoDocumentacion() {}

    public CambioEstadoDocumentacion(EstadoDocumentacion estado, LocalDateTime fechaHoraInicio, Empleado responsable) {
        this.estado = estado;
        this.fechaHoraInicio = fechaHoraInicio;
        this.responsable = responsable;
        this.fechaHoraFin = null;
    }

    public boolean sosActual() {
        return this.fechaHoraFin == null;
    }

    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) { this.fechaHoraInicio = fechaHoraInicio; }

    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }

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
