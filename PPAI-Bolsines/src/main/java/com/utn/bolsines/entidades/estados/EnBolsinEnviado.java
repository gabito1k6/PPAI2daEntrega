package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.CambioEstadoDocumentacion;
import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================================
 * ESTADO CONCRETO: EnBolsinEnviado (PATRÓN GoF STATE)
 * Diagrama de Clases y Diagrama de Secuencia Grupo 8 - Rediseño State Pattern
 * ============================================================================
 * Estado activo de la Documentación al llegar a la Comisión Médica en el CU 28.
 * 
 * ATRIBUTOS:
 * - nombre: string
 * - descripcion: string
 * 
 * OPERACIONES SEGÚN EL DIAGRAMA DE CLASES:
 * - + new(): EnBolsinEnviado
 * - + recibir(this: Documentacion, fechaHora: datetime, ce: CambioEstado[], responsable: Empleado): void
 * - + buscarCambioEstadoActual(): CambioEstado
 * - + crearRecibidaAceptada(): Estado
 * - + crearCE(fechaHoraInicio: datetime, responsable: Empleado, estado: EstadoDocumentacion): void
 * 
 * SECUENCIA EXACTA EN EL DIAGRAMA DE SECUENCIA (GRUPO 8):
 * 1. Documentacion -> recibir(this, fechaHora, ce, responsable) hacia :EnBolsinEnviado
 * 2. :EnBolsinEnviado -> buscarCambioEstadoActual() sobre Documentacion
 * 3. :EnBolsinEnviado -> actual:CambioEstadoDocumentacion.setFechaHoraFin(fechaHora)
 * 4. :EnBolsinEnviado -> crearRecibidaAceptada(): Estado  (ejecuta new RecibidaYAceptada())
 * 5. :EnBolsinEnviado -> Documentacion.crearCE(fechaHora, responsable, estado)
 * 6. :EnBolsinEnviado -> Documentacion.setEstadoActual(estado)
 * 7. :EnBolsinEnviado -> Documentacion.setCambioEstado(ce)
 */
public class EnBolsinEnviado extends EstadoDocumentacion {

    public EnBolsinEnviado() {
        super("En Bolsín Enviado", "Documentación enviada en bolsín pendiente de recepción.");
    }

    public EnBolsinEnviado(String nombre, String descripcion) {
        super(nombre, descripcion);
    }

    @Override
    public boolean sosEnviado() {
        return true;
    }

    /**
     * [PATRÓN STATE - FLUJO BÁSICO CU 28]
     * Implementación exacta del Diagrama de Secuencias (Patrón State Grupo 8).
     */
    @Override
    public void recibir(Documentacion contexto, LocalDateTime fechaHora, List<CambioEstadoDocumentacion> ce, Empleado responsable) {
        if (contexto == null) return;
        LocalDateTime ahora = (fechaHora != null) ? fechaHora : LocalDateTime.now();

        // 1. :EnBolsinEnviado pide el cambio de estado actual al contexto
        CambioEstadoDocumentacion ceActual = contexto.buscarCambioEstadoActual();

        // 2. :EnBolsinEnviado cierra el cambio de estado actual
        if (ceActual != null) {
            ceActual.setFechaHoraFin(ahora);
        }

        // 3. :EnBolsinEnviado crea el nuevo estado invocando crearRecibidaAceptada() (new RecibidaYAceptada())
        EstadoDocumentacion nuevoEstado = crearRecibidaAceptada();

        // 4. :EnBolsinEnviado solicita al contexto crear el nuevo CambioEstadoDocumentacion
        CambioEstadoDocumentacion nuevoCE = contexto.crearCE(ahora, responsable, nuevoEstado);

        // 5. :EnBolsinEnviado actualiza el estado actual en el contexto
        contexto.setEstadoActual(nuevoEstado);

        // 6. :EnBolsinEnviado registra el nuevo CambioEstado en el historial del contexto
        contexto.setCambioEstado(nuevoCE);
    }

    /**
     * Método del Diagrama de Clases: buscarCambioEstadoActual()
     */
    public CambioEstadoDocumentacion buscarCambioEstadoActual(Documentacion contexto) {
        return (contexto != null) ? contexto.buscarCambioEstadoActual() : null;
    }

    /**
     * Método del Diagrama de Clases y Secuencias: crearRecibidaAceptada(): Estado
     * Instancia el nuevo estado concreto sucesor (Patrón State).
     */
    public EstadoDocumentacion crearRecibidaAceptada() {
        return new RecibidaYAceptada();
    }

    /**
     * Alias por nomenclatura con "Y": crearRecibidaYAceptada()
     */
    public EstadoDocumentacion crearRecibidaYAceptada() {
        return crearRecibidaAceptada();
    }

    /**
     * Método del Diagrama de Clases: crearCE(fechaHoraInicio, responsable, estado): void
     */
    public void crearCE(Documentacion contexto, LocalDateTime fechaHoraInicio, Empleado responsable, EstadoDocumentacion estado) {
        if (contexto != null) {
            CambioEstadoDocumentacion nuevoCE = contexto.crearCE(fechaHoraInicio, responsable, estado);
            contexto.setEstadoActual(estado);
            contexto.setCambioEstado(nuevoCE);
        }
    }

    // =========================================================================
    // TRANSICIONES DE FLUJOS ALTERNATIVOS SEGÚN MÁQUINA DE ESTADOS (GRUPO 8)
    // =========================================================================

    /**
     * Flujo Alternativo A3: registrarFaltaDocumentacion -> transiciona a NoRecibida
     */
    @Override
    public void registrarFaltaDocumentacion(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        if (contexto == null) return;
        LocalDateTime ahora = (fechaHora != null) ? fechaHora : LocalDateTime.now();

        CambioEstadoDocumentacion ceActual = contexto.buscarCambioEstadoActual();
        if (ceActual != null) {
            ceActual.setFechaHoraFin(ahora);
        }

        EstadoDocumentacion nuevoEstado = new NoRecibida();
        CambioEstadoDocumentacion nuevoCE = contexto.crearCE(ahora, responsable, nuevoEstado);
        contexto.setEstadoActual(nuevoEstado);
        contexto.setCambioEstado(nuevoCE);
    }

    /**
     * Flujo Alternativo A4: rechazar -> transiciona a RecibidaYRechazada
     */
    @Override
    public void rechazar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        if (contexto == null) return;
        LocalDateTime ahora = (fechaHora != null) ? fechaHora : LocalDateTime.now();

        CambioEstadoDocumentacion ceActual = contexto.buscarCambioEstadoActual();
        if (ceActual != null) {
            ceActual.setFechaHoraFin(ahora);
        }

        EstadoDocumentacion nuevoEstado = new RecibidaYRechazada();
        CambioEstadoDocumentacion nuevoCE = contexto.crearCE(ahora, responsable, nuevoEstado);
        contexto.setEstadoActual(nuevoEstado);
        contexto.setCambioEstado(nuevoCE);
    }

    /**
     * Flujo Alternativo A5: redirigirBolsin -> transiciona a ParaRedirigir
     */
    @Override
    public void redirigirBolsin(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        if (contexto == null) return;
        LocalDateTime ahora = (fechaHora != null) ? fechaHora : LocalDateTime.now();

        CambioEstadoDocumentacion ceActual = contexto.buscarCambioEstadoActual();
        if (ceActual != null) {
            ceActual.setFechaHoraFin(ahora);
        }

        EstadoDocumentacion nuevoEstado = new ParaRedirigir();
        CambioEstadoDocumentacion nuevoCE = contexto.crearCE(ahora, responsable, nuevoEstado);
        contexto.setEstadoActual(nuevoEstado);
        contexto.setCambioEstado(nuevoCE);
    }
}
