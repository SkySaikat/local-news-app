package com.chittagong.localnews

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Firebase initialises itself through its bundled ContentProvider as soon as the
 * process starts (the google-services plugin wires `google-services.json` into
 * the generated resources), so no manual `FirebaseApp.initializeApp` is needed.
 */
@HiltAndroidApp
class LocalNewsApplication : Application()
