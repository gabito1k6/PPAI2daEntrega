package com.utn.bolsines.entidades.estados;

/**
 * ============================================================================
 * INTERFAZ / CONTRATO POLIMÓRFICO: Estado
 * ============================================================================
 * Define el contrato base común para todos los estados de las entidades del sistema
 * (Bolsín, Remito y Documentación) según el Diagrama de Clases (Grupo 8).
 * 
 * TIPO DE RELACIÓN Y JERARQUÍA:
 * - Es implementada por las clases base de estado de cada entidad:
 *     * EstadoBolsin (abstracta) implements Estado
 *     * EstadoRemito (abstracta) implements Estado
 *     * EstadoDocumentacion (abstracta - PATRÓN STATE GoF) implements Estado
 * 
 * Permite la búsqueda y filtrado polimórfico en el catálogo del Gestor
 * (Corrección 6 de DSI y RCU Paso 11).
 */
public interface Estado {

    String getNombre();

    default String getAmbito() {
        if (esAmbitoBolsin()) return "Bolsin";
        if (esAmbitoRemito()) return "Remito";
        if (esAmbitoDocumentacion()) return "Documentacion";
        return "General";
    }

    default String getDescripcion() {
        return getNombre();
    }

    // --- Métodos de Ámbito (Diagrama de Clases) ---
    default boolean esAmbitoBolsin() {
        return false;
    }

    default boolean esAmbitoRemito() {
        return false;
    }

    default boolean esAmbitoDocumentacion() {
        return false;
    }

    // --- Métodos de Verificación de Estado Específico (Diagrama de Clases) ---
    default boolean esEnviado() {
        return sosEnviado();
    }

    default boolean sosEnviado() {
        return false;
    }

    default boolean esRecibidoEnCMDestino() {
        return esRecibidoYAceptadoEnCMDestino();
    }

    default boolean esRecibidoYAceptadoEnCMDestino() {
        return false;
    }

    default boolean esRecibidoYAceptado() {
        return false;
    }

    default boolean esRecibidaYAceptada() {
        return false;
    }

    default boolean esRecibidaYRechazada() {
        return false;
    }

    default boolean esParaRedirigir() {
        return false;
    }

    default boolean esNoRecibido() {
        return false;
    }
}
