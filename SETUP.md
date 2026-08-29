# Firebase setup

The project ships with a **placeholder** `app/google-services.json` so it
configures and builds immediately. Authentication will fail at runtime until you
replace it with a real one. Nothing else is needed to build.

## 1. Create the Firebase project

1. Go to <https://console.firebase.google.com> and click **Add project**.
2. Name it (e.g. `local-news-chittagong`). Analytics is optional.

## 2. Register the Android app

1. In the project, click the Android icon to add an app.
2. **Android package name — must be exactly:**

   ```
   com.chittagong.localnews
   ```

   This matches `applicationId` in `app/build.gradle.kts`. The debug build type
   deliberately has **no** `applicationIdSuffix`, so one registered app covers
   both debug and release.
3. App nickname and the debug signing SHA-1 are optional for email/password
   auth. (You will need the SHA-1 later if you add Google Sign-In.)

## 3. Drop in google-services.json

Download the generated `google-services.json` and replace the placeholder:

```
app/google-services.json
```

It is listed in `.gitignore` — the file identifies your Firebase project, so
each developer supplies their own. `app/google-services.json.template` documents
the expected shape.

No code change is required: the `com.google.gms.google-services` Gradle plugin
reads the file at build time and Firebase initialises itself from the generated
resources through its bundled `ContentProvider`. There is no
`FirebaseApp.initializeApp()` call anywhere, and none is needed.

## 4. Enable Authentication

**Build → Authentication → Get started → Sign-in method → Email/Password →
Enable → Save.**

Without this, sign-in returns `ERROR_OPERATION_NOT_ALLOWED`, which the app
surfaces as *"Email/password sign-in is disabled for this project."*

Password-reset emails work with no extra setup; customise the wording under
**Authentication → Templates**.

## 5. Create the Firestore database

**Build → Firestore Database → Create database.** Pick a region close to
Bangladesh (`asia-south1`, Mumbai, is the nearest low-latency option). Start in
**production mode**, then paste the rules below.

### Security rules for v1.0

**Rules → paste → Publish:**

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // A signed-in user may read any profile (needed later to show post authors)
    // but may only create or edit their own document.
    match /users/{userId} {
      allow read: if request.auth != null;

      allow create: if request.auth != null
                    && request.auth.uid == userId
                    && request.resource.data.uid == userId
                    // Nobody gets to hand themselves a head start on reputation.
                    && request.resource.data.trustScore == 100;

      allow update: if request.auth != null
                    && request.auth.uid == userId
                    // trustScore is server-owned; clients can't raise it.
                    && request.resource.data.trustScore == resource.data.trustScore;

      allow delete: if false;
    }

    // Placeholders for v2.0 — locked until the Spot CRUD ships.
    match /posts/{postId} {
      allow read, write: if false;
    }
  }
}
```

The `users` document this app writes on sign-up:

| Field | Type | Value |
| --- | --- | --- |
| `uid` | string | Firebase Auth UID (also the document ID) |
| `displayName` | string | Name from the sign-up form |
| `email` | string | Sign-up email |
| `homeArea` | string | One of the Chittagong areas in `ChittagongAreas.ALL` |
| `trustScore` | number | `100` |
| `profileImageUrl` | string | `""` until v2.0 image upload |
| `joinedTimestamp` | number | Epoch millis |
| `createdAt` | number | Epoch millis (mirror of `joinedTimestamp`) |

## 6. Cloud Storage (optional now, required in v2.0)

`FirebaseStorage` is already provided by `FirebaseModule` but unused in v1.0.
Enable **Build → Storage** when you start the image-upload work.

## 7. Run it

```bash
./gradlew :app:installDebug
```

Create an account in the app, then confirm in the Firebase console that:

- **Authentication → Users** lists the new user, and
- **Firestore → users → {uid}** holds the profile document with
  `trustScore: 100`.

---

## Troubleshooting

| Symptom | Cause |
| --- | --- |
| `File google-services.json is missing` | The file isn't in `app/` (not the project root). |
| `No matching client found for package name` | The package in Firebase isn't `com.chittagong.localnews`, or a build type re-added an `applicationIdSuffix`. |
| *"Email/password sign-in is disabled"* | Step 4 was skipped. |
| *"You don't have permission to do that"* on sign-up | Firestore rules weren't published, or the database is still in locked mode. |
| Profile tab shows *"We couldn't find your profile"* | The Auth user exists but the `users/{uid}` document doesn't — sign up again, or add the document by hand. |
| *"API key not valid. Please pass a valid API key"* | `app/google-services.json` is still the placeholder. Do steps 2–3. |
| D8: *"Type ... is defined multiple times"* | iCloud sync duplicated files under `app/build/`. Run `./gradlew clean && rm -rf app/build`. |
