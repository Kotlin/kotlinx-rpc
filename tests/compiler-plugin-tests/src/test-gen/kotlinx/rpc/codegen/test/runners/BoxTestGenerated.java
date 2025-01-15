

/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test.runners;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TargetBackend;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link kotlinx.rpc.codegen.test.GenerateTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("src/testData/box")
@TestDataPath("$PROJECT_ROOT")
@Disabled("KRPC-137")
public class BoxTestGenerated extends AbstractBoxTest {
  @Test
  public void testAllFilesPresentInBox() {
    KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("src/testData/box"), Pattern.compile("^(.+)\\.kt$"), null, TargetBackend.JVM_IR, true);
  }

  @Test
  @TestMetadata("customParameterTypes.kt")
  public void testCustomParameterTypes() {
    runTest("src/testData/box/customParameterTypes.kt");
  }

  @Test
  @TestMetadata("fields.kt")
  public void testFields() {
    runTest("src/testData/box/fields.kt");
  }

  @Test
  @TestMetadata("flowParameter.kt")
  public void testFlowParameter() {
    runTest("src/testData/box/flowParameter.kt");
  }

  @Test
  @TestMetadata("multiModule.kt")
  public void testMultiModule() {
    runTest("src/testData/box/multiModule.kt");
  }

  @Test
  @TestMetadata("simple.kt")
  public void testSimple() {
    runTest("src/testData/box/simple.kt");
  }
}
