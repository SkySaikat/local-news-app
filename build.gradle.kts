// Top-level build file. Plugins are declared (but not applied) here so the
// versions resolve from gradle/libs.versions.toml for every module.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.google.services) apply false
}

/*
 * This repo lives under ~/Desktop, which macOS keeps in iCloud Drive. iCloud
 * syncs `build/` too, and resolves write races by duplicating files as
 * "Foo 2.class" — which makes D8 fail with "Type ... is defined multiple times".
 *
 * Redirecting build output outside the synced tree removes the whole class of
 * failure. Delete this block if you move the project somewhere unsynced.
 */
val buildRoot = File(System.getProperty("user.home"), ".gradle-build-dirs/local-news-app")
allprojects {
    layout.buildDirectory.set(File(buildRoot, project.path.replace(":", "-").trim('-').ifEmpty { "root" }))
}
