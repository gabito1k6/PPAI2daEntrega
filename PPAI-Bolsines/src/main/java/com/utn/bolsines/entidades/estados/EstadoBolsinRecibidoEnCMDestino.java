package com.utn.bolsines.entidades.estados;

public class EstadoBolsinRecibidoEnCMDestino extends EstadoBolsin {

    @Override
    public String getNombre() {
        return "Recibido en CM Destino";
    }

    @Override
    public boolean esRecibidoYAceptadoEnCMDestino() {
        return true;
    }
}
