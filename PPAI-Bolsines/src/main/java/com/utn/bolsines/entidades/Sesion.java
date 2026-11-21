package com.utn.bolsines.entidades;

import java.time.LocalDateTime;

/**
 * ============================================================================
 * ENTIDAD: Sesion (<<entity>>)
 * ============================================================================
 * Representa la sesión activa en el sistema iniciada por un usuario operador.
 * 
 * TIPO DE RELACIONES SEGÚN EL DIAGRAMA DE CLASES (Grupo 8):
 * - Asociación simple (1 a 1) con Usuario (rol: 'usuario'):
 *     La sesión almacena el usuario autenticado que opera el caso de uso.
 * 
 * MÉTODOS DEL DIAGRAMA DE SECUENCIAS:
 * - 'actual:Sesion.getUsuario()' -> Obtiene el usuario autenticado.
 */
public class Sesion {

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Usuario usuario; // Asociación 1 a 1 con Usuario (Diagrama de Clases)

    public Sesion() {
        this.fechaHoraInicio = LocalDateTime.now();
    }

    public Sesion(Usuario usuario) {
        this.fechaHoraInicio = LocalDateTime.now();
        this.usuario = usuario;
    }

    public Sesion(Empleado empleado) {
        this.fechaHoraInicio = LocalDateTime.now();
        if (empleado != null) {
            this.usuario = empleado.getUsuarioObj() != null 
                    ? empleado.getUsuarioObj() 
                    : new Usuario(empleado.getUsuario(), "1234", empleado);
        }
    }

    /**
     * Método del Diagrama de Secuencias:
     * 'actual:Sesion.getUsuario()' -> retorna el identificador/objeto del usuario activo.
     */
    public Usuario getUsuarioObjeto() {
        return this.usuario;
    }

    public String getUsuario() {
        return (usuario != null) ? usuario.getNombre() : null;
    }

    /**
     * Navegación hacia el Empleado logueado a través del Usuario de la sesión.
     */
    public Empleado getEmpleado() {
        return (usuario != null) ? usuario.getEmpleado() : null;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setUsuario(Empleado empleado) {
        if (empleado != null) {
            this.usuario = empleado.getUsuarioObj() != null 
                    ? empleado.getUsuarioObj() 
                    : new Usuario(empleado.getUsuario(), "1234", empleado);
        } else {
            this.usuario = null;
        }
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public LocalDateTime getFechaHoraAlta() {
        return fechaHoraInicio;
    }
}
