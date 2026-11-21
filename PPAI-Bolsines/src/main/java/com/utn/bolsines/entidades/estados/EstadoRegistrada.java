package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * ESTADO CONCRETO: EstadoRegistrada (PATRÓN GoF STATE)
 * ============================================================================
 * Estado inicial de una Documentación tras el CU 7: Registrar Documentación / new().
 * 
 * TIPO DE RELACIÓN Y HERENCIA:
 * - HEREDA ('extends') de la clase abstracta 'EstadoDocumentacion'.
 * - Es un Estado Concreto participante en el Patrón GoF State.
 * 
 * RESPONSABILIDAD DELEGADA (PATRÓN STATE):
 * Según la Máquina de Estados (Grupo 8):
 * - registrarEnRemito() (CU 15 / 16) -> Transiciona a 'EstadoEnRemito'.
 * - darDeBaja() (CU 9: Eliminar Documentación) -> Transiciona a 'EstadoEnBaja'.
 * - modificar() (CU 7) -> Permanece en 'EstadoRegistrada'.
 */
public class EstadoRegistrada extends EstadoDocumentacion {

    @Override
    public String getNombre() {
        return "Registrada";
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

    @Override
    public void modificar(Documentacion contexto) {
        System.out.println("[State] Documentación modificada mientras permanece en estado Registrada.");
    }
}
