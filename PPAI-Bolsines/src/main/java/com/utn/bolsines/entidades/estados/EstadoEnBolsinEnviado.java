package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

public class EstadoEnBolsinEnviado extends EstadoDocumentacion {

    @Override
    public String getNombre() {
        return "En Bolsín Enviado";
    }

    @Override
    public boolean sosEnviado() {
        return true;
    }

    @Override
    public void recibir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        EstadoDocumentacion destino = (nuevoEstado != null) ? nuevoEstado : new EstadoRecibidaYAceptada();
        contexto.actualizarEstado(destino, fechaHora, responsable);
    }

    @Override
    public void rechazar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        rechazar(contexto, new EstadoRecibidaYRechazada(), fechaHora, responsable);
    }

    @Override
    public void rechazar(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        EstadoDocumentacion destino = (nuevoEstado != null) ? nuevoEstado : new EstadoRecibidaYRechazada();
        contexto.actualizarEstado(destino, fechaHora, responsable);
    }

    @Override
    public void redirigir(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        redirigir(contexto, new EstadoParaRedirigir(), fechaHora, responsable);
    }

    @Override
    public void redirigir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        EstadoDocumentacion destino = (nuevoEstado != null) ? nuevoEstado : new EstadoParaRedirigir();
        contexto.actualizarEstado(destino, fechaHora, responsable);
    }

    @Override
    public void noRecibir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        EstadoDocumentacion destino = (nuevoEstado != null) ? nuevoEstado : new EstadoNoRecibido();
        contexto.actualizarEstado(destino, fechaHora, responsable);
    }
}
