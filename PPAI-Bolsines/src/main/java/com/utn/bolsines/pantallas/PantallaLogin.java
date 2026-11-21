package com.utn.bolsines.pantallas;

import com.utn.bolsines.entidades.*;
import com.utn.bolsines.entidades.estados.*;
import com.utn.bolsines.persistencia.RepositorioBolsin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla de Inicio de Sesión (Login) para el Sistema de Gestión de Bolsines.
 * Permite seleccionar el empleado autenticado y su Comisión Médica para ejecutar el CU 28.
 * Diseñada de forma simple, robusta y amigable para el usuario (Grupo 8).
 */
public class PantallaLogin extends JFrame {

    private JComboBox<Empleado> cmbUsuarios;
    private JPasswordField txtPassword;
    private JLabel lblDetalleEmpleado;
    private JLabel lblCMInfo;
    private List<Empleado> listaEmpleados;
    private List<Estado> catalogoEstados;

    public PantallaLogin() {
        this(null, null);
    }

    public PantallaLogin(List<Empleado> empleados, List<Estado> catalogo) {
        super("Iniciar Sesión - Sistema de Bolsines (Grupo 8)");
        this.listaEmpleados = empleados != null ? empleados : RepositorioBolsin.getInstancia().obtenerTodosLosEmpleados();
        this.catalogoEstados = catalogo != null ? catalogo : construirCatalogoEstados();

        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 380);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        // --- ENCABEZADO ---
        JPanel panelEncabezado = new JPanel(new GridLayout(2, 1, 3, 3));
        panelEncabezado.setBackground(new Color(33, 37, 41));
        panelEncabezado.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("SISTEMA DE GESTIÓN DE BOLSINES");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel lblSubtitulo = new JLabel("UTN - DSI 2026 | CU 28: Registrar Recepción de Bolsín (Grupo 8)");
        lblSubtitulo.setForeground(new Color(200, 200, 200));
        lblSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));

        panelEncabezado.add(lblTitulo);
        panelEncabezado.add(lblSubtitulo);
        add(panelEncabezado, BorderLayout.NORTH);

        // --- FORMULARIO CENTRAL ---
        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));
        panelCentro.setBorder(new EmptyBorder(15, 25, 15, 25));

        JPanel panelCampos = new JPanel(new GridLayout(2, 2, 10, 12));
        panelCampos.setBorder(new TitledBorder("Credenciales de Acceso"));

        panelCampos.add(new JLabel("Seleccionar Usuario:"));
        cmbUsuarios = new JComboBox<>();

        DefaultListCellRenderer empRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Empleado emp) {
                    setText(emp.getNombre() + " " + emp.getApellido() + " (" + emp.getUsuario() + ")");
                }
                return this;
            }
        };
        cmbUsuarios.setRenderer(empRenderer);

        Empleado usuarioDefecto = null;
        for (Empleado emp : listaEmpleados) {
            cmbUsuarios.addItem(emp);
            // Preseleccionar a Ana González (CM Villa María - Destino para el CU 28)
            if (emp.getId() == 20 || "agonzalez".equalsIgnoreCase(emp.getUsuario())) {
                usuarioDefecto = emp;
            }
        }
        if (usuarioDefecto != null) {
            cmbUsuarios.setSelectedItem(usuarioDefecto);
        }

        panelCampos.add(cmbUsuarios);

        panelCampos.add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField("1234");
        panelCampos.add(txtPassword);

        panelCentro.add(panelCampos);
        panelCentro.add(Box.createVerticalStrut(10));

        // Panel de información del usuario seleccionado
        JPanel panelInfo = new JPanel(new GridLayout(2, 1, 4, 4));
        panelInfo.setBorder(new TitledBorder("Datos del Empleado"));

        lblDetalleEmpleado = new JLabel("Legajo: - | Empleado: -");
        lblDetalleEmpleado.setFont(new Font("SansSerif", Font.BOLD, 12));

        lblCMInfo = new JLabel("Comisión Médica: -");
        lblCMInfo.setFont(new Font("SansSerif", Font.PLAIN, 12));

        panelInfo.add(lblDetalleEmpleado);
        panelInfo.add(lblCMInfo);
        panelCentro.add(panelInfo);

        actualizarInfoUsuario();
        cmbUsuarios.addActionListener(e -> actualizarInfoUsuario());

        add(panelCentro, BorderLayout.CENTER);

        // --- PANEL DE BOTONES ---
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        panelSur.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> System.exit(0));

        JButton btnIngresar = new JButton("Ingresar al Sistema →");
        btnIngresar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnIngresar.setBackground(new Color(0, 123, 255));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.addActionListener(e -> ejecutarLogin());

        // Permitir presionar ENTER para ingresar
        getRootPane().setDefaultButton(btnIngresar);

        panelSur.add(btnSalir);
        panelSur.add(btnIngresar);
        add(panelSur, BorderLayout.SOUTH);
    }

    private void actualizarInfoUsuario() {
        Empleado seleccionado = (Empleado) cmbUsuarios.getSelectedItem();
        if (seleccionado != null) {
            lblDetalleEmpleado.setText("Legajo: " + seleccionado.getLegajo() + " | ID Empleado: " + seleccionado.getId() +
                    " | " + seleccionado.getNombre() + " " + seleccionado.getApellido());
            lblCMInfo.setText("Comisión Médica: " + (seleccionado.getCM() != null ? seleccionado.getCM().getNombre() : "Sin asignar"));
        }
    }

    private void ejecutarLogin() {
        Empleado seleccionado = (Empleado) cmbUsuarios.getSelectedItem();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String pass = new String(txtPassword.getPassword()).trim();
        if (pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese su contraseña (por defecto: 1234).", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("\n[Login] Usuario autenticado: " + seleccionado.getNombre() + " " + seleccionado.getApellido() +
                " (Legajo: " + seleccionado.getLegajo() + ", CM: " + (seleccionado.getCM() != null ? seleccionado.getCM().getNombre() : "N/A") + ")");

        Sesion nuevaSesion = new Sesion(seleccionado);
        List<Bolsin> bolsines = RepositorioBolsin.getInstancia().obtenerTodosLosBolsines();

        PantallaRecepcionBolsin pantalla = new PantallaRecepcionBolsin(
                nuevaSesion,
                bolsines,
                this.catalogoEstados,
                this.listaEmpleados,
                true
        );

        this.setVisible(false);
        pantalla.opRegistrarRecepBolsin();
    }

    private static List<Estado> construirCatalogoEstados() {
        List<Estado> catalogo = new ArrayList<>();
        catalogo.add(new EstadoBolsinCreado());
        catalogo.add(new EstadoBolsinCerrado());
        catalogo.add(new EstadoBolsinEnviado());
        catalogo.add(new EstadoBolsinRecibidoEnCMDestino());
        catalogo.add(new EstadoBolsinDeBaja());

        catalogo.add(new EstadoRemitoCreado());
        catalogo.add(new EstadoRemitoEnBolsinSaliente());
        catalogo.add(new EstadoRemitoEnBolsinEnviado());
        catalogo.add(new EstadoRemitoRecibidoYAceptado());
        catalogo.add(new EstadoRemitoCancelado());

        catalogo.add(new EstadoRegistrada());
        catalogo.add(new EstadoEnRemito());
        catalogo.add(new EstadoEnBolsinSaliente());
        catalogo.add(new EstadoEnBolsinEnviado());
        catalogo.add(new EstadoRecibidaYAceptada());
        catalogo.add(new EstadoRecibidaYRechazada());
        catalogo.add(new EstadoParaRedirigir());
        catalogo.add(new EstadoNoRecibido());
        catalogo.add(new EstadoEnBaja());
        return catalogo;
    }
}
