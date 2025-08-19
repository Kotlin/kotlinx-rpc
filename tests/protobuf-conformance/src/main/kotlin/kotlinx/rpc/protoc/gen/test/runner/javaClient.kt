/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.test.runner

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission
import kotlin.io.path.absolutePathString
import kotlin.io.path.writeText

fun getJavaClient(
    jarPath: String,
    mode: String,
    outputDir: Path,
    testName: String? = null,
    debug: Boolean = false,
): Path {
    val executable = Files.createTempFile("clientRunner", ".sh")

    Files.setPosixFilePermissions(executable, PosixFilePermission.entries.toSet())

    val debugFlag = if (debug) {
        "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=*:5005"
    } else {
        ""
    }

    val dumpConformanceInputFilePath = Files.createTempFile("dump_conformance_input", ".bin").absolutePathString()
    val dumpConformanceInputFilePathTxt = outputDir.resolve("dump_conformance_input.bin.txt").absolutePathString()

    val dumpConformanceOutputFilePath = Files.createTempFile("dump_conformance_output", ".bin").absolutePathString()
    val dumpConformanceOutputFilePathTxt = outputDir.resolve("dump_conformance_output.bin.txt").absolutePathString()

    val dumpPayloadInputFilePath = Files.createTempFile("dump_payload_input", ".bin").absolutePathString()
    val dumpPayloadInputFilePathTxt = outputDir.resolve("dump_payload_input.bin.txt").absolutePathString()

    val dumpPayloadOutputFilePath = Files.createTempFile("dump_payload_output", ".bin").absolutePathString()
    val dumpPayloadOutputFilePathTxt = outputDir.resolve("dump_payload_output.bin.txt").absolutePathString()

    val protoscopePath = System.getenv("PROTOSCOPE_PATH")

    // protoscope doesn't support proto2 and editions tests
    val protoscope = protoscopePath != null &&
            testName != null &&
            !testName.contains("Proto2") &&
            !testName.contains("Editions")

    val conformancePbPath = System.getenv("CONFORMANCE_PB_PATH")
    val testAllTypesProto3PbPath = System.getenv("TEST_ALL_TYPES_PROTO3_PB_PATH")

    if (protoscope && conformancePbPath == null) {
        error("Expected environment variable 'CONFORMANCE_PB_PATH' to be set")
    }

    if (protoscope && testAllTypesProto3PbPath == null) {
        error("Expected environment variable 'TEST_ALL_TYPES_PROTO3_PB_PATH' to be set")
    }

    val conformanceDescriptorSetInput = "-descriptor-set $conformancePbPath -message-type conformance.ConformanceRequest -print-field-names -print-enum-names"
    val conformanceDescriptorSetOutput = "-descriptor-set $conformancePbPath -message-type conformance.ConformanceResponse -print-field-names -print-enum-names"

    val testAllTypesDescriptorSetInput = "-descriptor-set $testAllTypesProto3PbPath -message-type protobuf_test_messages.proto3.TestAllTypesProto3 -print-field-names -print-enum-names"
    val testAllTypesDescriptorSetOutput = "-descriptor-set $testAllTypesProto3PbPath -message-type protobuf_test_messages.proto3.TestAllTypesProto3 -print-field-names -print-enum-names"

    fun protoscope(cmd: String): String {
        if (protoscope) {
            return cmd
        }

        return ""
    }

    executable.writeText(
        """
            #!/bin/bash

            ${protoscope("export DUMP_CONFORMANCE_INPUT_FILE=$dumpConformanceInputFilePath")}
            ${protoscope("export DUMP_CONFORMANCE_OUTPUT_FILE=$dumpConformanceOutputFilePath")}

            ${protoscope("export DUMP_PAYLOAD_INPUT_FILE=$dumpPayloadInputFilePath")}
            ${protoscope("export DUMP_PAYLOAD_OUTPUT_FILE=$dumpPayloadOutputFilePath")}

            java $debugFlag -jar $jarPath $mode
            
            ${protoscope("$protoscopePath $conformanceDescriptorSetInput $dumpConformanceInputFilePath > $dumpConformanceInputFilePathTxt")}
            ${protoscope("$protoscopePath $conformanceDescriptorSetOutput $dumpConformanceOutputFilePath > $dumpConformanceOutputFilePathTxt")}

            ${protoscope("$protoscopePath $testAllTypesDescriptorSetInput $dumpPayloadInputFilePath > $dumpPayloadInputFilePathTxt")}
            ${protoscope("$protoscopePath $testAllTypesDescriptorSetOutput $dumpPayloadOutputFilePath > $dumpPayloadOutputFilePathTxt")}
        """.trimIndent()
    )

    return executable
}
