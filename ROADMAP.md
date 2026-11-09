# 🗺️ Hoja de ruta CourtGate v1.0

Objetivo: proyecto escaparate publicado en Google Play.
Sin fecha de entrega; las estimaciones son orientativas.

Última revisión: 2026-11-03

---

## 📍 Punto de partida

- Último commit: 2026-06-25, rama `feature/mybookings` (4 commits por delante de `Develop`).
- `main` no recibe cambios desde junio de 2025 (97 commits por detrás).
- La app compila. Los tests unitarios **no compilan**: `FakeRemoteDS` (`app/src/test/.../fakes.kt`) no implementa `getMyBookings` ni `deleteMyBookings`.

| Área | Estado |
|---|---|
| Login / Registro (Firebase) | ✅ Falta "Forgot password" |
| Home + últimos resultados (Room) | ✅ Solo local, sin pantalla para crear resultados |
| Find | ✅ Bug: muestra horas pasadas como reservables |
| Booking | ✅ Con transacción atómica y tests |
| Mis reservas | 🟡 Casi terminado |
| Match | ❌ Botón sin destino |
| Settings / Perfil / Logout | ❌ Botón sin destino |
| Notificaciones / WorkManager | ❌ Dependencia sin código |
| Retrofit | ❌ Declarado sin uso (era para pagos, fuera de v1.0) |

---

## Fase 0 · Poner la casa en orden (~1–2 días)

- [x] Implementar `getMyBookings` y `deleteMyBookings` en `FakeRemoteDS` y conseguir que todos los tests pasen
- [x] Mis reservas: formatear la info del bottom sheet (ahora sale cruda)
- [x] Mis reservas: eliminar el `!!` de `MyBookingsScreen.kt`
- [x] Tests de `MyBookingsViewModel`
- [x] Merge `feature/mybookings` → `Develop`, y `Develop` → `main`
- [x] Quitar Retrofit, Moshi y logging-interceptor de las dependencias
- [x] Actualizar el README (Retrofit, estado real, funcionalidades)

## Fase 1 · Bugs visibles para el usuario (~2–3 días)

- [ ] Find: no mostrar horas pasadas como reservables
- [ ] "Forgot password" con `sendPasswordResetEmail`
- [ ] Revisar mensajes de error: nada técnico de cara al usuario

## Fase 2 · Completar el MVP (~1–2 semanas)

- [ ] **Match**: pantalla para crear, editar y borrar resultados
  - Almacenamiento **solo local (Room)** en v1.0; los casos de uso ya existen
  - Falta UI, ViewModel, navegación y tests
  - Limpiar los TODO y el comentario "Step 5" de `MatchRepository`
- [ ] **Settings / Perfil**
  - [ ] Ver datos de la cuenta
  - [ ] Cerrar sesión
  - [ ] **Borrar cuenta** (obligatorio en Google Play para apps con registro)
    - Debe borrar también las reservas del usuario y los resultados locales

## Fase 3 · Calidad y deuda técnica (~1 semana)

- [ ] Mover todos los textos hardcodeados a `strings.xml` (ES + EN)
- [ ] Extraer `mapError` (duplicado en varios ViewModels) a un único sitio
- [ ] Unificar `myBookingsCard` y `ShowCourtCard` con un UiModel común
- [ ] Unificar estados de `BookingFlowSheetContent` y `MyBookingsFlowSheetContent` (sealed interface)
- [ ] Extraer el helper repetido en `FirebaseFirestoreDataSource`
- [ ] Room:
  - [ ] Quitar `fallbackToDestructiveMigration`
  - [ ] Configurar la carpeta de esquemas
  - [ ] Definir migraciones (los resultados de Match solo viven en el dispositivo y no se pueden perder)
- [ ] Tests de UI con Compose: login, reserva, cancelación, alta de resultado

## Fase 4 · Preparar la publicación (~1 semana + ~2 semanas de espera)

- [ ] Cambiar `applicationId` y `namespace` (`com.example.*` no se admite en Play; no se puede cambiar tras la primera subida)
- [ ] Reglas de seguridad de Firestore: cada usuario solo lee y borra sus propias reservas
- [ ] Comprobar el `targetSdk` exigido por Play y subirlo si hace falta (probablemente 36)
- [ ] Build de release:
  - [ ] `isMinifyEnabled = true` + reglas de ProGuard (Firebase, Serialization)
  - [ ] Keystore de firma fuera del repo
- [ ] Política de privacidad publicada (p. ej., GitHub Pages)
- [ ] Formulario "Seguridad de los datos" en Play Console
- [ ] Ficha de la tienda: icono, capturas, descripción, gráfico destacado
- [ ] Capturas en el README
- [ ] Prueba cerrada: 12 testers durante 14 días si la cuenta de desarrollador es personal y nueva
- [ ] Producción 🚀

---

## Después del lanzamiento (v1.1+)

- [ ] **Admin**: rol con Firebase Custom Claims
  - Gestionar reservas manualmente
  - Cambiar precios desde la app
- [ ] Sincronizar resultados de Match con Firestore
- [ ] Recordatorios de reserva (WorkManager / notificaciones push)
- [ ] Offline-first para reservas
- [ ] Pagos (Retrofit u otro SDK), si compensa
