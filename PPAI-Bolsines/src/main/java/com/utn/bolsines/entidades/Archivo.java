package com.utn.bolsines.entidades;

public class Archivo {
    private String nombre;
    private String ruta;
    private String tipo;

    public Archivo() {}

    public Archivo(String nombre, String ruta, String tipo) {
        this.nombre = nombre;
        this.ruta = ruta;
        this.tipo = tipo;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRuta() { return ruta; }
    public void setRuta(String ruta) { this.ruta = ruta; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    @Override
    public String toString() {
        return nombre + " (" + tipo + ")";
    }
}
