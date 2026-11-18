package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

public class EstadoRegistrada extends EstadoDocumentacion {

    @Override
    public String getNombre() {
        return "Registrada";
    }

    @Override
    public void registrarEnRemito(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.actualizarEstado(new EstadoEnRemito(), fechaHora, responsable);
    }

    @Override
    public void registrarBaja(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.actualizarEstado(new EstadoEnBaja(), fechaHora, responsable);
    }

    @Override
    public void modificar(Documentacion contexto) {
        System.out.println("Documentación modificada mientras permanece en estado Registrada.");
    }
}
