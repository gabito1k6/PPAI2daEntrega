package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.CambioEstadoDocumentacion;
import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================================
 * CLASE ABSTRACTA: EstadoDocumentacion (PATRÓN GoF STATE)
 * Diagrama de Clases Grupo 8 - Rediseño State Pattern
 * ============================================================================
 * Define el contrato y comportamiento por defecto de la máquina de estados
 * para la entidad Documentacion (Contexto).
 * 
 * ATRIBUTOS SEGÚN DIAGRAMA DE CLASES:
 * - nombre: string
 * - descripcion: string
 * 
 * OPERACIONES SEGÚN DIAGRAMA DE CLASES:
 * - registrar(): void
 * - registrarEnRemito(): void
 * - agregarEnBolsin(): void
 * - modificar(): void
 * - darDeBaja(): void
 * - recibir(this: Documentacion, fechaHora: datetime, ce: CambioEstado[], responsable: Empleado): void
 * - registrarFaltaDocumentacion(): void
 * - redirigirBolsin(): void
 * - registrarRetiro(): void
 */
public abstract class EstadoDocumentacion implements Estado {

    protected String nombre;
    protected String descripcion;

    public EstadoDocumentacion() {}

    public EstadoDocumentacion(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String getDescripcion() {
        return descripcion != null ? descripcion : nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean esAmbitoDocumentacion() {
        return true;
    }

    // =========================================================================
    // MÉTODOS DEL PATRÓN GOF STATE (SEGÚN DIAGRAMA DE CLASES GRUPO 8)
    // =========================================================================

    /**
     * Operación del Diagrama de Clases:
     * recibir(this: Documentacion, fechaHora: datetime, ce: CambioEstado[], responsable: Empleado): void
     */
    public void recibir(Documentacion contexto, LocalDateTime fechaHora, List<CambioEstadoDocumentacion> ce, Empleado responsable) {
        lanzarTransicionNoValida("recibir");
    }

    public void recibir(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        recibir(contexto, fechaHora, contexto != null ? contexto.getHistorialEstados() : null, responsable);
    }

    public void recibir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        recibir(contexto, fechaHora, contexto != null ? contexto.getHistorialEstados() : null, responsable);
    }

    public void recibir(Documentacion contexto) {
        recibir(contexto, LocalDateTime.now(), contexto != null ? contexto.getHistorialEstados() : null, null);
    }

    public void recibir() {
        lanzarTransicionNoValida("recibir");
    }

    /**
     * Operación del Diagrama de Clases: registrarFaltaDocumentacion()
     */
    public void registrarFaltaDocumentacion(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("registrarFaltaDocumentacion");
    }

    public void registrarFaltaDocumentacion() {
        lanzarTransicionNoValida("registrarFaltaDocumentacion");
    }

    public void noRecibir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        registrarFaltaDocumentacion(contexto, fechaHora, responsable);
    }

    /**
     * Operación del Diagrama de Clases: redirigirBolsin()
     */
    public void redirigirBolsin(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("redirigirBolsin");
    }

    public void redirigirBolsin() {
        lanzarTransicionNoValida("redirigirBolsin");
    }

    public void redirigir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        redirigirBolsin(contexto, fechaHora, responsable);
    }

    /**
     * Operación del Diagrama de Clases: registrar()
     */
    public void registrar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("registrar");
    }

    public void registrar() {
        lanzarTransicionNoValida("registrar");
    }

    /**
     * Operación del Diagrama de Clases: registrarEnRemito()
     */
    public void registrarEnRemito(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("registrarEnRemito");
    }

    public void registrarEnRemito() {
        lanzarTransicionNoValida("registrarEnRemito");
    }

    /**
     * Operación del Diagrama de Clases: agregarEnBolsin()
     */
    public void agregarEnBolsin(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("agregarEnBolsin");
    }

    public void agregarEnBolsin() {
        lanzarTransicionNoValida("agregarEnBolsin");
    }

    /**
     * Operación del Diagrama de Clases: modificar()
     */
    public void modificar(Documentacion contexto) {
        lanzarTransicionNoValida("modificar");
    }

    public void modificar() {
        lanzarTransicionNoValida("modificar");
    }

    /**
     * Operación del Diagrama de Clases: darDeBaja()
     */
    public void darDeBaja(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("darDeBaja");
    }

    public void darDeBaja() {
        lanzarTransicionNoValida("darDeBaja");
    }

    public void registrarBaja(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        darDeBaja(contexto, fechaHora, responsable);
    }

    /**
     * Operación del Diagrama de Clases: registrarRetiro()
     */
    public void registrarRetiro(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("registrarRetiro");
    }

    public void registrarRetiro() {
        lanzarTransicionNoValida("registrarRetiro");
    }

    public void enviar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        registrarRetiro(contexto, fechaHora, responsable);
    }

    public void rechazar(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("rechazar");
    }

    public void rechazar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        rechazar(contexto, null, fechaHora, responsable);
    }

    public void generar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        registrar(contexto, fechaHora, responsable);
    }

    public void darBajaBolsin(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        darDeBaja(contexto, fechaHora, responsable);
    }

    protected void lanzarTransicionNoValida(String evento) {
        throw new UnsupportedOperationException(
                "Transición '" + evento + "' no válida desde el estado actual: [" + getNombre() + "].");
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
