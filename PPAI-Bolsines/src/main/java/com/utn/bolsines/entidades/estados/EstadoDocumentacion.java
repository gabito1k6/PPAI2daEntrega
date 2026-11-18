package com.utn.bolsines.entidades.estados;

import com.utn.bolsines.entidades.Documentacion;
import com.utn.bolsines.entidades.Empleado;
import java.time.LocalDateTime;

/**
 * Clase abstracta del Patrón State para Documentación.
 * Define todos los eventos de la máquina de estados según la Corrección 3.
 * Por defecto, cada método lanza una excepción de transición no válida.
 */
public abstract class EstadoDocumentacion implements Estado {

    @Override
    public abstract String getNombre();

    @Override
    public boolean esAmbitoDocumentacion() {
        return true;
    }

    public void generar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("generar");
    }

    public void registrarEnRemito(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("registrarEnRemito");
    }

    public void enviar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("enviar");
    }

    public void recibir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("recibir");
    }

    public void recibir(Documentacion contexto) {
        recibir(contexto, new EstadoRecibidaYAceptada(), LocalDateTime.now(), null);
    }

    public void rechazar(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("rechazar");
    }

    public void rechazar(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("rechazar");
    }

    public void redirigir(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("redirigir");
    }

    public void redirigir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("redirigir");
    }

    public void noRecibir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("noRecibir");
    }

    public void registrarBaja(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("registrarBaja");
    }

    public void modificar(Documentacion contexto) {
        lanzarTransicionNoValida("modificar");
    }

    public void darBajaBolsin(Documentacion contexto, LocalDateTime fechaHora, Empleado responsable) {
        lanzarTransicionNoValida("darBajaBolsin");
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
