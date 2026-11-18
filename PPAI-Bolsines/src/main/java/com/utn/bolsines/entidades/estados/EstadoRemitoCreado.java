package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Empleado;
import com.utn.bolsines.entidades.Remito;
import java.time.LocalDateTime;

public class EstadoRemitoCreado extends EstadoRemito {

    @Override
    public String getNombre() {
        return "Creado";
    }

    @Override
    public void enviar(Remito contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.setEstado(new EstadoRemitoEnBolsinSaliente());
    }

    @Override
    public void cancelar(Remito contexto, LocalDateTime fechaHora, Empleado responsable) {
        contexto.setEstado(new EstadoRemitoCancelado());
    }
}
