package com.utn.bolsines.entidades;

import com.utn.bolsines.entidades.estados.EstadoDocumentacion;
import com.utn.bolsines.entidades.estados.Registrada;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * ENTIDAD: Documentacion (<<entity>>) - CONTEXTO DEL PATRÓN GOF STATE
 * Diagrama de Clases y Diagrama de Secuencia Grupo 8 - Rediseño State Pattern
 * ============================================================================
 * Representa un documento físico (expediente, pericia, estudio médico).
 * 
 * ATRIBUTOS SEGÚN DIAGRAMA DE CLASES:
 * - asunto: string
 * - fechaPase: date
 * - numero: int
 * 
 * OPERACIONES SEGÚN DIAGRAMA DE CLASES:
 * - + registrar(): void
 * - + registrarEnRemito(): void
 * - + agregarEnBolsin(): void
 * - + modificar(): void
 * - + darDeBaja(): void
 * - + recibir(this: Documentacion, fechaHora: datetime, ce: CambioEstado[], responsable: Empleado): void
 * - + registrarFaltaDocumentacion(): void
 * - + redirigirBolsin(): void
 * - + registrarRetiro(): void
 * - + setEstadoActual(estado: EstadoDocumentacion): void
 * - + setCambioEstado(CE: CambioEstadoDocumentacion): void
 * 
 * PATRÓN GOF STATE (GRUPO 8):
 * - Documentación actúa como CONTEXTO.
 * - Mantiene referencia polimórfica a su 'estadoActual: EstadoDocumentacion'.
 * - DELEGA las transiciones a su 'estadoActual'.
 * - El estado actual orquesta la creación del nuevo estado, cierre del CE anterior
 *   y asignación del nuevo estado mediante setEstadoActual() y setCambioEstado().
 */
public class Documentacion {

    private int id;
    private long numeroIdentificador; // 'numero: int' en Diagrama de Clases
    private String asunto;            // 'asunto: string' en Diagrama de Clases
    private LocalDate fechaPase;      // 'fechaPase: date' en Diagrama de Clases
    private TipoDocumento tipoDocumento;
    private ComisionMedica cmOrigen;
    private List<Archivo> archivos;
    private List<CambioEstadoDocumentacion> historialEstados; // Rol: 'cambioEstado' (1..*)

    /**
     * Referencia al Estado Concreto actual (PATRÓN GoF STATE).
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
            estadoInicial = new Registrada();
        }
        crearPrimerEstado(estadoInicial, LocalDateTime.now(), responsable);
    }

    private void crearPrimerEstado(EstadoDocumentacion estadoInicial, LocalDateTime fechaHora, Empleado responsable) {
        this.estadoActual = estadoInicial;
        CambioEstadoDocumentacion primerCE = new CambioEstadoDocumentacion(fechaHora, responsable, estadoInicial);
        this.historialEstados.add(primerCE);
    }

    // =========================================================================
    // MÉTODOS DEL PATRÓN GOF STATE SEGÚN DIAGRAMA DE SECUENCIA Y CLASES
    // =========================================================================

    /**
     * Invocado por DetalleRemito en el CU 28:
     * recibir(fechaHora: datetime, responsable: Empleado): void
     * [PATRÓN STATE] Delega en estadoActual.recibir(this, fechaHora, ce, responsable)
     */
    public void recibir(LocalDateTime fechaHora, Empleado responsable) {
        recibir(this, fechaHora, this.historialEstados, responsable);
    }

    /**
     * Operación del Diagrama de Clases y Secuencias:
     * recibir(this: Documentacion, fechaHora: datetime, ce: CambioEstado[], responsable: Empleado): void
     * Delega la transición al estado actual polimórfico.
     */
    public void recibir(Documentacion contexto, LocalDateTime fechaHora, List<CambioEstadoDocumentacion> ce, Empleado responsable) {
        obtenerEstadoActual().recibir(contexto != null ? contexto : this, fechaHora, ce, responsable);
    }

    public void recibir(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        recibir(this, fechaHora, this.historialEstados, responsable);
    }

    public void recibir() {
        recibir(LocalDateTime.now(), null);
    }

    /**
     * Operación del Diagrama de Secuencias: buscarCambioEstadoActual(): CambioEstado
     * Invocada por el estado actual (:EnBolsinEnviado) para obtener el cambio de estado vigente.
     */
    public CambioEstadoDocumentacion buscarCambioEstadoActual() {
        for (CambioEstadoDocumentacion ce : historialEstados) {
            if (ce.sosActual()) {
                return ce;
            }
        }
        return null;
    }

    /**
     * Operación del Diagrama de Secuencias:
     * crearCE(fechaHoraInicio: datetime, responsable: Empleado, estado: EstadoDocumentacion)
     * Invocada por el estado actual (:EnBolsinEnviado) para generar el nuevo CambioEstadoDocumentacion.
     */
    public CambioEstadoDocumentacion crearCE(LocalDateTime fechaHoraInicio, Empleado responsable, EstadoDocumentacion estado) {
        return new CambioEstadoDocumentacion(fechaHoraInicio, responsable, estado);
    }

    public void crearCE(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        actualizarEstado(nuevoEstado, fechaHora, responsable);
    }

    /**
     * Operación del Diagrama de Clases y Secuencias:
     * setEstadoActual(estado: EstadoDocumentacion): void
     * Invocada por :EnBolsinEnviado para fijar el nuevo estado.
     */
    public void setEstadoActual(EstadoDocumentacion estadoActual) {
        this.estadoActual = estadoActual;
    }

    /**
     * Operación del Diagrama de Clases y Secuencias:
     * setCambioEstado(CE: CambioEstadoDocumentacion): void
     * Invocada por :EnBolsinEnviado para agregar el nuevo cambio de estado al historial.
     */
    public void setCambioEstado(CambioEstadoDocumentacion CE) {
        if (CE != null && !this.historialEstados.contains(CE)) {
            this.historialEstados.add(CE);
        }
    }

    // =========================================================================
    // OTRAS OPERACIONES DE LA MÁQUINA DE ESTADOS (GRUPO 8)
    // =========================================================================

    /**
     * Operación del Diagrama de Clases: registrarFaltaDocumentacion()
     */
    public void registrarFaltaDocumentacion(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().registrarFaltaDocumentacion(this, fechaHora, responsable);
    }

    public void registrarFaltaDocumentacion() {
        registrarFaltaDocumentacion(LocalDateTime.now(), null);
    }

    public void noRecibir(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        registrarFaltaDocumentacion(fechaHora, responsable);
    }

    public void noRecibir(LocalDateTime fechaHora, Empleado responsable) {
        registrarFaltaDocumentacion(fechaHora, responsable);
    }

    /**
     * Operación del Diagrama de Clases: redirigirBolsin()
     */
    public void redirigirBolsin(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().redirigirBolsin(this, fechaHora, responsable);
    }

    public void redirigirBolsin() {
        redirigirBolsin(LocalDateTime.now(), null);
    }

    public void redirigir(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        redirigirBolsin(fechaHora, responsable);
    }

    public void redirigir(LocalDateTime fechaHora, Empleado responsable) {
        redirigirBolsin(fechaHora, responsable);
    }

    /**
     * Rechazar documentación que no corresponde (Flujo A4)
     */
    public void rechazar(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().rechazar(this, fechaHora, responsable);
    }

    public void rechazar(LocalDateTime fechaHora, Empleado responsable) {
        rechazar(null, fechaHora, responsable);
    }

    /**
     * Operación del Diagrama de Clases: registrar()
     */
    public void registrar(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().registrar(this, fechaHora, responsable);
    }

    public void registrar() {
        registrar(LocalDateTime.now(), null);
    }

    /**
     * Operación del Diagrama de Clases: registrarEnRemito()
     */
    public void registrarEnRemito(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().registrarEnRemito(this, fechaHora, responsable);
    }

    public void registrarEnRemito() {
        registrarEnRemito(LocalDateTime.now(), null);
    }

    /**
     * Operación del Diagrama de Clases: agregarEnBolsin()
     */
    public void agregarEnBolsin(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().agregarEnBolsin(this, fechaHora, responsable);
    }

    public void agregarEnBolsin() {
        agregarEnBolsin(LocalDateTime.now(), null);
    }

    /**
     * Operación del Diagrama de Clases: modificar()
     */
    public void modificar() {
        obtenerEstadoActual().modificar(this);
    }

    /**
     * Operación del Diagrama de Clases: darDeBaja()
     */
    public void darDeBaja(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().darDeBaja(this, fechaHora, responsable);
    }

    public void darDeBaja() {
        darDeBaja(LocalDateTime.now(), null);
    }

    /**
     * Operación del Diagrama de Clases: registrarRetiro()
     */
    public void registrarRetiro(LocalDateTime fechaHora, Empleado responsable) {
        obtenerEstadoActual().registrarRetiro(this, fechaHora, responsable);
    }

    public void registrarRetiro() {
        registrarRetiro(LocalDateTime.now(), null);
    }

    public void enviar(LocalDateTime fechaHora, Empleado responsable) {
        registrarRetiro(fechaHora, responsable);
    }

    // =========================================================================
    // MÉTODOS AUXILIARES Y GETTERS / SETTERS
    // =========================================================================

    public void actualizarEstado(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        LocalDateTime ahora = (fechaHora != null) ? fechaHora : LocalDateTime.now();
        CambioEstadoDocumentacion ceActual = buscarCambioEstadoActual();
        if (ceActual != null) {
            ceActual.setFechaHoraFin(ahora);
        }
        CambioEstadoDocumentacion nuevoCE = crearCE(ahora, responsable, nuevoEstado);
        setEstadoActual(nuevoEstado);
        setCambioEstado(nuevoCE);
    }

    public void actualizarEstado(EstadoDocumentacion nuevoEstado) {
        actualizarEstado(nuevoEstado, LocalDateTime.now(), null);
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

    public EstadoDocumentacion getEstadoActual() {
        return obtenerEstadoActual();
    }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public LocalDate getFechaPase() { return fechaPase; }
    public void setFechaPase(LocalDate fechaPase) { this.fechaPase = fechaPase; }

    public long getNumero() { return numeroIdentificador; }
    public void setNumero(long numero) { this.numeroIdentificador = numero; }

    public long getNumeroIdentificador() { return numeroIdentificador; }
    public void setNumeroIdentificador(long numeroIdentificador) { this.numeroIdentificador = numeroIdentificador; }

    public TipoDocumento getTipoDocumentacion() { return tipoDocumento; }
    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public ComisionMedica getCmOrigen() { return cmOrigen; }
    public void setCmOrigen(ComisionMedica cmOrigen) { this.cmOrigen = cmOrigen; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public List<Archivo> getArchivos() { return archivos; }
    public void agregarArchivo(Archivo archivo) { this.archivos.add(archivo); }

    public List<CambioEstadoDocumentacion> getHistorialEstados() { return historialEstados; }

    public String mostrarDocumentacion() {
        String tipoStr = (tipoDocumento != null) ? tipoDocumento.getNombre() : "Sin tipo";
        return "Doc #" + numeroIdentificador + " | Tipo: " + tipoStr + " | Asunto: " + asunto;
    }

    @Override
    public String toString() {
        return mostrarDocumentacion() + " [" + (obtenerEstadoActual() != null ? obtenerEstadoActual().getNombre() : "Sin Estado") + "]";
    }
}
