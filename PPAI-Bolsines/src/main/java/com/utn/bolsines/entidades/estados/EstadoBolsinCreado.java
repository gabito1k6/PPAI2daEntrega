package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Bolsin;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

public class EstadoBolsinCreado extends EstadoBolsin {

    @Override
    public String getNombre() {
        return "Creado";
    }

    @Override
    public void cerrar(Bolsin contexto, String nroPrecinto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.setNumeroPrecinto(nroPrecinto);
        contexto.actualizarEstado(new EstadoBolsinCerrado(), fechaHora, responsable);
    }

    @Override
    public void cancelar(Bolsin contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.actualizarEstado(new EstadoBolsinDeBaja(), fechaHora, responsable);
    }
}
