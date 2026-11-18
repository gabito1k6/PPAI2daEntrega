package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

public class EstadoEnRemito extends EstadoDocumentacion {

    @Override
    public String getNombre() {
        return "En Remito";
    }

    @Override
    public void enviar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.actualizarEstado(new EstadoEnBolsinSaliente(), fechaHora, responsable);
    }

    @Override
    public void registrarBaja(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.actualizarEstado(new EstadoEnBaja(), fechaHora, responsable);
    }
}
