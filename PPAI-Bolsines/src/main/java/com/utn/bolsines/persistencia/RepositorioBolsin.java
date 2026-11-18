package com.utn.bolsines.persistencia;

import com.utn.bolsines.entidades.*;
import com.utn.bolsines.entidades.estados.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Patrón Repositorio / DAO para persistencia relacional en SQLite.
 */
public class RepositorioBolsin {

    private static RepositorioBolsin instancia;

    private RepositorioBolsin() {
        ConexionBD.inicializarBaseDeDatos();
    }

    public static synchronized RepositorioBolsin getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioBolsin();
        }
        return instancia;
    }

    public void reiniciarDatosPrueba() {
        ConexionBD.reiniciarDatosSemilla();
    }

    /**
     * Carga todos los empleados y sus Comisiones Médicas desde SQLite.
     */
    public List<Empleado> obtenerEmpleados() {
        List<Empleado> empleados = new ArrayList<>();
        Map<Integer, ComisionMedica> mapCM = obtenerComisionesMap();

        String sql = "SELECT id, nombre, apellido, email, legajo, usuario, id_cm FROM empleado";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String email = rs.getString("email");
                String legajo = rs.getString("legajo");
                String usuario = rs.getString("usuario");
                int idCm = rs.getInt("id_cm");

                ComisionMedica cm = mapCM.get(idCm);
                Empleado emp = new Empleado(id, nombre, apellido, email, legajo, usuario, cm);
                if (cm != null) {
                    cm.agregarEmpleado(emp);
                }
                empleados.add(emp);
            }
        } catch (SQLException e) {
            System.err.println("Error cargando empleados: " + e.getMessage());
        }
        return empleados;
    }

    public Map<Integer, ComisionMedica> obtenerComisionesMap() {
        Map<Integer, ComisionMedica> mapa = new HashMap<>();
        String sql = "SELECT id, codigo, nombre, direccion, coordenadas, email FROM comision_medica";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                ComisionMedica cm = new ComisionMedica(
                        id,
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("coordenadas"),
                        rs.getString("email")
                );
                mapa.put(id, cm);
            }
        } catch (SQLException e) {
            System.err.println("Error cargando comisiones médicas: " + e.getMessage());
        }
        return mapa;
    }

    public Map<Integer, TipoDocumento> obtenerTiposDocumentoMap() {
        Map<Integer, TipoDocumento> mapa = new HashMap<>();
        String sql = "SELECT id, nombre, descripcion FROM tipo_documento";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                mapa.put(id, new TipoDocumento(id, rs.getString("nombre"), rs.getString("descripcion")));
            }
        } catch (SQLException e) {
            System.err.println("Error cargando tipos de documento: " + e.getMessage());
        }
        return mapa;
    }

    /**
     * Carga todos los bolsines con su jerarquía completa (Remito -> Detalle -> Documentacion)
     * e historial de cambios de estado desde SQLite.
     */
    public List<Bolsin> obtenerTodosLosBolsines() {
        List<Bolsin> bolsines = new ArrayList<>();
        Map<Integer, ComisionMedica> mapCM = obtenerComisionesMap();
        Map<Integer, TipoDocumento> mapTipos = obtenerTiposDocumentoMap();
        Map<Integer, Empleado> mapEmpleados = new HashMap<>();
        for (Empleado e : obtenerEmpleados()) {
            mapEmpleados.put(e.getId(), e);
        }

        String sqlBolsines = "SELECT id, numero_identificador, numero_precinto, fecha_alta, peso, id_cm_origen, id_cm_destino, estado_actual FROM bolsin";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement psB = conn.prepareStatement(sqlBolsines);
             ResultSet rsB = psB.executeQuery()) {

            while (rsB.next()) {
                int idBolsin = rsB.getInt("id");
                long nroId = rsB.getLong("numero_identificador");
                String precinto = rsB.getString("numero_precinto");
                LocalDateTime fechaAlta = LocalDateTime.parse(rsB.getString("fecha_alta"));
                double peso = rsB.getDouble("peso");
                ComisionMedica cmOri = mapCM.get(rsB.getInt("id_cm_origen"));
                ComisionMedica cmDes = mapCM.get(rsB.getInt("id_cm_destino"));
                String estadoStr = rsB.getString("estado_actual");

                Bolsin bolsin = new Bolsin();
                bolsin.setId(idBolsin);
                bolsin.setNumeroIdentificador(nroId);
                bolsin.setNumeroPrecinto(precinto);
                bolsin.setFechaAlta(fechaAlta);
                bolsin.setPeso(peso);
                bolsin.setCmOrigen(cmOri);
                bolsin.setCmDestino(cmDes);
                bolsin.setEstadoActual(crearEstadoBolsin(estadoStr));

                // Cargar historial de cambios de estado del bolsín
                cargarHistorialBolsin(conn, bolsin, mapEmpleados);

                // Cargar remitos del bolsín
                cargarRemitosBolsin(conn, bolsin, mapCM, mapTipos, mapEmpleados);

                bolsines.add(bolsin);
            }
        } catch (SQLException e) {
            System.err.println("Error cargando bolsines: " + e.getMessage());
        }
        return bolsines;
    }

    private void cargarHistorialBolsin(Connection conn, Bolsin bolsin, Map<Integer, Empleado> mapEmpleados) throws SQLException {
        String sql = "SELECT estado, fecha_hora_inicio, fecha_hora_fin, id_empleado_responsable FROM cambio_estado_bolsin WHERE id_bolsin = ? ORDER BY id ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bolsin.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EstadoBolsin estado = crearEstadoBolsin(rs.getString("estado"));
                    LocalDateTime inicio = LocalDateTime.parse(rs.getString("fecha_hora_inicio"));
                    String finStr = rs.getString("fecha_hora_fin");
                    LocalDateTime fin = (finStr != null && !finStr.isEmpty()) ? LocalDateTime.parse(finStr) : null;
                    Empleado resp = mapEmpleados.get(rs.getInt("id_empleado_responsable"));

                    CambioEstadoBolsin ceb = new CambioEstadoBolsin(estado, inicio, resp);
                    ceb.setFechaHoraFin(fin);
                    bolsin.getHistorialEstados().add(ceb);
                }
            }
        }
    }

    private void cargarRemitosBolsin(Connection conn, Bolsin bolsin, Map<Integer, ComisionMedica> mapCM,
                                     Map<Integer, TipoDocumento> mapTipos, Map<Integer, Empleado> mapEmpleados) throws SQLException {
        String sql = "SELECT id, numero_remito, fecha_hora_alta, id_empleado_emisor, id_cm_destino, estado FROM remito WHERE id_bolsin = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bolsin.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idRemito = rs.getInt("id");
                    long nroRemito = rs.getLong("numero_remito");
                    LocalDateTime fechaAlta = LocalDateTime.parse(rs.getString("fecha_hora_alta"));
                    Empleado emisor = mapEmpleados.get(rs.getInt("id_empleado_emisor"));
                    ComisionMedica cmDest = mapCM.get(rs.getInt("id_cm_destino"));
                    String estadoStr = rs.getString("estado");

                    Remito remito = new Remito(idRemito, nroRemito, fechaAlta, emisor, cmDest);
                    remito.setEstado(crearEstadoRemito(estadoStr));

                    // Cargar detalles y documentación del remito
                    cargarDetallesRemito(conn, remito, mapCM, mapTipos, mapEmpleados);

                    bolsin.asociarRemito(remito);
                }
            }
        }
    }

    private void cargarDetallesRemito(Connection conn, Remito remito, Map<Integer, ComisionMedica> mapCM,
                                      Map<Integer, TipoDocumento> mapTipos, Map<Integer, Empleado> mapEmpleados) throws SQLException {
        String sql = "SELECT dr.id AS dr_id, dr.cantidad, dr.descripcion, " +
                "d.id AS d_id, d.numero_identificador, d.asunto, d.fecha_pase, d.id_tipo_documento, d.id_cm_origen, d.estado_actual " +
                "FROM detalle_remito dr " +
                "LEFT JOIN documentacion d ON d.id_detalle_remito = dr.id " +
                "WHERE dr.id_remito = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, remito.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int drId = rs.getInt("dr_id");
                    int cantidad = rs.getInt("cantidad");
                    String desc = rs.getString("descripcion");

                    int docId = rs.getInt("d_id");
                    Documentacion doc = null;
                    if (docId > 0) {
                        long docNro = rs.getLong("numero_identificador");
                        String asunto = rs.getString("asunto");
                        String fPaseStr = rs.getString("fecha_pase");
                        LocalDate fPase = (fPaseStr != null) ? LocalDate.parse(fPaseStr) : LocalDate.now();
                        TipoDocumento tipo = mapTipos.get(rs.getInt("id_tipo_documento"));
                        ComisionMedica cmOri = mapCM.get(rs.getInt("id_cm_origen"));
                        String docEstado = rs.getString("estado_actual");

                        doc = new Documentacion();
                        doc.setId(docId);
                        doc.setNumeroIdentificador(docNro);
                        doc.setAsunto(asunto);
                        doc.setFechaPase(fPase);
                        doc.setTipoDocumento(tipo);
                        doc.setCmOrigen(cmOri);
                        doc.setEstadoActual(crearEstadoDocumentacion(docEstado));

                        cargarHistorialDocumentacion(conn, doc, mapEmpleados);
                    }

                    DetalleRemito detalle = new DetalleRemito(drId, cantidad, desc, doc);
                    remito.agregarDetalle(detalle);
                }
            }
        }
    }

    private void cargarHistorialDocumentacion(Connection conn, Documentacion doc, Map<Integer, Empleado> mapEmpleados) throws SQLException {
        String sql = "SELECT estado, fecha_hora_inicio, fecha_hora_fin, id_empleado_responsable FROM cambio_estado_documentacion WHERE id_documentacion = ? ORDER BY id ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doc.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EstadoDocumentacion estado = crearEstadoDocumentacion(rs.getString("estado"));
                    LocalDateTime inicio = LocalDateTime.parse(rs.getString("fecha_hora_inicio"));
                    String finStr = rs.getString("fecha_hora_fin");
                    LocalDateTime fin = (finStr != null && !finStr.isEmpty()) ? LocalDateTime.parse(finStr) : null;
                    Empleado resp = mapEmpleados.get(rs.getInt("id_empleado_responsable"));

                    CambioEstadoDocumentacion ced = new CambioEstadoDocumentacion(estado, inicio, resp);
                    ced.setFechaHoraFin(fin);
                    doc.getHistorialEstados().add(ced);
                }
            }
        }
    }

    /**
     * Persiste en la base SQLite los cambios de estado ejecutados en el CU 28:
     * - Actualiza el estado del Bolsín y agrega nuevo CambioEstadoBolsin.
     * - Actualiza el estado de los Remitos.
     * - Actualiza cada Documentación y agrega su nuevo CambioEstadoDocumentacion.
     */
    public boolean guardarRecepcionBolsin(Bolsin bolsin, Empleado responsable, LocalDateTime fechaHora) {
        String updBolsin = "UPDATE bolsin SET estado_actual = ? WHERE id = ?";
        String updCeBolsinAnt = "UPDATE cambio_estado_bolsin SET fecha_hora_fin = ? WHERE id_bolsin = ? AND fecha_hora_fin IS NULL";
        String insCeBolsin = "INSERT INTO cambio_estado_bolsin (id_bolsin, estado, fecha_hora_inicio, fecha_hora_fin, id_empleado_responsable) VALUES (?, ?, ?, NULL, ?)";

        String updRemito = "UPDATE remito SET estado = ? WHERE id = ?";

        String updDoc = "UPDATE documentacion SET estado_actual = ? WHERE id = ?";
        String updCeDocAnt = "UPDATE cambio_estado_documentacion SET fecha_hora_fin = ? WHERE id_documentacion = ? AND fecha_hora_fin IS NULL";
        String insCeDoc = "INSERT INTO cambio_estado_documentacion (id_documentacion, estado, fecha_hora_inicio, fecha_hora_fin, id_empleado_responsable) VALUES (?, ?, ?, NULL, ?)";

        String fechaIso = fechaHora.toString();

        try (Connection conn = ConexionBD.getConexion()) {
            conn.setAutoCommit(false);

            try {
                // 1. Actualizar Bolsín
                try (PreparedStatement ps = conn.prepareStatement(updBolsin)) {
                    ps.setString(1, bolsin.getEstadoActual().getNombre());
                    ps.setInt(2, bolsin.getId());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(updCeBolsinAnt)) {
                    ps.setString(1, fechaIso);
                    ps.setInt(2, bolsin.getId());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(insCeBolsin)) {
                    ps.setInt(1, bolsin.getId());
                    ps.setString(2, bolsin.getEstadoActual().getNombre());
                    ps.setString(3, fechaIso);
                    ps.setInt(4, responsable != null ? responsable.getId() : 20);
                    ps.executeUpdate();
                }

                // 2. Actualizar Remitos y Documentaciones
                for (Remito rem : bolsin.getRemitos()) {
                    try (PreparedStatement ps = conn.prepareStatement(updRemito)) {
                        ps.setString(1, rem.getEstado().getNombre());
                        ps.setInt(2, rem.getId());
                        ps.executeUpdate();
                    }

                    for (DetalleRemito det : rem.getDetalles()) {
                        Documentacion doc = det.getDocumentacion();
                        if (doc != null) {
                            try (PreparedStatement ps = conn.prepareStatement(updDoc)) {
                                ps.setString(1, doc.getEstadoActual().getNombre());
                                ps.setInt(2, doc.getId());
                                ps.executeUpdate();
                            }
                            try (PreparedStatement ps = conn.prepareStatement(updCeDocAnt)) {
                                ps.setString(1, fechaIso);
                                ps.setInt(2, doc.getId());
                                ps.executeUpdate();
                            }
                            try (PreparedStatement ps = conn.prepareStatement(insCeDoc)) {
                                ps.setInt(1, doc.getId());
                                ps.setString(2, doc.getEstadoActual().getNombre());
                                ps.setString(3, fechaIso);
                                ps.setInt(4, responsable != null ? responsable.getId() : 20);
                                ps.executeUpdate();
                            }
                        }
                    }
                }

                conn.commit();
                System.out.println("[Persistencia SQLite] Recepción del Bolsín #" + bolsin.getNumeroIdentificador() + " guardada exitosamente.");
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error en transacción SQLite: " + e.getMessage());
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión a BD SQLite: " + e.getMessage());
            return false;
        }
    }

    public static EstadoBolsin crearEstadoBolsin(String nombre) {
        if (nombre == null) return new EstadoBolsinCreado();
        return switch (nombre.trim()) {
            case "Enviado" -> new EstadoBolsinEnviado();
            case "Recibido en CM Destino" -> new EstadoBolsinRecibidoEnCMDestino();
            case "Cerrado" -> new EstadoBolsinCerrado();
            case "De Baja" -> new EstadoBolsinDeBaja();
            default -> new EstadoBolsinCreado();
        };
    }

    public static EstadoRemito crearEstadoRemito(String nombre) {
        if (nombre == null) return new EstadoRemitoCreado();
        return switch (nombre.trim()) {
            case "En Bolsín Enviado" -> new EstadoRemitoEnBolsinEnviado();
            case "Recibido y Aceptado" -> new EstadoRemitoRecibidoYAceptado();
            case "En Bolsín Saliente" -> new EstadoRemitoEnBolsinSaliente();
            case "Cancelado" -> new EstadoRemitoCancelado();
            default -> new EstadoRemitoCreado();
        };
    }

    public static EstadoDocumentacion crearEstadoDocumentacion(String nombre) {
        if (nombre == null) return new EstadoRegistrada();
        return switch (nombre.trim()) {
            case "En Bolsín Enviado" -> new EstadoEnBolsinEnviado();
            case "Recibida y Aceptada" -> new EstadoRecibidaYAceptada();
            case "Recibida y Rechazada" -> new EstadoRecibidaYRechazada();
            case "Para Redirigir" -> new EstadoParaRedirigir();
            case "No Recibido", "No Recibida" -> new EstadoNoRecibido();
            case "En Remito" -> new EstadoEnRemito();
            case "En Bolsín Saliente" -> new EstadoEnBolsinSaliente();
            case "En Baja" -> new EstadoEnBaja();
            default -> new EstadoRegistrada();
        };
    }
}
