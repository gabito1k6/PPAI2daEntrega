package com.utn.bolsines.entidades.estados;

/**
 * Interfaz base común para todos los estados del sistema.
 * Permite la búsqueda y filtrado polimórfico de estados
 * (esAmbitoBolsin, esAmbitoRemito, esAmbitoDocumentacion, etc.)
 * según lo especificado en la Corrección 6 y el RCU 28.
 */
public interface Estado {

    String getNombre();

    default boolean esAmbitoBolsin() {
        return false;
    }

    default boolean esAmbitoRemito() {
        return false;
    }

    default boolean esAmbitoDocumentacion() {
        return false;
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

    default boolean sosEnviado() {
        return false;
    }
}
