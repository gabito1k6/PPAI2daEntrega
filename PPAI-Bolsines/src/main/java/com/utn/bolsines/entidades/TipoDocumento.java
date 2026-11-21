package com.utn.bolsines.entidades;

/**
 * ============================================================================
 * ENTIDAD: TipoDocumento (<<entity>>)
 * ============================================================================
 * Clasificación de la documentación (ej: Expediente, Dictamen, Estudio Médico).
 * 
 * TIPO DE RELACIONES SEGÚN EL DIAGRAMA DE CLASES (Grupo 8):
 * - Asociación simple (1 a muchos) con Documentacion (rol: 'tipoDocumento').
 */
public class TipoDocumento {
    private int id;
    private String nombre;
    private String descripcion;

    public TipoDocumento() {}

    public TipoDocumento(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return nombre;
    }
}
