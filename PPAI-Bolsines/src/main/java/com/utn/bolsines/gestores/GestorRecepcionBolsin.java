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
 * Capa de Control / Gestor del CU 28: Registrar Recepción de Bolsín.
 * Cumple con el Flujo Básico y los Flujos Alternativos A1, A2, A3, A4, A5 y A6,
 * aplicando el Patrón State en Documentación y persistencia en SQLite.
 */
public class GestorRecepcionBolsin {

    private PantallaRecepcionBolsin pantalla;
    private Sesion sesion;
    private List<Bolsin> listaBolsines;
    private List<Estado> catalogoEstados;
    private List<Empleado> listaEmpleados;

    // Atributos de contexto durante el caso de uso
    private Empleado encargado;
    private ComisionMedica cmEmpleado;
    private Bolsin bolsinSeleccionado;
    private String numeroPrecintoBolsin;
    private ComisionMedica cmDestinoBolsin;
    private LocalDateTime fechaHoraActual;
    private int opcionRecepcionSeleccionada;
    private List<Documentacion> documentacionesObservadas = new ArrayList<>();

    // Estados buscados para la transición (Corrección 6 - Búsqueda polimórfica)
    private EstadoBolsin nuevoEstadoBolsin;
    private EstadoRemito nuevoEstadoRemito;
    private EstadoDocumentacion nuevoEstadoDocAceptada;
    private EstadoDocumentacion nuevoEstadoDocNoRecibida;
    private EstadoDocumentacion nuevoEstadoDocRechazada;
    private EstadoDocumentacion nuevoEstadoDocParaRedirigir;

    public GestorRecepcionBolsin(PantallaRecepcionBolsin pantalla, Sesion sesion,
                                List<Bolsin> listaBolsines, List<Estado> catalogoEstados,
                                List<Empleado> listaEmpleados) {
        this.pantalla = pantalla;
        this.sesion = sesion;
        this.listaBolsines = (listaBolsines != null) ? listaBolsines : new ArrayList<>();
        this.catalogoEstados = (catalogoEstados != null) ? catalogoEstados : new ArrayList<>();
        this.listaEmpleados = (listaEmpleados != null) ? listaEmpleados : new ArrayList<>();
    }

    /**
     * Paso 1-4 del CU: Inicia la recepción buscando encargado, CM y bolsines enviados.
     */
    public void registrarRecepcion() {
        // 1. Buscar encargado logueado
        buscarEncargado();

        // 2. Buscar Comisión Médica del encargado
        buscarCM();

        // 3. Mostrar CM en la pantalla
        if (pantalla != null && cmEmpleado != null) {
            pantalla.mostrarDatos(cmEmpleado.getNombre());
        }

        // 4. Buscar bolsines en estado enviado para la CM destino (Corrección 5: Patrón Experto)
        List<Bolsin> bolsinesParaRecepcion = buscarBolsin();

        // 5. Mostrar lista de bolsines en pantalla
        if (pantalla != null) {
            pantalla.mostrarBolsin(bolsinesParaRecepcion);
        }
    }

    public void buscarEncargado() {
        if (sesion != null && sesion.getUsuario() != null) {
            String usuarioLogueado = sesion.getUsuario();
            for (Empleado emp : listaEmpleados) {
                if (emp.sosUsuario(usuarioLogueado)) {
                    this.encargado = emp;
                    break;
                }
            }
        }
    }

    public void buscarCM() {
        if (encargado != null) {
            this.cmEmpleado = encargado.getCM();
        }
    }

    /**
     * Corrección 5: Filtrado delegado de Bolsines (Patrón Experto).
     * El Gestor le envía a cada Bolsín: sosEnviado() y tenesEstaCMDestino(cmEmpleado).
     */
    public List<Bolsin> buscarBolsin() {
        List<Bolsin> filtrados = new ArrayList<>();
        for (Bolsin b : listaBolsines) {
            if (b.sosEnviado() && b.tenesEstaCMDestino(cmEmpleado)) {
                filtrados.add(b);
            }
        }
        return filtrados;
    }

    /**
     * Soporte para Flujos Alternativos A1 y A2:
     * Filtra los bolsines por número de precinto y/o por Comisión Médica de origen.
     */
    public List<Bolsin> buscarBolsinPorFiltro(String precinto, String cmOrigenNombre) {
        List<Bolsin> candidatos = buscarBolsin();
        List<Bolsin> resultado = new ArrayList<>();

        for (Bolsin b : candidatos) {
            boolean coincidePrecinto = (precinto == null || precinto.trim().isEmpty())
                    || (b.getNumeroPrecinto() != null && b.getNumeroPrecinto().equalsIgnoreCase(precinto.trim()));

            boolean coincideOrigen = (cmOrigenNombre == null || cmOrigenNombre.trim().isEmpty())
                    || (b.getCmOrigen() != null && b.getCmOrigen().getNombre().toLowerCase().contains(cmOrigenNombre.trim().toLowerCase()));

            if (coincidePrecinto && coincideOrigen) {
                resultado.add(b);
            }
        }
        return resultado;
    }

    /**
     * Paso 5 del CU: El actor selecciona un Bolsín del listado.
     */
    public void tomarBolsin(Bolsin bolsin) {
        this.bolsinSeleccionado = bolsin;
        if (bolsin != null) {
            this.numeroPrecintoBolsin = bolsin.getNumeroPrecinto();
            this.cmDestinoBolsin = bolsin.getCmDestino();
        }

        // Paso 6: Buscar y mostrar remitos y documentos contenidos
        buscarRemito();

        // Paso 7: Mostrar opciones de recepción
        if (pantalla != null) {
            pantalla.mostrarOpRecepcion();
        }
    }

    public void buscarRemito() {
        if (bolsinSeleccionado != null && pantalla != null) {
            List<String> infoRemitos = bolsinSeleccionado.obtenerInformacionRemito();
            pantalla.mostrarDetalleRemitos(infoRemitos);
        }
    }

    /**
     * Paso 8 del CU: El actor selecciona una opción de recepción.
     * Opción 1: Contenido igual al registrado (Flujo Básico).
     */
    public void tomarOpRecepcion(int opcion) {
        tomarOpRecepcion(opcion, new ArrayList<>());
    }

    /**
     * Paso 8 extendido para Flujos Alternativos A3, A4 y A5:
     * Opción 2: Documentación faltante (A3).
     * Opción 3: Documentación que no corresponde al destino (A4).
     * Opción 4: Documentación para redirigir a otra área (A5).
     */
    public void tomarOpRecepcion(int opcion, List<Documentacion> docsAfectados) {
        this.opcionRecepcionSeleccionada = opcion;
        this.documentacionesObservadas = (docsAfectados != null) ? docsAfectados : new ArrayList<>();

        // Paso 9: Solicitar confirmación
        if (pantalla != null) {
            pantalla.solicitarConfirmacion();
        }
    }

    /**
     * Flujo Alternativo A6: El actor cancela la operación y no confirma.
     */
    public void cancelarRecepcion() {
        System.out.println("[Gestor] Flujo Alternativo A6: Operación cancelada por el usuario.");
        if (pantalla != null) {
            pantalla.notificarCancelacion("Operación cancelada. No se modificaron los estados del bolsín.");
        }
    }

    /**
     * Paso 10 del CU: El actor confirma la recepción.
     */
    public void tomarConfirmacion() {
        // 1. Obtener fecha y hora actual del sistema
        this.fechaHoraActual = getFechaHoraActual();

        // 2. Buscar los objetos Estado destino (Corrección 6)
        buscarEstados();

        // 3. Actualizar estados del Bolsín, Remitos y Documentaciones (Patrón State)
        actualizarEstados();

        // 4. Persistir en la base de datos SQLite
        guardarEnBaseDeDatos();

        // 5. Notificar a cada Comisión Médica de origen (CU 29: Notificar recepción)
        notificarACadaCM();

        // 6. Informar éxito al usuario y finalizar CU
        if (pantalla != null) {
            String detalleOpcion = switch (opcionRecepcionSeleccionada) {
                case 1 -> "Flujo Básico: Contenido íntegro recibido y aceptado.";
                case 2 -> "Flujo Alternativo A3: Recepción con " + documentacionesObservadas.size() + " documento(s) faltante(s) marcado(s) como No Recibido.";
                case 3 -> "Flujo Alternativo A4: Recepción con " + documentacionesObservadas.size() + " documento(s) rechazado(s) por no corresponder.";
                case 4 -> "Flujo Alternativo A5: Recepción con " + documentacionesObservadas.size() + " documento(s) marcado(s) para redirigir.";
                default -> "Recepción registrada.";
            };
            pantalla.notificarExito("Recepción del Bolsín N° " + bolsinSeleccionado.getNumeroIdentificador() +
                    " registrada exitosamente.\n" + detalleOpcion);
        }
        finCU();
    }

    public LocalDateTime getFechaHoraActual() {
        return LocalDateTime.now();
    }

    /**
     * Corrección 6: Búsqueda polimórfica de Estados en el catálogo.
     */
    public void buscarEstados() {
        // Búsqueda de Estado para Bolsín
        for (Estado e : catalogoEstados) {
            if (e.esAmbitoBolsin() && e.esRecibidoYAceptadoEnCMDestino()) {
                this.nuevoEstadoBolsin = (EstadoBolsin) e;
                break;
            }
        }

        // Búsqueda de Estado para Remito
        for (Estado e : catalogoEstados) {
            if (e.esAmbitoRemito() && e.esRecibidoYAceptado()) {
                this.nuevoEstadoRemito = (EstadoRemito) e;
                break;
            }
        }

        // Búsqueda de Estados para Documentación (Patrón State)
        for (Estado e : catalogoEstados) {
            if (e.esAmbitoDocumentacion()) {
                if (e.esRecibidaYAceptada()) {
                    this.nuevoEstadoDocAceptada = (EstadoDocumentacion) e;
                } else if (e.esNoRecibido()) {
                    this.nuevoEstadoDocNoRecibida = (EstadoDocumentacion) e;
                } else if (e.esRecibidaYRechazada()) {
                    this.nuevoEstadoDocRechazada = (EstadoDocumentacion) e;
                } else if (e.esParaRedirigir()) {
                    this.nuevoEstadoDocParaRedirigir = (EstadoDocumentacion) e;
                }
            }
        }

        // Fallbacks por seguridad si el catálogo inicial estuviera incompleto
        if (nuevoEstadoDocAceptada == null) nuevoEstadoDocAceptada = new EstadoRecibidaYAceptada();
        if (nuevoEstadoDocNoRecibida == null) nuevoEstadoDocNoRecibida = new EstadoNoRecibido();
        if (nuevoEstadoDocRechazada == null) nuevoEstadoDocRechazada = new EstadoRecibidaYRechazada();
        if (nuevoEstadoDocParaRedirigir == null) nuevoEstadoDocParaRedirigir = new EstadoParaRedirigir();
        if (nuevoEstadoBolsin == null) nuevoEstadoBolsin = new EstadoBolsinRecibidoEnCMDestino();
        if (nuevoEstadoRemito == null) nuevoEstadoRemito = new EstadoRemitoRecibidoYAceptado();
    }

    /**
     * Corrección 7 y Patrón State:
     * Delega la transición a las entidades. Documentacion delega a su EstadoDocumentacion actual.
     */
    public void actualizarEstados() {
        if (bolsinSeleccionado == null) return;

        EstadoDocumentacion estadoAlternativo = switch (opcionRecepcionSeleccionada) {
            case 2 -> nuevoEstadoDocNoRecibida;
            case 3 -> nuevoEstadoDocRechazada;
            case 4 -> nuevoEstadoDocParaRedirigir;
            default -> nuevoEstadoDocAceptada;
        };

        if (opcionRecepcionSeleccionada == 1 || documentacionesObservadas.isEmpty()) {
            // Flujo básico: todo aceptado
            bolsinSeleccionado.recibir(
                    nuevoEstadoBolsin,
                    nuevoEstadoRemito,
                    nuevoEstadoDocAceptada,
                    fechaHoraActual,
                    encargado
            );
        } else {
            // Flujos alternativos A3, A4, A5
            bolsinSeleccionado.recibirConAlternativas(
                    nuevoEstadoBolsin,
                    nuevoEstadoRemito,
                    nuevoEstadoDocAceptada,
                    estadoAlternativo,
                    documentacionesObservadas,
                    opcionRecepcionSeleccionada,
                    fechaHoraActual,
                    encargado
            );
        }
    }

    /**
     * Persiste los cambios de estado en la Base de Datos SQLite.
     */
    public void guardarEnBaseDeDatos() {
        if (bolsinSeleccionado != null) {
            RepositorioBolsin.getInstancia().guardarRecepcionBolsin(
                    bolsinSeleccionado,
                    encargado,
                    fechaHoraActual
            );
        }
    }

    /**
     * Paso 12: CU 29 Notificar recepción de bolsín.
     * Envía correo con la situación de la documentación recibida.
     */
    public void notificarACadaCM() {
        if (bolsinSeleccionado != null && bolsinSeleccionado.getCmOrigen() != null) {
            ComisionMedica origen = bolsinSeleccionado.getCmOrigen();
            String destinatario = origen.getEmail();
            if (destinatario == null || destinatario.isEmpty()) {
                destinatario = "contacto@" + origen.getNombre().toLowerCase().replace(" ", "") + ".gov.ar";
            }
            String asunto = "Notificación de Recepción de Bolsín N° " + bolsinSeleccionado.getNumeroIdentificador();

            String situacionDocs = switch (opcionRecepcionSeleccionada) {
                case 1 -> "Documentación recibida y aceptada íntegramente conforme a remito.";
                case 2 -> "Se registraron " + documentacionesObservadas.size() + " documento(s) faltantes marcados como No Recibido.";
                case 3 -> "Se rechazaron " + documentacionesObservadas.size() + " documento(s) por no pertenecer a esta CM.";
                case 4 -> "Se indicaron " + documentacionesObservadas.size() + " documento(s) para redirigir a otra área.";
                default -> "Recepción registrada.";
            };

            String cuerpo = "Estimados,\nSe ha registrado con éxito la recepción del Bolsín N° " +
                    bolsinSeleccionado.getNumeroIdentificador() +
                    " (Precinto: " + bolsinSeleccionado.getNumeroPrecinto() + ") en la " +
                    cmEmpleado.getNombre() + " el " + fechaHoraActual +
                    ".\nOperador responsable: " + (encargado != null ? encargado.getNombre() + " " + encargado.getApellido() : "Encargado") +
                    ".\nSituación de la documentación: " + situacionDocs;

            MailServerService.getInstancia().enviarMail(destinatario, asunto, cuerpo);
        }
    }

    public void finCU() {
        System.out.println("Caso de uso CU 28: Registrar recepción de bolsín finalizado correctamente.");
    }

    public void setListaBolsines(List<Bolsin> listaBolsines) {
        this.listaBolsines = (listaBolsines != null) ? listaBolsines : new ArrayList<>();
    }

    // Getters y Setters
    public Bolsin getBolsinSeleccionado() { return bolsinSeleccionado; }
    public Empleado getEncargado() { return encargado; }
    public ComisionMedica getCmEmpleado() { return cmEmpleado; }
    public LocalDateTime getFechaHoraActualRegistrada() { return fechaHoraActual; }
    public EstadoBolsin getNuevoEstadoBolsin() { return nuevoEstadoBolsin; }
    public EstadoRemito getNuevoEstadoRemito() { return nuevoEstadoRemito; }
    public EstadoDocumentacion getNuevoEstadoDocAceptada() { return nuevoEstadoDocAceptada; }
    public List<Documentacion> getDocumentacionesObservadas() { return documentacionesObservadas; }
    public int getOpcionRecepcionSeleccionada() { return opcionRecepcionSeleccionada; }
}
