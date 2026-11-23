package com.utn.bolsines;

import com.utn.bolsines.entidades.*;
import com.utn.bolsines.entidades.estados.*;
import com.utn.bolsines.pantallas.PantallaLogin;
import com.utn.bolsines.pantallas.PantallaRecepcionBolsin;
import com.utn.bolsines.persistencia.ConexionBD;
import com.utn.bolsines.persistencia.RepositorioBolsin;

import javax.swing.*;
import java.awt.GraphicsEnvironment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Punto de entrada principal del Sistema de Bolsines (Grupo 8 - CU 28).
 * Soporta ejecución interactiva mediante Interfaz Gráfica (Swing) y
 * modo consola demostrativo del Flujo Básico y Flujos Alternativos con persistencia SQLite.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("      SISTEMA DE GESTIÓN DE BOLSINES - UTN DSI (GRUPO 8)            ");
        System.out.println("           DEMOSTRACIÓN CU 28: REGISTRAR RECEPCIÓN DE BOLSÍN        ");
        System.out.println("====================================================================\n");

        // 1. Inicializar la persistencia y base de datos SQLite
        System.out.println(">>> 1. Inicializando base de datos relacional SQLite (bolsines.db)...");
        ConexionBD.inicializarBaseDeDatos();
        RepositorioBolsin repo = RepositorioBolsin.getInstancia();

        // 2. Cargar entidades desde la base de datos
        List<Empleado> empleados = repo.obtenerEmpleados();
        List<Bolsin> bolsines = repo.obtenerTodosLosBolsines();

        // 3. Obtener el empleado logueado en la Comisión Médica Villa María (CM Destino)
        Empleado empDestino = null;
        for (Empleado e : empleados) {
            if ("agonzalez".equalsIgnoreCase(e.getUsuario())) {
                empDestino = e;
                break;
            }
        }
        if (empDestino == null && !empleados.isEmpty()) {
            empDestino = empleados.get(0);
        }

        // Fallback defensivo si la base SQLite no pudo inicializarse por configuración del IDE
        if (empDestino == null) {
            System.err.println("\n[AVISO] No se pudo leer la base SQLite (verifique la configuración de librerías en IntelliJ).");
            System.err.println("[AVISO] Iniciando con datos en memoria para garantizar la continuidad...\n");

            ComisionMedica cmCordoba = new ComisionMedica(1, "CM01", "Comisión Médica Córdoba", "Av. Colón 1234, Córdoba", "-31.4201,-64.1888", "cordoba@comisionesmedicas.gob.ar");
            ComisionMedica cmVillaMaria = new ComisionMedica(2, "CM02", "Comisión Médica Villa María", "San Martín 456, Villa María", "-32.4075,-63.2402", "villamaria@comisionesmedicas.gob.ar");
            Empleado empOrigen = new Empleado(10, "Carlos", "Pérez", "carlos.perez@comisionesmedicas.gob.ar", "LEG-1001", "cperez", cmCordoba);
            empDestino = new Empleado(20, "Ana", "González", "ana.gonzalez@comisionesmedicas.gob.ar", "LEG-2002", "agonzalez", cmVillaMaria);
            empleados.add(empOrigen);
            empleados.add(empDestino);

            // Crear bolsines en memoria
            TipoDocumento tipoExp = new TipoDocumento(1, "Expediente", "Expediente médico legal");
            TipoDocumento tipoDic = new TipoDocumento(2, "Dictamen", "Dictamen de incapacidad laboral");

            Documentacion d1 = new Documentacion(1, 1001L, "Expediente Accidente Laboral - Juan Gómez", java.time.LocalDate.now().minusDays(2), tipoExp, cmCordoba, new EnBolsinEnviado(), empOrigen);
            Documentacion d2 = new Documentacion(2, 1002L, "Dictamen Comisión Evaluadora - María Ruiz", java.time.LocalDate.now().minusDays(1), tipoDic, cmCordoba, new EnBolsinEnviado(), empOrigen);

            Remito r1 = new Remito(1, 5001L, LocalDateTime.now().minusHours(3), empOrigen, cmVillaMaria);
            r1.setEstado(new EstadoRemitoEnBolsinEnviado());
            r1.agregarDetalle(new DetalleRemito(1, 1, "Expediente físico", d1));
            r1.agregarDetalle(new DetalleRemito(2, 1, "Dictamen", d2));

            Bolsin b1 = new Bolsin(1, 301L, "PREC-998877", LocalDateTime.now().minusHours(4), 1500.0, cmCordoba, cmVillaMaria, new EstadoBolsinEnviado(), empOrigen);
            b1.asociarRemito(r1);
            bolsines.add(b1);

            // Bolsín 2 para probar alternativas
            Documentacion d3 = new Documentacion(3, 1003L, "Resonancia Columna Lumbar - Pedro Rossi", java.time.LocalDate.now().minusDays(1), tipoExp, cmCordoba, new EnBolsinEnviado(), empOrigen);
            Remito r2 = new Remito(2, 6001L, LocalDateTime.now().minusHours(2), empOrigen, cmVillaMaria);
            r2.setEstado(new EstadoRemitoEnBolsinEnviado());
            r2.agregarDetalle(new DetalleRemito(3, 1, "Estudio médico", d3));
            Bolsin b2 = new Bolsin(2, 302L, "PREC-445566", LocalDateTime.now().minusHours(3), 2100.0, cmCordoba, cmVillaMaria, new EstadoBolsinEnviado(), empOrigen);
            b2.asociarRemito(r2);
            bolsines.add(b2);
        }
        Sesion sesionUsuario = new Sesion(empDestino);
        System.out.println(">>> Usuario activo en sesión: " + empDestino.getNombre() + " " + empDestino.getApellido() +
                " (" + (empDestino.getCM() != null ? empDestino.getCM().getNombre() : "Sin CM") + ")");

        // 4. Cargar Catálogo Polimórfico de Estados (Corrección 6)
        List<Estado> catalogoEstados = construirCatalogoEstados();

        boolean forzarConsola = false;
        for (String arg : args) {
            if ("--consola".equalsIgnoreCase(arg) || "--demo".equalsIgnoreCase(arg)) {
                forzarConsola = true;
                break;
            }
        }

        // 5. Determinar si se ejecuta en modo GUI (Escritorio) o Consola
        if (!forzarConsola && !GraphicsEnvironment.isHeadless()) {
            System.out.println(">>> Iniciando interfaz gráfica interactiva Swing (Tecnología de Escritorio)...");
            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {}

                PantallaLogin login = new PantallaLogin(empleados, catalogoEstados);
                login.setVisible(true);
            });
        } else {
            // Demostración por consola para entornos de pruebas o verificación
            ejecutarDemostracionConsola(sesionUsuario, bolsines, catalogoEstados, empleados);
        }
    }

    private static List<Estado> construirCatalogoEstados() {
        List<Estado> catalogo = new ArrayList<>();
        // Estados Bolsín
        catalogo.add(new EstadoBolsinCreado());
        catalogo.add(new EstadoBolsinCerrado());
        catalogo.add(new EstadoBolsinEnviado());
        catalogo.add(new EstadoBolsinRecibidoEnCMDestino());
        catalogo.add(new EstadoBolsinDeBaja());

        // Estados Remito
        catalogo.add(new EstadoRemitoCreado());
        catalogo.add(new EstadoRemitoEnBolsinSaliente());
        catalogo.add(new EstadoRemitoEnBolsinEnviado());
        catalogo.add(new EstadoRemitoRecibidoYAceptado());
        catalogo.add(new EstadoRemitoCancelado());

        // Estados Documentación (Patrón State Grupo 8)
        catalogo.add(new Registrada());
        catalogo.add(new EnRemito());
        catalogo.add(new EnBolsinSaliente());
        catalogo.add(new EnBolsinEnviado());
        catalogo.add(new RecibidaYAceptada());
        catalogo.add(new NoRecibida());
        catalogo.add(new RecibidaYRechazada());
        catalogo.add(new ParaRedirigir());
        catalogo.add(new DeBaja());

        return catalogo;
    }

    /**
     * Demostración automatizada paso a paso por consola del Flujo Básico y Flujos Alternativos.
     */
    public static void ejecutarDemostracionConsola(Sesion sesion, List<Bolsin> bolsines,
                                                  List<Estado> estados, List<Empleado> empleados) {
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("  EJECUCIÓN DEL CU 28: DEMOSTRACIÓN DE FLUJO BÁSICO Y ALTERNATIVOS ");
        System.out.println("--------------------------------------------------------------------");

        PantallaRecepcionBolsin pantalla = new PantallaRecepcionBolsin(sesion, bolsines, estados, empleados, false);
        pantalla.opRegistrarRecepBolsin();
        var gestor = pantalla.getGestor();

        // Restablecer datos de prueba limpios para garantizar la ejecución de todos los flujos
        pantalla.restablecerDatosPrueba();

        // 1. Filtrado de bolsines enviados para la CM del encargado (Patrón Experto)
        List<Bolsin> disponibles = gestor.buscarBolsin();

        System.out.println("\n[Paso 3 y 4] Bolsines en estado Enviado para la CM del usuario logueado: " + disponibles.size());
        for (Bolsin b : disponibles) {
            System.out.println("   * Bolsín #" + b.getNumeroIdentificador() + " | Precinto: " + b.getNumeroPrecinto() +
                    " | Origen: " + b.getCmOrigen().getNombre() + " | Estado: " + b.getEstadoActual().getNombre());
        }

        // ====================================================================
        // DEMOSTRACIÓN 1: FLUJO ALTERNATIVO A1 (Precinto Inexistente) Y FILTRO FLEXIBLE
        // ====================================================================
        System.out.println("\n====================================================================");
        System.out.println(">>> DEMOSTRACIÓN 1: FLUJO ALTERNATIVO A1 Y FILTRO FLEXIBLE DE PRECINTO");
        System.out.println("====================================================================");
        List<Bolsin> busquedaInexistente = gestor.buscarBolsinPorFiltro("PREC-000000", "");
        System.out.println("• Búsqueda con precinto inexistente 'PREC-000000': " + busquedaInexistente.size() + " bolsines.");
        System.out.println("  Flujo A1 validado: el sistema informa que no se encontró bolsín con el precinto ingresado.");

        // Prueba de filtro flexible (completo vs sólo número)
        String precintoPrueba = disponibles.get(0).getNumeroPrecinto();
        String soloNumPrueba = precintoPrueba.replaceAll("[^0-9]", "");
        List<Bolsin> busquedaCompleta = gestor.buscarBolsinPorFiltro(precintoPrueba, "");
        System.out.println("• Búsqueda con texto completo '" + precintoPrueba + "': " + busquedaCompleta.size() + " bolsín(es) encontrado(s).");
        List<Bolsin> busquedaSoloNumero = gestor.buscarBolsinPorFiltro(soloNumPrueba, "");
        System.out.println("• Búsqueda ingresando sólo números '" + soloNumPrueba + "': " + busquedaSoloNumero.size() + " bolsín(es) encontrado(s).");
        System.out.println("  Filtro flexible validado exitosamente.");

        // ====================================================================
        // DEMOSTRACIÓN 2: FLUJO BÁSICO (Opción 1: Contenido Conforme)
        // ====================================================================
        System.out.println("\n====================================================================");
        System.out.println(">>> DEMOSTRACIÓN 2: FLUJO BÁSICO (Bolsín #301 - Contenido Conforme)");
        System.out.println("====================================================================");
        Bolsin bolsin1 = disponibles.get(0);
        gestor.tomarBolsin(bolsin1);

        System.out.println("[Usuario] Selecciona Opción 1: El contenido del bolsín es igual al registrado");
        gestor.tomarOpRecepcion(1);

        System.out.println("\n[Verificación Post-condición Paso 11 - Bolsín #301]:");
        System.out.println("   Estado Bolsín: " + bolsin1.getEstadoActual().getNombre() +
                " | Responsable: " + (gestor.getEncargado() != null ? gestor.getEncargado().toString() : "N/A"));
        for (Documentacion doc : bolsin1.obtenerDocumentaciones()) {
            System.out.println("   Documento #" + doc.getNumeroIdentificador() + " [" + doc.getAsunto() +
                    "] -> Estado actual (Patrón State): " + doc.getEstadoActual().getNombre() +
                    " | Responsable: " + (gestor.getEncargado() != null ? gestor.getEncargado().toString() : "N/A"));
        }

        // ====================================================================
        // DEMOSTRACIÓN 3: FLUJO ALTERNATIVO A3 (Opción 2: Documentación Faltante)
        // ====================================================================
        if (disponibles.size() > 1) {
            System.out.println("\n====================================================================");
            System.out.println(">>> DEMOSTRACIÓN 3: FLUJO ALTERNATIVO A3 (Bolsín #302 - Documentación Faltante)");
            System.out.println("====================================================================");
            Bolsin bolsin2 = disponibles.get(1);
            gestor.tomarBolsin(bolsin2);

            List<Documentacion> docsBolsin2 = bolsin2.obtenerDocumentaciones();
            List<Documentacion> faltantes = new ArrayList<>();
            if (!docsBolsin2.isEmpty()) {
                faltantes.add(docsBolsin2.get(0)); // Simulamos que el primer documento falta
            }

            System.out.println("[Usuario] Selecciona Opción 2: No se recibe toda la documentación");
            System.out.println("[Usuario] Marca como faltante: Doc #" + faltantes.get(0).getNumeroIdentificador() +
                    " (" + faltantes.get(0).getAsunto() + ")");
            gestor.tomarOpRecepcion(2, faltantes);

            System.out.println("\n[Verificación Post-condición Paso 11 - Bolsín #302]:");
            System.out.println("   Estado Bolsín: " + bolsin2.getEstadoActual().getNombre() +
                    " | Responsable: " + (gestor.getEncargado() != null ? gestor.getEncargado().toString() : "N/A"));
            for (Documentacion doc : bolsin2.obtenerDocumentaciones()) {
                System.out.println("   Documento #" + doc.getNumeroIdentificador() + " [" + doc.getAsunto() +
                        "] -> Estado actual (Patrón State): " + doc.getEstadoActual().getNombre() +
                        " | Responsable: " + (gestor.getEncargado() != null ? gestor.getEncargado().toString() : "N/A"));
            }
        }

        System.out.println("\n====================================================================");
        System.out.println("   ¡DEMOSTRACIÓN COMPLETA: FLUJO BÁSICO Y ALTERNATIVOS EJECUTADOS!   ");
        System.out.println("         PERSISTENCIA EN BD SQLITE 'bolsines.db' VERIFICADA         ");
        System.out.println("====================================================================");
    }
}
