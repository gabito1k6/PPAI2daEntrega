package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.CambioEstadoDocumentacion;
import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * ESTADO CONCRETO: EnBolsinSaliente (PATRÓN GoF STATE)
 * Diagrama de Clases Grupo 8 - Rediseño State Pattern
 * ============================================================================
 * Estado de una Documentación que fue agregada a un bolsín preparado para salir.
 * 
 * ATRIBUTOS:
 * - nombre: string
 * - descripcion: string
 * 
 * OPERACIONES:
 * - + modificar(): void
 * - + registrarRetiro(): void
 * - + agregarEnBolsin(): void
 * - + new(): EnBolsinSaliente
 */
public class EnBolsinSaliente extends EstadoDocumentacion {

    public EnBolsinSaliente() {
        super("En Bolsín Saliente", "Documentación asignada a un bolsín saliente.");
    }

    public EnBolsinSaliente(String nombre, String descripcion) {
        super(nombre, descripcion);
    }

    /**
     * Operación del Diagrama de Clases: registrarRetiro()
     * CU 27: Registrar retiro de bolsines -> transiciona a EnBolsinEnviado
     */
    @Override
    public void registrarRetiro(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        if (contexto == null) return;
        LocalDateTime ahora = (fechaHora != null) ? fechaHora : LocalDateTime.now();

        CambioEstadoDocumentacion ceActual = contexto.buscarCambioEstadoActual();
        if (ceActual != null) ceActual.setFechaHoraFin(ahora);

        EstadoDocumentacion nuevo = new EnBolsinEnviado();
        CambioEstadoDocumentacion nuevoCE = contexto.crearCE(ahora, responsable, nuevo);
        contexto.setEstadoActual(nuevo);
        contexto.setCambioEstado(nuevoCE);
    }

    /**
     * Operación del Diagrama de Clases: modificar()
     */
    @Override
    public void modificar(Documentacion contexto) {
        // Permanece en EnBolsinSaliente tras modificación
    }

    /**
     * Operación del Diagrama de Clases: agregarEnBolsin()
     */
    @Override
    public void agregarEnBolsin(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        // Permanece en EnBolsinSaliente si se agregan más remitos/documentación
    }

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
}
