package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * ESTADO CONCRETO: EstadoNoRecibido / NoRecibida (PATRÓN GoF STATE)
 * ============================================================================
 * Representa la documentación faltante que no llegó físicamente en el bolsín
 * (Flujo Alternativo A3 del CU 28: Registrar Recepción de Bolsín).
 * 
 * TIPO DE RELACIÓN Y HERENCIA:
 * - HEREDA ('extends') de la clase abstracta 'EstadoDocumentacion'.
 * - Es un Estado Concreto participante en el Patrón GoF State.
 * 
 * RESPONSABILIDAD DELEGADA (PATRÓN STATE):
 * Según la Máquina de Estados (Grupo 8):
 * - registrar() (CU 31: Registrar Revisión de Documentación) -> Transiciona a 'EstadoRegistrada'.
 * - darDeBaja() (CU 9: Eliminar documentación) -> Transiciona a 'EstadoEnBaja'.
 * - registrarEnRemito() (CU 15 / 16) -> Transiciona a 'EstadoEnRemito'.
 */
public class EstadoNoRecibido extends EstadoDocumentacion {

    @Override
    public String getNombre() {
        return "No Recibido";
    }

    @Override
    public boolean esNoRecibido() {
        return true;
    }

    @Override
    public void registrar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.crearCE(new EstadoRegistrada(), fechaHora, responsable);
    }

    @Override
    public void registrarEnRemito(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.crearCE(new EstadoEnRemito(), fechaHora, responsable);
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
