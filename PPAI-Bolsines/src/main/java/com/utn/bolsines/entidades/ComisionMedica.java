package com.utn.bolsines.entidades;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ============================================================================
 * ENTIDAD: ComisionMedica (<<entity>>)
 * ============================================================================
 * Representa una sede física médica jurisdiccional del sistema.
 * 
 * TIPO DE RELACIONES SEGÚN EL DIAGRAMA DE CLASES (Grupo 8):
 * - Asociación simple (1 a muchos) con Empleado (rol: 'integrantes' / 'asignadoA'):
 *     Contiene los operadores y peritos médicos de la sede.
 * - Asociación con Bolsin (roles: 'origen' y 'destino'):
 *     Indica la CM remitente y la CM destinataria de un bolsín.
 * - Asociación con Remito (rol: 'destino'):
 *     Indica la CM a la que va dirigida la documentación.
 * - Asociación con Documentacion (rol: 'cmOrigen'):
 *     Indica la CM en donde se originó el documento legal o médico.
 */
public class ComisionMedica {
    private int id;
    private String codigo;
    private String nombre;
    private String direccion;
    private String coordenada;
    private String email;
    private String telefono;
    private List<Empleado> integrantes;

    public ComisionMedica() {
        this.integrantes = new ArrayList<>();
    }

    public ComisionMedica(int id, String codigo, String nombre, String direccion, String coordenada, String email) {
        this();
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.direccion = direccion;
        this.coordenada = coordenada;
        this.email = email;
    }

    public void agregarEmpleado(Empleado emp) {
        this.integrantes.add(emp);
        emp.setComisionMedica(this);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCoordenada() { return coordenada; }
    public void setCoordenada(String coordenada) { this.coordenada = coordenada; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public List<Empleado> getIntegrantes() { return integrantes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComisionMedica that = (ComisionMedica) o;
        return id == that.id || Objects.equals(nombre, that.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
