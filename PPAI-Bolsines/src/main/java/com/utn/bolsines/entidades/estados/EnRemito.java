package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.CambioEstadoDocumentacion;
import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * ESTADO CONCRETO: EnRemito (PATRÓN GoF STATE)
 * Diagrama de Clases Grupo 8 - Rediseño State Pattern
 * ============================================================================
 * Estado de una Documentación incluida dentro de un Remito emitido.
 * 
 * ATRIBUTOS:
 * - nombre: string
 * - descripcion: string
 * 
 * OPERACIONES:
 * - + registrarEnRemito(): void
 * - + registrar(): void
 * - + agregarEnBolsin(): void
 * - + new(): EnRemito
 */
public class EnRemito extends EstadoDocumentacion {

    public EnRemito() {
        super("En Remito", "Documentación asociada a un remito de entrega.");
    }

    public EnRemito(String nombre, String descripcion) {
        super(nombre, descripcion);
    }

    /**
     * Operación del Diagrama de Clases: agregarEnBolsin()
     * CU 19/20: Generar/Modificar Bolsín -> transiciona a EnBolsinSaliente
     */
    @Override
    public void agregarEnBolsin(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        if (contexto == null) return;
        LocalDateTime ahora = (fechaHora != null) ? fechaHora : LocalDateTime.now();

        CambioEstadoDocumentacion ceActual = contexto.buscarCambioEstadoActual();
        if (ceActual != null) ceActual.setFechaHoraFin(ahora);

        EstadoDocumentacion nuevo = new EnBolsinSaliente();
        CambioEstadoDocumentacion nuevoCE = contexto.crearCE(ahora, responsable, nuevo);
        contexto.setEstadoActual(nuevo);
        contexto.setCambioEstado(nuevoCE);
    }

    /**
     * Operación del Diagrama de Clases: registrar()
     * CU 17: Cancelar Remito -> vuelve a Registrada
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

    /**
     * Operación del Diagrama de Clases: registrarEnRemito()
     */
    @Override
    public void registrarEnRemito(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        // Permanece en EnRemito tras edición del remito
    }
}
