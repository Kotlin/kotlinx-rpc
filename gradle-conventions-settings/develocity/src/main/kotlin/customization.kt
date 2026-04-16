/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import com.gradle.develocity.agent.gradle.DevelocityConfiguration
import org.gradle.api.initialization.Settings

fun Settings.enrichTeamCityData(isCIRun: Boolean) {
    val ge = extensions.getByType(DevelocityConfiguration::class.java)

    gradle.projectsEvaluated {
        if (isCIRun) {
            val buildTypeId = "teamcity.buildType.id"
            val buildId = "teamcity.build.id"

            if (gradle.rootProject.hasProperty(buildId) && gradle.rootProject.hasProperty(buildTypeId)) {
                val buildIdValue = gradle.rootProject.property(buildId).toString()
                val teamCityBuildNumber = java.net.URLEncoder.encode(buildIdValue, Charsets.UTF_8)
                val teamCityBuildTypeId = gradle.rootProject.property(buildTypeId)

                ge.buildScan.link(
                    "kotlinx.rpc TeamCity build",
                    "${TEAMCITY_URL}/buildConfiguration/${teamCityBuildTypeId}/${teamCityBuildNumber}"
                )
            }

            if (gradle.rootProject.hasProperty(buildId)) {
                ge.buildScan.value("CI build id", gradle.rootProject.property(buildId) as String)
            }
        }
    }
}

fun Settings.enrichGitData(isCIRun: Boolean) {
    val ge = extensions.getByType(DevelocityConfiguration::class.java)

    val skipGitTags = settings.providers.gradleProperty("kotlinx.rpc.develocity.skipGitTags")
        .getOrElse("false")
        .toBooleanStrict()

    if (!isCIRun && !skipGitTags) {
        // Use background {} to run git commands outside the configuration cache fingerprinting window.
        // Previously, providers.exec was used inside projectsEvaluated {}, which made git output
        // a CC input — every commit invalidated the cache.
        val projectDir = settings.settingsDir // capture before entering background thread
        ge.buildScan.background {
            val commitId = execProcess("git", "rev-parse", "--verify", "HEAD", workDir = projectDir)
            if (commitId.isNotEmpty()) {
                value("Git Commit ID", commitId)
                link("GitHub Commit Link", "$GITHUB_REPO/tree/$commitId")
            }

            val branchName = execProcess("git", "rev-parse", "--abbrev-ref", "HEAD", workDir = projectDir)
            if (branchName.isNotEmpty()) {
                value("Git Branch Name", branchName)
                link("GitHub Branch Link", "$GITHUB_REPO/tree/$branchName")
            }

            val status = execProcess("git", "status", "--porcelain", workDir = projectDir)
            if (status.isNotEmpty()) {
                value("Git Status", status)
            }
        }
    }
}
