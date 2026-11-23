package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.CambioEstadoDocumentacion;
import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * ESTADO CONCRETO: NoRecibida (PATRÓN GoF STATE)
 * Diagrama de Clases Grupo 8 - Rediseño State Pattern
 * ============================================================================
 * Estado de una Documentación faltante en el bolsín (Flujo A3 del CU 28).
 * 
 * ATRIBUTOS:
 * - nombre: string
 * - descripcion: string
 * 
 * OPERACIONES:
 * - + darDeBaja(): void
 * - + registrar(): void
 * - + new(): NoRecibida
 */
public class NoRecibida extends EstadoDocumentacion {

    public NoRecibida() {
        super("No Recibida", "Documentación no recibida (faltante respecto al remito).");
    }

    public NoRecibida(String nombre, String descripcion) {
        super(nombre, descripcion);
    }

    @Override
    public boolean esNoRecibido() {
        return true;
    }

    /**
     * Operación del Diagrama de Clases: darDeBaja()
     */
    @Override
    public void darDeBaja(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        if (contexto == null) return;
        LocalDateTime ahora = (fechaHora != null) ? fechaHora : LocalDateTime.now();

        CambioEstadoDocumentacion ceActual = contexto.buscarCambioEstadoActual();
        if (ceActual != null) ceActual.setFechaHoraFin(ahora);

        EstadoDocumentacion nuevo = new DeBaja();
        CambioEstadoDocumentacion nuevoCE = contexto.crearCE(ahora, responsable, nuevo);
        contexto.setEstadoActual(nuevo);
        contexto.setCambioEstado(nuevoCE);
    }

    /**
     * Operación del Diagrama de Clases: registrar()
     */
    @Override
    public void registrar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        if (contexto == null) return;
        LocalDateTime ahora = (fechaHora != null) ? fechaHora : LocalDateTime.now();

        CambioEstadoDocumentacion ceActual = contexto.buscarCambioEstadoActual();
        if (ceActual != null) ceActual.setFechaHoraFin(ahora);

        EstadoDocumentacion nuevo = new Registrada();
        CambioEstadoDocumentacion nuevoCE = contexto.crearCE(ahora, responsable, nuevo);
        contexto.setEstadoActual(nuevo);
        contexto.setCambioEstado(nuevoCE);
    }
}
