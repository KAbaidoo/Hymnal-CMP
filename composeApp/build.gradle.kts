import java.io.File
import java.util.regex.Pattern

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.firebase.appdistribution)
}

kotlin {

    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }

    }
    
    sourceSets {

        androidMain.dependencies {

            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.splashscreen)
            implementation(compose.preview)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.common)
            implementation(libs.firebase.analytics)
            implementation(libs.sqldelight.android)


            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(compose.uiTooling)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)

            implementation(libs.voyager.navigator)
            implementation(libs.voyager.transitions)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.noargs)
            implementation(libs.sqldelight.coroutines)

            implementation(project.dependencies.platform(libs.koin.bom))
            api(libs.koin.core)
            implementation(libs.koin.compose)

        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.ios)
        }
    }

//    jvmToolchain(17)

}

val appVersionName = readLatestAppVersion()
val appVersionCode = versionNameToCode(appVersionName)

android {
    namespace = "com.kobby.hymnal"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/composeResources")

    defaultConfig {
        applicationId = "com.kobby.hymnal"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = appVersionCode
        versionName = "$appVersionName"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }

        getByName("debug") {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-alpha"

            firebaseAppDistribution {
                artifactType = "APK"
                releaseNotes = readLatestDevReleaseNotes(appVersionName)
                groups = "internal-testers"  // You can create a separate testing group
            }
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

sqldelight {
    databases {
        create("HymnDatabase") {
            packageName = "com.kobby.hymnal.composeApp.database"
        }
    }
}

/**
 * Reads the latest app version from the release notes file by finding the last version header.
 */
fun readLatestAppVersion(): String {
    val versionFile = File(project.rootDir, "release_notes")
    val lines = versionFile.readLines().reversed()

    val pattern = Pattern.compile("^(?:(\\d+)\\.)?(\\d+)\\.(\\d+)\$")

    for (line in lines) {
        val trimmedLine = line.trim()
        // Skip comments and blank lines
        if (trimmedLine.startsWith("#") || trimmedLine.isEmpty()) {
            continue
        }

        if (pattern.matcher(trimmedLine).matches()) {
            return trimmedLine
        }
    }

    // If no version found, return fallback
    return "0.0.0"
}
/**
 * Read the file that contains the release notes and return the latest one
 * that matches the conditions described in the release_notes.txt file.
 */
fun readLatestDevReleaseNotes(currentVersion: String): String {
    // val versionFile = File(project.rootDir, 'release_notes')
    // val stream = FileInputStream(versionFile)

    val versionFile = File(project.rootDir, "release_notes")
    val lines = versionFile.readLines()
    val sb = StringBuilder()

    val pattern = Pattern.compile("^(?:(\\d+)\\.)?(?:(\\d+)\\.)?(\\*|\\d+)\$")
    var foundCurrentVersion = false

    for (line in lines) {

        // if we find comment, move on to next line
        if (line.startsWith("#")) {
            // found comment
            continue
        }

        // find the current app version,
        // move on to next line afterwards
        if (line.equals(currentVersion, ignoreCase = true)) {
            foundCurrentVersion = true
            continue
        }

        // if already fond current version, append release lines
        if (foundCurrentVersion) {
            sb.append(line)
            sb.append(System.lineSeparator())
        }

        // when we find a new line or different app version, after
        // previous releases have been appended, exit.
        if (sb.isNotBlank() && (line.isBlank() || pattern.matcher(line).matches())) {
            break
        }
    }

    // if no release were added
    if (sb.isBlank()) {
        sb.append("No release notes added for version $currentVersion")
    }

    return sb.toString()
}

/**
 * Converts semantic version string to integer version code.
 * Format: major * 10000 + minor * 100 + patch
 * Example: "1.2.3" -> 10203, "2.0.0" -> 20000
 */
fun versionNameToCode(versionName: String): Int {
    val parts = versionName.split(".")
    val major = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0
    
    return major * 10000 + minor * 100 + patch
}
