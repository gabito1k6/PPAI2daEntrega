package com.utn.bolsines.entidades;

/**
 * ============================================================================
 * ENTIDAD: Empleado (<<entity>>)
 * ============================================================================
 * Representa a un agente u operador perteneciente a una Comisión Médica.
 * 
 * TIPO DE RELACIONES SEGÚN EL DIAGRAMA DE CLASES (Grupo 8):
 * - Asociación simple (1 a 1) con ComisionMedica (rol: 'asignadoA'):
 *     Indica a qué sede médica pertenece el empleado.
 * - Asociación simple (1 a 1) con Usuario (rol: 'usuario'):
 *     Credenciales asociadas al empleado para iniciar sesión.
 * - Asociación con CambioEstadoBolsin y CambioEstadoDocumentacion (rol: 'responsableCE'):
 *     Registra la responsabilidad de las transiciones de estado (Paso 11 del CU 28).
 */
public class Empleado {

    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String legajo;
    private String usuario; // Identificador textual de usuario
    private Usuario usuarioObj; // Objeto Usuario asociado (Diagrama de Clases)
    private ComisionMedica comisionMedica; // Rol: 'asignadoA'

    public Empleado() {}

    public Empleado(int id, String nombre, String apellido, String email, String legajo, String usuario, ComisionMedica comisionMedica) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.legajo = legajo;
        this.usuario = usuario;
        this.comisionMedica = comisionMedica;
        this.usuarioObj = new Usuario(usuario, "1234", this);
    }

    /**
     * Método del Diagrama de Secuencias (Paso 2):
     * 'logueado:Empleado.buscarCM()' -> retorna la Comisión Médica asignada al empleado.
     */
    public ComisionMedica buscarCM() {
        return this.comisionMedica;
    }

    /**
     * Alias del método buscarCM() según nomenclatura estándar get.
     */
    public ComisionMedica getCM() {
        return buscarCM();
    }

    /**
     * Método del Diagrama de Clases: esTuUsuario()
     */
    public boolean esTuUsuario(String nombreUsuario) {
        return this.usuario != null && this.usuario.equalsIgnoreCase(nombreUsuario);
    }

    public boolean esTuUsuario(Usuario u) {
        return u != null && (this.usuarioObj == u || esTuUsuario(u.getNombre()));
    }

    public boolean sosUsuario(String usuario) {
        return esTuUsuario(usuario);
    }

    public boolean esTuCM(ComisionMedica cm) {
        return this.comisionMedica != null && this.comisionMedica.equals(cm);
    }

    // Getters y Setters
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
    public void setUsuario(String usuario) { 
        this.usuario = usuario;
        if (this.usuarioObj != null) {
            this.usuarioObj.setNombre(usuario);
        }
    }

    public Usuario getUsuarioObj() { return usuarioObj; }
    public void setUsuarioObj(Usuario usuarioObj) { this.usuarioObj = usuarioObj; }

    public ComisionMedica getComisionMedica() { return comisionMedica; }
    public void setComisionMedica(ComisionMedica comisionMedica) { this.comisionMedica = comisionMedica; }

    @Override
    public String toString() {
        return nombre + " " + apellido + " (Legajo: " + legajo + ")";
    }
}
