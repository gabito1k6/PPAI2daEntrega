package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Bolsin;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

/**
 * Clase abstracta del Patrón State para Bolsín.
 * Define todos los eventos de la máquina de estados del Bolsín.
 */
public abstract class EstadoBolsin implements Estado {

    @Override
    public abstract String getNombre();

    @Override
    public boolean esAmbitoBolsin() {
        return true;
    }

    public void cerrar(Bolsin contexto, String nroPrecinto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("cerrar");
    }

    public void reabrir(Bolsin contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("reabrir");
    }

    public void enviar(Bolsin contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("enviar");
    }

    public void recibir(Bolsin contexto, EstadoBolsin nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("recibir");
    }

    public void recibir(Bolsin contexto) {
        recibir(contexto, new EstadoBolsinRecibidoEnCMDestino(), LocalDateTime.now(), null);
    }

    public void cancelar(Bolsin contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("cancelar");
    }

    private void lanzarTransicionNoValida(String evento) {
        throw new UnsupportedOperationException(
                "Transición '" + evento + "' no válida desde el estado: " + getNombre());
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
