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
 * Permite visualizar en tiempo real cómo impactan los cambios de estado
 * y las transiciones del Patrón State en las tablas físicas de la base de datos.
 */
public class VisorBaseDatosDialog extends JDialog {

    private JTable tablaBolsinesBD;
    private DefaultTableModel modeloBolsinesBD;

    private JTable tablaDocsBD;
    private DefaultTableModel modeloDocsBD;

    private JTable tablaHistorialDocsBD;
    private DefaultTableModel modeloHistorialDocsBD;

    public VisorBaseDatosDialog(Frame parent) {
        super(parent, "Inspección en Tiempo Real - Base de Datos SQLite (bolsines.db)", false);
        setSize(850, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JTabbedPane tabs = new JTabbedPane();

        // Pestaña 1: Bolsines y Documentación (Estados Actuales)
        JPanel panelEntidades = new JPanel(new GridLayout(2, 1, 10, 10));
        panelEntidades.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tabla Bolsines
        modeloBolsinesBD = new DefaultTableModel(new String[]{"ID", "N° Bolsín", "Precinto", "Estado Actual en BD"}, 0);
        tablaBolsinesBD = new JTable(modeloBolsinesBD);
        JScrollPane scrollBolsines = new JScrollPane(tablaBolsinesBD);
        scrollBolsines.setBorder(BorderFactory.createTitledBorder("Tabla: bolsin"));
        panelEntidades.add(scrollBolsines);

        // Tabla Documentaciones
        modeloDocsBD = new DefaultTableModel(new String[]{"ID", "N° Doc", "Asunto", "Estado Actual en BD (Patrón State)"}, 0);
        tablaDocsBD = new JTable(modeloDocsBD);
        JScrollPane scrollDocs = new JScrollPane(tablaDocsBD);
        scrollDocs.setBorder(BorderFactory.createTitledBorder("Tabla: documentacion"));
        panelEntidades.add(scrollDocs);

        tabs.addTab("Estados Actuales (bolsin / documentacion)", panelEntidades);

        // Pestaña 2: Historial de Cambios de Estado (Trazabilidad)
        JPanel panelHistorial = new JPanel(new BorderLayout());
        panelHistorial.setBorder(new EmptyBorder(10, 10, 10, 10));

        modeloHistorialDocsBD = new DefaultTableModel(
                new String[]{"ID", "ID Doc", "Estado Registrado", "Fecha/Hora Inicio", "Fecha/Hora Fin", "ID Resp"}, 0);
        tablaHistorialDocsBD = new JTable(modeloHistorialDocsBD);
        JScrollPane scrollHistorial = new JScrollPane(tablaHistorialDocsBD);
        scrollHistorial.setBorder(BorderFactory.createTitledBorder("Tabla: cambio_estado_documentacion (Trazabilidad histórica)"));
        panelHistorial.add(scrollHistorial, BorderLayout.CENTER);

        tabs.addTab("Historial de Estados (cambio_estado_documentacion)", panelHistorial);

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
        modeloDocsBD.setRowCount(0);
        modeloHistorialDocsBD.setRowCount(0);

        try (Connection conn = ConexionBD.getConexion(); Statement stmt = conn.createStatement()) {
            // Cargar Bolsines
            try (ResultSet rs = stmt.executeQuery("SELECT id, numero_identificador, numero_precinto, estado_actual FROM bolsin ORDER BY id ASC")) {
                while (rs.next()) {
                    modeloBolsinesBD.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getLong("numero_identificador"),
                            rs.getString("numero_precinto"),
                            rs.getString("estado_actual")
                    });
                }
            }

            // Cargar Documentaciones
            try (ResultSet rs = stmt.executeQuery("SELECT id, numero_identificador, asunto, estado_actual FROM documentacion ORDER BY id ASC")) {
                while (rs.next()) {
                    modeloDocsBD.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getLong("numero_identificador"),
                            rs.getString("asunto"),
                            rs.getString("estado_actual")
                    });
                }
            }

            // Cargar Historial Documentación
            try (ResultSet rs = stmt.executeQuery("SELECT id, id_documentacion, estado, fecha_hora_inicio, fecha_hora_fin, id_empleado_responsable FROM cambio_estado_documentacion ORDER BY id DESC")) {
                while (rs.next()) {
                    String finStr = rs.getString("fecha_hora_fin");
                    modeloHistorialDocsBD.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getInt("id_documentacion"),
                            rs.getString("estado"),
                            rs.getString("fecha_hora_inicio"),
                            (finStr != null && !finStr.isEmpty()) ? finStr : "ACTUAL (null)",
                            rs.getInt("id_empleado_responsable")
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
