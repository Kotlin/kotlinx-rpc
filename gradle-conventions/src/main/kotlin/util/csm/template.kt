/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.csm

import org.gradle.api.GradleException
import org.slf4j.Logger

object CsmTemplateProcessor {
    fun process(lines: List<String>, kotlinCompilerVersion: String, logger: Logger, replacementMap: Map<String, String>): List<String> {
        val result = mutableListOf<String>()

        val tags: ArrayDeque<String> = ArrayDeque()
        var section: String? = null
        var default = false
        var specific: Specific? = null
        var matchedSpecific = false
        var matchedDefault = false

        val defaultBuffer = mutableListOf<String>()
        val specificBuffer = mutableListOf<String>()

        lines.map { it.applyReplacements(replacementMap) }.forEachIndexed { i, line ->
            val trimmed = line.trim()
            when {
                trimmed.startsWith("//##csm") -> {
                    val header = trimmed.removePrefix("//##csm").trim()
                    val name = header.substringBefore("=")

                    if (name.startsWith("/")) {
                        val latest = tags.firstOrNull()
                        if (latest == null || name != "/$latest") {
                            throw GradleException("Expected tag '$latest' in csm header, but got '$name': $i >> $line")
                        }

                        tags.removeFirst()

                        when {
                            name == "/default" -> default = false
                            name == "/specific" -> specific = null
                            section != null && name == "/$section" -> {
                                when {
                                    matchedSpecific -> {
                                        result += specificBuffer
                                    }

                                    matchedDefault -> {
                                        logger.info("[CSM] Matched default for $section")
                                        result += defaultBuffer
                                    }
                                }

                                matchedSpecific = false
                                specificBuffer.clear()

                                matchedDefault = false
                                defaultBuffer.clear()

                                section = null
                            }

                            else -> throw GradleException("Unexpected tag in csm header '$name'. Expected $section: $i >> $line")
                        }
                    } else {
                        when {
                            default && name == "default" -> {
                                throw GradleException("Nested 'default' tag in csm header: $i >> $line")
                            }

                            specific != null && name == "specific" -> {
                                throw GradleException("Nested 'specific' tag in csm header: $i >> $line")
                            }

                            section != null && name == section -> {
                                throw GradleException("Nested '$section' tag in csm header: $i >> $line")
                            }
                        }

                        tags.addFirst(name)

                        when (name) {
                            "default" -> {
                                if (matchedDefault) {
                                    throw GradleException("Multiple 'default' tags in csm header: $i >> $line")
                                }

                                matchedDefault = true

                                default = true
                            }

                            "specific" -> {
                                val pattern = header.substringAfter("=")
                                    .removePrefix("[")
                                    .removeSuffix("]")
                                    .takeIf { it.isNotEmpty() }
                                    ?: throw GradleException("Expected pattern in csm header 'specific': $i >> $line")

                                specific = if (!matchedSpecific && matchesKotlinVersion(kotlinCompilerVersion, pattern)) {
                                    logger.info("[CSM] Matched specific '$pattern' for $section")
                                    matchedSpecific = true

                                    Specific.Apply
                                } else {
                                    Specific.Skip
                                }
                            }

                            else -> section = name
                        }
                    }
                }

                section != null && !default && specific == null -> {
                    throw GradleException("Expected 'default' or 'specific' tag before code in '$section' tag: $i >> $line")
                }

                section == null -> {
                    result += line
                }

                default -> {
                    defaultBuffer += line
                }

                specific == Specific.Apply -> {
                    specificBuffer += line
                }

                specific == Specific.Skip -> {}
            }
        }

        return result
    }

    private fun matchesKotlinVersion(projectVersion: String, pattern: String): Boolean {
         return pattern.split(",").any { part ->
            if (part.contains("...")) {
                val (from, to) = part
                    .split("...")

                if (from.contains("*")) {
                    throw GradleException("Wildcard is not allowed in 'from' part of kotlin version range: $from, $pattern")
                }

                if (from.contains("-") || to.contains("-")) {
                    throw GradleException("Non stable versions are not allowed in kotlin version range: $pattern")
                }

                val fromV = KotlinVersion.parse(from)
                val toV = KotlinVersion.parse(to)
                val projectV = KotlinVersion.parse(projectVersion)

                projectV in fromV..toV
            } else {
                val prefix = part.trim().substringBefore("*")
                projectVersion.startsWith(prefix)
            }
        }
    }

    private fun KotlinVersion.Companion.parse(string: String): KotlinVersion {
        val version = string
            .substringBefore("-")
            .replace("*", "${KotlinVersion.MAX_COMPONENT_VALUE}")
            .split(".")
            .map { it.toInt() }

        val major = version.getOrNull(0) ?: throw GradleException("Expected major version in kotlin version range: $string")
        val minor = version.getOrNull(1) ?: KotlinVersion.MAX_COMPONENT_VALUE
        val patch = version.getOrNull(2) ?: KotlinVersion.MAX_COMPONENT_VALUE

        return KotlinVersion(major, minor, patch)
    }

    private enum class Specific {
        Skip, Apply;
    }
}

private fun String.applyReplacements(replacementMap: Map<String, String>): String {
    var result = this
    replacementMap.forEach { (from, to) ->
        result = result.replace(from, to)
    }
    return result
}
