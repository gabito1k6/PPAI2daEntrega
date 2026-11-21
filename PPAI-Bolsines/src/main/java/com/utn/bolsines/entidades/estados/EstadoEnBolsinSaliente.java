package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * ESTADO CONCRETO: EstadoEnBolsinSaliente (PATRÓN GoF STATE)
 * ============================================================================
 * Representa la Documentación cargada dentro de un bolsín listo para ser despachado.
 * 
 * TIPO DE RELACIÓN Y HERENCIA:
 * - HEREDA ('extends') de la clase abstracta 'EstadoDocumentacion'.
 * - Es un Estado Concreto participante en el Patrón GoF State.
 * 
 * RESPONSABILIDAD DELEGADA (PATRÓN STATE):
 * Según la Máquina de Estados (Grupo 8):
 * - registrarRetiro() / enviar() (CU 27: Registrar el retiro de bolsines)
 *     -> Transiciona a 'EstadoEnBolsinEnviado'.
 * - registrarEnRemito() / darBajaBolsin() (CU 20: Modificar bolsín / CU 21: Eliminar bolsín)
 *     -> Vuelve a 'EstadoEnRemito'.
 * - modificar() (CU 20) -> Permanece en 'EstadoEnBolsinSaliente'.
 */
public class EstadoEnBolsinSaliente extends EstadoDocumentacion {

    @Override
    public String getNombre() {
        return "En Bolsín Saliente";
    }

    @Override
    public void registrarRetiro(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.crearCE(new EstadoEnBolsinEnviado(), fechaHora, responsable);
    }

    @Override
    public void enviar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        registrarRetiro(contexto, fechaHora, responsable);
    }

    @Override
    public void darBajaBolsin(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.crearCE(new EstadoEnRemito(), fechaHora, responsable);
    }

    @Override
    public void registrarEnRemito(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        darBajaBolsin(contexto, fechaHora, responsable);
    }

    @Override
    public void modificar(Documentacion contexto) {
        System.out.println("[State] Documentación modificada dentro del bolsín saliente.");
    }
}
