package com.utn.bolsines.pantallas;

import com.utn.bolsines.entidades.*;
import com.utn.bolsines.entidades.estados.Estado;
import com.utn.bolsines.gestores.GestorRecepcionBolsin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa Frontera / Pantalla del CU 28: Registrar Recepción de Bolsín.
 * Implementada con Java Swing (Tecnología de Escritorio interactiva).
 * Cumple con el Flujo Básico y los Flujos Alternativos A1, A2, A3, A4, A5 y A6.
 */
public class PantallaRecepcionBolsin extends JFrame {

    private GestorRecepcionBolsin gestor;

    // Dependencias
    private Sesion sesionActual;
    private List<Bolsin> bolsinesDisponibles;
    private List<Estado> catalogoEstados;
    private List<Empleado> empleadosRegistrados;

    // Componentes visuales Swing
    private JLabel lblUsuarioLogueado;
    private JLabel lblCMUsuario;
    private JTextField txtFiltroPrecinto;
    private JTextField txtFiltroOrigen;
    private JTable tablaBolsines;
    private DefaultTableModel modeloBolsines;
    private List<Bolsin> bolsinesEnTabla = new ArrayList<>();

    private JTextArea txtInfoRemitos;
    private JPanel panelOpciones;
    private ButtonGroup grupoOpciones;
    private JRadioButton rbOpcion1;
    private JRadioButton rbOpcion2;
    private JRadioButton rbOpcion3;
    private JRadioButton rbOpcion4;

    private JPanel panelSeleccionDoc;
    private List<JCheckBox> chkDocsList = new ArrayList<>();
    private List<Documentacion> docsBolsinActual = new ArrayList<>();

    private JButton btnConfirmar;
    private JButton btnCancelar;
    private JButton btnBuscar;
    private JButton btnLimpiarFiltro;

    private boolean esInteractivo = true;

    public PantallaRecepcionBolsin() {
        this(null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), !GraphicsEnvironment.isHeadless());
    }

    public PantallaRecepcionBolsin(Sesion sesionActual, List<Bolsin> bolsinesDisponibles,
                                  List<Estado> catalogoEstados, List<Empleado> empleadosRegistrados) {
        this(sesionActual, bolsinesDisponibles, catalogoEstados, empleadosRegistrados, !GraphicsEnvironment.isHeadless());
    }

    public PantallaRecepcionBolsin(Sesion sesionActual, List<Bolsin> bolsinesDisponibles,
                                  List<Estado> catalogoEstados, List<Empleado> empleadosRegistrados,
                                  boolean esInteractivo) {
        super("CU 28 - Registrar Recepción de Bolsín (Grupo 8)");
        this.sesionActual = sesionActual;
        this.bolsinesDisponibles = bolsinesDisponibles;
        this.catalogoEstados = catalogoEstados;
        this.empleadosRegistrados = empleadosRegistrados;
        this.esInteractivo = esInteractivo && !GraphicsEnvironment.isHeadless();

        if (this.esInteractivo) {
            construirInterfaz();
        }
    }

    /**
     * Paso 1 del CU: El actor selecciona la opción para registrar la recepción de un bolsín.
     * Instancia el GestorRecepcionBolsin con new() y dispara registrarRecepcion().
     */
    public void opRegistrarRecepBolsin() {
        habilitarPantalla();

        // Corrección 4: Instanciación explícita con new() del Gestor
        this.gestor = new GestorRecepcionBolsin(
                this,
                this.sesionActual,
                this.bolsinesDisponibles,
                this.catalogoEstados,
                this.empleadosRegistrados
        );

        this.gestor.registrarRecepcion();
    }

    public void habilitarPantalla() {
        if (esInteractivo) {
            setVisible(true);
            toFront();
        }
        System.out.println("\n=======================================================");
        System.out.println(">>> PANTALLA: REGISTRAR RECEPCIÓN DE BOLSÍN ACTIVADA <<<");
        System.out.println("=======================================================");
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(920, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel Encabezado (Paso 2: Datos de CM y usuario logueado)
        JPanel panelNorte = new JPanel(new BorderLayout(10, 10));
        panelNorte.setBackground(new Color(240, 244, 248));
        panelNorte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JPanel panelInfoUsuario = new JPanel(new GridLayout(2, 1, 4, 4));
        panelInfoUsuario.setOpaque(false);
        lblUsuarioLogueado = new JLabel("Usuario Logueado: " + (sesionActual != null ? sesionActual.getUsuario() : "N/A"));
        lblUsuarioLogueado.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblCMUsuario = new JLabel("Comisión Médica de Destino: Cargando...");
        lblCMUsuario.setFont(new Font("SansSerif", Font.PLAIN, 13));
        panelInfoUsuario.add(lblUsuarioLogueado);
        panelInfoUsuario.add(lblCMUsuario);
        panelNorte.add(panelInfoUsuario, BorderLayout.CENTER);

        JButton btnCerrarSesion = new JButton("🚪 Cerrar Sesión");
        btnCerrarSesion.setFocusable(false);
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        panelNorte.add(btnCerrarSesion, BorderLayout.EAST);

        add(panelNorte, BorderLayout.NORTH);

        // Panel Central: Filtros + Tabla de Bolsines + Detalle Remitos / Opciones
        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));
        panelCentro.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Subpanel Filtros (Flujos Alternativos A1 y A2)
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBorder(new TitledBorder("Filtrar Bolsines Pendientes de Recepción (Flujos A1 / A2)"));

        panelFiltros.add(new JLabel("N° Precinto:"));
        txtFiltroPrecinto = new JTextField(10);
        panelFiltros.add(txtFiltroPrecinto);

        panelFiltros.add(new JLabel("CM Origen:"));
        txtFiltroOrigen = new JTextField(12);
        panelFiltros.add(txtFiltroOrigen);

        btnBuscar = new JButton("Filtrar");
        btnLimpiarFiltro = new JButton("Limpiar");
        JButton btnCrearBolsin = new JButton("➕ Crear Nuevo Bolsín");
        btnCrearBolsin.setBackground(new Color(220, 245, 230));
        JButton btnRestablecer = new JButton("↻ Restablecer Bolsines a Enviado");
        btnRestablecer.setBackground(new Color(230, 240, 255));

        panelFiltros.add(btnBuscar);
        panelFiltros.add(btnLimpiarFiltro);
        panelFiltros.add(btnCrearBolsin);
        panelFiltros.add(btnRestablecer);

        JButton btnVerBD = new JButton("📊 Ver Base de Datos en Vivo");
        btnVerBD.setBackground(new Color(255, 243, 205));
        panelFiltros.add(btnVerBD);
        btnVerBD.addActionListener(e -> new VisorBaseDatosDialog(this).setVisible(true));
        btnCrearBolsin.addActionListener(e -> new DialogoCrearBolsin(this).setVisible(true));

        panelCentro.add(panelFiltros);

        btnBuscar.addActionListener(e -> aplicarFiltroBolsines());
        btnLimpiarFiltro.addActionListener(e -> {
            txtFiltroPrecinto.setText("");
            txtFiltroOrigen.setText("");
            if (gestor != null) {
                mostrarBolsin(gestor.buscarBolsin());
            }
        });
        btnRestablecer.addActionListener(e -> restablecerDatosPrueba());

        // Subpanel Tabla Bolsines (Paso 3 y 4 del CU)
        String[] columnas = {"N° Bolsín", "Precinto", "CM Origen", "CM Destino", "Peso (gr)", "Estado Actual"};
        modeloBolsines = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaBolsines = new JTable(modeloBolsines);
        tablaBolsines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaBolsines.setRowHeight(24);

        JScrollPane scrollTabla = new JScrollPane(tablaBolsines);
        scrollTabla.setPreferredSize(new Dimension(880, 140));
        scrollTabla.setBorder(new TitledBorder("Bolsines en estado Enviado disponibles para esta Comisión Médica"));
        panelCentro.add(scrollTabla);

        // Al seleccionar un bolsín de la tabla (Paso 5)
        tablaBolsines.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaBolsines.getSelectedRow();
                if (fila >= 0 && fila < bolsinesEnTabla.size()) {
                    Bolsin seleccionado = bolsinesEnTabla.get(fila);
                    tomarBolsin(seleccionado);
                }
            }
        });

        // Subpanel Detalle Remitos y Documentación (Paso 6)
        txtInfoRemitos = new JTextArea(4, 70);
        txtInfoRemitos.setEditable(false);
        txtInfoRemitos.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollRemitos = new JScrollPane(txtInfoRemitos);
        scrollRemitos.setBorder(new TitledBorder("Contenido del Bolsín Seleccionado (Remitos y Documentación)"));
        panelCentro.add(scrollRemitos);

        // Subpanel Opciones de Recepción (Paso 7: Flujo Básico y Alternativos A3, A4, A5)
        panelOpciones = new JPanel(new GridLayout(4, 1, 2, 2));
        panelOpciones.setBorder(new TitledBorder("Seleccione una Opción de Recepción del Bolsín"));

        grupoOpciones = new ButtonGroup();
        rbOpcion1 = new JRadioButton("1. El contenido del bolsín es igual al registrado (Flujo Básico)", true);
        rbOpcion2 = new JRadioButton("2. No se recibe toda la documentación asociada a los remitos (Flujo A3 - Faltante)");
        rbOpcion3 = new JRadioButton("3. Existe documentación que no corresponde al destino (Flujo A4 - Rechazo)");
        rbOpcion4 = new JRadioButton("4. La documentación se debe redirigir a otra área (Flujo A5 - Redirección)");

        grupoOpciones.add(rbOpcion1);
        grupoOpciones.add(rbOpcion2);
        grupoOpciones.add(rbOpcion3);
        grupoOpciones.add(rbOpcion4);

        panelOpciones.add(rbOpcion1);
        panelOpciones.add(rbOpcion2);
        panelOpciones.add(rbOpcion3);
        panelOpciones.add(rbOpcion4);
        panelCentro.add(panelOpciones);

        rbOpcion1.addActionListener(e -> actualizarVisibilidadDocsAfectados());
        rbOpcion2.addActionListener(e -> actualizarVisibilidadDocsAfectados());
        rbOpcion3.addActionListener(e -> actualizarVisibilidadDocsAfectados());
        rbOpcion4.addActionListener(e -> actualizarVisibilidadDocsAfectados());

        // Subpanel Documentos Afectados para Alternativas
        panelSeleccionDoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelSeleccionDoc.setBorder(new TitledBorder("Seleccione la documentación observada/afectada:"));
        panelSeleccionDoc.setVisible(false);
        panelCentro.add(panelSeleccionDoc);

        add(panelCentro, BorderLayout.CENTER);

        // Panel Sur: Botones de Acción (Paso 9 y 10: Confirmar / Cancelar A6)
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelSur.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        btnConfirmar = new JButton("Confirmar Recepción");
        btnConfirmar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnConfirmar.setBackground(new Color(40, 167, 69));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setEnabled(false);

        btnCancelar = new JButton("Cancelar (A6)");
        btnCancelar.setFont(new Font("SansSerif", Font.PLAIN, 13));

        panelSur.add(btnCancelar);
        panelSur.add(btnConfirmar);
        add(panelSur, BorderLayout.SOUTH);

        btnConfirmar.addActionListener(e -> ejecutarAccionConfirmar());
        btnCancelar.addActionListener(e -> ejecutarAccionCancelar());
    }

    /**
     * Paso 2 del CU: Muestra la Comisión Médica del usuario logueado.
     */
    public void mostrarDatos(String nombreCM) {
        if (lblCMUsuario != null) {
            lblCMUsuario.setText("Comisión Médica de Destino: " + nombreCM);
        }
        if (lblUsuarioLogueado != null && sesionActual != null && sesionActual.getEmpleado() != null) {
            Empleado emp = sesionActual.getEmpleado();
            lblUsuarioLogueado.setText("Usuario: " + emp.getNombre() + " " + emp.getApellido() + " (" + emp.getLegajo() + ")");
        }
        System.out.println("[Pantalla] Comisión Médica activa: " + nombreCM);
    }

    /**
     * Paso 4 del CU: Muestra los bolsines encontrados en estado enviado para la CM.
     */
    public void mostrarBolsin(List<Bolsin> bolsines) {
        this.bolsinesEnTabla = (bolsines != null) ? bolsines : new ArrayList<>();

        if (modeloBolsines != null) {
            modeloBolsines.setRowCount(0);

            if (bolsinesEnTabla.isEmpty()) {
                if (txtInfoRemitos != null) {
                    txtInfoRemitos.setText("No hay bolsines pendientes de recepción (todos fueron recibidos y guardados en SQLite).\n" +
                            "Haga clic en '↻ Restablecer Bolsines a Enviado' para volver a cargar datos y probar otro flujo.");
                }
                if (panelSeleccionDoc != null) {
                    panelSeleccionDoc.removeAll();
                    panelSeleccionDoc.revalidate();
                    panelSeleccionDoc.repaint();
                }
                if (btnConfirmar != null) btnConfirmar.setEnabled(false);
                return;
            }

            for (Bolsin b : bolsinesEnTabla) {
                modeloBolsines.addRow(new Object[]{
                        b.getNumeroIdentificador(),
                        b.getNumeroPrecinto(),
                        (b.getCmOrigen() != null ? b.getCmOrigen().getNombre() : "N/A"),
                        (b.getCmDestino() != null ? b.getCmDestino().getNombre() : "N/A"),
                        b.getPeso(),
                        (b.getEstadoActual() != null ? b.getEstadoActual().getNombre() : "N/A")
                });
            }

            if (tablaBolsines != null && tablaBolsines.getRowCount() > 0) {
                tablaBolsines.setRowSelectionInterval(0, 0);
            }
        }
    }

    private void aplicarFiltroBolsines() {
        if (gestor == null) return;
        String precinto = (txtFiltroPrecinto != null) ? txtFiltroPrecinto.getText() : "";
        String origen = (txtFiltroOrigen != null) ? txtFiltroOrigen.getText() : "";

        List<Bolsin> filtrados = gestor.buscarBolsinPorFiltro(precinto, origen);
        if (filtrados.isEmpty() && esInteractivo) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró ningún bolsín con los datos ingresados.\n(Flujo Alternativo A1 / A2)",
                    "Búsqueda sin resultados",
                    JOptionPane.WARNING_MESSAGE);
        }
        mostrarBolsin(filtrados);
    }

    /**
     * Paso 5 del CU: El actor selecciona un bolsín del listado.
     */
    public void tomarBolsin(Bolsin bolsin) {
        if (gestor != null && bolsin != null) {
            gestor.tomarBolsin(bolsin);
        }
    }

    /**
     * Paso 6 del CU: Muestra los remitos y su documentación asociada.
     */
    public void mostrarDetalleRemitos(List<String> remitosInfo) {
        if (txtInfoRemitos != null) {
            StringBuilder sb = new StringBuilder();
            for (String linea : remitosInfo) {
                sb.append(linea).append("\n");
            }
            txtInfoRemitos.setText(sb.toString());
        }

        if (panelSeleccionDoc != null) {
            panelSeleccionDoc.removeAll();
            chkDocsList.clear();
            docsBolsinActual.clear();

            if (gestor != null && gestor.getBolsinSeleccionado() != null) {
                docsBolsinActual = gestor.getBolsinSeleccionado().obtenerDocumentaciones();
                for (Documentacion doc : docsBolsinActual) {
                    JCheckBox chk = new JCheckBox("Doc #" + doc.getNumeroIdentificador() + " (" + doc.getAsunto() + ")");
                    chkDocsList.add(chk);
                    panelSeleccionDoc.add(chk);
                }
            }
            panelSeleccionDoc.revalidate();
            panelSeleccionDoc.repaint();
        }
    }

    /**
     * Paso 7 del CU: Habilita la selección de las opciones de recepción.
     */
    public void mostrarOpRecepcion() {
        if (panelOpciones != null) panelOpciones.setEnabled(true);
        if (btnConfirmar != null) btnConfirmar.setEnabled(true);
        actualizarVisibilidadDocsAfectados();
    }

    private void actualizarVisibilidadDocsAfectados() {
        if (panelSeleccionDoc != null && rbOpcion1 != null) {
            boolean requiereSeleccion = !rbOpcion1.isSelected();
            panelSeleccionDoc.setVisible(requiereSeleccion);
            panelSeleccionDoc.revalidate();
            panelSeleccionDoc.repaint();
        }
    }

    private int getOpcionSeleccionada() {
        if (rbOpcion2.isSelected()) return 2;
        if (rbOpcion3.isSelected()) return 3;
        if (rbOpcion4.isSelected()) return 4;
        return 1;
    }

    private List<Documentacion> obtenerDocumentacionesSeleccionadas() {
        List<Documentacion> seleccionadas = new ArrayList<>();
        for (int i = 0; i < chkDocsList.size(); i++) {
            if (chkDocsList.get(i).isSelected() && i < docsBolsinActual.size()) {
                seleccionadas.add(docsBolsinActual.get(i));
            }
        }
        return seleccionadas;
    }

    /**
     * Paso 8, 9 y 10: Ejecuta la selección de opción y confirmación.
     */
    private void ejecutarAccionConfirmar() {
        int opcion = getOpcionSeleccionada();
        List<Documentacion> observados = obtenerDocumentacionesSeleccionadas();

        if (opcion != 1 && observados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar al menos un documento afectado para la opción elegida.",
                    "Atención",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (gestor != null) {
            gestor.tomarOpRecepcion(opcion, observados);
        }
    }

    /**
     * Paso 9 del CU: Solicita confirmación.
     */
    public void solicitarConfirmacion() {
        int opcion = gestor != null ? gestor.getOpcionRecepcionSeleccionada() : getOpcionSeleccionada();
        String detalle = switch (opcion) {
            case 1 -> "Opción 1: Contenido íntegro recibido conforme.";
            case 2 -> "Opción 2: Documentación faltante (marcada como No Recibida).";
            case 3 -> "Opción 3: Documentación rechazada (no corresponde al destino).";
            case 4 -> "Opción 4: Documentación para redirigir a otra área.";
            default -> "";
        };

        if (esInteractivo) {
            int resp = JOptionPane.showConfirmDialog(
                    this,
                    "¿Confirma la recepción del Bolsín N° " +
                            (gestor != null && gestor.getBolsinSeleccionado() != null ? gestor.getBolsinSeleccionado().getNumeroIdentificador() : "") +
                            "?\n\n" + detalle,
                    "Confirmación de Recepción",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (resp == JOptionPane.YES_OPTION) {
                tomarConfirmacion();
            } else {
                ejecutarAccionCancelar();
            }
        } else {
            System.out.println("[Pantalla] Solicitud de confirmación emitida: " + detalle);
            System.out.println("[Usuario] Confirma la operación.");
            tomarConfirmacion();
        }
    }

    public void tomarConfirmacion() {
        if (gestor != null) {
            gestor.tomarConfirmacion();
        }
    }

    private void ejecutarAccionCancelar() {
        if (gestor != null) {
            gestor.cancelarRecepcion();
        }
    }

    /**
     * Paso 12 del CU: Informa la ejecución exitosa del caso de uso.
     * Muestra detalladamente la postcondición del Paso 11 incluyendo el legajo del responsable.
     */
    public void notificarExito(String mensaje) {
        String infoResp = "";
        if (sesionActual != null && sesionActual.getEmpleado() != null) {
            Empleado emp = sesionActual.getEmpleado();
            infoResp = "\n\n[Paso 11 - Empleado Responsable Registrado]:" +
                       "\n• Legajo: " + emp.getLegajo() + " (ID: " + emp.getId() + ")" +
                       "\n• Nombre: " + emp.getNombre() + " " + emp.getApellido() +
                       "\n• Comisión Médica: " + (emp.getCM() != null ? emp.getCM().getNombre() : "N/A");
        }

        System.out.println("\n[Pantalla - ÉXITO]\n" + mensaje + infoResp);
        System.out.println("[Persistencia] Cambios persistidos correctamente en SQLite (bolsines.db).");

        if (esInteractivo) {
            JOptionPane.showMessageDialog(this,
                    mensaje + infoResp + "\n\nSe actualizó la Base de Datos SQLite y se envió la notificación por email.",
                    "Recepción Registrada con Éxito (Paso 11 y 12)",
                    JOptionPane.INFORMATION_MESSAGE);

            // Refrescar la tabla para mostrar los estados actualizados en SQLite
            recargarBolsinesDesdeBD();
        }
    }

    public void notificarCancelacion(String mensaje) {
        System.out.println("\n[Pantalla - CANCELADO] " + mensaje);
        if (esInteractivo) {
            JOptionPane.showMessageDialog(this,
                    mensaje,
                    "Operación Cancelada (Flujo A6)",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void recargarBolsinesDesdeBD() {
        this.bolsinesDisponibles = com.utn.bolsines.persistencia.RepositorioBolsin.getInstancia().obtenerTodosLosBolsines();
        if (gestor != null) {
            gestor.setListaBolsines(this.bolsinesDisponibles);
            mostrarBolsin(gestor.buscarBolsin());
        }
    }

    private void cerrarSesion() {
        int resp = JOptionPane.showConfirmDialog(
                this,
                "¿Desea cerrar la sesión actual y volver al inicio de sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (resp == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new PantallaLogin(empleadosRegistrados, catalogoEstados).setVisible(true));
        }
    }

    public void restablecerDatosPrueba() {
        com.utn.bolsines.persistencia.RepositorioBolsin.getInstancia().reiniciarDatosPrueba();
        recargarBolsinesDesdeBD();
        if (esInteractivo) {
            JOptionPane.showMessageDialog(this,
                    "Datos de prueba restablecidos en SQLite.\nLos bolsines 301 y 302 vuelven a estar en estado 'Enviado'.",
                    "Datos Restablecidos",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public GestorRecepcionBolsin getGestor() { return gestor; }
}
