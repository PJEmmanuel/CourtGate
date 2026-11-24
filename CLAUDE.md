# CourtGate

App Android (Kotlin + Jetpack Compose) de reserva de pistas de pádel. Hoja de ruta en `ROADMAP.md`.

## Para compilar y lanzar tests

- La JBR de Android Studio está rota (le falta `lib/jvm.cfg`). Usa el JDK 23 desde PowerShell:

  ```powershell
  $env:JAVA_HOME = "C:\Program Files\Java\jdk-23"; .\gradlew.bat :app:testDebugUnitTest
  ```

## Convenciones

- **Errores**: `DomainError` (`domain/models/DomainError.kt`) + `DomainException` dentro de flows.
  Cada error nuevo necesita su texto en `DomainErrorUiMapper.kt` y su caso en `isRetryable`
  (es un `when` exhaustivo).
- **Carga de datos en ViewModels**: helper `loadLatest` (`ui/presentation/core/LoadLatest.kt`)
  con un `MutableStateFlow<Int>` de reintentos y una función `onRetry()`. Así lo hacen
  MyBookings, Booking, Find, Home, Match y Settings.
- **Pantallas de error**: `ErrorScreen(error, onRetry, failedAttempts, secondaryAction)`.
- **Textos**: ninguno escrito en el código. Cada texto nuevo va en `values/strings.xml` (español,
  idioma por defecto) y su traducción en `values-en/strings.xml`.
- **Room** (`framework/database/`): los resultados de Match solo están en el dispositivo.
  - Al cambiar una entidad: subir `CourtDatabase.VERSION`, añadir `AutoMigration(from = n, to = n + 1)`
    en `@Database` (con `AutoMigrationSpec` si se renombra o borra algo), commitear el JSON nuevo de
    `app/schemas/` y pasar `CourtMigrationTest` en el emulador. Migración manual
    (`COURT_MIGRATIONS`) solo si hay que transformar datos.
  - Nunca `fallbackToDestructiveMigration`. Solo se recrean las bases v1 y v2 (de desarrollo).
  - Una sola base para caché de Firestore y resultados en v1.0; separarlas se estudia tras publicar.
  - Antes de instalar en el emulador una rama con una versión de base más baja, copia `court_db`:
    Room no admite bajar de versión y desinstalar borra los resultados.
- **Tests**: JUnit4 + Mockito-Kotlin + Turbine + `CoroutinesTestRule`. Cada ViewModel nuevo lleva
  sus tests.

## Git

- Se trabaja en ramas creadas desde `Develop`. No hacer push ni merge sin preguntar.
