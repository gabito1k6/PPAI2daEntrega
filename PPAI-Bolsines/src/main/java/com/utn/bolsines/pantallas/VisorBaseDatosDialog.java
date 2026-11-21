package com.utn.bolsines.pantallas;

import com.utn.bolsines.persistencia.ConexionBD;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Ventana de auditoría en vivo de la Base de Datos SQLite (bolsines.db).
 * Muestra el estado físico en tiempo real de Bolsines, Remitos y Documentaciones (Patrón State),
 * así como los historiales de cambios de estado con el legajo del empleado responsable.
 */
public class VisorBaseDatosDialog extends JDialog {

    private JTable tablaBolsinesBD;
    private DefaultTableModel modeloBolsinesBD;

    private JTable tablaRemitosBD;
    private DefaultTableModel modeloRemitosBD;

    private JTable tablaDocsBD;
    private DefaultTableModel modeloDocsBD;

    private JTable tablaHistorialDocsBD;
    private DefaultTableModel modeloHistorialDocsBD;

    private JTable tablaHistorialBolsinBD;
    private DefaultTableModel modeloHistorialBolsinBD;

    public VisorBaseDatosDialog(Frame parent) {
        super(parent, "Inspección en Tiempo Real - Base de Datos SQLite (bolsines.db)", false);
        setSize(950, 680);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JTabbedPane tabs = new JTabbedPane();

        // Pestaña 1: Bolsines, Remitos y Documentaciones (Estados Actuales)
        JPanel panelEntidades = new JPanel(new GridLayout(3, 1, 8, 8));
        panelEntidades.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Tabla Bolsines
        modeloBolsinesBD = new DefaultTableModel(
                new String[]{"ID", "N° Bolsín", "Precinto", "CM Origen", "CM Destino", "Peso (gr)", "Estado Actual en BD"}, 0);
        tablaBolsinesBD = new JTable(modeloBolsinesBD);
        tablaBolsinesBD.setRowHeight(20);
        JScrollPane scrollBolsines = new JScrollPane(tablaBolsinesBD);
        scrollBolsines.setBorder(BorderFactory.createTitledBorder("Tabla: bolsin (Estado del Bolsín)"));
        panelEntidades.add(scrollBolsines);

        // 2. Tabla Remitos (Requerimiento agregado)
        modeloRemitosBD = new DefaultTableModel(
                new String[]{"ID", "N° Remito", "ID Bolsín", "Fecha Alta", "Emisor", "Destino", "Estado Actual en BD"}, 0);
        tablaRemitosBD = new JTable(modeloRemitosBD);
        tablaRemitosBD.setRowHeight(20);
        JScrollPane scrollRemitos = new JScrollPane(tablaRemitosBD);
        scrollRemitos.setBorder(BorderFactory.createTitledBorder("Tabla: remito (Estado del Remito asociado)"));
        panelEntidades.add(scrollRemitos);

        // 3. Tabla Documentaciones (Patrón State)
        modeloDocsBD = new DefaultTableModel(
                new String[]{"ID", "N° Doc", "Asunto", "ID Detalle Remito", "Tipo", "Estado Actual en BD (Patrón State)"}, 0);
        tablaDocsBD = new JTable(modeloDocsBD);
        tablaDocsBD.setRowHeight(20);
        JScrollPane scrollDocs = new JScrollPane(tablaDocsBD);
        scrollDocs.setBorder(BorderFactory.createTitledBorder("Tabla: documentacion (Patrón State de Documentación)"));
        panelEntidades.add(scrollDocs);

        tabs.addTab("Estados Actuales (bolsin / remito / documentacion)", panelEntidades);

        // Pestaña 2: Historiales de Cambios de Estado con Legajo de Responsable
        JPanel panelHistoriales = new JPanel(new GridLayout(2, 1, 8, 8));
        panelHistoriales.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Historial Documentación
        modeloHistorialDocsBD = new DefaultTableModel(
                new String[]{"ID", "ID Doc", "Asunto Doc", "Estado Registrado", "Fecha Inicio", "Fecha Fin", "Responsable (Legajo y Nombre)"}, 0);
        tablaHistorialDocsBD = new JTable(modeloHistorialDocsBD);
        tablaHistorialDocsBD.setRowHeight(20);
        JScrollPane scrollHistorialDoc = new JScrollPane(tablaHistorialDocsBD);
        scrollHistorialDoc.setBorder(BorderFactory.createTitledBorder("Tabla: cambio_estado_documentacion (Trazabilidad con Legajo)"));
        panelHistoriales.add(scrollHistorialDoc);

        // Historial Bolsín
        modeloHistorialBolsinBD = new DefaultTableModel(
                new String[]{"ID", "ID Bolsín", "N° Bolsín", "Estado Registrado", "Fecha Inicio", "Fecha Fin", "Responsable (Legajo y Nombre)"}, 0);
        tablaHistorialBolsinBD = new JTable(modeloHistorialBolsinBD);
        tablaHistorialBolsinBD.setRowHeight(20);
        JScrollPane scrollHistorialBolsin = new JScrollPane(tablaHistorialBolsinBD);
        scrollHistorialBolsin.setBorder(BorderFactory.createTitledBorder("Tabla: cambio_estado_bolsin (Trazabilidad con Legajo)"));
        panelHistoriales.add(scrollHistorialBolsin);

        tabs.addTab("Historial de Cambios de Estado (Trazabilidad y Responsable)", panelHistoriales);

        add(tabs, BorderLayout.CENTER);

        // Panel Sur con botón de refresco
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnRefrescar = new JButton("↻ Actualizar Datos de SQLite");
        btnRefrescar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnRefrescar.setBackground(new Color(0, 123, 255));
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.addActionListener(e -> cargarDatosDesdeSQLite());

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> setVisible(false));

        panelSur.add(btnRefrescar);
        panelSur.add(btnCerrar);
        add(panelSur, BorderLayout.SOUTH);

        cargarDatosDesdeSQLite();
    }

    public void cargarDatosDesdeSQLite() {
        modeloBolsinesBD.setRowCount(0);
        modeloRemitosBD.setRowCount(0);
        modeloDocsBD.setRowCount(0);
        modeloHistorialDocsBD.setRowCount(0);
        modeloHistorialBolsinBD.setRowCount(0);

        try (Connection conn = ConexionBD.getConexion(); Statement stmt = conn.createStatement()) {
            // 1. Cargar Bolsines
            String sqlB = "SELECT b.id, b.numero_identificador, b.numero_precinto, cmo.nombre AS cm_ori, cmd.nombre AS cm_des, b.peso, b.estado_actual " +
                    "FROM bolsin b " +
                    "LEFT JOIN comision_medica cmo ON b.id_cm_origen = cmo.id " +
                    "LEFT JOIN comision_medica cmd ON b.id_cm_destino = cmd.id " +
                    "ORDER BY b.id ASC";
            try (ResultSet rs = stmt.executeQuery(sqlB)) {
                while (rs.next()) {
                    modeloBolsinesBD.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getLong("numero_identificador"),
                            rs.getString("numero_precinto"),
                            rs.getString("cm_ori"),
                            rs.getString("cm_des"),
                            rs.getDouble("peso"),
                            rs.getString("estado_actual")
                    });
                }
            }

            // 2. Cargar Remitos
            String sqlR = "SELECT r.id, r.numero_remito, r.id_bolsin, r.fecha_hora_alta, " +
                    "(e.nombre || ' ' || e.apellido || ' [' || e.legajo || ']') AS emisor, " +
                    "cm.nombre AS cm_dest, r.estado " +
                    "FROM remito r " +
                    "LEFT JOIN empleado e ON r.id_empleado_emisor = e.id " +
                    "LEFT JOIN comision_medica cm ON r.id_cm_destino = cm.id " +
                    "ORDER BY r.id ASC";
            try (ResultSet rs = stmt.executeQuery(sqlR)) {
                while (rs.next()) {
                    modeloRemitosBD.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getLong("numero_remito"),
                            rs.getInt("id_bolsin"),
                            rs.getString("fecha_hora_alta"),
                            rs.getString("emisor"),
                            rs.getString("cm_dest"),
                            rs.getString("estado")
                    });
                }
            }

            // 3. Cargar Documentaciones
            String sqlD = "SELECT d.id, d.numero_identificador, d.asunto, d.id_detalle_remito, td.nombre AS tipo, d.estado_actual " +
                    "FROM documentacion d " +
                    "LEFT JOIN tipo_documento td ON d.id_tipo_documento = td.id " +
                    "ORDER BY d.id ASC";
            try (ResultSet rs = stmt.executeQuery(sqlD)) {
                while (rs.next()) {
                    modeloDocsBD.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getLong("numero_identificador"),
                            rs.getString("asunto"),
                            rs.getInt("id_detalle_remito"),
                            rs.getString("tipo"),
                            rs.getString("estado_actual")
                    });
                }
            }

            // 4. Cargar Historial Documentación con Legajo de Responsable
            String sqlHD = "SELECT ced.id, ced.id_documentacion, d.asunto, ced.estado, ced.fecha_hora_inicio, ced.fecha_hora_fin, " +
                    "e.legajo, e.nombre, e.apellido " +
                    "FROM cambio_estado_documentacion ced " +
                    "LEFT JOIN documentacion d ON ced.id_documentacion = d.id " +
                    "LEFT JOIN empleado e ON ced.id_empleado_responsable = e.id " +
                    "ORDER BY ced.id DESC";
            try (ResultSet rs = stmt.executeQuery(sqlHD)) {
                while (rs.next()) {
                    String finStr = rs.getString("fecha_hora_fin");
                    String legajo = rs.getString("legajo");
                    String nomResp = (legajo != null) ? legajo + " - " + rs.getString("nombre") + " " + rs.getString("apellido") : "N/A";

                    modeloHistorialDocsBD.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getInt("id_documentacion"),
                            rs.getString("asunto"),
                            rs.getString("estado"),
                            rs.getString("fecha_hora_inicio"),
                            (finStr != null && !finStr.isEmpty()) ? finStr : "ACTUAL (null)",
                            nomResp
                    });
                }
            }

            // 5. Cargar Historial Bolsín con Legajo de Responsable
            String sqlHB = "SELECT ceb.id, ceb.id_bolsin, b.numero_identificador, ceb.estado, ceb.fecha_hora_inicio, ceb.fecha_hora_fin, " +
                    "e.legajo, e.nombre, e.apellido " +
                    "FROM cambio_estado_bolsin ceb " +
                    "LEFT JOIN bolsin b ON ceb.id_bolsin = b.id " +
                    "LEFT JOIN empleado e ON ceb.id_empleado_responsable = e.id " +
                    "ORDER BY ceb.id DESC";
            try (ResultSet rs = stmt.executeQuery(sqlHB)) {
                while (rs.next()) {
                    String finStr = rs.getString("fecha_hora_fin");
                    String legajo = rs.getString("legajo");
                    String nomResp = (legajo != null) ? legajo + " - " + rs.getString("nombre") + " " + rs.getString("apellido") : "N/A";

                    modeloHistorialBolsinBD.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getInt("id_bolsin"),
                            rs.getLong("numero_identificador"),
                            rs.getString("estado"),
                            rs.getString("fecha_hora_inicio"),
                            (finStr != null && !finStr.isEmpty()) ? finStr : "ACTUAL (null)",
                            nomResp
                    });
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error leyendo datos de SQLite: " + e.getMessage(),
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
