package com.utn.bolsines.pantallas;

import com.utn.bolsines.entidades.*;
import com.utn.bolsines.entidades.estados.Estado;
import java.util.List;

/**
 * ============================================================================
 * CLASE FRONTERA / BOUNDARY: PantRecepcionBolsin (<<boundary>>)
 * ============================================================================
 * Representa la pantalla de recepción de bolsín con el nombre exacto especificado
 * en el Diagrama de Clases y Diagrama de Secuencias (Grupo 8).
 * 
 * TIPO DE RELACIÓN Y HERENCIA:
 * - HEREDA ('extends') de 'PantallaRecepcionBolsin' (tecnología de escritorio Swing JFrame).
 * - Asociación simple con 'GestorRecepcionBolsin' (Controlador).
 */
public class PantRecepcionBolsin extends PantallaRecepcionBolsin {

    public PantRecepcionBolsin() {
        super();
    }

    public PantRecepcionBolsin(Sesion sesionActual, List<Bolsin> bolsinesDisponibles,
                              List<Estado> catalogoEstados, List<Empleado> empleadosRegistrados) {
        super(sesionActual, bolsinesDisponibles, catalogoEstados, empleadosRegistrados);
    }

    public PantRecepcionBolsin(Sesion sesionActual, List<Bolsin> bolsinesDisponibles,
                              List<Estado> catalogoEstados, List<Empleado> empleadosRegistrados,
                              boolean esInteractivo) {
        super(sesionActual, bolsinesDisponibles, catalogoEstados, empleadosRegistrados, esInteractivo);
    }
}
