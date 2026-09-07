# Post-contenido — Unidad 3: Patrones Estructurales en ConfUDES

## Descripción

Repositorio del post-contenido de la Unidad 3 de Patrones de Diseño de Software. Es un único proyecto Spring Boot (`confudes-patrones-estructurales`) que resuelve cuatro necesidades reales del backend de ConfUDES, la plataforma con la que la universidad gestiona sus congresos académicos: el registro de asistencia contra un proveedor externo de validación QR, la emisión de certificados de participación, las mejoras opcionales que cada organizador puede activar sobre el certificado ya emitido, y el control de acceso a la descarga masiva de certificados.

Ninguna de las clases marcadas como dadas en el enunciado fue modificada. El SDK de QRCheckAPI, el contrato `ServicioAsistencia`, `ControladorCheckIn`, los cuatro servicios de certificados, `UtilidadesPDF` y `ContextoUsuario` están tal como se entregaron. Todo lo que se agregó vive en clases nuevas.

## Cómo ejecutar

```
$ mvn clean package
$ mvn spring-boot:run
$ mvn test
```

Requiere Java 17 o superior y Maven 3.8+.

## Estructura del proyecto

```
src/main/java/com/universidad/confudes/
├── ConfUdesApp.java
├── config/
│   └── ConfiguracionConfUdes.java        registra beans y arma las composiciones
├── externo/qrcheck/                      DADO: SDK del proveedor
│   ├── QRCheckClient.java
│   ├── QRCheckRequest.java
│   └── QRCheckResponse.java
├── asistencia/
│   ├── ServicioAsistencia.java           DADO: contrato interno
│   ├── ResultadoCheckIn.java             DADO
│   ├── ControladorCheckIn.java           DADO
│   └── AdaptadorQRCheck.java             Necesidad 1
├── certificados/
│   ├── ValidadorAsistencia.java          DADO
│   ├── GeneradorCertificadoPDF.java      DADO
│   ├── FirmaDigitalService.java          DADO
│   ├── EnvioCorreoService.java           DADO
│   ├── UtilidadesPDF.java                DADO
│   ├── ServicioCertificados.java         contrato formalizado en el Paso 7
│   ├── SolicitudCertificado.java
│   ├── AsistenciaInsuficienteException.java
│   ├── FachadaEmisionCertificados.java   Necesidad 2
│   ├── ControladorCertificados.java      refactorizado a un solo colaborador
│   ├── MejoraCertificado.java            Necesidad 3, clase base
│   ├── MejoraMarcaDeAgua.java            Necesidad 3
│   ├── MejoraCodigoQR.java               Necesidad 3
│   └── MejoraTraduccionIngles.java       Necesidad 3
└── acceso/
    ├── ContextoUsuario.java              DADO
    ├── ProxyControlAccesoCertificados.java   Necesidad 4
    └── ControladorDescargaMasiva.java
```

## Endpoints

| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/checkin?eventoId=&participanteId=&credencialQR=` | Registra asistencia validando la credencial contra QRCheckAPI |
| POST | `/api/certificados/{eventoId}/{participanteId}?nombre=&correoDestino=` | Emite y envía un certificado individual |
| POST | `/api/certificados/masivo/{eventoId}?participantes=&correoDestino=` | Descarga masiva en .zip, solo para ORGANIZADOR o ADMIN |

## Decisiones de diseño

### Necesidad 1 — Registro de asistencia

Patrón aplicado: **Adapter**, en la clase `AdaptadorQRCheck`.

El síntoma es claro: hay dos contratos que no encajan y ninguno de los dos se puede tocar. Por un lado `ServicioAsistencia`, que es lo que `ControladorCheckIn` y el módulo de reportes ya esperan, y que trabaja con `eventoId` como String, `credencialQR` como String y devuelve un `ResultadoCheckIn` con un booleano. Por el otro el SDK del proveedor, que exige un `QRCheckRequest` con el `idEvento` como `long`, un payload con prefijo `QR-` y devuelve un código numérico. El adaptador se queda en medio y hace exactamente tres traducciones: convierte el identificador de evento extrayendo sus dígitos, normaliza el prefijo del payload cuando la credencial ya lo trae, y mapea 200 a éxito, 401 a credencial inválida y cualquier otro código a un error inesperado del proveedor.

Alternativa descartada: **Facade**. Es la que más se le parece porque también envuelve a un objeto externo, pero un Facade existe para esconder la complejidad de varios colaboradores detrás de una operación simple. Aquí hay un solo colaborador (`QRCheckClient`) con un solo método, así que no hay complejidad que esconder. Y aunque se envolviera igual, un Facade no está obligado a implementar `ServicioAsistencia`, que es justamente lo que hace que `ControladorCheckIn` funcione sin modificarse: el problema no era simplificar, era hacer compatibles dos firmas que ya existían.

Sobre la normalización del payload vale la pena dejar constancia de una decisión concreta: el adaptador nunca inventa el prefijo `QR-`. Si lo agregara a cualquier cadena, una credencial falsa como `no-es-un-qr` pasaría a ser `QR-no-es-un-qr` y el proveedor la aprobaría, lo cual convertiría al adaptador en un agujero de seguridad. Solo se normaliza el prefijo cuando la credencial ya lo trae, en cualquier combinación de mayúsculas.

### Necesidad 2 — Emisión de certificados

Patrón aplicado: **Facade**, en la clase `FachadaEmisionCertificados`.

Aquí el síntoma es distinto: ninguno de los cuatro servicios tiene un contrato incompatible. `ValidadorAsistencia`, `GeneradorCertificadoPDF`, `FirmaDigitalService` y `EnvioCorreoService` funcionan tal como están y otros módulos los siguen usando directamente. El problema es que `ControladorCertificados` conocía a los cuatro y orquestaba la secuencia completa, así que cualquier cambio en la firma digital o en el correo obligaba a tocar un controlador que está en producción. La fachada recibe los cuatro servicios por constructor, encapsula la secuencia validar, generar, firmar y enviar detrás de un solo método `emitir`, y deja al controlador con una única dependencia y un cuerpo de siete líneas.

Alternativa descartada: **Adapter**. Un Adapter traduce un contrato a otro contrato preexistente, y aquí no hay ningún contrato preexistente que traducir. `ServicioCertificados` no existía antes: lo creamos nosotros como fachada. Además un Adapter clásico envuelve a un solo adaptado, mientras que el problema de esta necesidad es precisamente la cantidad de colaboradores. Si se aplicara un Adapter por servicio terminaríamos con cuatro adaptadores y el controlador seguiría conociendo a cuatro objetos, que es exactamente el problema que se quería resolver.

Para que la fachada pudiera exponer una sola operación sin devolverle al controlador la decisión sobre la asistencia mínima, la validación fallida se comunica con `AsistenciaInsuficienteException`. El controlador la traduce a un 403 sin necesidad de conocer al validador.

### Necesidad 3 — Mejoras opcionales del certificado

Patrón aplicado: **Decorator**, con la clase base `MejoraCertificado` y las tres mejoras `MejoraMarcaDeAgua`, `MejoraCodigoQR` y `MejoraTraduccionIngles`.

Cada mejora implementa `ServicioCertificados` y envuelve a otro `ServicioCertificados`, que puede ser la fachada base o una mejora ya aplicada. Eso permite apilarlas en cualquier orden: `new MejoraTraduccionIngles(new MejoraCodigoQR(new MejoraMarcaDeAgua(fachada)))` genera las tres, y quitar cualquiera de los envoltorios desactiva esa mejora sin tocar nada más. Son tres clases para ocho combinaciones posibles, y la fachada de la Necesidad 2 no se modificó.

Alternativa descartada, herencia: haría falta una subclase por combinación. Con tres mejoras son ocho clases (`CertificadoConMarcaDeAgua`, `CertificadoConMarcaDeAguaYQR`, `CertificadoConMarcaDeAguaYQRYTraduccion`, y así). Con cuatro mejoras serían dieciséis. El número de clases crece como 2 elevado a n, y cada mejora nueva obliga a duplicar todo lo que ya existía. Además Java no tiene herencia múltiple, así que combinar dos subclases ya escritas es imposible.

Alternativa descartada, parámetros booleanos: un método `emitir(solicitud, activarMarcaDeAgua, activarQR, activarTraduccion)` no crea clases nuevas, pero rompe el contrato `ServicioCertificados` que el resto del sistema ya inyecta, mete tres `if` dentro de la fachada que debían quedar fuera de ella, obliga a modificar la firma y la lógica base cada vez que se agrega una mejora, y fija el orden de aplicación en el código en lugar de dejarlo en manos de quien compone. Cuatro banderas booleanas seguidas en una llamada también son un punto clásico de errores por posición.

Por qué el patrón de la Necesidad 4 no serviría aquí: un Proxy controla el acceso a un objeto real y su decisión es binaria, deja pasar o no deja pasar. No está pensado para apilarse varias veces sobre sí mismo agregando capacidades acumulativas. Aunque técnicamente se pudieran encadenar tres proxies, ninguno estaría resolviendo el problema real, que es combinar mejoras libremente, y el nombre del patrón comunicaría lo contrario de lo que el código hace.

### Necesidad 4 — Control de acceso a la descarga masiva

Patrón aplicado: **Proxy de protección**, en la clase `ProxyControlAccesoCertificados`.

El proxy implementa `ServicioCertificados`, consulta `ContextoUsuario.rolActual()` y solo delega en el servicio real si el rol es ORGANIZADOR o ADMIN. Si no lo es, lanza `SecurityException` antes de llamar a `emitir`, así que la operación costosa (cada certificado consume al menos una de las 60 llamadas por minuto que permite el proveedor de firma) nunca se ejecuta. El resto del sistema, incluido el flujo de emisión individual que usan los participantes, sigue inyectando `ServicioCertificados` sin enterarse de que existe esta verificación.

Alternativa descartada: **Decorator**, la comparación más sutil del laboratorio. Estructuralmente son casi idénticos: los dos implementan `ServicioCertificados` y los dos envuelven a otro `ServicioCertificados`. La diferencia está en la intención y se ve en una sola línea de código. Un Decorator siempre llama a `envuelto.emitir(solicitud)` primero y después agrega algo al resultado; su razón de existir es que el objeto real se ejecute. Un Proxy decide antes de delegar y puede no delegar nunca. Si el control de acceso se hubiera escrito como una mejora, la emisión se ejecutaría completa (validación, generación, firma con su llamada al proveedor, envío del correo) y solo después se comprobaría el rol, es decir que un participante sin permiso igual habría consumido la cuota del proveedor y recibido su certificado por correo antes de que el sistema le negara el acceso. El test `noEjecutaLaOperacionCostosaCuandoElRolNoEstaAutorizado` verifica exactamente eso: envuelve un servicio espía y comprueba que no llegó a ejecutarse.

La conclusión práctica es que la estructura de clases no alcanza para identificar un patrón. Dos patrones con el mismo diagrama pueden resolver problemas opuestos, y lo que los distingue es qué pasa en el orden de las llamadas.

### Reflexión — Composite y Flyweight

Para la agenda de cada congreso encajaría **Composite**: tracks, sesiones y actividades forman un árbol de partes donde un track contiene sesiones y una sesión contiene actividades, y con Composite el código que calcula la duración total o imprime la agenda trata igual a una actividad suelta que a un track completo, sin preguntar de qué tipo es cada nodo.

**Flyweight** no aplica a las credenciales QR aunque haya miles: el patrón sirve cuando muchos objetos comparten estado intrínseco repetido y solo se diferencian en estado extrínseco. Cada credencial tiene payload, participante y evento únicos e irrepetibles, así que no hay nada que compartir y el patrón solo agregaría una fábrica de instancias sin ahorrar memoria.

## Herramientas utilizadas

- Java 17, Spring Boot 3.2.0, Apache Maven, JUnit 5
- Visual Studio Code, Git, GitHub

## Conclusiones

Lo más difícil de este laboratorio no fue escribir el código sino decidir entre patrones que se parecen mucho por fuera. Adapter y Facade se distinguieron rápido una vez que dejé de mirar la forma (los dos envuelven algo) y me puse a mirar el motivo: en la Necesidad 1 había dos contratos que ya existían y no encajaban, en la Necesidad 2 no había ningún contrato previo sino demasiados colaboradores. La comparación entre Decorator y Proxy fue mucho más incómoda porque las dos clases quedan casi idénticas en el editor, con el mismo campo envuelto y la misma interfaz implementada, y la diferencia real cabe en el orden de dos líneas: quien decora delega y después agrega, quien controla decide y a veces no delega. Escribir un test que comprobara que el objeto real no se ejecutó fue lo que terminó de aclararme la distinción, porque me obligó a expresar la intención del patrón como un comportamiento verificable y no como una definición aprendida. También me quedó claro que la restricción de no modificar el código dado es la que empuja hacia el patrón correcto: cuando no se puede tocar nada, las soluciones improvisadas dejan de estar disponibles y solo queda componer.
