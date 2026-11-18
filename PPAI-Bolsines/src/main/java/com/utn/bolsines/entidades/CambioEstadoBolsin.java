package com.utn.bolsines.entidades;

import com.utn.bolsines.entidades.estados.EstadoBolsin;
import java.time.LocalDateTime;

/**
 * Historial de cambio de estado para Bolsín (Corrección 1 y 7).
 */
public class CambioEstadoBolsin {
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private EstadoBolsin estado;
    private Empleado responsable;

    public CambioEstadoBolsin() {}

    public CambioEstadoBolsin(EstadoBolsin estado, LocalDateTime fechaHoraInicio, Empleado responsable) {
        this.estado = estado;
        this.fechaHoraInicio = fechaHoraInicio;
        this.responsable = responsable;
        this.fechaHoraFin = null;
    }

    public boolean sosActual() {
        return this.fechaHoraFin == null;
    }

    public boolean sosEnviado() {
        return this.estado != null && this.estado.sosEnviado();
    }

    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) { this.fechaHoraInicio = fechaHoraInicio; }

    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }

    public EstadoBolsin getEstado() { return estado; }
    public void setEstado(EstadoBolsin estado) { this.estado = estado; }

    public Empleado getResponsable() { return responsable; }
    public void setResponsable(Empleado responsable) { this.responsable = responsable; }

    @Override
    public String toString() {
        return "CambioEstadoBolsin{" +
                "estado=" + (estado != null ? estado.getNombre() : "null") +
                ", inicio=" + fechaHoraInicio +
                ", fin=" + fechaHoraFin +
                ", responsable=" + (responsable != null ? responsable.getNombre() : "N/A") +
                '}';
    }
}
