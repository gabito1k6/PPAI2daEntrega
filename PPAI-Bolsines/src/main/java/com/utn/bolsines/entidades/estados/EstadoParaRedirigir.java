package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

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
        contexto.actualizarEstado(new EstadoEnBolsinEnviado(), fechaHora, responsable);
    }

    @Override
    public void recibir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        EstadoDocumentacion destino = (nuevoEstado != null) ? nuevoEstado : new EstadoRecibidaYAceptada();
        contexto.actualizarEstado(destino, fechaHora, responsable);
    }
}
