package com.utn.bolsines.entidades.estados;

/**
 * ============================================================================
 * ESTADO CONCRETO: DeBaja (PATRÓN GoF STATE)
 * Diagrama de Clases Grupo 8 - Rediseño State Pattern
 * ============================================================================
 * Estado final de una Documentación dada de baja.
 * 
 * ATRIBUTOS:
 * - nombre: string
 * - descripcion: string
 * 
 * OPERACIONES:
 * - + new(): DeBaja
 */
public class DeBaja extends EstadoDocumentacion {

    public DeBaja() {
        super("De Baja", "Documentación dada de baja del sistema.");
    }

    public DeBaja(String nombre, String descripcion) {
        super(nombre, descripcion);
    }
}
