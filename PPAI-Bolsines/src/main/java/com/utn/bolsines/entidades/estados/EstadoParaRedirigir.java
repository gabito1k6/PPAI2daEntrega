package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * ESTADO CONCRETO: EstadoParaRedirigir (PATRÓN GoF STATE)
 * ============================================================================
 * Representa la documentación que debe ser redirigida a otra Comisión Médica
 * o a la Comisión Médica Central (Flujo Alternativo A5 del CU 28).
 * 
 * TIPO DE RELACIÓN Y HERENCIA:
 * - HEREDA ('extends') de la clase abstracta 'EstadoDocumentacion'.
 * - Es un Estado Concreto participante en el Patrón GoF State.
 * 
 * RESPONSABILIDAD DELEGADA (PATRÓN STATE):
 * Según la Máquina de Estados (Grupo 8):
 * - recibir() [Si es para CM destino] -> Transiciona a 'EstadoRecibidaYAceptada'.
 * - rechazar() [Si no corresponde] -> Transiciona a 'EstadoRecibidaYRechazada'.
 * - enviar() / registrarRetiro() -> Transiciona a 'EstadoEnBolsinEnviado'.
 */
public class EstadoParaRedirigir extends EstadoDocumentacion {

    @Override
    public String getNombre() {
        return "Para Redirigir";
    }

    @Override
    public boolean esParaRedirigir() {
        return true;
    }

    @Override
    public void enviar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.crearCE(new EstadoEnBolsinEnviado(), fechaHora, responsable);
    }

    @Override
    public void recibir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        EstadoDocumentacion destino = (nuevoEstado != null) ? nuevoEstado : new EstadoRecibidaYAceptada();
        contexto.crearCE(destino, fechaHora, responsable);
    }

    @Override
    public void rechazar(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        EstadoDocumentacion destino = (nuevoEstado != null) ? nuevoEstado : new EstadoRecibidaYRechazada();
        contexto.crearCE(destino, fechaHora, responsable);
    }
}
