# Local News Chittagong - Development Roadmap

## Architecture Overview
- **Language & UI:** Kotlin, Jetpack Compose, Material 3
- **Architecture:** MVVM + Clean Architecture + Repository Pattern
- **Backend:** Firebase Auth, Cloud Firestore, Cloud Storage, Firebase Cloud Messaging
- **APIs & Hardware:** Google Maps SDK, FusedLocationProviderClient, OpenWeather REST API (Retrofit)

---

## 3-Phase Milestone Plan

### Phase 1: Foundation & Authentication (Complete — v1.0)
- [x] Setup `build.gradle.kts` dependencies & Material 3 theming (Light/Dark mode)
- [x] Implement Firebase Auth (Login, Sign Up, Password Reset, Session Persistence)
- [x] Create Firestore `users` document creation on register
- [x] Build Navigation Graph & Bottom Navigation Bar shell
- [x] Build User Profile screen with active Logout action

Delivered on top of the checklist: Clean Architecture layering with Hilt DI,
type-safe navigation routes, `UiState<T>` state management, a shared design
system (spacing scale, branded components), and JVM unit tests for the
validators. See [README.md](README.md) and [SETUP.md](SETUP.md).

### Phase 2: Location Engine & Spot Management (Next — v2.0)
- [ ] Integrate Google Maps SDK and runtime Location permissions
- [ ] Extract real-time coordinates using `FusedLocationProviderClient`
- [ ] Create 'Add Spot' screen with validation, Camera/Gallery image picker, and Firebase Storage upload
- [ ] Firestore `posts` CRUD integration and map marker rendering

### Phase 3: Proximity Filtering, Engagement & Polish
- [ ] Implement Geohash spatial queries (500m / 2km / 10km radius)
- [ ] Add post details screen with comments subcollection and upvoting
- [ ] Integrate OpenWeather API with Retrofit for live weather cards
- [ ] Offline caching via Room database and FCM push notifications