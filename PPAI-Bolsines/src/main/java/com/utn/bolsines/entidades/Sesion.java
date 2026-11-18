package com.utn.bolsines.entidades;

import java.time.LocalDateTime;

public class Sesion {
    private LocalDateTime fechaHoraAlta;
    private Empleado usuario;

    public Sesion() {
        this.fechaHoraAlta = LocalDateTime.now();
    }

    public Sesion(Empleado usuario) {
        this.fechaHoraAlta = LocalDateTime.now();
        this.usuario = usuario;
    }

    public String getUsuario() {
        return (usuario != null) ? usuario.getUsuario() : null;
    }

    public Empleado getEmpleado() {
        return usuario;
    }

    public void setUsuario(Empleado usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaHoraAlta() {
        return fechaHoraAlta;
    }
}
