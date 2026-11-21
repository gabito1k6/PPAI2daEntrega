package com.utn.bolsines.pantallas;

import com.utn.bolsines.entidades.*;
import com.utn.bolsines.entidades.estados.EstadoBolsinEnviado;
import com.utn.bolsines.entidades.estados.EstadoEnBolsinEnviado;
import com.utn.bolsines.entidades.estados.EstadoRemitoEnBolsinEnviado;
import com.utn.bolsines.persistencia.RepositorioBolsin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Diálogo para registrar y enviar un nuevo Bolsín con su Remito y Documentación
 * directamente desde la interfaz gráfica interactiva.
 * Permite persistir en SQLite nuevos casos de prueba para el CU 28 (Grupo 8).
 */
public class DialogoCrearBolsin extends JDialog {

    private final PantallaRecepcionBolsin pantallaPadre;

    private JTextField txtNumeroBolsin;
    private JTextField txtNumeroPrecinto;
    private JTextField txtPeso;
    private JComboBox<ComisionMedica> cmbOrigen;
    private JComboBox<ComisionMedica> cmbDestino;

    private JTextField txtNumeroRemito;
    private JTextField txtAsuntoDoc;
    private JTextField txtNumeroDoc;
    private JComboBox<TipoDocumento> cmbTipoDoc;

    public DialogoCrearBolsin(PantallaRecepcionBolsin parent) {
        super(parent, "Crear y Enviar Nuevo Bolsín (Simulación de Origen)", true);
        this.pantallaPadre = parent;

        setSize(560, 560);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        construirInterfaz();
    }

    private void construirInterfaz() {
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(new EmptyBorder(15, 20, 15, 20));

        RepositorioBolsin repo = RepositorioBolsin.getInstancia();
        int proxIdBolsin = repo.obtenerMaxId("bolsin");
        int proxIdRemito = repo.obtenerMaxId("remito");
        int proxIdDoc = repo.obtenerMaxId("documentacion");
        int randomPrec = 100000 + new Random().nextInt(900000);

        // --- SECCIÓN 1: DATOS DEL BOLSÍN ---
        JPanel panelBolsin = new JPanel(new GridLayout(5, 2, 8, 8));
        panelBolsin.setBorder(new TitledBorder("1. Datos del Bolsín"));

        panelBolsin.add(new JLabel("N° Identificador Bolsín:"));
        txtNumeroBolsin = new JTextField(String.valueOf(300 + proxIdBolsin));
        panelBolsin.add(txtNumeroBolsin);

        panelBolsin.add(new JLabel("N° Precinto (ej: PREC-123456):"));
        txtNumeroPrecinto = new JTextField("PREC-" + randomPrec);
        panelBolsin.add(txtNumeroPrecinto);

        panelBolsin.add(new JLabel("Peso en gramos:"));
        txtPeso = new JTextField("1650.0");
        panelBolsin.add(txtPeso);

        panelBolsin.add(new JLabel("Comisión Médica Origen:"));
        cmbOrigen = new JComboBox<>();
        panelBolsin.add(cmbOrigen);

        panelBolsin.add(new JLabel("Comisión Médica Destino:"));
        cmbDestino = new JComboBox<>();
        panelBolsin.add(cmbDestino);

        // Cargar comisiones médicas
        List<ComisionMedica> comisiones = repo.obtenerComisiones();
        DefaultListCellRenderer cmRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ComisionMedica cm) {
                    setText(cm.getNombre() + " (" + cm.getDireccion() + ")");
                }
                return this;
            }
        };
        cmbOrigen.setRenderer(cmRenderer);
        cmbDestino.setRenderer(cmRenderer);

        for (ComisionMedica cm : comisiones) {
            cmbOrigen.addItem(cm);
            cmbDestino.addItem(cm);
        }

        // Configurar selección por defecto: Origen Córdoba (ID 1) y Destino Villa María (ID 2)
        if (comisiones.size() > 1) {
            cmbOrigen.setSelectedIndex(0);
            cmbDestino.setSelectedIndex(1);
        }

        panelPrincipal.add(panelBolsin);
        panelPrincipal.add(Box.createVerticalStrut(10));

        // --- SECCIÓN 2: DATOS DEL REMITO Y DOCUMENTACIÓN ASOCIADA ---
        JPanel panelRemitoDoc = new JPanel(new GridLayout(4, 2, 8, 8));
        panelRemitoDoc.setBorder(new TitledBorder("2. Remito y Documentación Contenida"));

        panelRemitoDoc.add(new JLabel("N° Remito:"));
        txtNumeroRemito = new JTextField(String.valueOf(5000 + proxIdRemito));
        panelRemitoDoc.add(txtNumeroRemito);

        panelRemitoDoc.add(new JLabel("N° Identificador Doc:"));
        txtNumeroDoc = new JTextField(String.valueOf(7000 + proxIdDoc));
        panelRemitoDoc.add(txtNumeroDoc);

        panelRemitoDoc.add(new JLabel("Asunto del Documento:"));
        txtAsuntoDoc = new JTextField("Expediente Dictamen SRT N° " + randomPrec);
        panelRemitoDoc.add(txtAsuntoDoc);

        panelRemitoDoc.add(new JLabel("Tipo de Documentación:"));
        cmbTipoDoc = new JComboBox<>();
        DefaultListCellRenderer tipoRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof TipoDocumento td) {
                    setText(td.getNombre());
                }
                return this;
            }
        };
        cmbTipoDoc.setRenderer(tipoRenderer);

        List<TipoDocumento> tipos = repo.obtenerTiposDocumento();
        for (TipoDocumento td : tipos) {
            cmbTipoDoc.addItem(td);
        }
        panelRemitoDoc.add(cmbTipoDoc);

        panelPrincipal.add(panelRemitoDoc);
        add(panelPrincipal, BorderLayout.CENTER);

        // --- PANEL DE BOTONES ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = new JButton("💾 Guardar y Enviar a Destino");
        btnGuardar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnGuardar.setBackground(new Color(40, 167, 69));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> guardarBolsin());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void guardarBolsin() {
        try {
            long nroBolsin = Long.parseLong(txtNumeroBolsin.getText().trim());
            String precinto = txtNumeroPrecinto.getText().trim();
            double peso = Double.parseDouble(txtPeso.getText().trim());
            ComisionMedica cmOri = (ComisionMedica) cmbOrigen.getSelectedItem();
            ComisionMedica cmDes = (ComisionMedica) cmbDestino.getSelectedItem();

            long nroRemito = Long.parseLong(txtNumeroRemito.getText().trim());
            long nroDoc = Long.parseLong(txtNumeroDoc.getText().trim());
            String asunto = txtAsuntoDoc.getText().trim();
            TipoDocumento tipoDoc = (TipoDocumento) cmbTipoDoc.getSelectedItem();

            if (precinto.isEmpty() || asunto.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Por favor complete todos los campos obligatorios.",
                        "Datos incompletos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            RepositorioBolsin repo = RepositorioBolsin.getInstancia();
            int idBolsin = repo.obtenerMaxId("bolsin");
            int idRemito = repo.obtenerMaxId("remito");
            int idDetalle = repo.obtenerMaxId("detalle_remito");
            int idDoc = repo.obtenerMaxId("documentacion");

            // Buscar empleado emisor acorde a la CM de origen (o default Carlos Pérez)
            List<Empleado> empleados = repo.obtenerTodosLosEmpleados();
            Empleado emisor = null;
            for (Empleado emp : empleados) {
                if (emp.esTuCM(cmOri)) {
                    emisor = emp;
                    break;
                }
            }
            if (emisor == null && !empleados.isEmpty()) {
                emisor = empleados.get(0);
            }

            LocalDateTime ahora = LocalDateTime.now();

            // Construir objetos de dominio respetando el modelo y GoF State
            Documentacion doc = new Documentacion(
                    idDoc,
                    nroDoc,
                    asunto,
                    LocalDate.now(),
                    tipoDoc,
                    cmOri,
                    new EstadoEnBolsinEnviado(),
                    emisor
            );

            DetalleRemito detalle = new DetalleRemito(idDetalle, 1, "Detalle Remito " + nroRemito, doc);

            Remito remito = new Remito(idRemito, nroRemito, ahora, emisor, cmDes);
            remito.setEstado(new EstadoRemitoEnBolsinEnviado());
            remito.agregarDetalle(detalle);

            Bolsin nuevoBolsin = new Bolsin(
                    idBolsin,
                    nroBolsin,
                    precinto,
                    ahora,
                    peso,
                    cmOri,
                    cmDes,
                    new EstadoBolsinEnviado(),
                    emisor
            );
            nuevoBolsin.asociarRemito(remito);

            // Persistir en SQLite mediante el Repositorio
            boolean exito = repo.crearNuevoBolsinConRemitoYDoc(nuevoBolsin, emisor);

            if (exito) {
                JOptionPane.showMessageDialog(this,
                        "¡Bolsín N° " + nroBolsin + " registrado y enviado exitosamente!\n\n" +
                                "• N° Precinto: " + precinto + "\n" +
                                "• Remito: #" + nroRemito + "\n" +
                                "• Documentación: #" + nroDoc + " (" + asunto + ")\n" +
                                "• Destino: " + (cmDes != null ? cmDes.getNombre() : "N/A") + "\n\n" +
                                "El bolsín quedó en estado 'Enviado' en SQLite y listo para ser recibido.",
                        "Bolsín Creado",
                        JOptionPane.INFORMATION_MESSAGE);

                if (pantallaPadre != null) {
                    pantallaPadre.recargarBolsinesDesdeBD();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Ocurrió un error al persistir el nuevo bolsín en la base de datos.",
                        "Error de Persistencia",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese valores numéricos válidos en los campos de N° Bolsín, Remito, Documento y Peso.",
                    "Formato Inválido",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
