package com.utn.bolsines.entidades;

import com.utn.bolsines.entidades.estados.EstadoDocumentacion;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * ENTIDAD: DetalleRemito (<<entity>>)
 * ============================================================================
 * Renglón de detalle dentro de un Remito.
 * 
 * TIPO DE RELACIONES SEGÚN EL DIAGRAMA DE CLASES (Grupo 8):
 * - Asociación simple (1 a 1) con Documentacion (rol: 'documentacion'):
 *     Vincula un documento físico particular con el remito.
 * - Pertenece a un Remito contenedor (1 Remito contiene 1..* DetalleRemito).
 */
public class DetalleRemito {

    private int id;
    private int cantidad;
    private String descripcion;
    private Documentacion documentacion; // Rol: 'documentacion' (1 a 1)

    public DetalleRemito() {}

    public DetalleRemito(int id, int cantidad, String descripcion, Documentacion documentacion) {
        this.id = id;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.documentacion = documentacion;
    }

    // =========================================================================
    // MÉTODOS DEL DIAGRAMA DE CLASES Y SECUENCIAS (GRUPO 8)
    // =========================================================================

    /**
     * Método del Diagrama de Clases y Secuencias: obtenerDatosDetalle()
     * Obtiene la descripción textual del documento contenido en el detalle.
     */
    public String obtenerDatosDetalle() {
        if (documentacion != null) {
            return documentacion.mostrarDocumentacion();
        }
        return "Detalle sin documentación asociada.";
    }

    public String mostrarDocumentacion() {
        return obtenerDatosDetalle();
    }

    /**
     * Método del Diagrama de Clases y Secuencias: actualizarEstadoDoc()
     * Propaga la recepción hacia el contexto de Documentacion, el cual delegará
     * en su estado actual (Patrón GoF State).
     */
    public void actualizarEstadoDoc(EstadoDocumentacion nuevoEstadoDoc, LocalDateTime fechaHora, Empleado responsable) {
        if (documentacion != null) {
            documentacion.recibir(nuevoEstadoDoc, fechaHora, responsable);
        }
    }

    public void recibirYAceptar(EstadoDocumentacion nuevoEstadoDoc, LocalDateTime fechaHora, Empleado responsable) {
        actualizarEstadoDoc(nuevoEstadoDoc, fechaHora, responsable);
    }

    // Getters y Setters
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
