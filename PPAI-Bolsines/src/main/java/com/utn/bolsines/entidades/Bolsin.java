package com.utn.bolsines.entidades;

import com.utn.bolsines.entidades.estados.EstadoBolsin;
import com.utn.bolsines.entidades.estados.EstadoBolsinCreado;
import com.utn.bolsines.entidades.estados.EstadoDocumentacion;
import com.utn.bolsines.entidades.estados.EstadoRemito;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Bolsín.
 * Cumple con las Correcciones 1, 2, 5 y 7.
 * Conoce Remitos en cascada (no detalles ni documentación directa).
 * Aplica el Patrón Experto para evaluar su estado y destino.
 */
public class Bolsin {
    private int id;
    private long numeroIdentificador;
    private String numeroPrecinto;
    private LocalDateTime fechaAlta;
    private double peso;
    private ComisionMedica cmOrigen;
    private ComisionMedica cmDestino;
    private List<Remito> remitos;
    private List<CambioEstadoBolsin> historialEstados;
    private EstadoBolsin estadoActual;

    public Bolsin() {
        this.remitos = new ArrayList<>();
        this.historialEstados = new ArrayList<>();
    }

    public Bolsin(int id, long numeroIdentificador, String numeroPrecinto, LocalDateTime fechaAlta,
                  double peso, ComisionMedica cmOrigen, ComisionMedica cmDestino,
                  EstadoBolsin estadoInicial, Empleado responsable) {
        this();
        this.id = id;
        this.numeroIdentificador = numeroIdentificador;
        this.numeroPrecinto = numeroPrecinto;
        this.fechaAlta = fechaAlta;
        this.peso = peso;
        this.cmOrigen = cmOrigen;
        this.cmDestino = cmDestino;

        if (estadoInicial == null) {
            estadoInicial = new EstadoBolsinCreado();
        }
        crearPrimerEstado(estadoInicial, fechaAlta, responsable);
    }

    private void crearPrimerEstado(EstadoBolsin estadoInicial, LocalDateTime fechaHora, Empleado responsable) {
        this.estadoActual = estadoInicial;
        CambioEstadoBolsin primerCE = new CambioEstadoBolsin(estadoInicial, fechaHora, responsable);
        this.historialEstados.add(primerCE);
    }

    public void asociarRemito(Remito remito) {
        this.remitos.add(remito);
    }

    // ==========================================
    // PATRÓN EXPERTO (Corrección 5)
    // ==========================================

    public boolean sosEnviado() {
        EstadoBolsin est = obtenerEstadoActual();
        return est != null && est.sosEnviado();
    }

    public boolean tenesEstaCMDestino(ComisionMedica destino) {
        return this.cmDestino != null && this.cmDestino.equals(destino);
    }

    public boolean esParaCMYEstasEnviado(ComisionMedica destino) {
        return sosEnviado() && tenesEstaCMDestino(destino);
    }

    public boolean esTuCMOrigen(ComisionMedica origen) {
        return this.cmOrigen != null && this.cmOrigen.equals(origen);
    }

    // ==========================================
    // TRAZABILIDAD Y CAMBIO DE ESTADO (Corrección 7)
    // ==========================================

    public void actualizarEstado(EstadoBolsin nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        LocalDateTime ahora = (fechaHora != null) ? fechaHora : LocalDateTime.now();

        // 1. Cerrar el cambio de estado actual
        CambioEstadoBolsin ceActual = buscarCambioEstadoActual();
        if (ceActual != null) {
            ceActual.setFechaHoraFin(ahora);
        }

        // 2. Crear y agregar el nuevo cambio de estado
        CambioEstadoBolsin nuevoCE = new CambioEstadoBolsin(nuevoEstado, ahora, responsable);
        this.historialEstados.add(nuevoCE);
        this.estadoActual = nuevoEstado;
    }

    public CambioEstadoBolsin buscarCambioEstadoActual() {
        for (CambioEstadoBolsin ce : historialEstados) {
            if (ce.sosActual()) {
                return ce;
            }
        }
        return null;
    }

    public EstadoBolsin obtenerEstadoActual() {
        if (this.estadoActual != null) {
            return this.estadoActual;
        }
        CambioEstadoBolsin actual = buscarCambioEstadoActual();
        if (actual != null) {
            this.estadoActual = actual.getEstado();
            return this.estadoActual;
        }
        return null;
    }

    public EstadoBolsin buscarEstadoActual() {
        return obtenerEstadoActual();
    }

    // ==========================================
    // REALIZACIÓN DEL CU 28: RECEPCIÓN
    // ==========================================

    public void recibir(EstadoBolsin nuevoEstadoBolsin, EstadoRemito nuevoEstadoRemito,
                        EstadoDocumentacion nuevoEstadoDoc, LocalDateTime fechaHora, Empleado responsable) {
        // Transición de estado del Bolsín delegada a su estado actual o directamente al nuevo
        if (obtenerEstadoActual() != null) {
            obtenerEstadoActual().recibir(this, nuevoEstadoBolsin, fechaHora, responsable);
        } else {
            actualizarEstado(nuevoEstadoBolsin, fechaHora, responsable);
        }

        // Actualizar remitos y documentos en cascada (Corrección 2)
        actualizarARecibidosYAceptados(nuevoEstadoRemito, nuevoEstadoDoc, fechaHora, responsable);
    }

    public void recibirConAlternativas(EstadoBolsin nuevoEstadoBolsin, EstadoRemito nuevoEstadoRemito,
                                       EstadoDocumentacion estadoDocAceptada, EstadoDocumentacion estadoDocAlternativo,
                                       List<Documentacion> docsAfectados, int opcion,
                                       LocalDateTime fechaHora, Empleado responsable) {
        if (obtenerEstadoActual() != null) {
            obtenerEstadoActual().recibir(this, nuevoEstadoBolsin, fechaHora, responsable);
        } else {
            actualizarEstado(nuevoEstadoBolsin, fechaHora, responsable);
        }

        if (this.remitos != null) {
            for (Remito r : remitos) {
                r.setEstado(nuevoEstadoRemito);
                for (DetalleRemito det : r.getDetalles()) {
                    Documentacion doc = det.getDocumentacion();
                    if (doc != null) {
                        boolean esAfectado = docsAfectados != null && docsAfectados.contains(doc);
                        if (esAfectado) {
                            switch (opcion) {
                                case 2 -> doc.noRecibir(estadoDocAlternativo, fechaHora, responsable);
                                case 3 -> doc.rechazar(estadoDocAlternativo, fechaHora, responsable);
                                case 4 -> doc.redirigir(estadoDocAlternativo, fechaHora, responsable);
                                default -> doc.recibir(estadoDocAlternativo, fechaHora, responsable);
                            }
                        } else {
                            doc.recibir(estadoDocAceptada, fechaHora, responsable);
                        }
                    }
                }
            }
        }
    }

    public List<Documentacion> obtenerDocumentaciones() {
        List<Documentacion> docs = new ArrayList<>();
        if (remitos != null) {
            for (Remito r : remitos) {
                if (r.getDetalles() != null) {
                    for (DetalleRemito d : r.getDetalles()) {
                        if (d.getDocumentacion() != null) {
                            docs.add(d.getDocumentacion());
                        }
                    }
                }
            }
        }
        return docs;
    }

    public void actualizarARecibidosYAceptados(EstadoRemito nuevoEstadoRemito, EstadoDocumentacion nuevoEstadoDoc,
                                              LocalDateTime fechaHora, Empleado responsable) {
        for (Remito remito : remitos) {
            remito.recibirYAceptar(nuevoEstadoRemito, nuevoEstadoDoc, fechaHora, responsable);
        }
    }

    public List<String> obtenerInformacionRemito() {
        List<String> info = new ArrayList<>();
        for (Remito remito : remitos) {
            info.add("Remito N°: " + remito.getNumero());
            List<String> docsInfo = remito.mostrarDocumentacion();
            for (String docStr : docsInfo) {
                info.add("   - " + docStr);
            }
        }
        return info;
    }

    public String mostrarDatos() {
        return "Bolsín N° " + numeroIdentificador + " | Precinto: " + numeroPrecinto +
                " | Origen: " + (cmOrigen != null ? cmOrigen.getNombre() : "N/A") +
                " | Destino: " + (cmDestino != null ? cmDestino.getNombre() : "N/A") +
                " | Peso: " + peso + " gr.";
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getNumeroIdentificador() { return numeroIdentificador; }
    public void setNumeroIdentificador(long numeroIdentificador) { this.numeroIdentificador = numeroIdentificador; }

    public String getNumeroPrecinto() { return numeroPrecinto; }
    public void setNumeroPrecinto(String numeroPrecinto) { this.numeroPrecinto = numeroPrecinto; }

    public LocalDateTime getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(LocalDateTime fechaAlta) { this.fechaAlta = fechaAlta; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public ComisionMedica getCmOrigen() { return cmOrigen; }
    public void setCmOrigen(ComisionMedica cmOrigen) { this.cmOrigen = cmOrigen; }

    public ComisionMedica getCmDestino() { return cmDestino; }
    public void setCmDestino(ComisionMedica cmDestino) { this.cmDestino = cmDestino; }

    public List<Remito> getRemitos() { return remitos; }

    public List<CambioEstadoBolsin> getHistorialEstados() { return historialEstados; }

    public EstadoBolsin getEstadoActual() { return obtenerEstadoActual(); }
    public void setEstadoActual(EstadoBolsin estadoActual) { this.estadoActual = estadoActual; }

    @Override
    public String toString() {
        return mostrarDatos() + " [" + (obtenerEstadoActual() != null ? obtenerEstadoActual().getNombre() : "N/A") + "]";
    }
}
