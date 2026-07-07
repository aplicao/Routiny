# Como armar el proyecto Android

No incluyo build.gradle ni AndroidManifest.xml a proposito: es mas
seguro que Android Studio los genere el, porque asi te asegura
versiones de Gradle/AGP compatibles con lo que tengas instalado. Yo no
puedo verificar eso desde aca.

## Pasos

1. Android Studio -> New Project -> **Empty Views Activity**
   (IMPORTANTE: no "Empty Activity", ese es el template de Compose/Kotlin.
   Tiene que decir "Views").
2. Language: **Java**
3. Package name: **com.rutinagamer.app** (para que calce con el codigo
   que te dejo aqui; si usas otro nombre, ajusta el `package` en
   MainActivity.java).
4. Minimum SDK: el que venga por defecto esta bien.
5. Con el proyecto ya creado, copia toda la carpeta `rutinagamer/`
   (con `desafio`, `excepciones`, `modelo`, `estadisticas` adentro,
   la que esta en `src/main/java/rutinagamer` de este zip) dentro de
   `app/src/main/java/` de tu proyecto Android nuevo, al lado del
   paquete `com/rutinagamer/app` que ya viene ahi.
6. Reemplaza el `MainActivity.java` que genero Android Studio por el
   `MainActivity.java` que esta en esta carpeta.
7. Reemplaza `app/src/main/res/layout/activity_main.xml` por el
   `activity_main.xml` de esta carpeta.
8. Abre `app/src/main/res/values/strings.xml` (ya existe, con un solo
   string "app_name") y agrega adentro las lineas de
   `strings_fragmento.xml`.
9. Sincroniza (Android Studio te lo pide solo) y corre en un emulador
   o celular.

## Si falla la compilacion por LocalTime/LocalDate/streams

Las clases de `rutinagamer` usan `java.time` (LocalTime, LocalDate) y
streams. En minSdk bajos, Android necesita "desugaring" para eso. Si
te sale un error mencionando alguna de esas clases, busca "core
library desugaring android" — es un ajuste corto en el build.gradle
del modulo `app` (una dependencia `desugar_jdk_libs` + una linea
`coreLibraryDesugaringEnabled true`). No lo agrego yo a mano porque la
sintaxis exacta depende de la version de AGP que te instale Android
Studio.

## Que hace esta primera version

Una sola alarma fija, con un DesafioMental, sin base de datos ni lista
de alarmas todavia. El boton "Sonar alarma" llama a `alarma.disparar()`
y muestra el enunciado; "Responder" llama a `alarma.intentarSuperar()`
y maneja la excepcion checked `DesafioExpiradoException` si el tiempo
se acaba. El objeto `Alarma` vive en un campo de la Activity, en RAM
— no sobrevive a cerrar la app, y esta bien que sea asi por ahora.
