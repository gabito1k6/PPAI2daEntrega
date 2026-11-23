package com.utn.bolsines.entidades;

import com.utn.bolsines.entidades.estados.EstadoDocumentacion;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * ENTIDAD: DetalleRemito (<<entity>>)
 * Diagrama de Clases y Diagrama de Secuencia Grupo 8 - Rediseño State Pattern
 * ============================================================================
 * Renglón de detalle dentro de un Remito.
 * 
 * ATRIBUTOS SEGÚN DIAGRAMA DE CLASES:
 * - areaCMCDestino: string
 * 
 * OPERACIONES SEGÚN DIAGRAMA DE CLASES:
 * - + actualizarEstadoDoc(fechaHora: datetime, responsable: Empleado): void
 * 
 * SECUENCIA EN CU 28:
 * Remito llama a actualizarEstadoDoc(fechaHora, responsable).
 * DetalleRemito propaga hacia Documentacion.recibir(fechaHora, responsable).
 */
public class DetalleRemito {

    private int id;
    private int cantidad;
    private String descripcion;
    private String areaCMCDestino; // Atributo según Diagrama de Clases Grupo 8
    private Documentacion documentacion; // Rol: 'documentacion' (1 a 1)

    public DetalleRemito() {}

    public DetalleRemito(int id, int cantidad, String descripcion, Documentacion documentacion) {
        this.id = id;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.documentacion = documentacion;
    }

    public DetalleRemito(int id, int cantidad, String descripcion, String areaCMCDestino, Documentacion documentacion) {
        this.id = id;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.areaCMCDestino = areaCMCDestino;
        this.documentacion = documentacion;
    }

    // =========================================================================
    // MÉTODOS DEL DIAGRAMA DE CLASES Y SECUENCIAS (GRUPO 8)
    // =========================================================================

    /**
     * Operación del Diagrama de Clases y Secuencias (Patrón State Grupo 8):
     * actualizarEstadoDoc(fechaHora: datetime, responsable: Empleado): void
     * Propaga la recepción hacia el contexto Documentación.
     */
    public void actualizarEstadoDoc(LocalDateTime fechaHora, Empleado responsable) {
        if (documentacion != null) {
            documentacion.recibir(fechaHora, responsable);
        }
    }

    /**
     * Sobrecarga para compatibilidad con firmas anteriores:
     */
    public void actualizarEstadoDoc(EstadoDocumentacion nuevoEstadoDoc, LocalDateTime fechaHora, Empleado responsable) {
        actualizarEstadoDoc(fechaHora, responsable);
    }

    public void recibirYAceptar(EstadoDocumentacion nuevoEstadoDoc, LocalDateTime fechaHora, Empleado responsable) {
        actualizarEstadoDoc(fechaHora, responsable);
    }

    /**
     * Método del Diagrama de Clases y Secuencias: obtenerDatosDetalle()
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

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getAreaCMCDestino() { return areaCMCDestino; }
    public void setAreaCMCDestino(String areaCMCDestino) { this.areaCMCDestino = areaCMCDestino; }

    public Documentacion getDocumentacion() { return documentacion; }
    public void setDocumentacion(Documentacion documentacion) { this.documentacion = documentacion; }

    @Override
    public String toString() {
        return "DetalleRemito [cant=" + cantidad + ", doc=" + (documentacion != null ? documentacion.getAsunto() : "null") +
                (areaCMCDestino != null ? ", areaCMC=" + areaCMCDestino : "") + "]";
    }
}
