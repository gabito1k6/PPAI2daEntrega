package com.utn.bolsines.entidades;

import com.utn.bolsines.entidades.estados.EstadoDocumentacion;
import com.utn.bolsines.entidades.estados.EstadoRegistrada;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * ENTIDAD: Documentacion (<<entity>>) - CONTEXTO DEL PATRÓN GOF STATE
 * ============================================================================
 * Representa un documento físico (expediente, pericia, estudio médico).
 * 
 * TIPO DE RELACIONES SEGÚN EL DIAGRAMA DE CLASES (Grupo 8):
 * - Contexto en el Patrón GoF STATE:
 *     Mantiene una referencia a su estado actual mediante el atributo polimórfico:
 *     'private EstadoDocumentacion estadoActual;'
 *     En lugar de usar sentencias condicionales (if/switch) según el estado,
 *     DOCUMENTACION DELEGA TODAS LAS TRANSICIONES A SU ESTADO ACTUAL.
 * - Asociación simple (1 a 1) con TipoDocumento (rol: 'tipoDocumento').
 * - Asociación simple (1 a 1) con ComisionMedica (rol: 'cmOrigen').
 * - Composición (1 a 1..*) con CambioEstadoDocumentacion (rol: 'cambioEstado'):
 *     Almacena la trazabilidad histórica de los estados por los que transitó.
 * - Composición (1 a *) con Archivo: Documentos adjuntos digitalizados.
 */
public class Documentacion {

    private int id;
    private long numeroIdentificador; // 'numero' en Diagrama de Clases
    private String asunto;            // 'asunto' en Diagrama de Clases
    private LocalDate fechaPase;      // 'fechaPase' en Diagrama de Clases
    private TipoDocumento tipoDocumento; // Rol: 'tipoDocumento' (1 a 1)
    private ComisionMedica cmOrigen;     // Rol: 'cmOrigen' (1 a 1)
    private List<Archivo> archivos;
    private List<CambioEstadoDocumentacion> historialEstados; // Rol: 'cambioEstado' (1..*)

    /**
     * Referencia al Estado Concreto actual (PATRÓN GoF STATE).
     * Apunta a una instancia de una subclase de la clase abstracta EstadoDocumentacion.
     */
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

        // Estado inicial de la Documentación (CU 7: Registrar Documentación / new())
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

    // =========================================================================
    // APLICACIÓN DEL PATRÓN GOF STATE: DELEGACIÓN AL ESTADO ACTUAL
    // =========================================================================
    // Cada uno de estos métodos representa un evento de la Máquina de Estados.
    // Documentacion (Contexto) NO decide qué hacer; DELEGA la responsabilidad
    // al objeto polimórfico 'estadoActual' pasándose a sí mismo como contexto ('this').

    /**
     * Evento CU 28: Recepción conforme de la documentación.
     * [PATRÓN STATE] DELEGA la transición a 'estadoActual.recibir(this, ...)'
     */
    public void recibir(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().recibir(this, nuevoEstado, fechaHora, responsable);
    }

    public void recibir() {
        obtenerEstadoActual().recibir(this);
    }

    /**
     * Evento CU 28: Documentación faltante (Flujo A3).
     * [PATRÓN STATE] DELEGA la transición a 'estadoActual.noRecibir(this, ...)'
     * (En la Máquina de Estados: registrarFaltaDocumentacion() -> transiciona a NoRecibida)
     */
    public void noRecibir(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().noRecibir(this, nuevoEstado, fechaHora, responsable);
    }

    public void noRecibir(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().noRecibir(this, null, fechaHora, responsable);
    }

    public void registrarFaltaDocumentacion(LocalDateTime fechaHora, Empleado responsable) {
        noRecibir(fechaHora, responsable);
    }

    /**
     * Evento CU 28: Documentación que no corresponde al destino (Flujo A4).
     * [PATRÓN STATE] DELEGA la transición a 'estadoActual.rechazar(this, ...)'
     * (En la Máquina de Estados: transiciona a RecibidaYRechazada)
     */
    public void rechazar(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().rechazar(this, nuevoEstado, fechaHora, responsable);
    }

    public void rechazar(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().rechazar(this, null, fechaHora, responsable);
    }

    /**
     * Evento CU 28: Documentación para redirigir a otra área (Flujo A5).
     * [PATRÓN STATE] DELEGA la transición a 'estadoActual.redirigir(this, ...)'
     * (En la Máquina de Estados: redirigirBolsin() -> transiciona a ParaRedirigir)
     */
    public void redirigir(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().redirigir(this, nuevoEstado, fechaHora, responsable);
    }

    public void redirigir(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().redirigir(this, null, fechaHora, responsable);
    }

    public void redirigirBolsin(LocalDateTime fechaHora, Empleado responsable) {
        redirigir(fechaHora, responsable);
    }

    /**
     * Evento CU 15 / 16: Generar / Modificar Remito.
     * [PATRÓN STATE] DELEGA la transición a 'estadoActual.registrarEnRemito(this, ...)'
     * (En la Máquina de Estados: transiciona a EnRemito)
     */
    public void registrarEnRemito(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().registrarEnRemito(this, fechaHora, responsable);
    }

    /**
     * Evento CU 19 / 20: Generar Bolsín / Modificar Bolsín.
     * [PATRÓN STATE] DELEGA la transición a 'estadoActual.agregarEnBolsin(this, ...)'
     * (En la Máquina de Estados: transiciona a EnBolsinSaliente)
     */
    public void agregarEnBolsin(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().agregarEnBolsin(this, fechaHora, responsable);
    }

    /**
     * Evento CU 27: Registrar retiro de bolsines.
     * [PATRÓN STATE] DELEGA la transición a 'estadoActual.registrarRetiro(this, ...)'
     * (En la Máquina de Estados: transiciona a EnBolsinEnviado)
     */
    public void registrarRetiro(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().registrarRetiro(this, fechaHora, responsable);
    }

    public void enviar(LocalDateTime fechaHora, Empleado responsable) {
        registrarRetiro(fechaHora, responsable);
    }

    /**
     * Evento CU 17 / 31: Cancelar Remito o Registrar Revisión.
     * [PATRÓN STATE] DELEGA la transición a 'estadoActual.registrar(this, ...)'
     * (En la Máquina de Estados: vuelve a Registrada)
     */
    public void registrar(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().registrar(this, fechaHora, responsable);
    }

    public void generar(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().generar(this, fechaHora, responsable);
    }

    /**
     * Evento CU 9: Eliminar documentación.
     * [PATRÓN STATE] DELEGA la transición a 'estadoActual.darDeBaja(this, ...)'
     * (En la Máquina de Estados: transiciona a De Baja)
     */
    public void darDeBaja(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().darDeBaja(this, fechaHora, responsable);
    }

    public void registrarBaja(LocalDateTime fechaHora, Empleado responsable) {
        darDeBaja(fechaHora, responsable);
    }

    public void darBajaBolsin(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().darBajaBolsin(this, fechaHora, responsable);
    }

    public void modificar() {
        obtenerEstadoActual().modificar(this);
    }

    // =========================================================================
    // MÉTODOS DEL DIAGRAMA DE CLASES Y SECUENCIAS (GRUPO 8)
    // =========================================================================

    /**
     * Método del Diagrama de Clases: getAsunto()
     */
    public String getAsunto() {
        return asunto;
    }

    /**
     * Método del Diagrama de Clases y Secuencias: getTipoDocumentacion()
     */
    public TipoDocumento getTipoDocumentacion() {
        return tipoDocumento;
    }

    public TipoDocumento getTipoDocumento() {
        return getTipoDocumentacion();
    }

    /**
     * Método del Diagrama de Clases y Secuencias: crearCE()
     * Registra el cambio de estado en la historia de la documentación
     * y actualiza el puntero de estadoActual (Corrección 7).
     */
    public void crearCE(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        actualizarEstado(nuevoEstado, fechaHora, responsable);
    }

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

    public void setAsunto(String asunto) { this.asunto = asunto; }

    public LocalDate getFechaPase() { return fechaPase; }
    public void setFechaPase(LocalDate fechaPase) { this.fechaPase = fechaPase; }

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
