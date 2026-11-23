package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.CambioEstadoDocumentacion;
import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * ESTADO CONCRETO: Registrada (PATRÓN GoF STATE)
 * Diagrama de Clases Grupo 8 - Rediseño State Pattern
 * ============================================================================
 * Estado inicial de una Documentación tras ser creada (CU 7: Registrar Documentación / new()).
 * 
 * ATRIBUTOS:
 * - nombre: string
 * - descripcion: string
 * 
 * OPERACIONES:
 * - + registrarEnRemito(): void
 * - + darDeBaja(): void
 * - + new(): Registrada
 */
public class Registrada extends EstadoDocumentacion {

    public Registrada() {
        super("Registrada", "Documentación dada de alta y registrada en el sistema.");
    }

    public Registrada(String nombre, String descripcion) {
        super(nombre, descripcion);
    }

    /**
     * Operación del Diagrama de Clases: registrarEnRemito()
     * CU 15: Generar Remito -> transiciona a EnRemito
     */
    @Override
    public void registrarEnRemito(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        if (contexto == null) return;
        LocalDateTime ahora = (fechaHora != null) ? fechaHora : LocalDateTime.now();

        CambioEstadoDocumentacion ceActual = contexto.buscarCambioEstadoActual();
        if (ceActual != null) ceActual.setFechaHoraFin(ahora);

        EstadoDocumentacion nuevo = new EnRemito();
        CambioEstadoDocumentacion nuevoCE = contexto.crearCE(ahora, responsable, nuevo);
        contexto.setEstadoActual(nuevo);
        contexto.setCambioEstado(nuevoCE);
    }

    /**
     * Operación del Diagrama de Clases: darDeBaja()
     * CU 9: Eliminar documentación -> transiciona a DeBaja
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
}
