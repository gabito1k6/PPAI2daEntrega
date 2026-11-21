package com.utn.bolsines.entidades;

import com.utn.bolsines.entidades.estados.EstadoDocumentacion;
import com.utn.bolsines.entidades.estados.EstadoRemito;
import com.utn.bolsines.entidades.estados.EstadoRemitoCreado;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * ENTIDAD: Remito (<<entity>>)
 * ============================================================================
 * Documento de envío que agrupa uno o más ítems de documentación dentro del bolsín.
 * 
 * TIPO DE RELACIONES SEGÚN EL DIAGRAMA DE CLASES (Grupo 8):
 * - Agregación / Composición (1 a 1..*) con DetalleRemito (rol: 'detalleRemito'):
 *     Cada remito contiene uno o más renglones de detalle.
 * - Asociación simple (1 a 1) con ComisionMedica (rol: 'destino'):
 *     Indica la sede destinataria hacia donde viaja la documentación.
 * - Asociación simple (1 a 1) con EstadoRemito (rol: 'estado'):
 *     Estado en el que se encuentra el remito (ej: 'En Bolsín Enviado', 'Recibido y Aceptado').
 * - Asociación con Empleado (rol: 'empleadoEmisor'):
 *     Empleado de la CM de origen que emitió el remito.
 */
public class Remito {

    private int id;
    private long numeroRemito; // 'numero' en Diagrama de Clases
    private LocalDateTime fechaHoraAlta; // 'fecha' en Diagrama de Clases
    private Empleado empleadoEmisor;
    private ComisionMedica cmDestino;
    private List<DetalleRemito> detalles; // Rol: 'detalleRemito' (1..*)
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

    // =========================================================================
    // MÉTODOS DEL DIAGRAMA DE CLASES Y SECUENCIAS (GRUPO 8)
    // =========================================================================

    /**
     * Método del Diagrama de Clases y Secuencias: getNumero()
     */
    public long getNumero() {
        return numeroRemito;
    }

    /**
     * Método del Diagrama de Clases y Secuencias: obtenerInfoDocumentacion()
     * Itera por los detalles del remito para obtener el detalle de cada documentación.
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
     * Método del Diagrama de Clases y Secuencias: recibirYAceptar()
     * Transiciona el estado del remito a Recibido y Aceptado y delega la actualización
     * a cada uno de sus detalles.
     */
    public void recibirYAceptar(EstadoRemito nuevoEstadoRemito, EstadoDocumentacion nuevoEstadoDoc, LocalDateTime fechaHora, Empleado responsable) {
        if (this.estado != null) {
            this.estado.recibirYAceptar(this, nuevoEstadoRemito, nuevoEstadoDoc, fechaHora, responsable);
        } else {
            this.estado = nuevoEstadoRemito;
            for (DetalleRemito det : detalles) {
                det.actualizarEstadoDoc(nuevoEstadoDoc, fechaHora, responsable);
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
