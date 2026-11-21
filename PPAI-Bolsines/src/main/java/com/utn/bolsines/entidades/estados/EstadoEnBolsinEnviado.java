package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * ESTADO CONCRETO: EstadoEnBolsinEnviado (PATRÓN GoF STATE)
 * ============================================================================
 * Representa el estado inicial de la Documentación al llegar a la Comisión Médica
 * en el CU 28: Registrar Recepción de Bolsín.
 * 
 * TIPO DE RELACIÓN Y HERENCIA:
 * - HEREDA ('extends') de la clase abstracta 'EstadoDocumentacion'.
 * - Es un Estado Concreto participante en el Patrón GoF State.
 * 
 * RESPONSABILIDAD DELEGADA (PATRÓN STATE):
 * Al ejecutarse el CU 28, este estado es el encargado de procesar los eventos
 * de recepción y transicionar el contexto 'Documentacion':
 * 1. Flujo Básico: recibir() -> Transiciona a 'EstadoRecibidaYAceptada'.
 * 2. Flujo A3 (Falta): noRecibir() / registrarFaltaDocumentacion() -> Transiciona a 'EstadoNoRecibido'.
 * 3. Flujo A4 (Rechazo): rechazar() -> Transiciona a 'EstadoRecibidaYRechazada'.
 * 4. Flujo A5 (Redirección): redirigir() / redirigirBolsin() -> Transiciona a 'EstadoParaRedirigir'.
 */
public class EstadoEnBolsinEnviado extends EstadoDocumentacion {

    @Override
    public String getNombre() {
        return "En Bolsín Enviado";
    }

    @Override
    public boolean sosEnviado() {
        return true;
    }

    /**
     * [PATRÓN STATE] Implementación de la transición del Flujo Básico:
     * El contenido llega conforme y la documentación pasa a 'Recibida y Aceptada'.
     * Cierra el CambioEstado anterior y crea el nuevo con el responsable.
     */
    @Override
    public void recibir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        EstadoDocumentacion destino = (nuevoEstado != null) ? nuevoEstado : new EstadoRecibidaYAceptada();
        // [PATRÓN STATE] El estado actual delega en el contexto para crear el nuevo CE y actualizar estadoActual
        contexto.crearCE(destino, fechaHora, responsable);
    }

    /**
     * [PATRÓN STATE] Implementación de la transición del Flujo Alternativo A3:
     * Documentación faltante asociada al remito. Transiciona a 'No Recibido'.
     */
    @Override
    public void noRecibir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        EstadoDocumentacion destino = (nuevoEstado != null) ? nuevoEstado : new EstadoNoRecibido();
        contexto.crearCE(destino, fechaHora, responsable);
    }

    @Override
    public void registrarFaltaDocumentacion(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        noRecibir(contexto, null, fechaHora, responsable);
    }

    /**
     * [PATRÓN STATE] Implementación de la transición del Flujo Alternativo A4:
     * Documentación que no corresponde a la CM destino. Transiciona a 'Recibida y Rechazada'.
     */
    @Override
    public void rechazar(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        EstadoDocumentacion destino = (nuevoEstado != null) ? nuevoEstado : new EstadoRecibidaYRechazada();
        contexto.crearCE(destino, fechaHora, responsable);
    }

    @Override
    public void rechazar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        rechazar(contexto, new EstadoRecibidaYRechazada(), fechaHora, responsable);
    }

    /**
     * [PATRÓN STATE] Implementación de la transición del Flujo Alternativo A5:
     * Documentación a redirigir a otra área (ej: Comisión Médica Central).
     * Transiciona a 'Para Redirigir'.
     */
    @Override
    public void redirigir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        EstadoDocumentacion destino = (nuevoEstado != null) ? nuevoEstado : new EstadoParaRedirigir();
        contexto.crearCE(destino, fechaHora, responsable);
    }

    @Override
    public void redirigir(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        redirigir(contexto, new EstadoParaRedirigir(), fechaHora, responsable);
    }

    @Override
    public void redirigirBolsin(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        redirigir(contexto, fechaHora, responsable);
    }
}
