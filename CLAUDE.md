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
- **Textos**: los nuevos van en `strings.xml` en español. Los que ya están escritos en el código
  son trabajo de la Fase 3: no los muevas mientras tanto.
- **Tests**: JUnit4 + Mockito-Kotlin + Turbine + `CoroutinesTestRule`. Cada ViewModel nuevo lleva
  sus tests.

## Git

- Se trabaja en ramas creadas desde `Develop`. No hacer push ni merge sin preguntar.
