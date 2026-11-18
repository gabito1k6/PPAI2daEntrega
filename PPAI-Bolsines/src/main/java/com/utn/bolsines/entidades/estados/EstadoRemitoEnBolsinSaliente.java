package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Empleado;
import com.utn.bolsines.entidades.Remito;
import java.time.LocalDateTime;

public class EstadoRemitoEnBolsinSaliente extends EstadoRemito {

    @Override
    public String getNombre() {
        return "En Bolsín Saliente";
    }

    @Override
    public void enviar(Remito contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.setEstado(new EstadoRemitoEnBolsinEnviado());
    }

    @Override
    public void cancelar(Remito contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.setEstado(new EstadoRemitoCreado());
    }
}
