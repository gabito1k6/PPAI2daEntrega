package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * ESTADO CONCRETO: EstadoEnRemito (PATRÓN GoF STATE)
 * ============================================================================
 * Representa la Documentación incluida en un Remito de envío.
 * 
 * TIPO DE RELACIÓN Y HERENCIA:
 * - HEREDA ('extends') de la clase abstracta 'EstadoDocumentacion'.
 * - Es un Estado Concreto participante en el Patrón GoF State.
 * 
 * RESPONSABILIDAD DELEGADA (PATRÓN STATE):
 * Según la Máquina de Estados (Grupo 8):
 * - agregarEnBolsin() (CU 19 / 20) -> Transiciona a 'EstadoEnBolsinSaliente'.
 * - registrar() (CU 17: Cancelar Remito) -> Transiciona a 'EstadoRegistrada'.
 * - registrarEnRemito() (CU 16: Modificar Remito) -> Permanece en 'EstadoEnRemito'.
 * - darDeBaja() (CU 9) -> Transiciona a 'EstadoEnBaja'.
 */
public class EstadoEnRemito extends EstadoDocumentacion {

    @Override
    public String getNombre() {
        return "En Remito";
    }

    @Override
    public void agregarEnBolsin(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.crearCE(new EstadoEnBolsinSaliente(), fechaHora, responsable);
    }

    @Override
    public void enviar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        agregarEnBolsin(contexto, fechaHora, responsable);
    }

    @Override
    public void registrar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.crearCE(new EstadoRegistrada(), fechaHora, responsable);
    }

    @Override
    public void registrarEnRemito(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        System.out.println("[State] Documentación modificada en remito. Permanece en En Remito.");
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
