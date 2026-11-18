# Proyecto Práctico de Aplicación Integrador (PPAI) - 2da Entrega: Diseño e Implementación
**Universidad Tecnológica Nacional – Facultad Regional Córdoba (UTN-FRC)**  
**Cátedra:** Diseño de Sistemas de Información (DSI)  
**Grupo:** Grupo 12 (Grupo Par)  
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
* **d. Base de datos y persistencia:** Base de datos relacional **SQLite** (`bolsines.db`) implementada bajo la arquitectura en capas con el patrón **DAO / Repository** (`RepositorioBolsin.java` y `ConexionBD.java`) y transacciones atómicas SQL.

---

## 2. Arquitectura del Sistema y Consistencia con el Modelado

El diseño respeta de manera estricta el patrón arquitectónico **Boundary - Control - Entity** (Frontera - Control - Entidad) de GRASP y las correcciones de la cátedra:

```
src/main/java/com/utn/bolsines/
├── Main.java                                <--- Punto de entrada del sistema
├── pantallas/                               <--- Capa FRONTERA / BOUNDARY
│   ├── PantallaRecepcionBolsin.java         <--- GUI interactiva de escritorio (Swing)
│   └── VisorBaseDatosDialog.java            <--- Visor de auditoría de SQLite en vivo
├── gestores/                                <--- Capa CONTROLADOR / GESTOR
│   └── GestorRecepcionBolsin.java           <--- Orquestador del flujo del CU 28
├── entidades/                               <--- Capa DOMINIO / ENTIDADES
│   ├── Bolsin.java                          <--- Contexto de Bolsín (Patrón Experto)
│   ├── Remito.java                          <--- Jerarquía en cascada
│   ├── DetalleRemito.java
│   ├── Documentacion.java                   <--- Contexto del Patrón STATE (GoF)
│   ├── CambioEstadoBolsin.java              <--- Trazabilidad histórica de estados
│   ├── CambioEstadoDocumentacion.java       <--- Trazabilidad histórica de estados
│   ├── ComisionMedica.java
│   ├── Empleado.java
│   ├── Sesion.java
│   ├── TipoDocumento.java
│   ├── Archivo.java
│   └── estados/                             <--- Jerarquía del Patrón STATE
│       ├── Estado.java                      <--- Interfaz con búsqueda polimórfica
│       ├── EstadoDocumentacion.java         <--- Clase Abstracta STATE (GoF)
│       ├── EstadoEnBolsinEnviado.java       <--- Estado Concreto inicial
│       ├── EstadoRecibidaYAceptada.java     <--- Estado Concreto (Flujo Básico)
│       ├── EstadoNoRecibido.java            <--- Estado Concreto (Flujo Alternativo A3)
│       ├── EstadoRecibidaYRechazada.java    <--- Estado Concreto (Flujo Alternativo A4)
│       ├── EstadoParaRedirigir.java         <--- Estado Concreto (Flujo Alternativo A5)
│       ├── EstadoRegistrada.java
│       ├── EstadoEnRemito.java
│       ├── EstadoEnBolsinSaliente.java
│       └── EstadoEnBaja.java
├── persistencia/                            <--- Capa PERSISTENCIA / REPOSITORIO
│   ├── ConexionBD.java                      <--- Conexión SQLite y esquema DDL
│   └── RepositorioBolsin.java               <--- DAO con consultas y transacciones SQL
└── servicios/
    └── MailServerService.java               <--- Simulación CU 29 (Notificación a CM Origen)
```

---

## 3. Aplicación del Patrón de Diseño GoF: STATE

El rediseño del CU 28 aplica el patrón **State** de Gamma (GoF) sobre la clase **`Documentacion`**:

1. **Contexto (`Documentacion`):**
   * Mantiene una referencia a su estado actual mediante el atributo polimórfico `private EstadoDocumentacion estadoActual`.
   * En lugar de evaluar sentencias condicionales (`if/switch`) según el estado, **delega la ejecución de los eventos** (`recibir()`, `noRecibir()`, `rechazar()`, `redirigir()`) a su objeto estado actual.
2. **Clase Abstracta State (`EstadoDocumentacion`):**
   * Define la interfaz de comportamiento para todos los estados posibles de la documentación.
   * Provee implementaciones por defecto que lanzan `UnsupportedOperationException("Transición no válida")` para evitar transiciones ilegales en el ciclo de vida.
3. **Estados Concretos (`EstadoEnBolsinEnviado`, `EstadoRecibidaYAceptada`, `EstadoNoRecibido`, etc.):**
   * Cada estado concreto implementa la transición válida. Por ejemplo:
     * Desde `EstadoEnBolsinEnviado`, invocar `recibir(...)` transiciona el contexto a `EstadoRecibidaYAceptada`.
     * Invocar `noRecibir(...)` transiciona el contexto a `EstadoNoRecibido`.
     * Invocar `rechazar(...)` transiciona a `EstadoRecibidaYRechazada`.
4. **Trazabilidad de Estados (Corrección 7 de DSI):**
   * Al ejecutarse `actualizarEstado(...)`, la entidad busca su `CambioEstadoDocumentacion` actual (donde `sosActual() == true`), le asigna la `fechaHoraFin` al instante de la recepción, e instancia el nuevo `CambioEstadoDocumentacion` con `fechaHoraInicio` y el `Empleado` responsable.

---

## 4. Flujos Implementados y Verificados

El sistema implementa el **Flujo Básico** y **5 Flujos Alternativos** especificados en el Requerimiento Funcional:

| Flujo | Opción / Acción | Descripción y Comportamiento del Patrón State |
| :--- | :--- | :--- |
| **Flujo Básico** | **Opción 1: Contenido igual al registrado** | Todo el contenido llega conforme. El Bolsín pasa a *Recibido en CM Destino*, los Remitos a *Recibido y Aceptado*, y todas las documentaciones transicionan a **`Recibida y Aceptada`**. |
| **Flujo Alternativo A1** | **Precinto no encontrado** | El usuario filtra por un precinto inexistente en la barra superior. El sistema emite advertencia de *"No se encontró bolsín con el precinto ingresado"* sin alterar el sistema. |
| **Flujo Alternativo A2** | **CM Origen no encontrada** | El usuario filtra por un origen que no tiene bolsines enviados hacia su CM. El sistema emite advertencia. |
| **Flujo Alternativo A3** | **Opción 2: Documentación faltante** | El operador tilda cuál documento no llegó físicamente. El documento faltante ejecuta `noRecibir(...)` y pasa a **`No Recibido`** mediante el Patrón State. Los demás pasan a `Recibida y Aceptada`. |
| **Flujo Alternativo A4** | **Opción 3: Documentación no corresponde al destino** | El operador selecciona el documento erróneo, que ejecuta `rechazar(...)` y transiciona a **`Recibida y Rechazada`**. |
| **Flujo Alternativo A5** | **Opción 4: Documentación para redirigir** | El documento seleccionado ejecuta `redirigir(...)` y pasa a **`Para Redirigir`**. |
| **Flujo Alternativo A6** | **Cancelación voluntaria** | Al presionar *"Cancelar (A6)"* o seleccionar *"No"* en el diálogo de confirmación, se cancela la operación sin modificar la BD ni los estados. |

---

## 5. Arquitectura de Persistencia: Esquema en Capas vs. Super Objeto Persistente

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

## 6. ¿Cómo Inspeccionar la Base de Datos en Vivo?

Existen tres formas directas de ver cómo cambian los estados en la base de datos mientras el programa corre:

### Forma 1: Visor Integrado en la Aplicación (Recomendado para la Defensa)
En la misma pantalla del sistema, hacé clic en el botón amarillo:
```
[ 📊 Ver Base de Datos en Vivo ]
```
Se abre una ventana paralela con las tablas de SQLite en tiempo real:
* **Pestaña 1:** Muestra las tablas `bolsin` y `documentacion` con sus columnas `estado_actual`.
* **Pestaña 2:** Muestra la tabla `cambio_estado_documentacion` con todas las transiciones históricas (`fecha_hora_inicio`, `fecha_hora_fin` y responsable).
* Al confirmar una recepción en la pantalla principal, hacé clic en **"↻ Actualizar Datos de SQLite"** en el visor y verás cómo las filas cambian de estado inmediatamente.

### Forma 2: Con DB Browser for SQLite (Software externo)
1. Descargá o abrí [DB Browser for SQLite](https://sqlitebrowser.org/).
2. Abrí el archivo `bolsines.db` ubicado en la raíz del proyecto.
3. Andá a la pestaña *"Examinar datos"*, seleccioná la tabla `documentacion` o `bolsin`, y presioná `F5` (Refrescar) para ver las actualizaciones en vivo.

### Forma 3: Desde IntelliJ IDEA (Database Tools)
1. En IntelliJ IDEA, abrí la pestaña **Database** (a la derecha).
2. Hacé clic en **`+`** -> **Data Source** -> **SQLite**.
3. En el campo *File*, seleccioná el archivo `bolsines.db` del proyecto.
4. Hacé doble clic en la tabla `documentacion` para ver los registros y usá el botón de refresco (Ctrl + F5).

---

## 7. Instrucciones de Ejecución

### Opción A: Desde Windows (Doble clic)
* **`ejecutar.bat`**: Inicia la interfaz gráfica interactiva Swing.
* **`ejecutar_consola.bat`**: Ejecuta la demostración automatizada paso a paso por consola.

### Opción B: Por Línea de Comandos
```powershell
# Modo Gráfico Interactivo (Swing)
java -cp "lib/*;PPAI-Bolsines/lib/*;out/production/PPAI 2da Entrega;PPAI-Bolsines/bin" com.utn.bolsines.Main

# Modo Demostración Consola
java -cp "lib/*;PPAI-Bolsines/lib/*;out/production/PPAI 2da Entrega;PPAI-Bolsines/bin" com.utn.bolsines.Main --consola
```

### Opción C: Desde IntelliJ IDEA
1. Asegurarse de que la carpeta `lib/` esté agregada como biblioteca (*Clic derecho en `lib` -> Add as Library...*).
2. Abrir `src/main/java/com/utn/bolsines/Main.java`.
3. Clic derecho -> **Run 'Main.main()'**.
