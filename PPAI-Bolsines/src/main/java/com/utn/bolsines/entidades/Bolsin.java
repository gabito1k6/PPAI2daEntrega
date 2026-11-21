package com.utn.bolsines.entidades;

import com.utn.bolsines.entidades.estados.EstadoBolsin;
import com.utn.bolsines.entidades.estados.EstadoBolsinCreado;
import com.utn.bolsines.entidades.estados.EstadoDocumentacion;
import com.utn.bolsines.entidades.estados.EstadoRemito;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * ENTIDAD: Bolsin (<<entity>>)
 * ============================================================================
 * Representa la saca o bolso de transporte físico de documentación médica
 * entre Comisiones Médicas.
 * 
 * TIPO DE RELACIONES SEGÚN EL DIAGRAMA DE CLASES (Grupo 8):
 * - Agregación / Composición (1 a 1..*) con Remito (rol: 'remito'):
 *     Un Bolsín contiene uno o más Remitos enviados conjuntamente.
 * - Composición (1 a 1..*) con CambioEstadoBolsin (rol: 'cambioEstadoBolsin'):
 *     Historial cronológico de cambios de estado del bolsín.
 * - Asociación simple (1 a 1) con ComisionMedica (rol: 'origen'):
 *     Comisión Médica remitente que envía el bolsín.
 * - Asociación simple (1 a 1) con ComisionMedica (rol: 'destino'):
 *     Comisión Médica destinataria donde se debe registrar la recepción (CU 28).
 * - Asociación con EstadoBolsin (rol: 'estadoActual'):
 *     Estado en el que se encuentra actualmente el bolsín.
 * 
 * PATRONES DE DISEÑO APLICADOS:
 * - Patrón Experto en Información (GRASP): Es la clase que conoce su destino,
 *   precinto y estado, evaluando sosEnviado() y esTuCMDestino().
 * - Cascada de actualización hacia Remito y DetalleRemito.
 */
public class Bolsin {

    private int id;
    private long numeroIdentificador; // 'numeroBolsin' en Diagrama de Clases
    private String numeroPrecinto;    // 'numeroPrecinto' / 'nroPrecinto'
    private LocalDateTime fechaAlta;  // 'fecha' en Diagrama de Clases
    private double peso;
    private ComisionMedica cmOrigen;  // Rol: 'origen'
    private ComisionMedica cmDestino; // Rol: 'destino'
    private List<Remito> remitos;     // Rol: 'remito' (1..*)
    private List<CambioEstadoBolsin> historialEstados; // Rol: 'cambioEstadoBolsin' (1..*)
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

    // =========================================================================
    // MÉTODOS DEL DIAGRAMA DE CLASES Y SECUENCIAS (GRUPO 8)
    // =========================================================================

    /**
     * Método del Diagrama de Clases y Secuencias: esTuCMDestino()
     * Verifica si la Comisión Médica pasada por parámetro coincide con el destino del Bolsín.
     */
    public boolean esTuCMDestino(ComisionMedica cm) {
        return this.cmDestino != null && this.cmDestino.equals(cm);
    }

    public boolean tenesEstaCMDestino(ComisionMedica destino) {
        return esTuCMDestino(destino);
    }

    /**
     * Método del Diagrama de Clases y Secuencias: sosEnviado()
     * Delega en su CambioEstadoBolsin actual para determinar si el estado es 'Enviado'.
     */
    public boolean sosEnviado() {
        CambioEstadoBolsin ceActual = buscarCambioEstadoActual();
        if (ceActual != null) {
            return ceActual.sosEnviado();
        }
        EstadoBolsin est = obtenerEstadoActual();
        return est != null && est.sosEnviado();
    }

    public boolean esParaCMYEstasEnviado(ComisionMedica destino) {
        return sosEnviado() && esTuCMDestino(destino);
    }

    /**
     * Método del Diagrama de Clases: getNumeroBolsin()
     */
    public long getNumeroBolsin() {
        return this.numeroIdentificador;
    }

    /**
     * Método del Diagrama de Clases y Secuencias: mostrarTuCMOrigen()
     */
    public String mostrarTuCMOrigen() {
        return (this.cmOrigen != null) ? this.cmOrigen.getNombre() : "N/A";
    }

    /**
     * Método del Diagrama de Clases y Secuencias: getNroPrecinto()
     */
    public String getNroPrecinto() {
        return this.numeroPrecinto;
    }

    /**
     * Método del Diagrama de Clases y Secuencias: obtenerRemito()
     * Retorna los remitos contenidos en el bolsín.
     */
    public List<Remito> obtenerRemito() {
        return this.remitos;
    }

    public boolean esTuCMOrigen(ComisionMedica origen) {
        return this.cmOrigen != null && this.cmOrigen.equals(origen);
    }

    // =========================================================================
    // TRAZABILIDAD Y CAMBIO DE ESTADO (Corrección 7 y Diagrama)
    // =========================================================================

    /**
     * Método del Diagrama de Clases: crearCEBolsin()
     * Cierra el cambio de estado actual y genera un nuevo CambioEstadoBolsin.
     */
    public void crearCEBolsin(EstadoBolsin nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        actualizarEstado(nuevoEstado, fechaHora, responsable);
    }

    public void actualizarEstado(EstadoBolsin nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        LocalDateTime ahora = (fechaHora != null) ? fechaHora : LocalDateTime.now();

        // 1. Cerrar el cambio de estado actual (fechaHoraFin)
        CambioEstadoBolsin ceActual = buscarCambioEstadoActual();
        if (ceActual != null) {
            ceActual.setFechaHoraFin(ahora);
        }

        // 2. Crear y agregar el nuevo cambio de estado con el responsable
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

    // =========================================================================
    // REALIZACIÓN DEL CU 28: RECEPCIÓN
    // =========================================================================

    /**
     * Método del Diagrama de Clases y Secuencias: recibir()
     * Actualiza el bolsín y propaga la recepción en cascada a sus remitos.
     */
    public void recibir(EstadoBolsin nuevoEstadoBolsin, EstadoRemito nuevoEstadoRemito,
                        EstadoDocumentacion nuevoEstadoDoc, LocalDateTime fechaHora, Empleado responsable) {
        crearCEBolsin(nuevoEstadoBolsin, fechaHora, responsable);
        actualizarARecibidosYAceptados(nuevoEstadoRemito, nuevoEstadoDoc, fechaHora, responsable);
    }

    public void recibirConAlternativas(EstadoBolsin nuevoEstadoBolsin, EstadoRemito nuevoEstadoRemito,
                                       EstadoDocumentacion estadoDocAceptada, EstadoDocumentacion estadoDocAlternativo,
                                       List<Documentacion> docsAfectados, int opcion,
                                       LocalDateTime fechaHora, Empleado responsable) {
        crearCEBolsin(nuevoEstadoBolsin, fechaHora, responsable);

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
            List<String> docsInfo = remito.obtenerInfoDocumentacion();
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
