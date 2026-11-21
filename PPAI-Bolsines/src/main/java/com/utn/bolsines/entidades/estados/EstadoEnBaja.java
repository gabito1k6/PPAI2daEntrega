package com.utn.bolsines.entidades.estados;

/**
 * ============================================================================
 * ESTADO CONCRETO: EstadoEnBaja (PATRÓN GoF STATE)
 * ============================================================================
 * Representa el estado final de una Documentación eliminada del circuito
 * (CU 9: Eliminar Documentación).
 * 
 * TIPO DE RELACIÓN Y HERENCIA:
 * - HEREDA ('extends') de la clase abstracta 'EstadoDocumentacion'.
 * - Es un Estado Concreto participante en el Patrón GoF State.
 * 
 * RESPONSABILIDAD DELEGADA (PATRÓN STATE):
 * Es un estado final sin transiciones de salida válidas.
 */
public class EstadoEnBaja extends EstadoDocumentacion {

    @Override
    public String getNombre() {
        return "En Baja";
    }
}
