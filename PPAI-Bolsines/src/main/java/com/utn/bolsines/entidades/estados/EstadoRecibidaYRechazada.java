package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * ESTADO CONCRETO: EstadoRecibidaYRechazada (PATRÓN GoF STATE)
 * ============================================================================
 * Representa el estado de la Documentación observada por no pertenecer al
 * destino (Flujo Alternativo A4 del CU 28: Registrar Recepción de Bolsín).
 * 
 * TIPO DE RELACIÓN Y HERENCIA:
 * - HEREDA ('extends') de la clase abstracta 'EstadoDocumentacion'.
 * - Es un Estado Concreto participante en el Patrón GoF State.
 * 
 * RESPONSABILIDAD DELEGADA (PATRÓN STATE):
 * Según la Máquina de Estados (Grupo 8), es un estado final de observación.
 * Permite la baja de documentación (CU 9) transicionando a 'EstadoEnBaja'.
 */
public class EstadoRecibidaYRechazada extends EstadoDocumentacion {

    @Override
    public String getNombre() {
        return "Recibida y Rechazada";
    }

    @Override
    public boolean esRecibidaYRechazada() {
        return true;
    }

    @Override
    public void darDeBaja(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.crearCE(new EstadoEnBaja(), fechaHora, responsable);
    }

    @Override
    public void registrarBaja(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        darDeBaja(contexto, fechaHora, responsable);
    }
}
