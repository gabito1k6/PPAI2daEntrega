package com.utn.bolsines.entidades.estados;

/**
 * ============================================================================
 * ESTADO CONCRETO: RecibidaYAceptada (PATRÓN GoF STATE)
 * Diagrama de Clases Grupo 8 - Rediseño State Pattern
 * ============================================================================
 * Representa el estado final de una Documentación que fue recibida y aceptada
 * íntegramente de acuerdo al remito en el CU 28 (Flujo Básico).
 * 
 * ATRIBUTOS:
 * - nombre: string
 * - descripcion: string
 * 
 * OPERACIONES:
 * - + new(): RecibidaYAceptada
 */
public class RecibidaYAceptada extends EstadoDocumentacion {

    public RecibidaYAceptada() {
        super("Recibida y Aceptada", "Documentación recibida y aceptada íntegramente conforme a remito.");
    }

    public RecibidaYAceptada(String nombre, String descripcion) {
        super(nombre, descripcion);
    }

    @Override
    public boolean esRecibidaYAceptada() {
        return true;
    }
}
