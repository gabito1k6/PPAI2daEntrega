package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Bolsin;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

public class EstadoBolsinEnviado extends EstadoBolsin {

    @Override
    public String getNombre() {
        return "Enviado";
    }

    @Override
    public boolean sosEnviado() {
        return true;
    }

    @Override
    public void recibir(Bolsin contexto, EstadoBolsin nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        EstadoBolsin destino = (nuevoEstado != null) ? nuevoEstado : new EstadoBolsinRecibidoEnCMDestino();
        contexto.actualizarEstado(destino, fechaHora, responsable);
    }
}
