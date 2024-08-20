# Proyecto de Aplicación Android con Configuración de Variantes y Firebase Crashlytics

Este proyecto es una aplicación Android configurada para liberarse en tres ambientes distintos (Dev, QA, Prod). Además, está integrada con Firebase Crashlytics para la gestión de errores y logs.

## Requerimientos Cumplidos

### 1. Configuración del Proyecto y Gradle para Variantes de Build

El proyecto se configuró en `build.gradle.kts` para manejar tres variantes de compilación:
- **Dev**: Ambiente de desarrollo.
- **QA**: Ambiente de testing.
- **Prod**: Ambiente de producción.

#### Configuración de Variantes en `build.gradle.kts`

```kotlin
android {
    ...
    flavorDimensions("version")
    productFlavors {
        create("dev") {
            dimension = "version"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "FLAVOR", "\"dev\"")
        }
        create("qa") {
            dimension = "version"
            applicationIdSuffix = ".qa"
            versionNameSuffix = "-qa"
            buildConfigField("String", "FLAVOR", "\"qa\"")
        }
        create("prod") {
            dimension = "version"
            buildConfigField("String", "FLAVOR", "\"prod\"")
        }
    }
    ...
}
```

Cada flavor tiene su propio applicationIdSuffix y versionNameSuffix para diferenciar las versiones de la aplicación.

### 2. Integración con Firebase y Pruebas con Crashlytics y 3. Envío de Keys a Firebase Crashlytics:

El proyecto fue integrado con Firebase para usar Crashlytics. Se conectó el proyecto de Android Studio con el proyecto en Firebase, y se realizó una prueba para asegurar que Crashlytics funciona adecuadamente.

#### Integración con Firebase

1. **Configuración del Proyecto en Firebase:**
   - Se creó un nuevo proyecto en la consola de Firebase.
   - Se agregó la aplicación Android al proyecto Firebase utilizando el `applicationId` correspondiente.
   - Se descargó el archivo `google-services.json` y se colocó en el directorio `app` de cada variante.

2. **Configuración en Android Studio:**
   - Se añadió el plugin de Firebase al proyecto en el archivo `build.gradle.kts` del proyecto:
   
   ```kotlin
   dependencies {
       implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
       implementation("com.google.firebase:firebase-crashlytics")
       implementation("com.google.firebase:firebase-analytics")
   }
   ```
   - Se aplicó el plugin de Google Services en el archivo build.gradle.kts del módulo app:

  ```kotlin
     plugins {
    id("com.android.application")
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")
}
```

  **Código para Enviar un Log a Crashlytics y keys según la variante:**

En la MainActivity, se añadió un botón que, al ser presionado, envía un log a Firebase Crashlytics y lanza una excepción para verificar la captura de errores.
```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.myButton.setOnClickListener {
            val logMessage = "Este es un mensaje de prueba(“Desarrollo de aplicaciones empresariales Android")"
            FirebaseCrashlytics.getInstance().log(logMessage)
            FirebaseCrashlytics.getInstance().setCustomKey("app_env", BuildConfig.FLAVOR)
            throw RuntimeException("Test Crash")
        }
    }
}
```

**Probar Crashlytics**

Para probar que Firebase Crashlytics funciona correctamente:
1. Se ejecutó la aplicación en un dispositivo o emulador.
2. Se presionó el botón que genera un log y lanza una excepción.
3. Se verificó en la consola de Firebase que el log y  la key de su respectiva variante (qa, dev, prod)  aparezcan en los reportes de Crashlytics.

### 4. Creación del Archivo de Firmas (JKS) con Alias Distintos

Para firmar la aplicación para diferentes entornos (QA y Producción), se creó un archivo de claves (JKS) con dos alias diferentes: uno para QA y otro para Producción.

#### Creación del archivo JKS

Se utilizó la herramienta `keytool` para crear un archivo JKS llamado `desEval.jks` con dos alias (`prodalias` y `qaalias`). A continuación se muestra cómo se realizó este proceso:

1. **Crear el archivo JKS con alias para Producción:**

    ```bash
    keytool -genkeypair -v -keystore desEval.jks -keyalg RSA -keysize 2048 -validity 10000 -alias prodalias  -sigalg SHA256withRSA
    ```

2. **Agregar un alias para QA:**

    ```bash
    keytool -genkeypair -v -keystore desEval.jks -keyalg RSA -keysize 2048 -validity 10000 -alias qaalias
    ```

#### Verificación del archivo JKS

Para verificar que los alias fueron creados correctamente dentro del archivo JKS, se utilizó el siguiente comando:

```bash
keytool -list -v -keystore desEval.jks
 ```

Esto produjo una salida indicando que el archivo de claves contiene dos alias:

```bash
Your keystore contains 2 entries

Alias name: prodalias
Creation date: 20-08-2024
...

Alias name: qaalias
Creation date: 20-08-2024
...
 ```

**Se generaron dos APKs:**

Dos APKs fueron generados y firmados con los alias correspondientes:

  - app-prod-release.apk: Firmado con prodalias.
  - app-qa-release.apk: Firmado con qaalias.

**Firma y Verificación de los APKs:**

Cada APK se firmó utilizando el alias correspondiente y luego se verificó la firma con `jarsigner`

**Verificación de las firmas en la unicación de cada apk:**
```bash
jarsigner -verify app-prod-release.apk

jarsigner -verify app-qa-release.apk

 ```

**Verificación en Android Studio**

Finalmente, se verificaron los APKs en Android Studio, confirmando la presencia de los archivos `QAALIAS.SF` y `QAALIAS.RSA` en el APK de QA, y `PRODALIAS.SF` y `PRODALIAS.RSA` en el APK de Producción dentro de la carpeta META-INF.

Esto asegura que cada build variant esté firmada con su alias correspondiente, garantizando la seguridad y autenticidad de las versiones liberadas.





### 5. Generación de Versiones de la Aplicación con Cambios de Color según el Build Variant

Para cumplir con el requerimiento de cambiar el color de fondo del `MainActivity` según el build variant (`Dev`, `QA`, `Prod`), se configuraron diferentes colores en los recursos y se implementó la lógica correspondiente en el código de la actividad principal.

#### Configuración de los Colores

Se crearon los tres colores en el  archivo `colors.xml  para definir los colores de fondo específicos para cada variant:

 **`res/values/colors.xml`:**

    ```xml
    <resources>
        <color name="dev_background">#FFFFFF</color> <!-- Blanco -->
        <color name="qa_background">#FFA500</color> <!-- Naranja -->
        <color name="prod_background">#000000</color> <!-- Negro -->
    </resources>
    ```



#### Implementación en `MainActivity`

El código en `MainActivity` se modificó para cambiar el color de fondo y mostrar el nombre del build variant actual en el `TextView`:

```kotlin
class MainActivity : AppCompatActivity() {

    ...

        // Cambiar el color de fondo según el flavor
        val backgroundColor = when (BuildConfig.FLAVOR) {
            "dev" -> R.color.dev_background
            "qa" -> R.color.qa_background
            "prod" -> R.color.prod_background
            else -> R.color.white // Default por si acaso
        }
        binding.main.setBackgroundColor(getColor(backgroundColor))

        // Mostrar el nombre del flavor en el TextView
        binding.textView.text = BuildConfig.FLAVOR

       ...
}
