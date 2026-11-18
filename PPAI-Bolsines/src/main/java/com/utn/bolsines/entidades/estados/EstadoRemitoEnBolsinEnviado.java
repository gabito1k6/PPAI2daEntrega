package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.DetalleRemito;
import com.utn.bolsines.entidades.Empleado;
import com.utn.bolsines.entidades.Remito;
import java.time.LocalDateTime;

public class EstadoRemitoEnBolsinEnviado extends EstadoRemito {

    @Override
    public String getNombre() {
        return "En Bolsín Enviado";
    }

    @Override
    public boolean sosEnviado() {
        return true;
    }

    @Override
    public void recibirYAceptar(Remito contexto, EstadoRemito nuevoEstadoRemito, EstadoDocumentacion nuevoEstadoDoc, LocalDateTime fechaHora, Empleado responsable) {
        EstadoRemito destino = (nuevoEstadoRemito != null) ? nuevoEstadoRemito : new EstadoRemitoRecibidoYAceptado();
        contexto.setEstado(destino);
        for (DetalleRemito detalle : contexto.getDetalles()) {
            detalle.recibirYAceptar(nuevoEstadoDoc, fechaHora, responsable);
        }
    }
}
