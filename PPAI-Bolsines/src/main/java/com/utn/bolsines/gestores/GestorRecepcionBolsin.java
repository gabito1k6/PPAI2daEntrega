package com.utn.bolsines.gestores;

import com.utn.bolsines.entidades.*;
import com.utn.bolsines.entidades.estados.*;
import com.utn.bolsines.pantallas.PantallaRecepcionBolsin;
import com.utn.bolsines.persistencia.RepositorioBolsin;
import com.utn.bolsines.servicios.MailServerService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * CONTROLADOR / GESTOR: GestorRecepcionBolsin (<<control>>)
 * ============================================================================
 * Orquestador principal del Caso de Uso 28: Registrar Recepción de Bolsín.
 * 
 * TIPO DE RELACIONES SEGÚN EL DIAGRAMA DE CLASES (Grupo 8):
 * - Asociación con PantallaRecepcionBolsin (Frontera / Boundary).
 * - Conoce la Sesion activa y la lista de Bolsines del sistema.
 * - Conoce el catálogo polimórfico de Estados (Corrección 6).
 * - Conoce la lista de Empleados registrados.
 * 
 * PATRONES DE DISEÑO APLICADOS:
 * - Controlador GRASP: Coordina el flujo de negocio sin manipular UI ni SQL.
 * - Patrón Experto en Información: Delega las preguntas de negocio a Bolsín.
 * - Patrón GoF STATE: En el Paso 11, delega la transición de estado a Documentación,
 *   la cual a su vez delega en su 'EstadoDocumentacion' actual.
 */
public class GestorRecepcionBolsin {

    // Referencias a componentes del sistema
    private PantallaRecepcionBolsin pantalla;
    private Sesion sesion;
    private List<Bolsin> listaBolsines;
    private List<Estado> catalogoEstados;
    private List<Empleado> listaEmpleados;

    // =========================================================================
    // ATRIBUTOS SEGÚN EL DIAGRAMA DE CLASES (GRUPO 8)
    // =========================================================================
    private Empleado usuarioLogueado;             // 'usuarioLogueado: Empleado'
    private ComisionMedica CMUsuarioLog;          // 'CMUsuarioLog' (CM del usuario)
    private List<Bolsin> bolsinesEnviados = new ArrayList<>(); // 'bolsinesEnviados'
    private ComisionMedica CMOrigen;              // 'CMOrigen'
    private List<String> datosBolsin = new ArrayList<>();      // 'datosBolsin'
    private String nroPrecinto;                   // 'nroPrecinto'
    private Bolsin bolsinSeleccionado;            // 'bolsinSeleccionado'
    private List<String> datosRemitosBolsin = new ArrayList<>(); // 'datosRemitosBolsin'
    private List<Documentacion> documentacionDelRemitos = new ArrayList<>(); // 'documentacionDelRemitos'
    private int opcionSelecc;                     // 'opcionSelecc'
    private boolean confirmacionRecepcion;        // 'confirmacionRecepcion'
    private LocalDateTime fechaHoraActual;        // 'fechaHoraActual'
    private Remito remitoSeleccionado;            // 'remitoSeleccionado'

    // Atributos adicionales de soporte
    private List<Documentacion> documentacionesObservadas = new ArrayList<>();

    // Estados de destino (Búsqueda en catálogo según Diagrama de Secuencias)
    private EstadoBolsin nuevoEstadoBolsin;
    private EstadoRemito nuevoEstadoRemito;

    public GestorRecepcionBolsin(PantallaRecepcionBolsin pantalla, Sesion sesion,
                                List<Bolsin> listaBolsines, List<Estado> catalogoEstados,
                                List<Empleado> listaEmpleados) {
        this.pantalla = pantalla;
        this.sesion = sesion;
        this.listaBolsines = (listaBolsines != null) ? listaBolsines : new ArrayList<>();
        this.catalogoEstados = (catalogoEstados != null) ? catalogoEstados : new ArrayList<>();
        this.listaEmpleados = (listaEmpleados != null) ? listaEmpleados : new ArrayList<>();
    }

    // =========================================================================
    // PASOS 1 A 4: INICIO Y BÚSQUEDA DE BOLSINES
    // =========================================================================

    /**
     * Método del Diagrama de Clases y Secuencias: registrarRecepcionBolsin()
     * Inicia el flujo del caso de uso.
     */
    public void registrarRecepcionBolsin() {
        // 1. Obtener usuario logueado de la sesión
        pedirUsuarioLogueado();

        // 2. Buscar la Comisión Médica a la que pertenece el usuario
        buscarCM();

        // 3. Mostrar Comisión Médica activa en la interfaz frontera
        if (pantalla != null && CMUsuarioLog != null) {
            pantalla.mostrarCM(CMUsuarioLog.getNombre());
        }

        // 4. Buscar bolsines en estado enviado para la CM destino (Patrón Experto)
        this.bolsinesEnviados = buscarBolsines();

        // 5. Preparar datos de presentación y solicitar selección en pantalla
        buscarDatosBolsin();
        if (pantalla != null) {
            pantalla.solicitarSeleccionBolsin(this.bolsinesEnviados);
        }
    }

    public void registrarRecepcion() {
        registrarRecepcionBolsin();
    }

    /**
     * Método del Diagrama de Secuencias: pedirUsuarioLogueado()
     * Obtiene el empleado autenticado a partir de la sesión activa.
     */
    public void pedirUsuarioLogueado() {
        if (sesion != null) {
            if (sesion.getEmpleado() != null) {
                this.usuarioLogueado = sesion.getEmpleado();
            } else if (sesion.getUsuario() != null) {
                String nombreUsuario = sesion.getUsuario();
                for (Empleado emp : listaEmpleados) {
                    if (emp.esTuUsuario(nombreUsuario)) {
                        this.usuarioLogueado = emp;
                        break;
                    }
                }
            }
        }
    }

    public void buscarEncargado() {
        pedirUsuarioLogueado();
    }

    /**
     * Método del Diagrama de Secuencias: buscarCM()
     * Solicita al empleado logueado su Comisión Médica ('logueado:Empleado.buscarCM()').
     */
    public void buscarCM() {
        if (usuarioLogueado != null) {
            this.CMUsuarioLog = usuarioLogueado.buscarCM();
        }
    }

    /**
     * Método del Diagrama de Secuencias: buscarBolsines()
     * Aplica el Patrón Experto en Información (Corrección 5 de DSI):
     * Le pregunta a cada Bolsín: 'esTuCMDestino(CMUsuarioLog)' y 'sosEnviado()'.
     */
    public List<Bolsin> buscarBolsines() {
        List<Bolsin> filtrados = new ArrayList<>();
        for (Bolsin b : listaBolsines) {
            // El Bolsín es experto en verificar su propio estado y destino
            if (b.esTuCMDestino(CMUsuarioLog) && b.sosEnviado()) {
                filtrados.add(b);
            }
        }
        return filtrados;
    }

    public List<Bolsin> buscarBolsin() {
        return buscarBolsines();
    }

    /**
     * Método del Diagrama de Secuencias: buscarDatosBolsin()
     * Para cada bolsín enviado, recopila la CM origen y el número de precinto.
     */
    public List<String> buscarDatosBolsin() {
        this.datosBolsin.clear();
        for (Bolsin b : bolsinesEnviados) {
            String origen = b.mostrarTuCMOrigen();
            String precinto = b.getNroPrecinto();
            this.datosBolsin.add("Bolsín #" + b.getNumeroBolsin() + " | Precinto: " + precinto + " | Origen: " + origen);
        }
        return this.datosBolsin;
    }

    // =========================================================================
    // FILTROS DE BÚSQUEDA (Flujos Alternativos A1 y A2)
    // =========================================================================

    public List<Bolsin> buscarBolsinPorFiltro(String precinto, String cmOrigenNombre) {
        List<Bolsin> candidatos = buscarBolsines();
        List<Bolsin> resultado = new ArrayList<>();

        for (Bolsin b : candidatos) {
            boolean coincidePrecinto = coincidePrecinto(b.getNroPrecinto(), precinto);
            boolean coincideOrigen = (cmOrigenNombre == null || cmOrigenNombre.trim().isEmpty())
                    || (b.getCmOrigen() != null && b.getCmOrigen().getNombre().toLowerCase().contains(cmOrigenNombre.trim().toLowerCase()));

            if (coincidePrecinto && coincideOrigen) {
                resultado.add(b);
            }
        }
        return resultado;
    }

    public boolean coincidePrecinto(String precintoBolsin, String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) return true;
        if (precintoBolsin == null) return false;

        String pBolsin = precintoBolsin.trim().toUpperCase();
        String pFiltro = filtro.trim().toUpperCase();

        if (pBolsin.contains(pFiltro)) return true;

        String numBolsin = pBolsin.replaceAll("[^0-9]", "");
        String numFiltro = pFiltro.replaceAll("[^0-9]", "");
        if (!numFiltro.isEmpty() && !numBolsin.isEmpty()) {
            return numBolsin.equals(numFiltro) || numBolsin.contains(numFiltro);
        }
        return false;
    }

    // =========================================================================
    // PASOS 5 Y 6: SELECCIÓN DE BOLSÍN Y BÚSQUEDA DE REMITOS
    // =========================================================================

    /**
     * Método del Diagrama de Secuencias: tomarSeleccionBolsin()
     */
    public void tomarSeleccionBolsin(Bolsin bolsin) {
        this.bolsinSeleccionado = bolsin;
        if (bolsin != null) {
            this.nroPrecinto = bolsin.getNroPrecinto();
            this.CMOrigen = bolsin.getCmOrigen();
        }

        // Paso 6: Buscar remitos y documentación contenida
        buscarRemito();

        // Paso 7: Habilitar en pantalla la selección de opciones de recepción
        if (pantalla != null) {
            pantalla.pedirSeleccionOpcionesDeRecepcion();
        }
    }

    public void tomarBolsin(Bolsin bolsin) {
        tomarSeleccionBolsin(bolsin);
    }

    public void tomarSeleccionBolsin(String numeroPrecinto) {
        for (Bolsin b : bolsinesEnviados) {
            if (coincidePrecinto(b.getNroPrecinto(), numeroPrecinto)) {
                tomarSeleccionBolsin(b);
                return;
            }
        }
    }

    /**
     * Método del Diagrama de Secuencias: buscarRemito()
     * Obtiene los remitos asociados al bolsín y la información de sus documentaciones.
     */
    public void buscarRemito() {
        if (bolsinSeleccionado == null) return;

        this.datosRemitosBolsin.clear();
        this.documentacionDelRemitos.clear();

        // bolsin.obtenerRemito()
        List<Remito> remitos = bolsinSeleccionado.obtenerRemito();
        if (remitos != null && !remitos.isEmpty()) {
            this.remitoSeleccionado = remitos.get(0);
        }

        this.datosRemitosBolsin = bolsinSeleccionado.obtenerInformacionRemito();
        this.documentacionDelRemitos = bolsinSeleccionado.obtenerDocumentaciones();

        if (pantalla != null) {
            pantalla.mostrarDatosRemitoYDocumentacion(this.datosRemitosBolsin, this.documentacionDelRemitos);
        }
    }

    // =========================================================================
    // PASOS 7 Y 8: SELECCIÓN DE OPCIÓN DE RECEPCIÓN
    // =========================================================================

    /**
     * Método del Diagrama de Clases y Secuencias: tomarSelecOpcion()
     */
    public void tomarSelecOpcion(int opcion) {
        tomarSelecOpcion(opcion, new ArrayList<>());
    }

    public void tomarSelecOpcion(int opcion, List<Documentacion> docsAfectados) {
        this.opcionSelecc = opcion;
        this.documentacionesObservadas = (docsAfectados != null) ? docsAfectados : new ArrayList<>();

        // Paso 9: La pantalla solicita confirmación al actor
        if (pantalla != null) {
            pantalla.pedirConfirmacion();
        }
    }

    public void tomarOpRecepcion(int opcion) {
        tomarSelecOpcion(opcion);
    }

    public void tomarOpRecepcion(int opcion, List<Documentacion> docsAfectados) {
        tomarSelecOpcion(opcion, docsAfectados);
    }

    /**
     * Flujo Alternativo A6: Cancelación de la operación.
     */
    public void cancelarRecepcion() {
        System.out.println("[Gestor] Flujo Alternativo A6: Operación cancelada por el usuario.");
        if (pantalla != null) {
            pantalla.notificarCancelacion("Operación cancelada. No se modificaron los estados del bolsín.");
        }
    }

    // =========================================================================
    // PASO 10 Y 11: CONFIRMACIÓN Y ACTUALIZACIÓN DE ESTADOS (PATRÓN STATE)
    // =========================================================================

    /**
     * Método del Diagrama de Secuencias: tomarConfirmacion()
     * Implementación exacta del flujo rediseñado con Patrón State (Grupo 8).
     * 1. buscarEstadoRecibidoCMDestino(): Estado
     * 2. buscarEstadoRecibidoYAceptado(): Estado
     * 3. getFechaHoraActual(): datetime
     * 4. crearCEBolsin(): void
     * 5. bolsinSeleccionado.recibir(fechaHora, responsable): void
     *    -> bolsin cierra CE anterior y propaga a remitos
     *    -> remito propaga a detalleRemito
     *    -> detalleRemito propaga a documentacion
     *    -> documentacion delega a su estado actual (:EnBolsinEnviado)
     *    -> :EnBolsinEnviado crea RecibidaYAceptada, cierra CE anterior y crea nuevo CE
     */
    public void tomarConfirmacion() {
        this.confirmacionRecepcion = true;

        // 1. Búsqueda de objetos Estado para Bolsín y Remito en el catálogo
        this.nuevoEstadoBolsin = buscarEstadoRecibidoCMDestino();
        this.nuevoEstadoRemito = buscarEstadoRecibidoYAceptado();

        // 2. Obtener fecha y hora actual del sistema
        this.fechaHoraActual = getFechaHoraActual();

        // 3. Crear Cambio de Estado de Bolsín
        crearCEBolsin();

        // 4. [PATRÓN GOF STATE]: Propagación de recepción desde Bolsín a Documentación
        actualizarEstados();

        // 5. Persistir los cambios en la base de datos relacional SQLite
        guardarEnBaseDeDatos();

        // 6. Paso 12: CU 29 Notificar recepción a la Comisión Médica de origen
        llamarCU29NotifRecepcionBolsin();

        // 7. Notificar éxito al actor en la interfaz frontera y finalizar CU
        if (pantalla != null) {
            String detalleOpcion = switch (opcionSelecc) {
                case 1 -> "Flujo Básico: Contenido íntegro recibido y aceptado.";
                case 2 -> "Flujo Alternativo A3: Recepción con " + documentacionesObservadas.size() + " documento(s) faltante(s) marcado(s) como No Recibido.";
                case 3 -> "Flujo Alternativo A4: Recepción con " + documentacionesObservadas.size() + " documento(s) rechazado(s) por no corresponder.";
                case 4 -> "Flujo Alternativo A5: Recepción con " + documentacionesObservadas.size() + " documento(s) marcado(s) para redirigir.";
                default -> "Recepción registrada.";
            };
            pantalla.notificarRecepcionBolsinRegistrada("Recepción del Bolsín N° " + bolsinSeleccionado.getNumeroBolsin() +
                    " registrada exitosamente.\n" + detalleOpcion);
        }
        finCU();
    }

    public LocalDateTime getFechaHoraActual() {
        return LocalDateTime.now();
    }

    // --- Búsqueda Polimórfica de Estados en el Catálogo (Corrección 6) ---

    public EstadoBolsin buscarEstadoRecibidoCMDestino() {
        for (Estado e : catalogoEstados) {
            if (e.esAmbitoBolsin() && (e.esRecibidoEnCMDestino() || e.esRecibidoYAceptadoEnCMDestino())) {
                return (EstadoBolsin) e;
            }
        }
        return new EstadoBolsinRecibidoEnCMDestino();
    }

    public EstadoRemito buscarEstadoRecibidoYAceptado() {
        for (Estado e : catalogoEstados) {
            if (e.esAmbitoRemito() && e.esRecibidoYAceptado()) {
                return (EstadoRemito) e;
            }
        }
        return new EstadoRemitoRecibidoYAceptado();
    }

    public EstadoDocumentacion buscarEstadoRecibidaYAceptada() {
        for (Estado e : catalogoEstados) {
            if (e.esAmbitoDocumentacion() && e.esRecibidaYAceptada()) {
                return (EstadoDocumentacion) e;
            }
        }
        return new RecibidaYAceptada();
    }

    public void buscarEstados() {
        this.nuevoEstadoBolsin = buscarEstadoRecibidoCMDestino();
        this.nuevoEstadoRemito = buscarEstadoRecibidoYAceptado();
    }

    // =========================================================================
    // [PATRÓN GOF STATE]: ACTUALIZACIÓN POLIMÓRFICA DE ESTADOS
    // =========================================================================
    /**
     * Paso 11 del CU 28:
     * - El Bolsín pasa al estado Recibido en CM destino, registrando empleado.
     * - Los Remitos pasan al estado Recibido y Aceptado.
     * - La Documentación asociada pasa a Recibida y Aceptada (o estado alternativo),
     *   registrando el empleado responsable.
     * 
     * DELEGACIÓN DEL PATRÓN STATE (GRUPO 8):
     * El Gestor NO busca estados de documentación en el catálogo.
     * Invoca bolsinSeleccionado.recibir(fechaHoraActual, usuarioLogueado).
     * El estado actual :EnBolsinEnviado es quien instancia RecibidaYAceptada,
     * cierra el CambioEstado anterior y crea el nuevo CambioEstado.
     */
    public void actualizarEstados() {
        if (bolsinSeleccionado == null) return;

        if (opcionSelecc == 1 || documentacionesObservadas.isEmpty()) {
            // Flujo básico: bolsinSeleccionado.recibir(fechaHora, responsable)
            bolsinSeleccionado.recibir(fechaHoraActual, usuarioLogueado);
        } else {
            // Flujos alternativos A3, A4, A5 (sin pasar estados desde el gestor)
            bolsinSeleccionado.recibirConAlternativas(
                    fechaHoraActual,
                    usuarioLogueado,
                    documentacionesObservadas,
                    opcionSelecc
            );
        }

        // Generar nuevo CambioEstadoBolsin con new(fechaHoraInicio) y enlazarlo
        CambioEstadoBolsin nuevoCEBolsin = new CambioEstadoBolsin(nuevoEstadoBolsin, fechaHoraActual, usuarioLogueado);
        bolsinSeleccionado.setCambioEstado(nuevoCEBolsin);
        bolsinSeleccionado.setEstado(nuevoEstadoBolsin);

        // Asegurar que los remitos tengan el nuevo estado
        if (bolsinSeleccionado.obtenerRemito() != null) {
            for (Remito r : bolsinSeleccionado.obtenerRemito()) {
                r.setEstado(nuevoEstadoRemito);
            }
        }

        // Paso 11: Exhibir en consola la actualización completa con Legajo del Responsable
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("[Paso 11 del CU 28] Actualización de Estados y Empleado Responsable:");
        System.out.println("  • Bolsín #" + bolsinSeleccionado.getNumeroBolsin() + " -> " + nuevoEstadoBolsin.getNombre() +
                " [Responsable: " + (usuarioLogueado != null ? usuarioLogueado.getNombre() + " " + usuarioLogueado.getApellido() +
                " (Legajo: " + usuarioLogueado.getLegajo() + ", ID: " + usuarioLogueado.getId() + ")" : "N/A") + "]");
        System.out.println("  • Remitos asociados -> " + nuevoEstadoRemito.getNombre());
        for (Documentacion doc : bolsinSeleccionado.obtenerDocumentaciones()) {
            System.out.println("  • Documentación #" + doc.getNumeroIdentificador() + " (" + doc.getAsunto() + ") -> " +
                    (doc.getEstadoActual() != null ? doc.getEstadoActual().getNombre() : "N/A") +
                    " [Responsable: " + (usuarioLogueado != null ? usuarioLogueado.getNombre() + " " + usuarioLogueado.getApellido() +
                    " (Legajo: " + usuarioLogueado.getLegajo() + ", ID: " + usuarioLogueado.getId() + ")" : "N/A") + "]");
        }
        System.out.println("--------------------------------------------------------------------");
    }

    public void crearCEBolsin() {
        if (bolsinSeleccionado != null && nuevoEstadoBolsin != null) {
            CambioEstadoBolsin ceActual = bolsinSeleccionado.buscarCambioEstadoActual();
            if (ceActual != null) {
                ceActual.setFechaHoraFin(fechaHoraActual);
            }
        }
    }

    public void guardarEnBaseDeDatos() {
        if (bolsinSeleccionado != null) {
            RepositorioBolsin.getInstancia().guardarRecepcionBolsin(
                    bolsinSeleccionado,
                    usuarioLogueado,
                    fechaHoraActual
            );
        }
    }

    // =========================================================================
    // PASO 12: NOTIFICACIÓN Y FINALIZACIÓN DEL CU
    // =========================================================================

    /**
     * Método del Diagrama de Secuencias: llamarCU29NotifRecepcionBolsin()
     * Envía notificación a cada CM origen (CU 29: Notificar recepción de bolsín).
     */
    public void llamarCU29NotifRecepcionBolsin() {
        if (bolsinSeleccionado != null && bolsinSeleccionado.getCmOrigen() != null) {
            ComisionMedica origen = bolsinSeleccionado.getCmOrigen();
            String destinatario = origen.getEmail();
            if (destinatario == null || destinatario.isEmpty()) {
                destinatario = "contacto@" + origen.getNombre().toLowerCase().replace(" ", "") + ".gov.ar";
            }
            String asunto = "Notificación de Recepción de Bolsín N° " + bolsinSeleccionado.getNumeroBolsin();

            String situacionDocs = switch (opcionSelecc) {
                case 1 -> "Documentación recibida y aceptada íntegramente conforme a remito.";
                case 2 -> "Se registraron " + documentacionesObservadas.size() + " documento(s) faltantes marcados como No Recibido.";
                case 3 -> "Se rechazaron " + documentacionesObservadas.size() + " documento(s) por no pertenecer a esta CM.";
                case 4 -> "Se indicaron " + documentacionesObservadas.size() + " documento(s) para redirigir a otra área.";
                default -> "Recepción registrada.";
            };

            String cuerpo = "Estimados,\nSe ha registrado con éxito la recepción del Bolsín N° " +
                    bolsinSeleccionado.getNumeroBolsin() +
                    " (Precinto: " + bolsinSeleccionado.getNroPrecinto() + ") en la " +
                    (CMUsuarioLog != null ? CMUsuarioLog.getNombre() : "Comisión Médica") + " el " + fechaHoraActual +
                    ".\nOperador responsable: " + (usuarioLogueado != null ? usuarioLogueado.getNombre() + " " + usuarioLogueado.getApellido() : "Encargado") +
                    ".\nSituación de la documentación: " + situacionDocs;

            MailServerService.getInstancia().enviarMail(destinatario, asunto, cuerpo);
        }
    }

    public void notificarACadaCM() {
        llamarCU29NotifRecepcionBolsin();
    }

    public void finCU() {
        System.out.println("Caso de uso CU 28: Registrar recepción de bolsín finalizado correctamente.");
    }

    public void setListaBolsines(List<Bolsin> listaBolsines) {
        this.listaBolsines = (listaBolsines != null) ? listaBolsines : new ArrayList<>();
    }

    // Getters y Setters
    public Bolsin getBolsinSeleccionado() { return bolsinSeleccionado; }
    public Empleado getUsuarioLogueado() { return usuarioLogueado; }
    public Empleado getEncargado() { return usuarioLogueado; }
    public ComisionMedica getCMUsuarioLog() { return CMUsuarioLog; }
    public ComisionMedica getCmEmpleado() { return CMUsuarioLog; }
    public LocalDateTime getFechaHoraActualRegistrada() { return fechaHoraActual; }
    public EstadoBolsin getNuevoEstadoBolsin() { return nuevoEstadoBolsin; }
    public EstadoRemito getNuevoEstadoRemito() { return nuevoEstadoRemito; }
    public List<Documentacion> getDocumentacionesObservadas() { return documentacionesObservadas; }
    public int getOpcionRecepcionSeleccionada() { return opcionSelecc; }
    public int getOpcionSelecc() { return opcionSelecc; }
}
