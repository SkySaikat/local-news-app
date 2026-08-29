# Local News Chittagong — v1.0

A hyper-local, map-driven community alert app for the Chittagong metro area.
This repository currently contains **Version 1.0: Foundation, Architecture &
Authentication** — the full app skeleton, theming, navigation and a working
Firebase email/password auth flow with Firestore profile creation.

Maps, Spot CRUD, geohash proximity queries, weather and notifications arrive in
v2.0 and v3.0; the Feed and Add Spot tabs already exist as real navigation
destinations with placeholder bodies.

---

## Tech stack

| Concern | Choice |
| --- | --- |
| Language / UI | Kotlin, Jetpack Compose, Material 3 (dynamic color, light + dark) |
| Architecture | Clean Architecture + MVVM + Repository pattern |
| State | `StateFlow` + `UiState<T>`, one-shot `UiEvent` channel |
| DI | Hilt (KSP) |
| Navigation | Navigation Compose with `@Serializable` type-safe routes |
| Backend | Firebase Auth + Cloud Firestore (+ Storage wired for v2.0) |
| Build | AGP 9.3.2 (built-in Kotlin 2.2.10), Gradle 9.7.1, compileSdk 37, minSdk 26 |

## Getting started

1. Install JDK 17+ (Android Studio's bundled JBR works).
2. Create a Firebase project and drop `google-services.json` into `app/`.
   Full walkthrough: **[SETUP.md](SETUP.md)**. A committed
   `app/google-services.json.template` shows the expected shape, and the repo
   ships with a placeholder so the project configures out of the box — the app
   will not authenticate until you replace it.
3. Build and install:

   ```bash
   ./gradlew :app:assembleDebug
   ./gradlew :app:installDebug     # with a device or emulator attached
   ./gradlew :app:testDebugUnitTest
   ```

## Architecture

Dependencies point inward only: `ui → domain ← data`. The UI layer never sees a
Firebase type — repositories translate SDK exceptions into user-readable
messages before they cross the boundary.

```
        ┌──────────────────────────────────────────────┐
        │  ui/         Compose screens + ViewModels     │
        │              UiState<T>, UiEvent              │
        └───────────────────────┬──────────────────────┘
                                │ use cases
        ┌───────────────────────▼──────────────────────┐
        │  domain/     Models, repository interfaces,   │
        │              use cases (pure Kotlin)          │
        └───────────────────────▲──────────────────────┘
                                │ implements
        ┌───────────────────────┴──────────────────────┐
        │  data/       Firebase Auth + Firestore,       │
        │              DTOs and mappers                 │
        └──────────────────────────────────────────────┘
```

### Package structure

```
com.chittagong.localnews
├── LocalNewsApplication.kt          @HiltAndroidApp
├── MainActivity.kt                  single activity, edge-to-edge, splash install
│
├── core/
│   ├── common/    UiState, UiEvent, ChittagongAreas, Firestore constants
│   └── util/      Validators (pure, unit-tested), AuthErrorMapper
│
├── domain/
│   ├── model/     UserProfile (+ TrustTier), AuthUser
│   ├── repository/  AuthRepository, UserRepository (interfaces)
│   └── usecase/   SignIn, SignUp, SendPasswordReset, SignOut,
│                  GetAuthSession, ObserveCurrentUserProfile
│
├── data/
│   ├── remote/dto/  UserDto + DocumentSnapshot mapper
│   └── repository/  AuthRepositoryImpl, UserRepositoryImpl
│
├── di/            FirebaseModule, RepositoryModule, CoroutinesModule, Qualifiers
│
└── ui/
    ├── theme/       Color, Type, Shape, Spacing, Theme (dynamic color)
    ├── navigation/  Destinations (type-safe routes), LocalNewsNavHost
    ├── components/  AppTextField, PasswordTextField, AreaDropdown, Buttons,
    │                AuthBackdrop, BrandMark, AppSnackbar, ComingSoonScreen
    ├── auth/        splash/, login/ (+ ForgotPasswordDialog), signup/
    ├── main/        MainShellScreen (Scaffold + bottom navigation)
    ├── feed/        FeedScreen        — placeholder for v2.0
    ├── addspot/     AddSpotScreen     — placeholder for v2.0
    └── profile/     ProfileScreen + ProfileViewModel (live Firestore + logout)
```

### Navigation graph

```
NavHost (root)
├── AuthGraph
│   ├── SplashRoute      session check → routes to shell or login
│   ├── LoginRoute       email/password + forgot-password dialog
│   └── SignUpRoute(prefilledEmail)
└── MainShellRoute       Scaffold + bottom bar, owns a nested NavHost:
    └── MainGraph
        ├── FeedRoute       (placeholder)
        ├── AddSpotRoute    (placeholder)
        └── ProfileRoute    (live)
```

Routes are `@Serializable` objects/classes rather than strings, so arguments are
compile-time checked. Crossing between the auth and main branches always clears
the other side's back stack.

## What v1.0 does

- **Splash** — reads the persisted Firebase session and routes straight to the
  shell or to login, behind a short brand animation.
- **Login** — live per-field validation, animated inline errors, loading state on
  the CTA, coloured snackbars, and a password-reset dialog that pre-fills the
  email already typed.
- **Sign up** — name, email, home-area dropdown (66 areas, grouped into Chittagong city and Greater Chittagong), password
  with a strength meter and confirmation. On success it creates the Auth
  credential **and** the `users/{uid}` Firestore document
  (`uid`, `displayName`, `email`, `homeArea`, `trustScore: 100`,
  `profileImageUrl`, `joinedTimestamp`, `createdAt`). If the profile write fails
  the session is signed back out so the app can never boot into an empty profile.
- **Shell** — Material 3 bottom bar with Feed / Add Spot / Profile, state-saving
  tab navigation, animated icon transitions.
- **Profile** — live Firestore snapshot listener (updates without a refresh),
  gradient initials avatar, trust-score card with tier, member details, and a
  confirmed logout that clears the back stack.

## Testing

```bash
./gradlew :app:testDebugUnitTest
```

10 JVM unit tests cover the form validators and the profile domain model.
`Validators` deliberately avoids `android.util.Patterns` so it runs without
Robolectric.

## Known environment gotcha

This project lives under `~/Desktop`, which is **iCloud-synced**. iCloud syncs
`app/build/` and resolves races by creating duplicate files named `Foo 2.class`,
which makes D8 fail with *"Type ... is defined multiple times"*. If you see that:

```bash
./gradlew clean && rm -rf app/build
```

To stop it recurring, move the repo somewhere unsynced (e.g. `~/AndroidStudioProjects`).

## Roadmap

See [ProjectPlan.md](ProjectPlan.md). Phase 1 is complete.
