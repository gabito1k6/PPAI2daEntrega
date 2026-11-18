package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Empleado;
import com.utn.bolsines.entidades.Remito;
import java.time.LocalDateTime;

/**
 * Clase abstracta del Patrón State para Remito.
 */
public abstract class EstadoRemito implements Estado {

    @Override
    public abstract String getNombre();

    @Override
    public boolean esAmbitoRemito() {
        return true;
    }

    public void enviar(Remito contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("enviar");
    }

    public void recibirYAceptar(Remito contexto, EstadoRemito nuevoEstadoRemito, EstadoDocumentacion nuevoEstadoDoc, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("recibirYAceptar");
    }

    public void cancelar(Remito contexto, LocalDateTime fechaHora, Empleado responsable) {
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
