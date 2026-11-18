package com.utn.bolsines.entidades;

import com.utn.bolsines.entidades.estados.EstadoDocumentacion;
import java.time.LocalDateTime;

/**
 * Detalle del Remito (Corrección 2).
 * Vincula una cantidad con una Documentación específica.
 */
public class DetalleRemito {
    private int id;
    private int cantidad;
    private String descripcion;
    private Documentacion documentacion;

    public DetalleRemito() {}

    public DetalleRemito(int id, int cantidad, String descripcion, Documentacion documentacion) {
        this.id = id;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.documentacion = documentacion;
    }

    public String mostrarDocumentacion() {
        if (documentacion != null) {
            return documentacion.mostrarDocumentacion();
        }
        return "Detalle sin documentación asociada.";
    }

    public void recibirYAceptar(EstadoDocumentacion nuevoEstadoDoc, LocalDateTime fechaHora, Empleado responsable) {
        if (documentacion != null) {
            documentacion.recibir(nuevoEstadoDoc, fechaHora, responsable);
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Documentacion getDocumentacion() { return documentacion; }
    public void setDocumentacion(Documentacion documentacion) { this.documentacion = documentacion; }

    @Override
    public String toString() {
        return "DetalleRemito [cant=" + cantidad + ", doc=" + (documentacion != null ? documentacion.getAsunto() : "null") + "]";
    }
}
