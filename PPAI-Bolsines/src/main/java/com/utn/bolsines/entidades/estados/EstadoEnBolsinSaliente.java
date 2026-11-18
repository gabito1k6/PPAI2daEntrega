package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

public class EstadoEnBolsinSaliente extends EstadoDocumentacion {

    @Override
    public String getNombre() {
        return "En Bolsín Saliente";
    }

    @Override
    public void enviar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.actualizarEstado(new EstadoEnBolsinEnviado(), fechaHora, responsable);
    }

    @Override
    public void darBajaBolsin(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.actualizarEstado(new EstadoEnRemito(), fechaHora, responsable);
    }
}
