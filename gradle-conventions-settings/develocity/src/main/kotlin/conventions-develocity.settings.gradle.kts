/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import java.util.*

plugins {
    id("com.gradle.develocity")
    id("com.gradle.common-custom-user-data-gradle-plugin")
}

develocity {
    // Write scan journal to .gradle/ so it doesn't change the included build's directory fingerprint.
    // Previously written to settingsDir, which caused CC invalidation on every build after a scan publish.
    val scanJournal = File(settings.rootDir, ".gradle/scan-journal.log")

    server.set(DEVELOCITY_SERVER)

    buildScan {
        uploadInBackground.set(!isCIRun)

        // obfuscate NIC since we don't want to expose user real IP (will be relevant without VPN)
        obfuscation {
            ipAddresses { addresses -> addresses.map { _ -> "0.0.0.0" } }
        }

        capture {
            fileFingerprints.set(true)
        }

        buildScanPublished {
            // Note: startParameter was previously logged here but removed because
            // gradle.startParameter is not CC-serializable — capturing it in this lambda
            // caused configuration cache violations.
            scanJournal.appendText("${Date()} — $buildScanUri\n")
        }

        val skipBuildScans = settings.providers.gradleProperty("kotlinx.rpc.develocity.skipBuildScans")
            .getOrElse("false")
            .toBooleanStrict()

        publishing.onlyIf { !skipBuildScans }
    }
}

buildCache {
    if (isCIRun) {
        local {
            isEnabled = false
        }
    }

    remote(develocity.buildCache) {
        isPush = isCIRun
        isEnabled = true
    }
}

enrichTeamCityData()
enrichGitData()
