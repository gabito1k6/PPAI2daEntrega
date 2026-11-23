package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.CambioEstadoDocumentacion;
import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================================
 * ESTADO CONCRETO: ParaRedirigir (PATRÓN GoF STATE)
 * Diagrama de Clases Grupo 8 - Rediseño State Pattern
 * ============================================================================
 * Estado de una Documentación que requiere ser redirigida a otra área o CMC
 * (Flujo A5 del CU 28).
 * 
 * ATRIBUTOS:
 * - nombre: string
 * - descripcion: string
 * 
 * OPERACIONES:
 * - + recibir(): void
 * - + new(): ParaRedirigir
 */
public class ParaRedirigir extends EstadoDocumentacion {

    public ParaRedirigir() {
        super("Para Redirigir", "Documentación pendiente de redirección a otra Comisión Médica.");
    }

    public ParaRedirigir(String nombre, String descripcion) {
        super(nombre, descripcion);
    }

    @Override
    public boolean esParaRedirigir() {
        return true;
    }

    /**
     * Operación del Diagrama de Clases: recibir()
     */
    @Override
    public void recibir(Documentacion contexto, LocalDateTime fechaHora, List<CambioEstadoDocumentacion> ce, Empleado responsable) {
        if (contexto == null) return;
        LocalDateTime ahora = (fechaHora != null) ? fechaHora : LocalDateTime.now();

        CambioEstadoDocumentacion ceActual = contexto.buscarCambioEstadoActual();
        if (ceActual != null) ceActual.setFechaHoraFin(ahora);

        EstadoDocumentacion nuevo = new RecibidaYAceptada();
        CambioEstadoDocumentacion nuevoCE = contexto.crearCE(ahora, responsable, nuevo);
        contexto.setEstadoActual(nuevo);
        contexto.setCambioEstado(nuevoCE);
    }

    @Override
    public void rechazar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        if (contexto == null) return;
        LocalDateTime ahora = (fechaHora != null) ? fechaHora : LocalDateTime.now();

        CambioEstadoDocumentacion ceActual = contexto.buscarCambioEstadoActual();
        if (ceActual != null) ceActual.setFechaHoraFin(ahora);

        EstadoDocumentacion nuevo = new RecibidaYRechazada();
        CambioEstadoDocumentacion nuevoCE = contexto.crearCE(ahora, responsable, nuevo);
        contexto.setEstadoActual(nuevo);
        contexto.setCambioEstado(nuevoCE);
    }
}
