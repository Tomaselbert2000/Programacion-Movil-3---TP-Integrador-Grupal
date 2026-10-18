![Safe-Run-isologo.svg](Safe-Run-isologo.svg)

# SafeRun - Asistente de Deportes y Seguridad Vial

![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple?style=for-the-badge&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=android)
![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange?style=for-the-badge)

---

## Descripción general

**SafeRun** es una aplicación nativa para Android desarrollada con **Kotlin** y **Jetpack Compose**, diseñada para
acompañar a ciclistas y deportistas urbanos. La aplicación actúa como un copiloto inteligente que aprovecha los
**sensores físicos** del smartphone para ofrecer telemetría en tiempo real y mejorar la seguridad durante la actividad
física, operando bajo un enfoque **100% local (Local-First)**.

## Funcionalidades

- **Detección de emergencias:** Reconocimiento de impactos violentos o frenadas bruscas durante la actividad, mediante
  el acelerómetro y giroscopio, activando una alerta sonora y visual local para asistir al usuario en caso de
  accidentes.
- **Visibilidad automática:** Ajuste de la interfaz a un modo de alto contraste y activación del parpadeo de la linterna
  mediante el sensor de luz al ingresar a zonas oscuras (túneles, atardecer).
- **Orientación y telemetría:** Ofrece lectura de velocidad, inclinación del terreno y rumbo (brújula
  mediante magnetómetro).
- **Traducción en ruta:** Permite escanear carteles o señales en idiomas extranjeros usando la cámara del equipo.

## Compilación del proyecto

### Pre-requisitos

- **JDK**: versión 11 o superior
- **Android** SDK: 29
- **Lenguaje & UI**: Kotlin 1.9+ y Jetpack Compose

### Clonar el repositorio

Ejecutar el siguiente comando en la terminal del sistema (Bash o CMD) para clonar el repositorio:

```shell
# Clonamos el repo
git clone https://github.com/Tomaselbert2000/Programacion-Movil-3---TP-Integrador-Grupal.git

# Nos movemos a la carpeta principal
cd /Programacion-Movil-3---TP-Integrador-Grupal 
```

Luego, verificar estado y compilar con Gradle Wrapper:

```shell
# El binario de Gradle ya se encuentra dentro del directorio raíz del proyecto
./gradlew check
```

### Ejecución de tests

Las pruebas unitarias del proyecto podrán ejecutarse a partir del siguiente comando de Gradle:

```shell
# Pruebas aisladas sin necesidad de un dispositivo ni emulador
./gradlew test
```

Por otra parte, la suite de pruebas de integración de componentes de Jetpack y servicios del sistema correrán ejecutando
lo siguiente:

```shell
# Será necesario conectar un equipo real, o bien un emulador
./gradlew connectedAndroidTest
```

### Configuración de API Key para mapas

Para configurar correctamente la clave API para utilizar mapas en la aplicación, es necesario crear una copia del
archivo ```local.properties.template``` en la raíz del proyecto, y renombrarla a ```local.properties```. Luego, en dicho
archivo
se almacenará la clave API con el formato a continuación:

```properties
MAPS_API_KEY=VALOR_DE_LA_CLAVE
```

Ya con la clave configurada, solo restará compilar el proyecto con Gradle y ejecutarlo. La clave API se inyectará
automáticamente en el archivo ```AndroidManifest.xml```.