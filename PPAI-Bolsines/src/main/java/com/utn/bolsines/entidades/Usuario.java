package com.utn.bolsines.entidades;

/**
 * ============================================================================
 * ENTIDAD: Usuario (<<entity>>)
 * ============================================================================
 * Representa las credenciales de un operador del sistema según el Diagrama de Clases (Grupo 8).
 * 
 * TIPO DE RELACIÓN:
 * - Asociación simple (1 a 1) con Empleado: un Usuario pertenece a un Empleado.
 * - Asociación simple (1 a 1) con Sesion: una Sesion referencia al Usuario logueado.
 */
public class Usuario {

    private String nombre;
    private String contraseña;
    private Empleado empleado;

    public Usuario() {}

    public Usuario(String nombre, String contraseña, Empleado empleado) {
        this.nombre = nombre;
        this.contraseña = contraseña;
        this.empleado = empleado;
        if (empleado != null && empleado.getUsuarioObj() == null) {
            empleado.setUsuarioObj(this);
        }
    }

    public Usuario(String nombre, String contraseña) {
        this(nombre, contraseña, null);
    }

    public boolean validarPassword(String passwordIngresada) {
        return this.contraseña != null && this.contraseña.equals(passwordIngresada);
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
