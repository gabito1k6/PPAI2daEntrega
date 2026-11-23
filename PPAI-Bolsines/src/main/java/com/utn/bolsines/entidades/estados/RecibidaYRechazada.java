package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.CambioEstadoDocumentacion;
import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * ESTADO CONCRETO: RecibidaYRechazada (PATRÓN GoF STATE)
 * Diagrama de Clases Grupo 8 - Rediseño State Pattern
 * ============================================================================
 * Estado de una Documentación que no corresponde al destino (Flujo A4 del CU 28).
 * 
 * ATRIBUTOS:
 * - nombre: string
 * - descripcion: string
 * 
 * OPERACIONES:
 * - + registrar(): void
 * - + new(): RecibidaYRechazada
 */
public class RecibidaYRechazada extends EstadoDocumentacion {

    public RecibidaYRechazada() {
        super("Recibida y Rechazada", "Documentación rechazada por no corresponder a la Comisión Médica destino.");
    }

    public RecibidaYRechazada(String nombre, String descripcion) {
        super(nombre, descripcion);
    }

    @Override
    public boolean esRecibidaYRechazada() {
        return true;
    }

    /**
     * Operación del Diagrama de Clases: registrar()
     * Permite revisiones posteriores según máquina de estados.
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
