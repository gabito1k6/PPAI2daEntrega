package com.utn.bolsines.entidades.estados;

public class EstadoRemitoRecibidoYAceptado extends EstadoRemito {

    @Override
    public String getNombre() {
        return "Recibido y Aceptado";
    }

    @Override
    public boolean esRecibidoYAceptado() {
        return true;
    }
}
