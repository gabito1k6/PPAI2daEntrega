package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

public class EstadoRecibidaYAceptada extends EstadoDocumentacion {

    @Override
    public String getNombre() {
        return "Recibida y Aceptada";
    }

    @Override
    public boolean esRecibidaYAceptada() {
        return true;
    }

    @Override
    public void registrarBaja(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.actualizarEstado(new EstadoEnBaja(), fechaHora, responsable);
    }
}
