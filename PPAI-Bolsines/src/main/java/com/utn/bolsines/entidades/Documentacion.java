package com.utn.bolsines.entidades;

import com.utn.bolsines.entidades.estados.EstadoDocumentacion;
import com.utn.bolsines.entidades.estados.EstadoRegistrada;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Contexto del Patrón State para la Documentación.
 * Cumple con las Correcciones 1, 3 y 7.
 */
public class Documentacion {
    private int id;
    private long numeroIdentificador;
    private String asunto;
    private LocalDate fechaPase;
    private TipoDocumento tipoDocumento;
    private ComisionMedica cmOrigen;
    private List<Archivo> archivos;
    private List<CambioEstadoDocumentacion> historialEstados;
    private EstadoDocumentacion estadoActual;

    public Documentacion() {
        this.archivos = new ArrayList<>();
        this.historialEstados = new ArrayList<>();
    }

    public Documentacion(int id, long numeroIdentificador, String asunto, LocalDate fechaPase,
                         TipoDocumento tipoDocumento, ComisionMedica cmOrigen,
                         EstadoDocumentacion estadoInicial, Empleado responsable) {
        this();
        this.id = id;
        this.numeroIdentificador = numeroIdentificador;
        this.asunto = asunto;
        this.fechaPase = fechaPase;
        this.tipoDocumento = tipoDocumento;
        this.cmOrigen = cmOrigen;
        if (estadoInicial == null) {
            estadoInicial = new EstadoRegistrada();
        }
        crearPrimerEstado(estadoInicial, LocalDateTime.now(), responsable);
    }

    private void crearPrimerEstado(EstadoDocumentacion estadoInicial, LocalDateTime fechaHora, Empleado responsable) {
        this.estadoActual = estadoInicial;
        CambioEstadoDocumentacion primerCE = new CambioEstadoDocumentacion(estadoInicial, fechaHora, responsable);
        this.historialEstados.add(primerCE);
    }

    // ==========================================
    // MÉTODOS DEL PATRÓN STATE (Corrección 3)
    // Delegan a su estado actual
    // ==========================================

    public void generar(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().generar(this, fechaHora, responsable);
    }

    public void registrarEnRemito(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().registrarEnRemito(this, fechaHora, responsable);
    }

    public void enviar(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().enviar(this, fechaHora, responsable);
    }

    public void recibir(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().recibir(this, nuevoEstado, fechaHora, responsable);
    }

    public void recibir() {
        obtenerEstadoActual().recibir(this);
    }

    public void rechazar(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().rechazar(this, fechaHora, responsable);
    }

    public void rechazar(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().rechazar(this, nuevoEstado, fechaHora, responsable);
    }

    public void redirigir(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().redirigir(this, fechaHora, responsable);
    }

    public void redirigir(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().redirigir(this, nuevoEstado, fechaHora, responsable);
    }

    public void noRecibir(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().noRecibir(this, nuevoEstado, fechaHora, responsable);
    }

    public void noRecibir(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().noRecibir(this, null, fechaHora, responsable);
    }

    public void registrarBaja(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().registrarBaja(this, fechaHora, responsable);
    }

    public void modificar() {
        obtenerEstadoActual().modificar(this);
    }

    public void darBajaBolsin(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().darBajaBolsin(this, fechaHora, responsable);
    }

    // ==========================================
    // TRAZABILIDAD Y ACTUALIZACIÓN (Corrección 7)
    // ==========================================

    public void actualizarEstado(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        LocalDateTime ahora = (fechaHora != null) ? fechaHora : LocalDateTime.now();

        // 1. Buscar en el historial el CambioEstado actual (sosActual() == true) y cerrarlo
        CambioEstadoDocumentacion ceActual = buscarCambioEstadoActual();
        if (ceActual != null) {
            ceActual.setFechaHoraFin(ahora);
        }

        // 2. Crear nuevo CambioEstadoDocumentacion y agregarlo
        CambioEstadoDocumentacion nuevoCE = new CambioEstadoDocumentacion(nuevoEstado, ahora, responsable);
        this.historialEstados.add(nuevoCE);
        this.estadoActual = nuevoEstado;
    }

    public void actualizarEstado(EstadoDocumentacion nuevoEstado) {
        actualizarEstado(nuevoEstado, LocalDateTime.now(), null);
    }

    public CambioEstadoDocumentacion buscarCambioEstadoActual() {
        for (CambioEstadoDocumentacion ce : historialEstados) {
            if (ce.sosActual()) {
                return ce;
            }
        }
        return null;
    }

    public EstadoDocumentacion obtenerEstadoActual() {
        if (this.estadoActual != null) {
            return this.estadoActual;
        }
        CambioEstadoDocumentacion actual = buscarCambioEstadoActual();
        if (actual != null) {
            this.estadoActual = actual.getEstado();
            return this.estadoActual;
        }
        return null;
    }

    public void agregarArchivo(Archivo archivo) {
        this.archivos.add(archivo);
    }

    public String mostrarDocumentacion() {
        String tipoStr = (tipoDocumento != null) ? tipoDocumento.getNombre() : "Sin tipo";
        return "Doc #" + numeroIdentificador + " | Tipo: " + tipoStr + " | Asunto: " + asunto;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getNumeroIdentificador() { return numeroIdentificador; }
    public void setNumeroIdentificador(long numeroIdentificador) { this.numeroIdentificador = numeroIdentificador; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public LocalDate getFechaPase() { return fechaPase; }
    public void setFechaPase(LocalDate fechaPase) { this.fechaPase = fechaPase; }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public ComisionMedica getCmOrigen() { return cmOrigen; }
    public void setCmOrigen(ComisionMedica cmOrigen) { this.cmOrigen = cmOrigen; }

    public List<Archivo> getArchivos() { return archivos; }
    public List<CambioEstadoDocumentacion> getHistorialEstados() { return historialEstados; }

    public EstadoDocumentacion getEstadoActual() { return obtenerEstadoActual(); }
    public void setEstadoActual(EstadoDocumentacion estadoActual) { this.estadoActual = estadoActual; }

    @Override
    public String toString() {
        return mostrarDocumentacion() + " [" + (obtenerEstadoActual() != null ? obtenerEstadoActual().getNombre() : "Sin Estado") + "]";
    }
}
