package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * CLASE ABSTRACTA: EstadoDocumentacion (PATRÓN GoF STATE)
 * ============================================================================
 * Define la interfaz de comportamiento para la máquina de estados de la entidad
 * Documentacion (Contexto), de acuerdo a la Corrección 3 de DSI.
 * 
 * TIPO DE RELACIÓN Y HERENCIA:
 * - Es una CLASE ABSTRACTA ('abstract class').
 * - Implementa ('implements') la interfaz polimórfica 'Estado'.
 * - Es la superclase de la que HEREDAN ('extends') todos los Estados Concretos:
 *     * EstadoRegistrada
 *     * EstadoEnRemito
 *     * EstadoEnBolsinSaliente
 *     * EstadoEnBolsinEnviado (Estado activo en CU 28)
 *     * EstadoRecibidaYAceptada (Flujo Básico CU 28)
 *     * EstadoNoRecibido (Flujo Alternativo A3)
 *     * EstadoRecibidaYRechazada (Flujo Alternativo A4)
 *     * EstadoParaRedirigir (Flujo Alternativo A5)
 *     * EstadoEnBaja
 * 
 * MECANISMO DE CONTROL DE TRANSICIONES:
 * Por defecto, todos los métodos lanzan UnsupportedOperationException.
 * Cada Estado Concreto SOBREESCRIBE (@Override) únicamente las transiciones
 * válidas según el diagrama de la Máquina de Estados (Grupo 8).
 */
public abstract class EstadoDocumentacion implements Estado {

    @Override
    public abstract String getNombre();

    @Override
    public boolean esAmbitoDocumentacion() {
        return true;
    }

    // =========================================================================
    // MÉTODOS DEL PATRÓN STATE (TRANSICIONES DE LA MÁQUINA DE ESTADOS)
    // =========================================================================

    /**
     * Evento CU 28: Recepción conforme de la documentación.
     * En 'EstadoEnBolsinEnviado' o 'EstadoParaRedirigir': transiciona a 'EstadoRecibidaYAceptada'.
     */
    public void recibir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("recibir");
    }

    public void recibir(Documentacion contexto) {
        recibir(contexto, new EstadoRecibidaYAceptada(), LocalDateTime.now(), null);
    }

    /**
     * Evento CU 28 (Flujo A3): Documentación faltante.
     * En 'EstadoEnBolsinEnviado': transiciona a 'EstadoNoRecibido'.
     * Máquina de Estados: registrarFaltaDocumentacion().
     */
    public void noRecibir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("noRecibir / registrarFaltaDocumentacion");
    }

    public void registrarFaltaDocumentacion(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        noRecibir(contexto, null, fechaHora, responsable);
    }

    /**
     * Evento CU 28 (Flujo A4): Documentación que no corresponde al destino.
     * En 'EstadoEnBolsinEnviado' o 'EstadoParaRedirigir': transiciona a 'EstadoRecibidaYRechazada'.
     */
    public void rechazar(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("rechazar");
    }

    public void rechazar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        rechazar(contexto, null, fechaHora, responsable);
    }

    /**
     * Evento CU 28 (Flujo A5): Documentación para redirigir a otra área (CMC).
     * En 'EstadoEnBolsinEnviado': transiciona a 'EstadoParaRedirigir'.
     * Máquina de Estados: redirigirBolsin().
     */
    public void redirigir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("redirigir / redirigirBolsin");
    }

    public void redirigir(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        redirigir(contexto, null, fechaHora, responsable);
    }

    public void redirigirBolsin(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        redirigir(contexto, null, fechaHora, responsable);
    }

    /**
     * Evento CU 15 / 16: Generar o Modificar Remito.
     * En 'EstadoRegistrada' o 'EstadoEnBolsinSaliente': transiciona a 'EstadoEnRemito'.
     */
    public void registrarEnRemito(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("registrarEnRemito");
    }

    /**
     * Evento CU 19 / 20: Generar o Modificar Bolsín.
     * En 'EstadoEnRemito': transiciona a 'EstadoEnBolsinSaliente'.
     */
    public void agregarEnBolsin(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("agregarEnBolsin");
    }

    /**
     * Evento CU 27: Registrar retiro de bolsines.
     * En 'EstadoEnBolsinSaliente': transiciona a 'EstadoEnBolsinEnviado'.
     */
    public void registrarRetiro(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("registrarRetiro / enviar");
    }

    public void enviar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        registrarRetiro(contexto, fechaHora, responsable);
    }

    /**
     * Evento CU 17 / 31: Cancelar Remito o Registrar Revisión.
     * En 'EstadoEnRemito' o 'EstadoNoRecibido': vuelve a 'EstadoRegistrada'.
     */
    public void registrar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("registrar");
    }

    public void generar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("generar");
    }

    /**
     * Evento CU 9: Eliminar documentación.
     * En 'EstadoRegistrada' o 'EstadoNoRecibido': transiciona a 'EstadoEnBaja'.
     */
    public void darDeBaja(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("darDeBaja / registrarBaja");
    }

    public void registrarBaja(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        darDeBaja(contexto, fechaHora, responsable);
    }

    public void modificar(Documentacion contexto) {
        lanzarTransicionNoValida("modificar");
    }

    public void darBajaBolsin(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("darBajaBolsin");
    }

    private void lanzarTransicionNoValida(String evento) {
        throw new UnsupportedOperationException(
                "Transición '" + evento + "' no válida desde el estado actual: [" + getNombre() + "].");
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
