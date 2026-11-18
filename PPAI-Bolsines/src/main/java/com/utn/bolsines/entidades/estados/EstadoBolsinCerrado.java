package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Bolsin;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

public class EstadoBolsinCerrado extends EstadoBolsin {

    @Override
    public String getNombre() {
        return "Cerrado";
    }

    @Override
    public void reabrir(Bolsin contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.setNumeroPrecinto(null);
        contexto.actualizarEstado(new EstadoBolsinCreado(), fechaHora, responsable);
    }

    @Override
    public void enviar(Bolsin contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.actualizarEstado(new EstadoBolsinEnviado(), fechaHora, responsable);
    }

    @Override
    public void cancelar(Bolsin contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.actualizarEstado(new EstadoBolsinDeBaja(), fechaHora, responsable);
    }
}
