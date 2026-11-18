package com.utn.bolsines.entidades;

import com.utn.bolsines.entidades.estados.EstadoDocumentacion;
import com.utn.bolsines.entidades.estados.EstadoRemito;
import com.utn.bolsines.entidades.estados.EstadoRemitoCreado;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Remito (Corrección 2 y 7).
 * Se asocia a 1..* DetalleRemito.
 */
public class Remito {
    private int id;
    private long numeroRemito;
    private LocalDateTime fechaHoraAlta;
    private Empleado empleadoEmisor;
    private ComisionMedica cmDestino;
    private List<DetalleRemito> detalles;
    private EstadoRemito estado;

    public Remito() {
        this.detalles = new ArrayList<>();
        this.estado = new EstadoRemitoCreado();
    }

    public Remito(int id, long numeroRemito, LocalDateTime fechaHoraAlta, Empleado empleadoEmisor, ComisionMedica cmDestino) {
        this();
        this.id = id;
        this.numeroRemito = numeroRemito;
        this.fechaHoraAlta = fechaHoraAlta;
        this.empleadoEmisor = empleadoEmisor;
        this.cmDestino = cmDestino;
    }

    public void agregarDetalle(DetalleRemito detalle) {
        this.detalles.add(detalle);
    }

    public long getNumero() {
        return numeroRemito;
    }

    public List<String> mostrarDocumentacion() {
        List<String> docsInfo = new ArrayList<>();
        for (DetalleRemito det : detalles) {
            docsInfo.add(det.mostrarDocumentacion());
        }
        return docsInfo;
    }

    public void recibirYAceptar(EstadoRemito nuevoEstadoRemito, EstadoDocumentacion nuevoEstadoDoc, LocalDateTime fechaHora, Empleado responsable) {
        if (this.estado != null) {
            this.estado.recibirYAceptar(this, nuevoEstadoRemito, nuevoEstadoDoc, fechaHora, responsable);
        } else {
            this.estado = nuevoEstadoRemito;
            for (DetalleRemito det : detalles) {
                det.recibirYAceptar(nuevoEstadoDoc, fechaHora, responsable);
            }
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
