# Proyecto Práctico de Aplicación Integrador (PPAI) - 2da Entrega: Diseño e Implementación
**Universidad Tecnológica Nacional – Facultad Regional Córdoba (UTN-FRC)**  
**Cátedra:** Diseño de Sistemas de Información (DSI)  
**Grupo:** Grupo 8 (Grupo Par)  
**Caso de Estudio:** Bolsines (Comisiones Médicas)  
**Caso de Uso Asignado:** **CU 28: Registrar recepción de bolsín**  
**Máquina de Estados Asignada:** **Documentación (Patrón GoF State)**  

---

## 1. Detalles de la Implementación (Consignas Oficiales)

* **a. Lenguaje de programación:** **Java 21 (LTS)**.
* **b. Framework / Librerías:** 
  * **Java Swing** (`javax.swing.*`, `java.awt.*`) para la interfaz gráfica interactiva de escritorio.
  * **SQLite JDBC Driver** (`org.xerial:sqlite-jdbc:3.45.2.0`) para conexión a base de datos embebida.
  * **SLF4J** (`slf4j-api`, `slf4j-simple`) como logger facade para el driver JDBC.
* **c. Tecnología:** **Escritorio** (Desktop GUI nativa Swing).
* **d. Base de datos y persistencia:** Base de datos relacional **SQLite** (`bolsines.db`) implementada bajo la arquitectura en capas con el patrón **DAO / Repository** (`RepositorioBolsin.java` y `ConexionBD.java`) y transacciones atómicas SQL (`setAutoCommit(false)`).

---

## 2. Arquitectura del Sistema y Organización de Paquetes

El diseño respeta de manera estricta el patrón arquitectónico **Boundary - Control - Entity** (Frontera - Control - Entidad) de GRASP, la jerarquía de clases oficial de DSI (Grupo 8) y las directivas de la cátedra:

```
src/main/java/com/utn/bolsines/
├── Main.java                                <--- Punto de entrada del sistema (GUI y Consola)
├── pantallas/                               <--- Capa FRONTERA / BOUNDARY
│   ├── PantallaLogin.java                   <--- Inicio de sesión interactivo y selección de empleado
│   ├── PantRecepcionBolsin.java             <--- <<boundary>> Nombre formal del Diagrama de Clases
│   ├── PantallaRecepcionBolsin.java         <--- GUI interactiva de escritorio Swing (JFrame)
│   ├── DialogoCrearBolsin.java              <--- Diálogo modal para registrar y enviar nuevos bolsines
│   └── VisorBaseDatosDialog.java            <--- Visor de auditoría de SQLite en vivo (Bolsín, Remito, Doc)
├── gestores/                                <--- Capa CONTROLADOR / GESTOR
│   └── GestorRecepcionBolsin.java           <--- <<control>> Orquestador del CU 28 y filtro flexible
├── entidades/                               <--- Capa DOMINIO / ENTIDADES
│   ├── Bolsin.java                          <--- <<entity>> Contexto de Bolsín (Patrón Experto)
│   ├── Remito.java                          <--- <<entity>> Remito de entrega (cascada a detalles)
│   ├── DetalleRemito.java                   <--- <<entity>> Renglón asociado a una documentación
│   ├── Documentacion.java                   <--- <<entity>> Contexto del Patrón STATE (GoF)
│   ├── CambioEstadoBolsin.java              <--- <<entity>> Trazabilidad histórica con legajo
│   ├── CambioEstadoDocumentacion.java       <--- <<entity>> Trazabilidad histórica con legajo
│   ├── ComisionMedica.java                  <--- <<entity>> Sedes médicas de origen y destino
│   ├── Empleado.java                        <--- <<entity>> Identificador, legajo y asignación a CM
│   ├── Usuario.java                         <--- <<entity>> Credenciales del operador autenticado
│   ├── Sesion.java                          <--- <<entity>> Sesión activa en el sistema
│   ├── TipoDocumento.java                   <--- <<entity>> Clasificación del documento médico/legal
│   ├── Archivo.java                         <--- <<entity>> Archivo digital adjunto
│   └── estados/                             <--- Jerarquía del Patrón STATE (GoF)
│       ├── Estado.java                      <--- Interfaz con búsqueda polimórfica en catálogo
│       ├── EstadoDocumentacion.java         <--- <<abstract>> Clase Abstracta State (GoF)
│       ├── EstadoBolsin.java                <--- Jerarquía de estados de Bolsín
│       ├── EstadoRemito.java                <--- Jerarquía de estados de Remito
│       ├── EnBolsinEnviado.java             <--- Estado Concreto inicial del CU 28 (Estado activo)
│       ├── RecibidaYAceptada.java           <--- Estado Concreto sucesor (Flujo Básico CU 28)
│       ├── NoRecibida.java                  <--- Estado Concreto sucesor (Flujo Alternativo A3: Faltante)
│       ├── RecibidaYRechazada.java          <--- Estado Concreto sucesor (Flujo Alternativo A4: Rechazo)
│       ├── ParaRedirigir.java               <--- Estado Concreto sucesor (Flujo Alternativo A5: Redirección)
│       ├── Registrada.java                  <--- Estado Concreto inicial tras CU 7
│       ├── EnRemito.java                    <--- Estado Concreto tras asignación a remito
│       ├── EnBolsinSaliente.java            <--- Estado Concreto en bolsín preparado
│       └── DeBaja.java                      <--- Estado Concreto final de baja
├── persistencia/                            <--- Capa PERSISTENCIA / REPOSITORIO
│   ├── ConexionBD.java                      <--- Conexión SQLite y esquema DDL relacional
│   └── RepositorioBolsin.java               <--- DAO con consultas y transacciones SQL atómicas
└── servicios/
    └── MailServerService.java               <--- Simulación CU 29 (Notificación a CM Origen)
```

---

## 3. Consistencia Total con los Diagramas de DSI (Grupo 8)

Para garantizar consistencia absoluta con el **Diagrama de Clases**, el **Diagrama de Secuencias** y la **Máquina de Estados de Documentación** del Grupo 8, cada método, atributo y relación del diseño fue implementado de forma nominal y funcional:

### A. Capa Frontera: `<<boundary>> PantRecepcionBolsin`
* **Nombres de Métodos exactos:**
  * `opRegistrarRecepcionBolsin()`: Disparado por el actor al ingresar a la opción.
  * `habilitarVentana()`: Hace visible la interfaz gráfica de usuario.
  * `mostrarCM(nombreCM)`: Muestra la Comisión Médica activa del usuario logueado (Paso 2).
  * `solicitarSeleccionBolsin(bolsines)`: Presenta en la tabla los bolsines en estado *Enviado* para la CM destino (Paso 4).
  * `tomarSeleccionBolsin(bolsin)`: Toma el bolsín seleccionado por el actor (Paso 5).
  * `mostrarDatosRemitoYDocumentacion(remitos, docs)`: Exhibe los remitos y documentos contenidos (Paso 6).
  * `pedirSeleccionOpcionesDeRecepcion()`: Habilita los radio buttons de opciones de recepción (Paso 7).
  * `tomarSelecOpcion(opcion)`: Registra la opción elegida (Paso 8: Básica o Alternativas A3, A4, A5).
  * `pedirConfirmacion()`: Solicita confirmación explícita al actor (Paso 9).
  * `tomarConfirmacion()`: Envía la confirmación al gestor (Paso 10).
  * `notificarRecepcionBolsinRegistrada(mensaje)`: Informa la finalización exitosa (Paso 12).

### B. Capa Control: `<<control>> GestorRecepcionBolsin`
* **Atributos exactos del Diagrama de Clases:**
  * `usuarioLogueado`: Referencia al `Empleado` autenticado.
  * `CMUsuarioLog`: `ComisionMedica` asignada al usuario.
  * `bolsinesEnviados`: Lista de bolsines en estado enviado para el destino.
  * `CMOrigen`: `ComisionMedica` remitente del bolsín seleccionado.
  * `datosBolsin`: Lista de cadenas con precinto y origen para presentación.
  * `nroPrecinto`: Precinto del bolsín en tratamiento.
  * `bolsinSeleccionado`: Referencia al `Bolsin` elegido.
  * `datosRemitosBolsin`: Líneas descriptivas de remitos.
  * `documentacionDetRemitos`: Objetos `Documentacion` contenidos.
  * `opcionSelecc`: Entero con la opción seleccionada (1 a 4).
  * `confirmacionRecepcion`: Booleano de confirmación.
  * `fechaHoraActual`: Marca temporal obtenida en `getFechaHoraActual()`.
  * `remitoSeleccionado`: `Remito` activo en la evaluación.
* **Métodos exactos del Diagrama de Secuencias:**
  * `tomarConfirmacion()`
  * `getFechaHoraActual()`
  * `buscarEstadoRecibidoCMDestino()`
  * `buscarEstadoRecibidoYAceptado()`
  * `crearCEBolsin()`
  * `actualizarEstados()` -> invoca `bolsinSeleccionado.recibir(fechaHora, responsable)`.
  * `guardarEnBaseDeDatos()`
  * `llamarCU29NotifRecepcionBolsin()`
  * `finCU()`

### C. Capa Dominio / Entidades
* **`Bolsin`:** Atributos: `fecha`, `numeroBolsin`, `numeroPrecinto`, `peso`. Método exacto: `recibir(fechaHora: datetime, responsable: Empleado): void`.
* **`Remito`:** Atributos: `fecha`, `numero`. Métodos exactos: `getNumero(): int`, `obtenerInfoDocumentacion(): List<String>`, `recibirYAceptar(fechaHora: datetime, responsable: Empleado): void`.
* **`DetalleRemito`:** Atributo: `areaCMCDestino: string`. Método exacto: `actualizarEstadoDoc(fechaHora: datetime, responsable: Empleado): void`.
* **`Documentacion`:** Atributos: `asunto`, `fechaPase`, `numero`. Métodos exactos: `recibir(this, fechaHora, ce, responsable)`, `buscarCambioEstadoActual()`, `crearCE(fechaHoraInicio, responsable, estado)`, `setEstadoActual(estado)`, `setCambioEstado(ce)`, `registrarFaltaDocumentacion()`, `redirigirBolsin()`.
* **`Empleado`:** Atributos: `nombre`, `apellido`, `email`, `legajo`. Métodos exactos: `buscarCM()`, `esTuUsuario(user: Usuario): boolean`.
* **`CambioEstadoDocumentacion`:** Atributos: `fechaHoraFin`, `fechaHoraInicio`, `responsable`. Métodos exactos: `sosActual(): boolean`, `setFechaHoraFin(fechaHoraFin): void`, constructor `new(fechaHoraInicio, responsable, estado)`.
* **`CambioEstadoBolsin`:** Atributos: `fechaHoraFin`, `fechaHoraInicio`. Métodos exactos: `setFechaHoraFin(fechaHoraFin): void`, constructor `new(fechaHoraInicio)`.

---

## 4. Aplicación Detallada del Patrón de Diseño GoF: STATE (Grupo 8)

El rediseño del CU 28 aplica el patrón **State** (Gamma et al.) sobre la clase **`Documentacion`**, modelando su ciclo de vida como una máquina de estados orientada a objetos:

```
                  +-------------------------------------------------------+
                  |                     Documentacion                     | <--- CONTEXTO
                  +-------------------------------------------------------+
                  | - estadoActual: EstadoDocumentacion                   |
                  | - cambioEstado: List<CambioEstadoDocumentacion>       |
                  +-------------------------------------------------------+
                  | + recibir(fechaHora, responsable): void               | ---+
                  | + buscarCambioEstadoActual(): CambioEstadoDoc         |    |
                  | + crearCE(fechaHora, resp, estado): CambioEstadoDoc   |    | Delegación al estado actual
                  | + setEstadoActual(estado: EstadoDocumentacion): void  |    | estadoActual.recibir(this, fechaHora, ce, resp)
                  | + setCambioEstado(ce: CambioEstadoDocumentacion): void|    |
                  | + registrarFaltaDocumentacion(fechaHora, resp): void  |    |
                  | + redirigirBolsin(fechaHora, resp): void              |    |
                  +-------------------------------------------------------+    |
                                              |                                |
                                              v                                v
                  +-------------------------------------------------------+
                  | <<abstract>>                                          |
                  | EstadoDocumentacion                                   | <--- CLASE ABSTRACTA STATE
                  +-------------------------------------------------------+
                  | + recibir(this, fechaHora, ce, resp): void            | (Por defecto: lanza
                  | + registrarFaltaDocumentacion(this, fechaHora, resp)  |  UnsupportedOperationException)
                  | + redirigirBolsin(this, fechaHora, resp)              |
                  | + registrar(this, fechaHora, resp)                    |
                  | + registrarEnRemito(this, fechaHora, resp)            |
                  | + agregarEnBolsin(this, fechaHora, resp)              |
                  | + modificar(this)                                     |
                  | + darDeBaja(this, fechaHora, resp)                    |
                  | + registrarRetiro(this, fechaHora, resp)              |
                  +-------------------------------------------------------+
                                              ^
                                              | (Herencia: extends)
        +-------------------------------------+-------------------------------------+
        |                                     |                                     |
+-----------------------------------+ +-----------------------------------+ +-----------------------------------+
| EnBolsinEnviado                   | | RecibidaYAceptada                 | | NoRecibida                        |
+-----------------------------------+ +-----------------------------------+ +-----------------------------------+
| + recibir(this, fH, ce, resp)     | | + new(): RecibidaYAceptada        | | + darDeBaja(): void               |
| + buscarCambioEstadoActual()      | +-----------------------------------+ | + registrar(): void               |
| + crearRecibidaAceptada(): Estado | (Flujo Básico CU 28)                | +-----------------------------------+
| + crearCE(fH, resp, estado): void |                                     | (Flujo Alternativo A3: Faltante)    |
| + new(): EnBolsinEnviado          |                                     +-----------------------------------+
+-----------------------------------+
(Estado Inicial activo en el CU 28)
```

---

### A. Clases Abstractas vs. Estados Concretos (Herencia)

1. **Clase Abstracta State (`EstadoDocumentacion`):**
   * Es una clase abstracta pura (`public abstract class EstadoDocumentacion implements Estado`).
   * Define la firma de todos los eventos del ciclo de vida de la documentación (`recibir`, `registrarFaltaDocumentacion`, `redirigirBolsin`, `registrar`, `registrarEnRemito`, `agregarEnBolsin`, `modificar`, `darDeBaja`, `registrarRetiro`).
   * Provee implementaciones por defecto que lanzan `UnsupportedOperationException("Transición no válida desde: [...]")` para bloquear automáticamente cualquier transición inválida según las reglas del negocio.

2. **Estados Concretos (`extends EstadoDocumentacion`):**
   * Heredan de `EstadoDocumentacion` e implementan **únicamente las transiciones válidas** que les corresponden según la Máquina de Estados:
     * **`EnBolsinEnviado`:** Es el estado activo cuando el bolsín llega a la Comisión Médica. Implementa `recibir(...)` (transiciona a `RecibidaYAceptada`), `registrarFaltaDocumentacion(...)` (transiciona a `NoRecibida`) y `redirigirBolsin(...)` (transiciona a `ParaRedirigir`).
     * **`RecibidaYAceptada`:** Estado final del Flujo Básico. Representa que el documento fue recibido y aceptado conforme a remito.
     * **`NoRecibida`:** Estado asignado en el Flujo A3 a documentos faltantes. Permite posteriormente `darDeBaja()` o `registrar()` (revisión).
     * **`RecibidaYRechazada`:** Estado asignado en el Flujo A4 a documentos que no pertenecen a la Comisión Médica destino.
     * **`ParaRedirigir`:** Estado asignado en el Flujo A5 a documentos que deben enviarse a otra sede médica o CMC.
     * **`Registrada`**, **`EnRemito`**, **`EnBolsinSaliente`**, **`DeBaja`**: Estados de las etapas previas o posteriores del circuito documental.

---

### B. ¿Dónde y Cómo se Delega la Responsabilidad al Estado Actual?

En una implementación procedural tradicional, la clase `Documentacion` o el Gestor tendrían estructuras `if/else` o `switch(estado)` para decidir qué hacer.  
Bajo el **Patrón GoF State**, **no existe ningún condicional de tipo de estado**. La responsabilidad se transfiere en tres etapas estrictamente desacopladas:

#### Etapa 1: Propagación limpia desde la entidad contenedora
Cuando el `Bolsin` recibe la orden de recepcionarse, propaga la llamada a cada `Remito`, este a cada `DetalleRemito`, y este último le envía el mensaje simple a `Documentacion`:

```java
// En DetalleRemito.java
public void actualizarEstadoDoc(LocalDateTime fechaHora, Empleado responsable) {
    if (documentacion != null) {
        // Invoca a Documentacion sin saber en qué estado está ni a cuál pasará
        documentacion.recibir(fechaHora, responsable);
    }
}
```

#### Etapa 2: El Contexto delega la decisión a su Estado Actual
En [Documentacion.java](PPAI-Bolsines/src/main/java/com/utn/bolsines/entidades/Documentacion.java), la clase `Documentacion` (Contexto) **no toma ninguna decisión**. Inmediatamente delega la responsabilidad a su objeto polimórfico `estadoActual`, pasándose a sí misma (`this`) y a su historial:

```java
// En Documentacion.java (Contexto del Patrón State)
public void recibir(LocalDateTime fechaHora, Empleado responsable) {
    // DELEGACIÓN PURA: Le entrega el control al objeto polimórfico 'estadoActual'
    obtenerEstadoActual().recibir(this, fechaHora, this.historialEstados, responsable);
}
```

#### Etapa 3: El Estado Actual concreto toma el control absoluto
Al ejecutarse el CU 28, el objeto referenciado por `estadoActual` es una instancia de **`EnBolsinEnviado`**.  
En [EnBolsinEnviado.java](PPAI-Bolsines/src/main/java/com/utn/bolsines/entidades/estados/EnBolsinEnviado.java), es este estado quien **orquesta por completo la transición**:

```java
// En EnBolsinEnviado.java (Estado Concreto)
@Override
public void recibir(Documentacion contexto, LocalDateTime fechaHora, 
                    List<CambioEstadoDocumentacion> ce, Empleado responsable) {
    LocalDateTime ahora = (fechaHora != null) ? fechaHora : LocalDateTime.now();

    // 1. Pide al contexto su CambioEstado vigente (evaluando sosActual())
    CambioEstadoDocumentacion ceActual = contexto.buscarCambioEstadoActual();

    // 2. Cierra el CambioEstado anterior fijando la fechaHoraFin
    if (ceActual != null) {
        ceActual.setFechaHoraFin(ahora);
    }

    // 3. CREA EL NUEVO ESTADO SUCESOR llamando a su propio método crearRecibidaAceptada()
    //    (Cero condicionales: EnBolsinEnviado sabe que ante 'recibir' el sucesor es RecibidaYAceptada)
    EstadoDocumentacion nuevoEstado = crearRecibidaAceptada(); // new RecibidaYAceptada()

    // 4. Solicita al contexto crear la nueva instancia de CambioEstadoDocumentacion
    CambioEstadoDocumentacion nuevoCE = contexto.crearCE(ahora, responsable, nuevoEstado);

    // 5. Asigna el nuevo estado al contexto (actualiza la referencia polimórfica)
    contexto.setEstadoActual(nuevoEstado);

    // 6. Registra el nuevo CambioEstado en la lista histórica del contexto
    contexto.setCambioEstado(nuevoCE);
}
```

```java
// Método propio de EnBolsinEnviado que concreta el nuevo estado
public EstadoDocumentacion crearRecibidaAceptada() {
    return new RecibidaYAceptada();
}
```

---

### C. Diagrama de Secuencia de la Delegación (Patrón State)

La interacción entre objetos durante la recepción conforme sigue la siguiente secuencia cronológica:

```
:DetalleRemito        :Documentacion         :EnBolsinEnviado      actual:CambioEstadoDoc   nuevo:RecibidaYAceptada   nuevo:CambioEstadoDoc
      |                      |                      |                        |                         |                         |
      |---recibir(fH, resp)->|                      |                        |                         |                         |
      |                      |---recibir(this, fH)->|                        |                         |                         |
      |                      |                      |---buscarCEActual()---->|                         |                         |
      |                      |                      |<--retorna ceActual-----|                         |                         |
      |                      |                      |                                                  |                         |
      |                      |                      |---setFechaHoraFin(fH)--------------------------->|                         |
      |                      |                      |                                                  |                         |
      |                      |                      |---crearRecibidaAceptada()                        |                         |
      |                      |                      |   \--new()-------------------------------------->|                         |
      |                      |                      |<--retorna nuevoEstado----------------------------|                         |
      |                      |                      |                                                                            |
      |                      |                      |---crearCE(fH, resp, nuevoEstado)------------------------------------------>|
      |                      |                      |   \--new(fH, resp, nuevoEstado)------------------------------------------->|
      |                      |                      |<--retorna nuevoCE----------------------------------------------------------|
      |                      |                      |                                                                            |
      |                      |                      |---setEstadoActual(nuevoEstado)-->                                          |
      |                      |                      |---setCambioEstado(nuevoCE)------>                                          |
      |                      |<---------------------|                                                                            |
      |<---------------------|                      |                                                                            |
```

---

### D. Trazabilidad con Responsable y Legajo (Corrección 7 y Paso 11)

El Paso 11 del CU 28 exige explícitamente:
> *"El sistema actualiza la documentación asociada al estado Recibida y Aceptada, **registrando el empleado responsable**"*.

La trazabilidad se asegura de manera permanente a través de la entidad **`CambioEstadoDocumentacion`**:
* Cada transición genera una nueva tupla en el historial con:
  * `fechaHoraInicio`: Momento exacto de la confirmación (`fechaHoraActual`).
  * `fechaHoraFin`: Inicialmente `null` (indicador de estado activo mediante `sosActual() == true`).
  * `responsable`: Asociación al objeto `Empleado` autenticado, que almacena su **Nombre**, **Apellido** y **Legajo** (ej. `LEG-2002`).
  * `estado`: Referencia al nuevo estado concreto asignado (`RecibidaYAceptada`).
* Toda esta información se persiste atómicamente en la tabla `cambio_estado_documentacion` de SQLite, garantizando una auditoría histórica inmutable.

---

## 5. Flujos Implementados y Verificados

El sistema implementa el **Flujo Básico** y **5 Flujos Alternativos** especificados en el Requerimiento Funcional oficial:

| Flujo / Funcionalidad | Opción / Acción | Descripción y Comportamiento del Sistema |
| :--- | :--- | :--- |
| **Inicio de Sesión (Login)** | **Autenticación y Selección de CM** | Pantalla simple (`PantallaLogin.java`) para seleccionar al empleado y validar credenciales. Preselecciona a Ana González (CM Villa María). Permite *Cerrar Sesión* para cambiar de operador. |
| **Filtro Flexible de Precinto** | **Búsqueda completa o sólo numérica** | El Gestor (`GestorRecepcionBolsin.coincidePrecinto`) admite tanto el formato completo (ej: `PREC-445566`) como sólo los números (ej: `445566` o `44`), insensible a mayúsculas/minúsculas. |
| **Creación de Bolsines en Vivo** | **Botón `➕ Crear Nuevo Bolsín`** | Diálogo modal (`DialogoCrearBolsin.java`) para dar de alta bolsines con remito y documentación asociada directamente en SQLite, permitiendo crear casos de prueba infinitos. |
| **Flujo Básico** | **Opción 1: Contenido igual al registrado** | Todo el contenido llega conforme. En el **Paso 11**, el Bolsín pasa a *Recibido en CM Destino*, los Remitos a *Recibido y Aceptado*, y todas las documentaciones transicionan a **`Recibida y Aceptada`**, registrando el **Legajo y Nombre** del empleado responsable. |
| **Flujo Alternativo A1** | **Precinto no encontrado** | El usuario filtra por un precinto inexistente en la barra superior. El sistema emite advertencia de *"No se encontró bolsín con el precinto ingresado"* sin alterar el sistema. |
| **Flujo Alternativo A2** | **CM Origen no encontrada** | El usuario filtra por un origen que no tiene bolsines enviados hacia su CM. El sistema emite advertencia. |
| **Flujo Alternativo A3** | **Opción 2: Documentación faltante** | El operador tilda el documento que no llegó físicamente. El documento faltante ejecuta `registrarFaltaDocumentacion(...)` y pasa a **`NoRecibida`**. Los demás pasan a `Recibida y Aceptada`. |
| **Flujo Alternativo A4** | **Opción 3: Documentación no corresponde al destino** | El operador marca el documento erróneo, que ejecuta `rechazar(...)` y transiciona a **`RecibidaYRechazada`**. |
| **Flujo Alternativo A5** | **Opción 4: Documentación para redirigir** | El documento seleccionado ejecuta `redirigirBolsin(...)` y pasa a **`ParaRedirigir`**. |
| **Flujo Alternativo A6** | **Cancelación voluntaria** | Al presionar *"Cancelar (A6)"* o seleccionar *"No"* en la confirmación, se cancela la operación sin modificar la base de datos ni los estados. |

---

## 6. Arquitectura de Persistencia: Esquema en Capas vs. Super Objeto Persistente

Para la persistencia de datos se implementó un **Esquema de Persistencia en Capas (Capa de Persistencia Separada / Patrón DAO - Repository)**, descartando explícitamente el enfoque de *Super Objeto Persistente (Active Record)*:

### A. Justificación del Enfoque Seleccionado
* **Super Objeto Persistente (Descartado):** En este patrón, las entidades del dominio heredan de una superclase base persistente (ej. `ObjetoPersistente`) que contiene métodos como `guardar()` o `actualizar()`, o la entidad misma ejecuta sentencias SQL. **Problema:** Mezcla lógica de negocio con infraestructura técnica, violando el Principio de Responsabilidad Única (SRP) de SOLID y acoplando fuertemente el dominio al motor de datos.
* **Esquema de Persistencia en Capas (Implementado):** 
  * Las entidades de dominio (`Bolsin`, `Remito`, `DetalleRemito`, `Documentacion` y la jerarquía `EstadoDocumentacion`) son **objetos puros de negocio (POJOs)**: no contienen sentencias SQL, no conocen la tecnología del motor y no heredan de clases de infraestructura.
  * Se incorpora una **capa de persistencia desacoplada** (`com.utn.bolsines.persistencia`):
    * **`ConexionBD.java` (Capa de Administración de Datos):** Administra el ciclo de vida de la conexión JDBC hacia SQLite y el esquema DDL.
    * **`RepositorioBolsin.java` (Capa de Persistencia / DAO):** Se encarga exclusivamente de transferir y mapear datos entre las tablas relacionales y las entidades de dominio, ejecutando transacciones atómicas seguras (`conn.setAutoCommit(false)`).
  * **Beneficio:** Garantiza **Bajo Acoplamiento y Alta Cohesión**. Si el motor cambiara a PostgreSQL, MySQL u Oracle, las entidades de dominio y el Patrón State permanecen intactos.

---

## 7. ¿Cómo Inspeccionar la Base de Datos en Vivo?

Existen tres formas directas de ver cómo cambian los estados en la base de datos mientras el programa corre:

### Forma 1: Visor Integrado en la Aplicación (Recomendado para la Defensa)
En la misma pantalla del sistema, hacer clic en el botón amarillo:
```
[ 📊 Ver Base de Datos en Vivo ]
```
Se abre una ventana paralela (`VisorBaseDatosDialog.java`) con las tablas de SQLite en tiempo real:
* **Pestaña 1 (Estados Actuales):** Muestra las 3 tablas centrales del circuito:
  * `bolsin`: Estado actual en BD (*Recibido en CM Destino* / *Enviado*), peso y precinto.
  * `remito`: Estado actual en BD (*Recibido y Aceptado* / *En Bolsín Enviado*), emisor y destino.
  * `documentacion`: Estado actual en BD (*Recibida y Aceptada*, *NoRecibida*, etc. bajo el Patrón State).
* **Pestaña 2 (Historial de Estados y Trazabilidad):**
  * `cambio_estado_documentacion`: Muestra todas las transiciones con `fecha_hora_inicio`, `fecha_hora_fin` y la columna **Responsable (Legajo y Nombre)** (ej: `Ana González (Legajo: LEG-2002)`).
  * `cambio_estado_bolsin`: Muestra el historial del bolsín con la identificación del responsable por legajo.
* Al confirmar una recepción en la pantalla principal, hacer clic en **"↻ Actualizar Datos de SQLite"** en el visor y verás cómo las filas cambian de estado inmediatamente.

### Forma 2: Con DB Browser for SQLite (Software externo)
1. Descargar o abrir [DB Browser for SQLite](https://sqlitebrowser.org/).
2. Abrir el archivo `bolsines.db` ubicado en la raíz del proyecto.
3. Ir a la pestaña *"Examinar datos"*, seleccionar la tabla `documentacion`, `remito` o `bolsin`, y presionar `F5` (Refrescar).

### Forma 3: Desde IntelliJ IDEA (Database Tools)
1. En IntelliJ IDEA, abrir la pestaña **Database** (a la derecha).
2. Hacer clic en **`+`** -> **Data Source** -> **SQLite**.
3. En el campo *File*, seleccionar el archivo `bolsines.db` del proyecto.
4. Hacer doble clic en la tabla `documentacion` para ver los registros y usar el botón de refresco (Ctrl + F5).

---

## 8. Instrucciones de Ejecución y Guía de Pruebas

### Opción A: Desde Windows (Doble clic)
* **`ejecutar.bat`**: Inicia la interfaz gráfica interactiva Swing comenzando por la pantalla de login.
* **`ejecutar_consola.bat`**: Ejecuta la demostración automatizada paso a paso por consola validando todos los flujos.

### Opción B: Por Línea de Comandos (PowerShell / CMD)
```powershell
# Modo Gráfico Interactivo (Login + Swing Desktop)
java -cp "lib/*;PPAI-Bolsines/lib/*;out/production/PPAI 2da Entrega;PPAI-Bolsines/bin" com.utn.bolsines.Main

# Modo Demostración Consola Automatizada
java -cp "lib/*;PPAI-Bolsines/lib/*;out/production/PPAI 2da Entrega;PPAI-Bolsines/bin" com.utn.bolsines.Main --consola
```

### Opción C: Desde IntelliJ IDEA
1. Asegurarse de que la carpeta `lib/` esté agregada como biblioteca (*Clic derecho en `lib` -> Add as Library...*).
2. Abrir `src/main/java/com/utn/bolsines/Main.java`.
3. Clic derecho -> **Run 'Main.main()'**.

---

### Guía Rápida de Demostración para la Defensa

1. **Login:** Iniciar sesión con **Ana González (CM Villa María)**.
2. **Flujo Básico:** Seleccionar el Bolsín #301 (`PREC-998877`), dejar elegida la Opción 1, hacer clic en *"Confirmar Recepción"*. Verificar que pasa a *Recibido en CM Destino* y los documentos a *Recibida y Aceptada*, registrando a Ana González (`LEG-2002`).
3. **Filtro Flexible (Flujo A1):** Escribir `PREC-000000` para comprobar el mensaje de no encontrado (A1). Luego escribir sólo los números `445566` para encontrar de inmediato el Bolsín #302.
4. **Flujo A3 (Faltante):** Con el Bolsín #302, seleccionar la Opción 2, tildar el `Doc #1003` y confirmar. Verificar que la documentación transiciona a `NoRecibida`.
5. **Auditoría en vivo:** Abrir el botón *"📊 Ver Base de Datos en Vivo"* para mostrarle al docente cómo cambió el estado en las tablas `bolsin`, `remito` y `documentacion` y cómo se registró el legajo del operador.
