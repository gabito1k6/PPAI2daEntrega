package com.utn.bolsines.entidades;

public class Empleado {
    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String legajo;
    private String usuario;
    private ComisionMedica comisionMedica;

    public Empleado() {}

    public Empleado(int id, String nombre, String apellido, String email, String legajo, String usuario, ComisionMedica comisionMedica) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.legajo = legajo;
        this.usuario = usuario;
        this.comisionMedica = comisionMedica;
    }

    public boolean sosUsuario(String usuario) {
        return this.usuario != null && this.usuario.equalsIgnoreCase(usuario);
    }

    public ComisionMedica getCM() {
        return comisionMedica;
    }

    public boolean esTuCM(ComisionMedica cm) {
        return this.comisionMedica != null && this.comisionMedica.equals(cm);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLegajo() { return legajo; }
    public void setLegajo(String legajo) { this.legajo = legajo; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public ComisionMedica getComisionMedica() { return comisionMedica; }
    public void setComisionMedica(ComisionMedica comisionMedica) { this.comisionMedica = comisionMedica; }

    @Override
    public String toString() {
        return nombre + " " + apellido + " (Legajo: " + legajo + ")";
    }
}
