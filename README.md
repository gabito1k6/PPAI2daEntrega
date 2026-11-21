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
* **c. Tecnología:** **Escritorio** (Desktop GUI nativa).
* **d. Base de datos y persistencia:** Base de datos relacional **SQLite** (`bolsines.db`) implementada bajo la arquitectura en capas con el patrón **DAO / Repository** (`RepositorioBolsin.java` y `ConexionBD.java`) y transacciones atómicas SQL (`setAutoCommit(false)`).

---

## 2. Arquitectura del Sistema y Consistencia con el Modelado

El diseño respeta de manera estricta el patrón arquitectónico **Boundary - Control - Entity** (Frontera - Control - Entidad) de GRASP, la jerarquía de clases oficial de DSI (Grupo 8) y las correcciones de la cátedra:

```
src/main/java/com/utn/bolsines/
├── Main.java                                <--- Punto de entrada del sistema
├── pantallas/                               <--- Capa FRONTERA / BOUNDARY
│   ├── PantallaLogin.java                   <--- Inicio de sesión interactivo y selección de empleado
│   ├── PantRecepcionBolsin.java             <--- <<boundary>> Nombre exacto del Diagrama de Clases
│   ├── PantallaRecepcionBolsin.java         <--- GUI interactiva de escritorio Swing (JFrame)
│   ├── DialogoCrearBolsin.java              <--- Diálogo modal para registrar y enviar nuevos bolsines
│   └── VisorBaseDatosDialog.java            <--- Visor de auditoría de SQLite en vivo (Bolsín, Remito, Doc)
├── gestores/                                <--- Capa CONTROLADOR / GESTOR
│   └── GestorRecepcionBolsin.java           <--- <<control>> Orquestador del CU 28 y filtro flexible
├── entidades/                               <--- Capa DOMINIO / ENTIDADES
│   ├── Bolsin.java                          <--- <<entity>> Contexto de Bolsín (Patrón Experto)
│   ├── Remito.java                          <--- <<entity>> Remito de envío y cascada hacia detalles
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
│       ├── EstadoDocumentacion.java         <--- CLASE ABSTRACTA State (GoF) de Documentación
│       ├── EstadoBolsin.java                <--- CLASE ABSTRACTA State de Bolsín
│       ├── EstadoRemito.java                <--- CLASE ABSTRACTA State de Remito
│       ├── EstadoEnBolsinEnviado.java       <--- Estado Concreto inicial del CU 28
│       ├── EstadoRecibidaYAceptada.java     <--- Estado Concreto (Flujo Básico)
│       ├── EstadoNoRecibido.java            <--- Estado Concreto (Flujo Alternativo A3: Faltante)
│       ├── EstadoRecibidaYRechazada.java    <--- Estado Concreto (Flujo Alternativo A4: Rechazo)
│       ├── EstadoParaRedirigir.java         <--- Estado Concreto (Flujo Alternativo A5: Redirección)
│       ├── EstadoRegistrada.java            <--- Estado Concreto inicial tras CU 7
│       ├── EstadoEnRemito.java              <--- Estado Concreto (asociado a remito)
│       ├── EstadoEnBolsinSaliente.java      <--- Estado Concreto (en bolsín listo para retiro)
│       └── EstadoEnBaja.java                <--- Estado Concreto final de baja
├── persistencia/                            <--- Capa PERSISTENCIA / REPOSITORIO
│   ├── ConexionBD.java                      <--- Conexión SQLite y esquema DDL relacional
│   └── RepositorioBolsin.java               <--- DAO con consultas y transacciones SQL atómicas
└── servicios/
    └── MailServerService.java               <--- Simulación CU 29 (Notificación a CM Origen)
```

---

## 3. Consistencia Total con los Diagramas de DSI (Grupo 8)

Para garantizar consistencia absoluta con el **Diagrama de Clases**, el **Diagrama de Secuencias** y la **Máquina de Estados de Documentación** del Grupo 8, cada método y relación del diseño fue implementado de forma nominal y funcional:

### A. Capa Frontera: `<<boundary>> PantRecepcionBolsin`
* **Nombres de Métodos exactos:**
  * `opRegistrarRecepcionBolsin()`: Disparado por el actor al ingresar a la opción.
  * `habilitarVentana()`: Habilita y hace visible la ventana gráfica interactiva.
  * `mostrarCM(nombreCM)`: Muestra la Comisión Médica activa del usuario logueado (Paso 2).
  * `solicitarSeleccionBolsin(bolsines)`: Presenta en la tabla los bolsines en estado *Enviado* para la CM destino (Paso 4).
  * `tomarSeleccionBolsin(bolsin)`: Toma la fila o bolsín seleccionado por el actor (Paso 5).
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
  * `documentacionDelRemitos`: Objetos `Documentacion` contenidos.
  * `opcionSelecc`: Entero con la opción seleccionada (1 a 4).
  * `confirmacionRecepcion`: Booleano de confirmación.
  * `fechaHoraActual`: Marca temporal obtenida en `getFechaHoraActual()`.
  * `remitoSeleccionado`: `Remito` activo en la evaluación.
* **Métodos exactos del Diagrama de Secuencias:**
  * `registrarRecepcionBolsin()`
  * `pedirUsuarioLogueado()` -> navega por la `Sesion` activa.
  * `buscarCM()` -> invoca `usuarioLogueado.buscarCM()`.
  * `buscarBolsines()` -> aplica Patrón Experto sobre cada Bolsín.
  * `buscarDatosBolsin()` -> invoca `mostrarTuCMOrigen()` y `getNroPrecinto()`.
  * `tomarSeleccionBolsin(...)`
  * `buscarRemito()` -> invoca `bolsin.obtenerRemito()`.
  * `tomarSelecOpcion(...)`
  * `tomarConfirmacion()`
  * `getFechaHoraActual()`
  * `buscarEstadoRecibidoCMDestino()`
  * `buscarEstadoRecibidoYAceptado()`
  * `buscarEstadoRecibidaYAceptada()`
  * `actualizarEstados()` -> invoca `bolsin.recibir(...)` y delega al Patrón State.
  * `crearCEBolsin()` -> actualiza el historial del bolsín.
  * `llamarCU29NotifRecepcionBolsin()` -> envía notificación a la CM remitente.
  * `finCU()`

### C. Capa Dominio / Entidades
* **`Bolsin`:** Métodos exactos: `esTuCMDestino(cm)`, `sosEnviado()`, `getNumeroBolsin()`, `mostrarTuCMOrigen()`, `getNroPrecinto()`, `obtenerRemito()`, `recibir()`, `crearCEBolsin()`.
* **`Remito`:** Métodos exactos: `getNumero()`, `obtenerInfoDocumentacion()`, `recibirYAceptar()`.
* **`DetalleRemito`:** Métodos exactos: `actualizarEstadoDoc()`, `obtenerDatosDetalle()`.
* **`Empleado`:** Métodos exactos: `buscarCM()`, `esTuUsuario(nombre)`. Atributos: `nombre`, `apellido`, `email`, `legajo`.
* **`Usuario`:** Entidad con atributos `nombre`, `contraseña`, vinculada con `Empleado` y `Sesion`.
* **`Sesion`:** Atributos `fechaHoraInicio`, `fechaHoraFin`, `usuario: Usuario` y método `getUsuario()`.
* **`CambioEstadoBolsin` y `CambioEstadoDocumentacion`:** Métodos `sosActual()`, `sosEnviado()`, `setFechaHoraFin()`, `new()`.
* **`Estado`:** Interfaz común con `esAmbitoBolsin()`, `esAmbitoRemito()`, `esAmbitoDocumentacion()`, `esEnviado()`, `esRecibidoEnCMDestino()`, `esRecibidoYAceptado()`, `esRecibidaYAceptada()`.

---

## 4. Aplicación del Patrón de Diseño GoF: STATE

El rediseño del CU 28 aplica el patrón **State** de Gamma (GoF) sobre la clase **`Documentacion`** y cumple con las Correcciones 1, 3 y 7 de la cátedra:

```
                  +-----------------------+
                  |     Documentacion     | <--- CONTEXTO
                  +-----------------------+
                  | - estadoActual        |
                  +-----------------------+
                  | + recibir(...)        | ---+
                  | + noRecibir(...)      |    |
                  | + rechazar(...)       |    | Delega al estado actual
                  | + redirigir(...)      |    | obtenerEstadoActual().recibir(this, ...)
                  +-----------------------+    |
                              |                |
                              v                v
                 +--------------------------+
                 | <<abstract>>             |
                 | EstadoDocumentacion      | <--- CLASE ABSTRACTA STATE
                 +--------------------------+
                 | + recibir(...)           | (Lanza UnsupportedOperationException)
                 | + noRecibir(...)         |
                 | + rechazar(...)          |
                 | + redirigir(...)         |
                 +--------------------------+
                              ^
                              | (Herencia: extends)
        +---------------------+---------------------+---------------------+
        |                     |                     |                     |
+-------------------+ +-------------------+ +-------------------+ +-------------------+
| EstadoEnBolsin    | | EstadoRecibida    | | EstadoNoRecibido  | | EstadoPara        |
| Enviado           | | YAceptada         | |                   | | Redirigir         |
+-------------------+ +-------------------+ +-------------------+ +-------------------+
| (CU 28 Inicial)   | | (Flujo Básico)    | | (Flujo A3 Falt.)  | | (Flujo A5 Redir.) |
+-------------------+ +-------------------+ +-------------------+ +-------------------+
```

### A. Clases Abstractas vs. Estados Concretos (Herencia)
1. **Clase Abstracta State (`EstadoDocumentacion`):**
   * Es una clase abstracta (`public abstract class EstadoDocumentacion implements Estado`).
   * Define la firma de todos los eventos del ciclo de vida de la documentación (`recibir`, `noRecibir`, `rechazar`, `redirigir`, `registrar`, `registrarEnRemito`, `agregarEnBolsin`, `registrarRetiro`, `darDeBaja`).
   * Provee implementaciones por defecto que lanzan `UnsupportedOperationException("Transición no válida")` para bloquear transiciones ilegales.
2. **Estados Concretos (`extends EstadoDocumentacion`):**
   * Heredan de `EstadoDocumentacion` e implementan **únicamente las transiciones válidas** de la máquina de estados.
   * `EstadoEnBolsinEnviado`: Al recibir la llamada `recibir(...)`, transiciona a `EstadoRecibidaYAceptada`. Al recibir `noRecibir(...)`, transiciona a `EstadoNoRecibido`. Al recibir `rechazar(...)`, transiciona a `EstadoRecibidaYRechazada`. Al recibir `redirigir(...)`, transiciona a `EstadoParaRedirigir`.

### B. Dónde se Delega la Transición al Estado Actual
En [Documentacion.java](file:///c:/Users/ggara_qabgn/Documents/4toUTN/DSI/PPAI%202da%20Entrega/PPAI-Bolsines/src/main/java/com/utn/bolsines/entidades/Documentacion.java#L70-L160), la clase Contexto **no evalúa condiciones (sin sentencias if/switch)**:
```java
// [PATRÓN STATE] DELEGA la transición a 'estadoActual.recibir(this, ...)'
public void recibir(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
    obtenerEstadoActual().recibir(this, nuevoEstado, fechaHora, responsable);
}

// [PATRÓN STATE] DELEGA la transición a 'estadoActual.noRecibir(this, ...)'
public void noRecibir(EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
    obtenerEstadoActual().noRecibir(this, nuevoEstado, fechaHora, responsable);
}
```
Y en [EstadoEnBolsinEnviado.java](file:///c:/Users/ggara_qabgn/Documents/4toUTN/DSI/PPAI%202da%20Entrega/PPAI-Bolsines/src/main/java/com/utn/bolsines/entidades/estados/EstadoEnBolsinEnviado.java#L30-L75), el estado actual concreta el cambio sobre el contexto:
```java
@Override
public void recibir(Documentacion contexto, EstadoDocumentacion nuevoEstado, LocalDateTime fechaHora, Empleado responsable) {
    EstadoDocumentacion destino = (nuevoEstado != null) ? nuevoEstado : new EstadoRecibidaYAceptada();
    contexto.crearCE(destino, fechaHora, responsable);
}
```

### C. Trazabilidad con Responsable y Legajo (Corrección 7 y Paso 11)
El método `crearCE(...)` de `Documentacion` cierra el cambio de estado anterior asignándole `fechaHoraFin` al instante de la recepción, crea el nuevo objeto `CambioEstadoDocumentacion` con `fechaHoraInicio` y asocia al `Empleado` responsable, almacenando y exponiendo su **Legajo**.

---

## 5. Flujos Implementados y Verificados

El sistema implementa el **Flujo Básico** y **5 Flujos Alternativos** especificados en el Requerimiento Funcional, junto con las funcionalidades clave de la 2da Entrega:

| Flujo / Funcionalidad | Opción / Acción | Descripción y Comportamiento del Sistema |
| :--- | :--- | :--- |
| **Inicio de Sesión (Login)** | **Autenticación y Selección de CM** | Pantalla simple y amigable (`PantallaLogin.java`) para seleccionar al empleado y validar credenciales. Preselecciona a Ana González (CM Villa María). Permite además *Cerrar Sesión* desde la interfaz principal para cambiar de operador. |
| **Filtro Flexible de Precinto** | **Búsqueda completa o sólo numérica** | El Gestor (`GestorRecepcionBolsin.coincidePrecinto`) admite tanto el formato completo (ej: `PREC-445566` o `PREC445566`) como sólo los números (ej: `445566` o `44`), insensible a mayúsculas/minúsculas. |
| **Creación de Bolsines en Vivo** | **Botón `➕ Crear Nuevo Bolsín`** | Diálogo modal (`DialogoCrearBolsin.java`) para dar de alta bolsines con remito y documentación asociada con numeración autoincremental, persistiendo inmediatamente en SQLite para generar nuevos casos de prueba. |
| **Flujo Básico** | **Opción 1: Contenido igual al registrado** | Todo el contenido llega conforme. En el **Paso 11**, el Bolsín pasa a *Recibido en CM Destino*, los Remitos a *Recibido y Aceptado*, y todas las documentaciones transicionan a **`Recibida y Aceptada`**, registrando y exhibiendo el **Legajo y Nombre** del empleado responsable. |
| **Flujo Alternativo A1** | **Precinto no encontrado** | El usuario filtra por un precinto inexistente en la barra superior. El sistema emite advertencia de *"No se encontró bolsín con el precinto ingresado"* sin alterar el sistema. |
| **Flujo Alternativo A2** | **CM Origen no encontrada** | El usuario filtra por un origen que no tiene bolsines enviados hacia su CM. El sistema emite advertencia. |
| **Flujo Alternativo A3** | **Opción 2: Documentación faltante** | El operador tilda cuál documento no llegó físicamente. El documento faltante ejecuta `noRecibir(...)` y pasa a **`No Recibido`** mediante el Patrón State. Los demás pasan a `Recibida y Aceptada`. |
| **Flujo Alternativo A4** | **Opción 3: Documentación no corresponde al destino** | El operador selecciona el documento erróneo, que ejecuta `rechazar(...)` y transiciona a **`Recibida y Rechazada`**. |
| **Flujo Alternativo A5** | **Opción 4: Documentación para redirigir** | El documento seleccionado ejecuta `redirigir(...)` y pasa a **`Para Redirigir`**. |
| **Flujo Alternativo A6** | **Cancelación voluntaria** | Al presionar *"Cancelar (A6)"* o seleccionar *"No"* en el diálogo de confirmación, se cancela la operación sin modificar la BD ni los estados. |

---

## 6. Arquitectura de Persistencia: Esquema en Capas vs. Super Objeto Persistente

Para la persistencia de datos se implementó un **Esquema de Persistencia en Capas (Capa de Persistencia Separada / Patrón DAO - Repository)**, descartando explícitamente el enfoque de *Super Objeto Persistente (Active Record)*:

### A. Justificación del Enfoque Seleccionado
* **Super Objeto Persistente (No utilizado):** En este patrón, las clases del dominio heredan de una clase base (ej. `ObjetoPersistente`) que contiene métodos como `guardar()` o `actualizar()`, o la entidad misma escribe sentencias SQL. **Problema:** Mezcla lógica de negocio con infraestructura de base de datos, violando el Principio de Responsabilidad Única (SRP) de SOLID y generando un alto acoplamiento con el motor de base de datos.
* **Esquema de Persistencia en Capas (Implementado):** 
  * Las entidades de dominio (`Bolsin`, `Remito`, `DetalleRemito`, `Documentacion` y la jerarquía `EstadoDocumentacion`) son **objetos puros de negocio (POJOs)**: no contienen sentencias SQL, no conocen la tecnología del motor y no heredan de clases de infraestructura.
  * Se incorpora una **capa de persistencia desacoplada** (`com.utn.bolsines.persistencia`):
    * **`ConexionBD.java` (Capa de Administración de Datos):** Administra el ciclo de vida de la conexión JDBC hacia SQLite y el esquema DDL.
    * **`RepositorioBolsin.java` (Capa de Persistencia / DAO):** Se encarga exclusivamente de transferir y mapear datos entre las tablas relacionales y las entidades de dominio, ejecutando transacciones atómicas seguras (`conn.setAutoCommit(false)`).
  * **Beneficio:** Garantiza **Bajo Acoplamiento y Alta Cohesión**. Si el motor de base de datos cambiara a PostgreSQL, MySQL u Oracle, las entidades de dominio y el Patrón State permanecen intactos.

### B. Motor y Esquema de Base de Datos SQLite (`bolsines.db`)
El sistema cuenta con un motor de base de datos relacional SQLite real en archivo local:
* **Tablas Relacionales:** `comision_medica`, `empleado`, `tipo_documento`, `bolsin`, `cambio_estado_bolsin`, `remito`, `detalle_remito`, `documentacion`, `cambio_estado_documentacion`.
* **Inicialización automática:** Si `bolsines.db` no existe, se crea automáticamente el esquema e inserta los datos semilla de prueba:
  * Bolsín #301 (origen Córdoba, enviado a Villa María).
  * Bolsín #302 (origen Rosario, enviado a Villa María).
  * Bolsín #303 (en estado *Creado*, para validar el filtrado del Patrón Experto).
* **Restablecimiento de datos:** Cuenta con el método `reiniciarDatosSemilla()` y un botón en la interfaz para volver a cargar los bolsines listos para ser recibidos tantas veces como se desee.

---

## 7. ¿Cómo Inspeccionar la Base de Datos en Vivo?

Existen tres formas directas de ver cómo cambian los estados en la base de datos mientras el programa corre:

### Forma 1: Visor Integrado en la Aplicación (Recomendado para la Defensa)
En la misma pantalla del sistema, hacé clic en el botón amarillo:
```
[ 📊 Ver Base de Datos en Vivo ]
```
Se abre una ventana paralela (`VisorBaseDatosDialog.java`) con las tablas de SQLite en tiempo real:
* **Pestaña 1 (Estados Actuales):** Muestra las 3 tablas centrales del circuito:
  * `bolsin`: Estado actual en BD (*Recibido en CM Destino* / *Enviado*), peso y precinto.
  * `remito`: Estado actual en BD (*Recibido y Aceptado* / *En Bolsín Enviado*), emisor y destino.
  * `documentacion`: Estado actual en BD (*Recibida y Aceptada*, *No Recibido*, etc. bajo el Patrón State).
* **Pestaña 2 (Historial de Estados y Trazabilidad):**
  * `cambio_estado_documentacion`: Muestra todas las transiciones con `fecha_hora_inicio`, `fecha_hora_fin` y la columna **Responsable (Legajo y Nombre)** (ej: `Ana González (Legajo: LEG-2002)`).
  * `cambio_estado_bolsin`: Muestra el historial del bolsín con la identificación del responsable por legajo.
* Al confirmar una recepción en la pantalla principal, hacé clic en **"↻ Actualizar Datos de SQLite"** en el visor y verás cómo las filas cambian de estado inmediatamente.

### Forma 2: Con DB Browser for SQLite (Software externo)
1. Descargá o abrí [DB Browser for SQLite](https://sqlitebrowser.org/).
2. Abrí el archivo `bolsines.db` ubicado en la raíz del proyecto.
3. Andá a la pestaña *"Examinar datos"*, seleccioná la tabla `documentacion`, `remito` o `bolsin`, y presioná `F5` (Refrescar).

### Forma 3: Desde IntelliJ IDEA (Database Tools)
1. En IntelliJ IDEA, abrí la pestaña **Database** (a la derecha).
2. Hacé clic en **`+`** -> **Data Source** -> **SQLite**.
3. En el campo *File*, seleccioná el archivo `bolsines.db` del proyecto.
4. Hacé doble clic en la tabla `documentacion` para ver los registros y usá el botón de refresco (Ctrl + F5).

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
4. **Flujo A3 (Faltante):** Con el Bolsín #302, seleccionar la Opción 2, tildar el `Doc #1003` y confirmar. Verificar que la documentación transiciona a `No Recibido`.
5. **Auditoría en vivo:** Abrir el botón *"📊 Ver Base de Datos en Vivo"* para mostrarle al docente cómo cambió el estado en las tablas `bolsin`, `remito` y `documentacion` y cómo se registró el legajo del operador.
