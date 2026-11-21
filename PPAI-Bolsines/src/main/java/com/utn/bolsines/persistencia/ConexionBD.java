package com.utn.bolsines.persistencia;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestor de conexión y creación del esquema en SQLite para el CU 28.
 */
public class ConexionBD {

    private static final String DB_PATH = "bolsines.db";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver SQLite no encontrado en el classpath", e);
        }
        return DriverManager.getConnection(URL);
    }

    /**
     * Crea las tablas necesarias si no existen y precarga datos iniciales.
     */
    public static void inicializarBaseDeDatos() {
        boolean baseNueva = !new File(DB_PATH).exists() || new File(DB_PATH).length() == 0;

        String ddl = """
            CREATE TABLE IF NOT EXISTS comision_medica (
                id INTEGER PRIMARY KEY,
                codigo TEXT NOT NULL,
                nombre TEXT NOT NULL,
                direccion TEXT,
                coordenadas TEXT,
                email TEXT
            );

            CREATE TABLE IF NOT EXISTS empleado (
                id INTEGER PRIMARY KEY,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                email TEXT,
                legajo TEXT,
                usuario TEXT NOT NULL,
                id_cm INTEGER,
                FOREIGN KEY(id_cm) REFERENCES comision_medica(id)
            );

            CREATE TABLE IF NOT EXISTS tipo_documento (
                id INTEGER PRIMARY KEY,
                nombre TEXT NOT NULL,
                descripcion TEXT
            );

            CREATE TABLE IF NOT EXISTS bolsin (
                id INTEGER PRIMARY KEY,
                numero_identificador INTEGER NOT NULL,
                numero_precinto TEXT NOT NULL,
                fecha_alta TEXT NOT NULL,
                peso REAL,
                id_cm_origen INTEGER,
                id_cm_destino INTEGER,
                estado_actual TEXT NOT NULL,
                FOREIGN KEY(id_cm_origen) REFERENCES comision_medica(id),
                FOREIGN KEY(id_cm_destino) REFERENCES comision_medica(id)
            );

            CREATE TABLE IF NOT EXISTS cambio_estado_bolsin (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_bolsin INTEGER NOT NULL,
                estado TEXT NOT NULL,
                fecha_hora_inicio TEXT NOT NULL,
                fecha_hora_fin TEXT,
                id_empleado_responsable INTEGER,
                FOREIGN KEY(id_bolsin) REFERENCES bolsin(id),
                FOREIGN KEY(id_empleado_responsable) REFERENCES empleado(id)
            );

            CREATE TABLE IF NOT EXISTS remito (
                id INTEGER PRIMARY KEY,
                id_bolsin INTEGER NOT NULL,
                numero_remito INTEGER NOT NULL,
                fecha_hora_alta TEXT NOT NULL,
                id_empleado_emisor INTEGER,
                id_cm_destino INTEGER,
                estado TEXT NOT NULL,
                FOREIGN KEY(id_bolsin) REFERENCES bolsin(id),
                FOREIGN KEY(id_empleado_emisor) REFERENCES empleado(id),
                FOREIGN KEY(id_cm_destino) REFERENCES comision_medica(id)
            );

            CREATE TABLE IF NOT EXISTS detalle_remito (
                id INTEGER PRIMARY KEY,
                id_remito INTEGER NOT NULL,
                cantidad INTEGER NOT NULL,
                descripcion TEXT,
                FOREIGN KEY(id_remito) REFERENCES remito(id)
            );

            CREATE TABLE IF NOT EXISTS documentacion (
                id INTEGER PRIMARY KEY,
                id_detalle_remito INTEGER NOT NULL,
                numero_identificador INTEGER NOT NULL,
                asunto TEXT NOT NULL,
                fecha_pase TEXT,
                id_tipo_documento INTEGER,
                id_cm_origen INTEGER,
                estado_actual TEXT NOT NULL,
                FOREIGN KEY(id_detalle_remito) REFERENCES detalle_remito(id),
                FOREIGN KEY(id_tipo_documento) REFERENCES tipo_documento(id),
                FOREIGN KEY(id_cm_origen) REFERENCES comision_medica(id)
            );

            CREATE TABLE IF NOT EXISTS cambio_estado_documentacion (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_documentacion INTEGER NOT NULL,
                estado TEXT NOT NULL,
                fecha_hora_inicio TEXT NOT NULL,
                fecha_hora_fin TEXT,
                id_empleado_responsable INTEGER,
                FOREIGN KEY(id_documentacion) REFERENCES documentacion(id),
                FOREIGN KEY(id_empleado_responsable) REFERENCES empleado(id)
            );
        """;

        try (Connection conn = getConexion(); Statement stmt = conn.createStatement()) {
            for (String sql : ddl.split(";")) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql.trim());
                }
            }
            if (baseNueva || estaVacia(conn)) {
                insertarDatosSemilla(stmt);
            }
        } catch (SQLException e) {
            System.err.println("Error inicializando base de datos SQLite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Limpia la base y vuelve a insertar los datos iniciales de prueba (Bolsines en estado Enviado).
     */
    public static void reiniciarDatosSemilla() {
        String cleanSql = """
            DELETE FROM cambio_estado_documentacion;
            DELETE FROM documentacion;
            DELETE FROM detalle_remito;
            DELETE FROM remito;
            DELETE FROM cambio_estado_bolsin;
            DELETE FROM bolsin;
            DELETE FROM tipo_documento;
            DELETE FROM empleado;
            DELETE FROM comision_medica;
        """;
        try (Connection conn = getConexion(); Statement stmt = conn.createStatement()) {
            for (String sql : cleanSql.split(";")) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql.trim());
                }
            }
            insertarDatosSemilla(stmt);
            System.out.println("[Persistencia SQLite] Datos de prueba restablecidos a su estado inicial (Bolsines Enviados).");
        } catch (SQLException e) {
            System.err.println("Error restableciendo datos en SQLite: " + e.getMessage());
        }
    }

    private static boolean estaVacia(Connection conn) {
        try (Statement stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM bolsin")) {
            if (rs.next()) {
                return rs.getInt("total") == 0;
            }
        } catch (SQLException ignored) {}
        return true;
    }

    private static void insertarDatosSemilla(Statement stmt) throws SQLException {
        String seedSql = """
            -- Comisiones Médicas
            INSERT INTO comision_medica (id, codigo, nombre, direccion, coordenadas, email) VALUES
            (1, 'CM01', 'Comisión Médica Córdoba', 'Av. Colón 1234, Córdoba', '-31.4201,-64.1888', 'cordoba@comisionesmedicas.gob.ar'),
            (2, 'CM02', 'Comisión Médica Villa María', 'San Martín 456, Villa María', '-32.4075,-63.2402', 'villamaria@comisionesmedicas.gob.ar'),
            (3, 'CM03', 'Comisión Médica Rosario', 'Pellegrini 890, Rosario', '-32.9587,-60.6558', 'rosario@comisionesmedicas.gob.ar');

            -- Empleados
            INSERT INTO empleado (id, nombre, apellido, email, legajo, usuario, id_cm) VALUES
            (10, 'Carlos', 'Pérez', 'carlos.perez@comisionesmedicas.gob.ar', 'LEG-1001', 'cperez', 1),
            (20, 'Ana', 'González', 'ana.gonzalez@comisionesmedicas.gob.ar', 'LEG-2002', 'agonzalez', 2),
            (30, 'Marcos', 'Suárez', 'marcos.suarez@comisionesmedicas.gob.ar', 'LEG-3003', 'msuarez', 3);

            -- Tipos de Documento
            INSERT INTO tipo_documento (id, nombre, descripcion) VALUES
            (1, 'Expediente', 'Expediente médico legal'),
            (2, 'Dictamen', 'Dictamen de incapacidad laboral'),
            (3, 'Estudio Médico', 'Estudios de diagnóstico e imágenes');

            -- Bolsín 1: Enviado hacia Villa María (Destino del Encargado logueado)
            INSERT INTO bolsin (id, numero_identificador, numero_precinto, fecha_alta, peso, id_cm_origen, id_cm_destino, estado_actual) VALUES
            (1, 301, 'PREC-998877', '2026-11-18T08:00:00', 1500.0, 1, 2, 'Enviado');

            INSERT INTO cambio_estado_bolsin (id_bolsin, estado, fecha_hora_inicio, fecha_hora_fin, id_empleado_responsable) VALUES
            (1, 'Creado', '2026-11-17T10:00:00', '2026-11-17T15:00:00', 10),
            (1, 'Enviado', '2026-11-17T15:00:00', NULL, 10);

            -- Remito 1 del Bolsín 1
            INSERT INTO remito (id, id_bolsin, numero_remito, fecha_hora_alta, id_empleado_emisor, id_cm_destino, estado) VALUES
            (1, 1, 5001, '2026-11-17T14:30:00', 10, 2, 'En Bolsín Enviado');

            INSERT INTO detalle_remito (id, id_remito, cantidad, descripcion) VALUES
            (1, 1, 1, 'Expediente físico con estudios complementarios'),
            (2, 1, 1, 'Dictamen pericial médico rubricado');

            -- Documentaciones del Remito 1
            INSERT INTO documentacion (id, id_detalle_remito, numero_identificador, asunto, fecha_pase, id_tipo_documento, id_cm_origen, estado_actual) VALUES
            (1, 1, 1001, 'Expediente Accidente Laboral - Juan Gómez', '2026-11-15', 1, 1, 'En Bolsín Enviado'),
            (2, 2, 1002, 'Dictamen Comisión Evaluadora - María Ruiz', '2026-11-16', 2, 1, 'En Bolsín Enviado');

            INSERT INTO cambio_estado_documentacion (id_documentacion, estado, fecha_hora_inicio, fecha_hora_fin, id_empleado_responsable) VALUES
            (1, 'Registrada', '2026-11-15T09:00:00', '2026-11-17T14:00:00', 10),
            (1, 'En Bolsín Enviado', '2026-11-17T14:00:00', NULL, 10),
            (2, 'Registrada', '2026-11-16T11:00:00', '2026-11-17T14:00:00', 10),
            (2, 'En Bolsín Enviado', '2026-11-17T14:00:00', NULL, 10);

            -- Bolsín 2: Enviado desde Rosario hacia Villa María
            INSERT INTO bolsin (id, numero_identificador, numero_precinto, fecha_alta, peso, id_cm_origen, id_cm_destino, estado_actual) VALUES
            (2, 302, 'PREC-445566', '2026-11-18T07:30:00', 2100.0, 3, 2, 'Enviado');

            INSERT INTO cambio_estado_bolsin (id_bolsin, estado, fecha_hora_inicio, fecha_hora_fin, id_empleado_responsable) VALUES
            (2, 'Enviado', '2026-11-18T07:30:00', NULL, 30);

            INSERT INTO remito (id, id_bolsin, numero_remito, fecha_hora_alta, id_empleado_emisor, id_cm_destino, estado) VALUES
            (2, 2, 6001, '2026-11-18T07:00:00', 30, 2, 'En Bolsín Enviado');

            INSERT INTO detalle_remito (id, id_remito, cantidad, descripcion) VALUES
            (3, 2, 1, 'Estudio de resonancia magnética');

            INSERT INTO documentacion (id, id_detalle_remito, numero_identificador, asunto, fecha_pase, id_tipo_documento, id_cm_origen, estado_actual) VALUES
            (3, 3, 1003, 'Resonancia Columna Lumbar - Pedro Rossi', '2026-11-17', 3, 3, 'En Bolsín Enviado');

            INSERT INTO cambio_estado_documentacion (id_documentacion, estado, fecha_hora_inicio, fecha_hora_fin, id_empleado_responsable) VALUES
            (3, 'En Bolsín Enviado', '2026-11-18T07:00:00', NULL, 30);

            -- Bolsín 3: En estado Creado (NO debe aparecer en la recepción porque no está Enviado)
            INSERT INTO bolsin (id, numero_identificador, numero_precinto, fecha_alta, peso, id_cm_origen, id_cm_destino, estado_actual) VALUES
            (3, 303, 'PREC-112233', '2026-11-18T09:00:00', 900.0, 1, 2, 'Creado');

            INSERT INTO cambio_estado_bolsin (id_bolsin, estado, fecha_hora_inicio, fecha_hora_fin, id_empleado_responsable) VALUES
            (3, 'Creado', '2026-11-18T09:00:00', NULL, 10);
        """;

        for (String sql : seedSql.split(";")) {
            if (!sql.trim().isEmpty()) {
                stmt.execute(sql.trim());
            }
        }
    }

    public static void main(String[] args) {
        inicializarBaseDeDatos();
        reiniciarDatosSemilla();
        System.out.println("Base de datos SQLite restablecida a datos iniciales de prueba.");
    }
}
